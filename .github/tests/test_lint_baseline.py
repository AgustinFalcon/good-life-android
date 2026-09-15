#!/usr/bin/env python3
from __future__ import annotations
import copy, json, subprocess, sys, tempfile, unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCRIPTS = ROOT / ".github" / "scripts"
sys.path.insert(0, str(SCRIPTS))
from lint_baseline_common import canonical_message, parse_report
PYTHON, GENERATE, VERIFY = sys.executable, SCRIPTS / "generate_lint_baseline.py", SCRIPTS / "verify_lint_baseline.py"

def xml(path: str, message: str = "Warning message", rule: str = "SampleRule", severity: str = "Warning", location: bool = True) -> str:
    location_xml = f'<location file="{path}"/>' if location else ""
    return f'<?xml version="1.0"?><issues><issue id="{rule}" severity="{severity}" message="{message}">{location_xml}</issue></issues>'

class LintBaselineTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name) / "repo"
        (self.root / "app").mkdir(parents=True)
        self.main = self.root / "app" / "Main.kt"
        self.main.write_text("fun main() = Unit\n", encoding="utf-8")
        self.debug, self.release, self.baseline = self.root / "debug.xml", self.root / "release.xml", self.root / "baseline.json"
        for report in (self.debug, self.release): report.write_text(xml(str(self.main)), encoding="utf-8")
        self.execute(GENERATE, "--source-revision", "5a7be32", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(self.baseline), "--repo-root", ".")
    def tearDown(self): self.temp.cleanup()
    def execute(self, script, *args, ok=True):
        result = subprocess.run([PYTHON, str(script), *args], text=True, capture_output=True, cwd=self.root)
        if ok: self.assertEqual(0, result.returncode, result.stderr + result.stdout)
        else: self.assertNotEqual(0, result.returncode)
        return result
    def verify(self, *extra, ok=True): return self.execute(VERIFY, "--baseline", str(self.baseline), "--debug-report", str(self.debug), "--release-report", str(self.release), "--repo-root", ".", *extra, ok=ok)
    def data(self): return json.loads(self.baseline.read_text(encoding="utf-8"))
    def save(self, payload): self.baseline.write_text(json.dumps(payload), encoding="utf-8")

    def test_exact_multiset_and_relative_root_pass(self): self.verify()
    def test_missing_report_fails(self): self.debug.unlink(); self.verify(ok=False)
    def test_missing_location_fails(self): self.debug.write_text(xml(str(self.main), location=False), encoding="utf-8"); self.verify(ok=False)
    def test_error_lint_fails(self): self.debug.write_text(xml(str(self.main), severity="Error"), encoding="utf-8"); self.verify(ok=False)
    def test_removed_warning_without_inventory_update_fails(self): self.debug.write_text("<?xml version=\"1.0\"?><issues/>", encoding="utf-8"); self.verify(ok=False)
    def test_duplicate_warning_fails(self):
        self.debug.write_text(xml(str(self.main))[:-9] + xml(str(self.main)).split("<issue", 1)[1].replace("</issues>", "") + "</issues>", encoding="utf-8"); self.verify(ok=False)
    def test_same_rule_replacement_fails(self):
        other = self.root / "app" / "Other.kt"; other.write_text("", encoding="utf-8"); self.debug.write_text(xml(str(other)), encoding="utf-8"); self.verify(ok=False)
    def test_reintroduction_after_inventory_removal_fails(self):
        data = self.data(); data["variants"]["debug"]["warnings"] = []; self.save(data); self.debug.write_text("<?xml version=\"1.0\"?><issues/>", encoding="utf-8"); self.verify(); self.debug.write_text(xml(str(self.main)), encoding="utf-8"); self.verify(ok=False)
    def test_volatile_suggestions_are_canonical(self):
        for rule in ("GradleDependency", "AndroidGradlePluginVersion"):
            self.assertEqual(canonical_message(rule, "A newer version of x:y than 1.0 is available: 2.0."), canonical_message(rule, "A newer version of x:y than 1.0 is available: 3.0."))
    def test_external_advisory_drift_is_visible_but_not_blocking(self):
        for report in (self.debug, self.release): report.write_text(xml(str(self.main), "A newer version of x:y than 1.0 is available: 2.0.", "GradleDependency"), encoding="utf-8")
        self.execute(GENERATE, "--source-revision", "5a7be32", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(self.baseline), "--repo-root", ".")
        self.debug.write_text(xml(str(self.main), "A newer version of a:b than 1.0 is available: 2.0.", "GradleDependency"), encoding="utf-8")
        result = self.verify()
        self.assertIn("External lint advisory (debug)", result.stdout)
        self.verify("--fail-on-advisory-drift", ok=False)
    def test_external_suggested_version_change_is_not_drift(self):
        for report in (self.debug, self.release): report.write_text(xml(str(self.main), "A newer version of x:y than 1.0 is available: 2.0.", "GradleDependency"), encoding="utf-8")
        self.execute(GENERATE, "--source-revision", "5a7be32", "--debug-report", str(self.debug), "--release-report", str(self.release), "--output", str(self.baseline), "--repo-root", ".")
        self.debug.write_text(xml(str(self.main), "A newer version of x:y than 1.0 is available: 3.0.", "GradleDependency"), encoding="utf-8")
        self.verify("--fail-on-advisory-drift")
    def test_unknown_rule_remains_blocking(self):
        self.debug.write_text(xml(str(self.main), "Different deterministic warning", "UnknownRule"), encoding="utf-8")
        self.verify(ok=False)
    def test_windows_and_unix_inside_roots_have_same_fingerprint(self):
        win, unix = self.root / "win.xml", self.root / "unix.xml"; win.write_text(xml(r"C:\workspace\repo\app\Main.kt"), encoding="utf-8"); unix.write_text(xml("/workspace/repo/app/Main.kt"), encoding="utf-8")
        self.assertEqual(parse_report(win, "debug", r"C:\workspace\repo")[0]["fingerprint"], parse_report(unix, "debug", "/workspace/repo")[0]["fingerprint"])
    def test_outside_root_and_dtd_fail(self):
        self.debug.write_text(xml(str(Path(self.temp.name) / "outside.kt")), encoding="utf-8")
        with self.assertRaises(ValueError): parse_report(self.debug, "debug", str(self.root))
        self.debug.write_text("<!DOCTYPE issues [<!ENTITY x 'x'>]><issues/>", encoding="utf-8")
        with self.assertRaises(ValueError): parse_report(self.debug, "debug", str(self.root))
    def test_invalid_schema_and_exception_fail(self):
        original = self.data()
        data = copy.deepcopy(original); data["variants"]["debug"]["warnings"][0].pop("classification"); self.save(data); self.verify(ok=False)
        data = copy.deepcopy(original); exception = copy.deepcopy(data["variants"]["debug"]["warnings"][0]); exception["classification"] = "temporary_exception"; exception["expiresOn"] = "2000-01-01"; data["exceptions"] = [exception]; self.save(data); self.verify(ok=False)
        data = copy.deepcopy(original); exception = copy.deepcopy(data["variants"]["debug"]["warnings"][0]); exception["classification"] = "temporary_exception"; exception["removalCondition"] = " "; data["exceptions"] = [exception]; self.save(data); self.verify(ok=False)
    def test_exception_shape_and_direction_are_enforced(self):
        original = self.data()
        data = copy.deepcopy(original); data["variants"]["debug"]["warnings"][0]["classification"] = "temporary_exception"; self.save(data); self.verify(ok=False)
        data = copy.deepcopy(original); exception = copy.deepcopy(data["variants"]["debug"]["warnings"][0]); exception["removalCondition"] = "Remove in #11"; data["exceptions"] = [exception]; self.save(data); self.verify(ok=False)
        data = copy.deepcopy(original); expected = data["variants"]["debug"]["warnings"].pop(0); expected["classification"] = "temporary_exception"; expected["removalCondition"] = "Remove in #11"; data["exceptions"] = [expected]; self.save(data); self.verify()
        data = copy.deepcopy(original); data["variants"]["debug"]["warnings"][0]["unexpected"] = "x"; self.save(data); self.verify(ok=False)

if __name__ == "__main__": unittest.main()
