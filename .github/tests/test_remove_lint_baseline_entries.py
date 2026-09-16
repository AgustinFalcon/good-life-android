from __future__ import annotations

import copy
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / ".github" / "scripts" / "remove_lint_baseline_entries.py"
PYTHON = sys.executable
LEGACY_TARGETS = (
    "color/black", "color/white", "color/purple_200", "color/purple_500",
    "color/purple_700", "color/teal_200", "color/teal_700",
)
AUTH_TARGETS = (
    "string/biometric_checkbox_label", "string/biometric_icon_description",
    "string/biometric_prompt_negative_button", "string/biometric_prompt_subtitle",
    "string/biometric_prompt_title", "string/login_button", "string/login_email_placeholder",
    "string/login_forgot_password", "string/login_password_placeholder", "string/login_remember_user",
    "string/login_subtitle", "string/login_success", "string/login_title",
)
KEEP_SYMBOL = "string/keep_me"


def fingerprint(seed: int) -> str:
    return f"{seed:064x}"


def entry(variant: str, symbol: str, value: str) -> dict:
    resource_type, name = symbol.split("/", 1)
    return {
        "fingerprint": value,
        "variant": variant,
        "ruleId": "UnusedResources",
        "relativePath": f"app/src/main/res/values/{'colors' if resource_type == 'color' else 'strings'}.xml",
        "message": f"The resource `R.{resource_type}.{name}` appears to be unused",
        "classification": "investigate",
        "owner": "android-maintainers",
        "justification": "test",
        "linkedIssueOrPr": "#38",
        "createdOn": "2026-09-16",
    }


class RemoveBaselineEntriesTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        self.baseline = self.root / "baseline.json"
        self.manifest = self.root / "manifest.json"
        self.output = self.root / "updated.json"
        self.original = self.payload_for(LEGACY_TARGETS)
        self.baseline.write_text(json.dumps(self.original, indent=2), encoding="utf-8")
        self.write_manifest("legacy-colors", LEGACY_TARGETS)

    def tearDown(self) -> None:
        self.temp.cleanup()

    @staticmethod
    def payload_for(targets: tuple[str, ...]) -> dict:
        variants = {}
        for variant, offset in (("debug", 0), ("release", 100)):
            variants[variant] = {"warnings": [entry(variant, symbol, fingerprint(index + offset)) for index, symbol in enumerate(targets, start=1)] + [entry(variant, KEEP_SYMBOL, fingerprint(900 + offset))]}
        return {
            "schemaVersion": 2,
            "normalizationVersion": 1,
            "generatedFrom": {"sourceRevision": "abc1234", "commands": [":app:lintDebug", ":app:lintRelease"], "toolchain": {"gradleWrapper": "8", "agp": "8", "jdk": "17", "sdk": "35"}, "authority": "test"},
            "variants": variants,
            "externalAdvisories": {"debug": {"warnings": []}, "release": {"warnings": []}},
            "exceptions": [],
        }

    def write_manifest(self, scope: str, targets: tuple[str, ...]) -> None:
        resources = [{"symbol": symbol, "debugFingerprint": fingerprint(index), "releaseFingerprint": fingerprint(index + 100)} for index, symbol in enumerate(targets, start=1)]
        self.manifest.write_text(json.dumps({"schemaVersion": 2, "issue": "#38", "scope": scope, "resources": resources}, indent=2), encoding="utf-8")

    def read_baseline(self) -> dict:
        return json.loads(self.baseline.read_text(encoding="utf-8"))

    def read_manifest(self) -> dict:
        return json.loads(self.manifest.read_text(encoding="utf-8"))

    def write_manifest_payload(self, payload: dict) -> None:
        self.manifest.write_text(json.dumps(payload), encoding="utf-8")

    def run_reducer(self):
        return subprocess.run([PYTHON, str(SCRIPT), "--baseline", str(self.baseline), "--manifest", str(self.manifest), "--output", str(self.output)], capture_output=True, text=True)

    def assert_fails_closed(self, expected: str) -> None:
        result = self.run_reducer()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn(expected, result.stderr)
        self.assertFalse(self.output.exists())

    def test_removes_exact_targets_and_preserves_non_target_and_protected_sections(self):
        result = self.run_reducer()
        self.assertEqual(result.returncode, 0, result.stderr)
        updated = json.loads(self.output.read_text(encoding="utf-8"))
        for section in ("generatedFrom", "externalAdvisories", "exceptions"):
            self.assertEqual(updated[section], self.original[section])
        for variant in ("debug", "release"):
            self.assertEqual(updated["variants"][variant]["warnings"], [self.original["variants"][variant]["warnings"][-1]])

    def test_missing_target_fails_closed(self):
        payload = self.read_manifest()
        payload["resources"].pop()
        self.write_manifest_payload(payload)
        self.assert_fails_closed("immutable legacy-colors scope")

    def test_extra_legitimate_resource_fails_closed(self):
        payload = self.read_manifest()
        payload["resources"].append({"symbol": KEEP_SYMBOL, "debugFingerprint": fingerprint(900), "releaseFingerprint": fingerprint(1000)})
        self.write_manifest_payload(payload)
        self.assert_fails_closed("immutable legacy-colors scope")

    def test_fingerprint_mismatch_fails_closed(self):
        payload = self.read_manifest()
        payload["resources"][0]["debugFingerprint"] = "0" * 64
        self.write_manifest_payload(payload)
        self.assert_fails_closed("fingerprint mismatch")

    def test_wrong_baseline_variant_fails_closed(self):
        payload = self.read_baseline()
        payload["variants"]["debug"]["warnings"][0]["variant"] = "release"
        self.baseline.write_text(json.dumps(payload), encoding="utf-8")
        self.assert_fails_closed("declares variant")

    def test_wrong_target_semantics_fails_closed(self):
        payload = self.read_baseline()
        payload["variants"]["debug"]["warnings"][0]["ruleId"] = "WrongRule"
        self.baseline.write_text(json.dumps(payload), encoding="utf-8")
        self.assert_fails_closed("exactly one")

    def test_duplicate_baseline_entry_fails_closed(self):
        payload = self.read_baseline()
        payload["variants"]["debug"]["warnings"].append(copy.deepcopy(payload["variants"]["debug"]["warnings"][0]))
        self.baseline.write_text(json.dumps(payload), encoding="utf-8")
        self.assert_fails_closed("exactly one")

    def test_duplicate_manifest_symbol_fails_closed(self):
        payload = self.read_manifest()
        payload["resources"].append(copy.deepcopy(payload["resources"][0]))
        self.write_manifest_payload(payload)
        self.assert_fails_closed("duplicate")

    def test_auth_strings_scope_is_immutable_and_removes_exact_targets(self):
        self.original = self.payload_for(AUTH_TARGETS)
        self.baseline.write_text(json.dumps(self.original, indent=2), encoding="utf-8")
        self.write_manifest("auth-strings", AUTH_TARGETS)
        result = self.run_reducer()
        self.assertEqual(result.returncode, 0, result.stderr)
        updated = json.loads(self.output.read_text(encoding="utf-8"))
        for variant, offset in (("debug", 0), ("release", 100)):
            self.assertEqual(len(updated["variants"][variant]["warnings"]), 1)
            self.assertEqual(updated["variants"][variant]["warnings"][0]["fingerprint"], fingerprint(900 + offset))

    def test_auth_scope_rejects_legacy_symbol(self):
        payload = self.read_manifest()
        payload["scope"] = "auth-strings"
        self.write_manifest_payload(payload)
        self.assert_fails_closed("immutable auth-strings scope")

    def test_schema_one_manifest_fails_closed(self):
        payload = self.read_manifest()
        payload["schemaVersion"] = 1
        self.write_manifest_payload(payload)
        self.assert_fails_closed("manifest schema is invalid")


if __name__ == "__main__":
    unittest.main()
