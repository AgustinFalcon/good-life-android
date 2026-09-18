#!/usr/bin/env python3
from __future__ import annotations

import copy
import json
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCRIPTS = ROOT / ".github" / "scripts"
sys.path.insert(0, str(SCRIPTS))
from lint_baseline_common import canonical_message, parse_report

PYTHON, GENERATE, VERIFY = sys.executable, SCRIPTS / "generate_lint_baseline.py", SCRIPTS / "verify_lint_baseline.py"
REMOTE_ROOT = "/home/runner/work/good-life-android/good-life-android"


def xml(path: str, message: str = "Warning message", rule: str = "SampleRule", severity: str = "Warning", location: bool = True) -> str:
    location_xml = f'<location file="{path}"/>' if location else ""
    return f'<?xml version="1.0"?><issues><issue id="{rule}" severity="{severity}" message="{message}">{location_xml}</issue></issues>'


def report_with_deterministic_and_advisory(path: str, advisory_message: str = "A newer version of x:y than 1.0 is available: 2.0.") -> str:
    return (
        '<?xml version="1.0"?><issues>'
        f'<issue id="SampleRule" severity="Warning" message="Warning message"><location file="{path}"/></issue>'
        f'<issue id="GradleDependency" severity="Warning" message="{advisory_message}"><location file="{path}"/></issue>'
        '</issues>'
    )


class LintBaselineTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name) / "repo"
        (self.root / "app").mkdir(parents=True)
        self.main = self.root / "app" / "Main.kt"
        self.main.write_text("fun main() = Unit\n", encoding="utf-8")
        self.debug, self.release, self.baseline = self.root / "debug.xml", self.root / "release.xml", self.root / "baseline.json"
        for report in (self.debug, self.release):
            report.write_text(xml(str(self.main)), encoding="utf-8")
        self.execute(GENERATE, "--source-revision", "5a7be32", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(self.baseline), "--repo-root", ".")

    def tearDown(self):
        self.temp.cleanup()

    def execute(self, script, *args, ok=True):
        result = subprocess.run([PYTHON, str(script), *args], text=True, capture_output=True, cwd=self.root)
        if ok:
            self.assertEqual(0, result.returncode, result.stderr + result.stdout)
        else:
            self.assertNotEqual(0, result.returncode)
        return result

    def verify(self, *extra, ok=True):
        return self.execute(VERIFY, "--baseline", str(self.baseline), "--debug-report", str(self.debug), "--release-report", str(self.release), "--repo-root", ".", *extra, ok=ok)

    def data(self, path=None):
        return json.loads((path or self.baseline).read_text(encoding="utf-8"))

    def save(self, payload):
        self.baseline.write_text(json.dumps(payload), encoding="utf-8")

    def remote_path(self):
        return f"{REMOTE_ROOT}/app/Main.kt"

    def write_remote_reports(self, path=None):
        for report in (self.debug, self.release):
            report.write_text(report_with_deterministic_and_advisory(path or self.remote_path()), encoding="utf-8")

    def refresh(self, output, *extra, ok=True):
        return self.execute(
            GENERATE,
            "--debug-report", str(self.debug),
            "--release-report", str(self.release),
            "--output", str(output),
            "--repo-root", ".",
            "--report-root", REMOTE_ROOT,
            "--refresh-external-from", str(self.baseline),
            "--linked-issue-or-pr", "#52",
            *extra,
            ok=ok,
        )

    def test_exact_multiset_and_relative_root_pass(self):
        self.verify()

    def test_initial_generation_requires_valid_source_revision(self):
        output = self.root / "invalid-source.json"
        output.write_text("sentinel", encoding="utf-8")
        self.execute(GENERATE, "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(output), "--repo-root", ".", ok=False)
        self.assertEqual("sentinel", output.read_text(encoding="utf-8"))
        self.execute(GENERATE, "--source-revision", "5a7be32", "--jdk", "", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(output), "--repo-root", ".", ok=False)
        self.assertEqual("sentinel", output.read_text(encoding="utf-8"))
        self.execute(GENERATE, "--source-revision", "not-a-sha", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(output), "--repo-root", ".", ok=False)
        self.assertEqual("sentinel", output.read_text(encoding="utf-8"))

    def test_missing_report_fails(self):
        self.debug.unlink()
        self.verify(ok=False)

    def test_missing_location_fails(self):
        self.debug.write_text(xml(str(self.main), location=False), encoding="utf-8")
        self.verify(ok=False)

    def test_error_lint_fails(self):
        self.debug.write_text(xml(str(self.main), severity="Error"), encoding="utf-8")
        self.verify(ok=False)

    def test_removed_warning_without_inventory_update_fails(self):
        self.debug.write_text("<?xml version=\"1.0\"?><issues/>", encoding="utf-8")
        self.verify(ok=False)

    def test_duplicate_warning_fails(self):
        self.debug.write_text(xml(str(self.main))[:-9] + xml(str(self.main)).split("<issue", 1)[1].replace("</issues>", "") + "</issues>", encoding="utf-8")
        self.verify(ok=False)

    def test_same_rule_replacement_fails(self):
        other = self.root / "app" / "Other.kt"
        other.write_text("", encoding="utf-8")
        self.debug.write_text(xml(str(other)), encoding="utf-8")
        self.verify(ok=False)

    def test_reintroduction_after_inventory_removal_fails(self):
        data = self.data()
        data["variants"]["debug"]["warnings"] = []
        self.save(data)
        self.debug.write_text("<?xml version=\"1.0\"?><issues/>", encoding="utf-8")
        self.verify()
        self.debug.write_text(xml(str(self.main)), encoding="utf-8")
        self.verify(ok=False)

    def test_volatile_suggestions_are_canonical(self):
        for rule in ("GradleDependency", "AndroidGradlePluginVersion"):
            self.assertEqual(canonical_message(rule, "A newer version of x:y than 1.0 is available: 2.0."), canonical_message(rule, "A newer version of x:y than 1.0 is available: 3.0."))

    def test_external_advisory_drift_is_visible_but_not_blocking(self):
        for report in (self.debug, self.release):
            report.write_text(xml(str(self.main), "A newer version of x:y than 1.0 is available: 2.0.", "GradleDependency"), encoding="utf-8")
        self.execute(GENERATE, "--source-revision", "5a7be32", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(self.baseline), "--repo-root", ".")
        self.debug.write_text(xml(str(self.main), "A newer version of a:b than 1.0 is available: 2.0.", "GradleDependency"), encoding="utf-8")
        result = self.verify()
        self.assertIn("External lint advisory (debug)", result.stdout)
        self.verify("--fail-on-advisory-drift", ok=False)

    def test_external_suggested_version_change_is_not_drift(self):
        for report in (self.debug, self.release):
            report.write_text(xml(str(self.main), "A newer version of x:y than 1.0 is available: 2.0.", "GradleDependency"), encoding="utf-8")
        self.execute(GENERATE, "--source-revision", "5a7be32", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(self.baseline), "--repo-root", ".")
        self.debug.write_text(xml(str(self.main), "A newer version of x:y than 1.0 is available: 3.0.", "GradleDependency"), encoding="utf-8")
        self.verify("--fail-on-advisory-drift")

    def test_unknown_rule_remains_blocking(self):
        self.debug.write_text(xml(str(self.main), "Different deterministic warning", "UnknownRule"), encoding="utf-8")
        self.verify(ok=False)

    def test_windows_and_unix_inside_roots_have_same_fingerprint(self):
        win, unix = self.root / "win.xml", self.root / "unix.xml"
        win.write_text(xml(r"C:\workspace\repo\app\Main.kt"), encoding="utf-8")
        unix.write_text(xml("/workspace/repo/app/Main.kt"), encoding="utf-8")
        self.assertEqual(parse_report(win, "debug", r"C:\workspace\repo")[0]["fingerprint"], parse_report(unix, "debug", "/workspace/repo")[0]["fingerprint"])

    def test_outside_root_and_dtd_fail(self):
        self.debug.write_text(xml(str(Path(self.temp.name) / "outside.kt")), encoding="utf-8")
        with self.assertRaises(ValueError):
            parse_report(self.debug, "debug", str(self.root))
        self.debug.write_text("<!DOCTYPE issues [<!ENTITY x 'x'>]><issues/>", encoding="utf-8")
        with self.assertRaises(ValueError):
            parse_report(self.debug, "debug", str(self.root))

    def test_invalid_schema_and_exception_fail(self):
        original = self.data()
        data = copy.deepcopy(original)
        data["variants"]["debug"]["warnings"][0].pop("classification")
        self.save(data)
        self.verify(ok=False)
        data = copy.deepcopy(original)
        exception = copy.deepcopy(data["variants"]["debug"]["warnings"][0])
        exception["classification"] = "temporary_exception"
        exception["expiresOn"] = "2000-01-01"
        data["exceptions"] = [exception]
        self.save(data)
        self.verify(ok=False)
        data = copy.deepcopy(original)
        exception = copy.deepcopy(data["variants"]["debug"]["warnings"][0])
        exception["classification"] = "temporary_exception"
        exception["removalCondition"] = " "
        data["exceptions"] = [exception]
        self.save(data)
        self.verify(ok=False)

    def test_exception_shape_and_direction_are_enforced(self):
        original = self.data()
        data = copy.deepcopy(original)
        data["variants"]["debug"]["warnings"][0]["classification"] = "temporary_exception"
        self.save(data)
        self.verify(ok=False)
        data = copy.deepcopy(original)
        exception = copy.deepcopy(data["variants"]["debug"]["warnings"][0])
        exception["removalCondition"] = "Remove in #11"
        data["exceptions"] = [exception]
        self.save(data)
        self.verify(ok=False)
        data = copy.deepcopy(original)
        expected = data["variants"]["debug"]["warnings"].pop(0)
        expected["classification"] = "temporary_exception"
        expected["removalCondition"] = "Remove in #11"
        data["exceptions"] = [expected]
        self.save(data)
        self.verify()
        data = copy.deepcopy(original)
        data["variants"]["debug"]["warnings"][0]["unexpected"] = "x"
        self.save(data)
        self.verify(ok=False)

    def test_remote_ci_refresh_is_atomic_and_verifies_strictly(self):
        before = self.data()
        self.write_remote_reports()
        refreshed = self.root / "refreshed.json"
        self.refresh(refreshed)
        after = self.data(refreshed)
        self.assertEqual(before["generatedFrom"], after["generatedFrom"])
        self.assertEqual(before["variants"], after["variants"])
        self.assertEqual(before["exceptions"], after["exceptions"])
        for variant in ("debug", "release"):
            for warning in after["externalAdvisories"][variant]["warnings"]:
                self.assertEqual("#52", warning["linkedIssueOrPr"])
        self.execute(VERIFY, "--baseline", str(refreshed), "--debug-report", str(self.debug), "--release-report", str(self.release), "--repo-root", ".", "--report-root", REMOTE_ROOT, "--fail-on-advisory-drift")

    def test_refresh_rejects_invalid_issue_and_does_not_write(self):
        self.write_remote_reports()
        output = self.root / "untouched.json"
        output.write_text("sentinel", encoding="utf-8")
        self.refresh(output, "--linked-issue-or-pr", "52", ok=False)
        self.assertEqual("sentinel", output.read_text(encoding="utf-8"))

    def test_refresh_deterministic_drift_does_not_write(self):
        self.write_remote_reports()
        (self.root / "app" / "Other.kt").write_text("fun other() = Unit\n", encoding="utf-8")
        self.debug.write_text(report_with_deterministic_and_advisory(f"{REMOTE_ROOT}/app/Other.kt"), encoding="utf-8")
        output = self.root / "untouched.json"
        output.write_text("sentinel", encoding="utf-8")
        self.refresh(output, ok=False)
        self.assertEqual("sentinel", output.read_text(encoding="utf-8"))

    def test_invalid_report_root_rejected_even_without_locations(self):
        for report in (self.debug, self.release):
            report.write_text('<?xml version="1.0"?><issues/>', encoding="utf-8")
        output = self.root / "rejected-empty.xml.json"
        output.write_text("sentinel", encoding="utf-8")
        self.execute(GENERATE, "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(output), "--repo-root", ".", "--report-root", "/", "--refresh-external-from", str(self.baseline), "--linked-issue-or-pr", "#52", ok=False)
        self.assertEqual("sentinel", output.read_text(encoding="utf-8"))
        self.execute(VERIFY, "--baseline", str(self.baseline), "--debug-report", str(self.debug), "--release-report", str(self.release), "--repo-root", ".", "--report-root", "/", ok=False)

    def test_remote_root_rejections_fail_both_clis(self):
        cases = [
            ("/", self.remote_path()),
            ("relative/root", self.remote_path()),
            ("/home/runner/../work", self.remote_path()),
            (f"{REMOTE_ROOT}/", self.remote_path()),
            (REMOTE_ROOT, REMOTE_ROOT),
            (REMOTE_ROOT, f"{REMOTE_ROOT}-evil/app/Main.kt"),
            (REMOTE_ROOT, f"{REMOTE_ROOT}/app//Main.kt"),
            (REMOTE_ROOT, f"{REMOTE_ROOT}/app/Main.kt/"),
            (REMOTE_ROOT, self.remote_path().upper()),
            (REMOTE_ROOT, "app/Main.kt"),
            (REMOTE_ROOT, "//server/share/Main.kt"),
            (REMOTE_ROOT, "/??/C:/Main.kt"),
            (REMOTE_ROOT, "/./app/Main.kt"),
        ]
        for root, raw_path in cases:
            with self.subTest(root=root, raw_path=raw_path):
                for report in (self.debug, self.release):
                    report.write_text(report_with_deterministic_and_advisory(raw_path), encoding="utf-8")
                output = self.root / "rejected.json"
                output.write_text("sentinel", encoding="utf-8")
                self.execute(GENERATE, "--source-revision", "ignored", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(output), "--repo-root", ".", "--report-root", root, "--refresh-external-from", str(self.baseline), "--linked-issue-or-pr", "#52", ok=False)
                self.assertEqual("sentinel", output.read_text(encoding="utf-8"))
                self.execute(VERIFY, "--baseline", str(self.baseline), "--debug-report", str(self.debug), "--release-report", str(self.release), "--repo-root", ".", "--report-root", root, ok=False)


if __name__ == "__main__":
    unittest.main()