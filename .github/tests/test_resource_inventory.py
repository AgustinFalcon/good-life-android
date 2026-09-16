#!/usr/bin/env python3
import copy, json, subprocess, sys, tempfile, unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
SCRIPT=ROOT/'.github/scripts/resource_inventory.py'
BASELINE=ROOT/'config/lint-baseline.json'
MARKDOWN=ROOT/'sdd/wip/20260915-goodlife-unused-resources/4-implementation/resource-inventory.md'
PYTHON=sys.executable
class ResourceInventoryTest(unittest.TestCase):
    def run_check(self, payload, ok):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); baseline=root/'baseline.json'; output=root/'inventory.json'
            baseline.write_text(json.dumps(payload),encoding='utf-8')
            if ok:
                result=subprocess.run([PYTHON,str(SCRIPT),'--baseline',str(baseline),'--output-json',str(output),'--markdown',str(MARKDOWN)],capture_output=True,text=True)
            else:
                result=subprocess.run([PYTHON,str(SCRIPT),'--baseline',str(baseline),'--output-json',str(output),'--markdown',str(MARKDOWN)],capture_output=True,text=True)
            self.assertEqual(result.returncode,0 if ok else 1,result.stdout+result.stderr)
    def base(self): return json.loads(BASELINE.read_text(encoding='utf-8'))
    def test_real_schema_generates_15_symbols(self):
        with tempfile.TemporaryDirectory() as d:
            output=Path(d)/'inventory.json'
            result=subprocess.run([PYTHON,str(SCRIPT),'--baseline',str(BASELINE),'--output-json',str(output),'--markdown',str(MARKDOWN)],capture_output=True,text=True)
            self.assertEqual(result.returncode,0,result.stdout+result.stderr)
            self.assertEqual(len(json.loads(output.read_text())['symbols']),15)
    def test_missing_cross_variant_symbol_fails(self):
        p=self.base(); p['variants']['release']['warnings']=[x for x in p['variants']['release']['warnings'] if 'daily_item_type_meal' not in x.get('message','')]; self.run_check(p,False)
    def test_duplicate_semantic_identity_fails(self):
        p=self.base(); row=next(x for x in p['variants']['debug']['warnings'] if x['ruleId']=='UnusedResources'); p['variants']['debug']['warnings'].append(copy.deepcopy(row)); self.run_check(p,False)
    def test_unknown_message_fails(self):
        p=self.base(); row=next(x for x in p['variants']['debug']['warnings'] if x['ruleId']=='UnusedResources'); row['message']='The resource R.string.changed appears unused'; self.run_check(p,False)
    def test_empty_required_field_fails(self):
        p=self.base(); row=next(x for x in p["variants"]["debug"]["warnings"] if x["ruleId"]=="UnusedResources"); row["owner"]="   "; self.run_check(p,False)
    def test_unsupported_classification_fails(self):
        p=self.base(); row=next(x for x in p["variants"]["debug"]["warnings"] if x["ruleId"]=="UnusedResources"); row["classification"]="delete_now"; self.run_check(p,False)
    def test_unsafe_path_fails(self):
        p=self.base(); row=next(x for x in p["variants"]["debug"]["warnings"] if x["ruleId"]=="UnusedResources"); row["relativePath"]="../secrets.xml"; self.run_check(p,False)
    def test_symbol_path_family_mismatch_fails(self):
        p=self.base(); row=next(x for x in p["variants"]["debug"]["warnings"] if x["ruleId"]=="UnusedResources" and "R.drawable." in x["message"]); row["relativePath"]="app/src/main/res/layout/core_compat.xml"; self.run_check(p,False)
    def test_multiplicity_not_one_fails(self):
        p=self.base(); row=next(x for x in p["variants"]["debug"]["warnings"] if x["ruleId"]=="UnusedResources"); p["variants"]["release"]["warnings"].append(copy.deepcopy(row) | {"variant":"release"}); self.run_check(p,False)
    def test_stale_generated_json_fails(self):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); output=root/'inventory.json'; output.write_text('{}')
            result=subprocess.run([PYTHON,str(SCRIPT),'--baseline',str(BASELINE),'--output-json',str(output),'--markdown',str(MARKDOWN),'--check'],capture_output=True,text=True)
            self.assertEqual(result.returncode,1,result.stdout+result.stderr)
    def markdown_check(self, mutate):
        with tempfile.TemporaryDirectory() as d:
            root=Path(d); output=root/'inventory.json'; markdown=root/'inventory.md'
            markdown.write_text(mutate(MARKDOWN.read_text(encoding='utf-8')),encoding='utf-8')
            result=subprocess.run([PYTHON,str(SCRIPT),'--baseline',str(BASELINE),'--output-json',str(output),'--markdown',str(markdown)],capture_output=True,text=True)
            self.assertEqual(result.returncode,1,result.stdout+result.stderr)
    def test_markdown_blank_triage_field_fails(self):
        self.markdown_check(lambda text: text.replace('AndroidX notification compatibility family; ownership belongs to dependency/toolchain review.', '', 1))
    def test_markdown_definition_path_mismatch_fails(self):
        self.markdown_check(lambda text: text.replace('values/core_compat.xml; default', '`values/other.xml`; default', 1))
    def test_markdown_semantic_multiplicity_fails(self):
        self.markdown_check(lambda text: text.replace('UnusedResources; color/notification_template_icon_bg_compat; canonical unused; D×1/R×1', 'UnusedResources; color/notification_template_icon_bg_compat; canonical unused; D×2/R×1', 1))
    def test_markdown_invalid_classification_fails(self):
        self.markdown_check(lambda text: text.replace('| needs_owner |', '| remove |', 1))
    def test_markdown_missing_launcher_qualifier_fails(self):
        self.markdown_check(lambda text: text.replace('mipmap-xxxhdpi/ic_launcher.webp; ', '', 1))
if __name__=='__main__': unittest.main()
