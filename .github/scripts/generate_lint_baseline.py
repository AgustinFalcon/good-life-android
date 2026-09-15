#!/usr/bin/env python3
"""Generate a reproducible deterministic lint baseline and external-advisory snapshot."""
from __future__ import annotations
import argparse, json
from datetime import date
from pathlib import Path
from lint_baseline_common import NORMALIZATION_VERSION, VOLATILE_RULES, parse_report

parser = argparse.ArgumentParser()
parser.add_argument("--source-revision", required=True)
parser.add_argument("--debug-report", type=Path, required=True)
parser.add_argument("--release-report", type=Path, required=True)
parser.add_argument("--output", type=Path, required=True)
parser.add_argument("--repo-root", default=str(Path.cwd()))
parser.add_argument("--gradle-wrapper", default="8.12.1")
parser.add_argument("--agp", default="8.10.0")
parser.add_argument("--jdk", default="17")
parser.add_argument("--sdk", default="35")
args = parser.parse_args()
root = str(Path(args.repo_root).resolve())

def records(report, variant, external):
    rows = [row for row in parse_report(report, variant, root) if (row["ruleId"] in VOLATILE_RULES) == external]
    classification = "split_upgrade" if external else "investigate"
    return [{**row, "classification": classification, "owner": "android-maintainers", "justification": "External dependency advisory snapshot." if external else "Pre-existing deterministic lint debt measured for issue #10.", "linkedIssueOrPr": "#10", "createdOn": str(date.today())} for row in rows]

metadata = {"sourceRevision": args.source_revision, "commands": [":app:lintDebug", ":app:lintRelease"], "toolchain": {"gradleWrapper": args.gradle_wrapper, "agp": args.agp, "jdk": args.jdk, "sdk": args.sdk}, "authority": "CI validates PR HEAD; the evaluated SHA is not versioned here."}
payload = {"schemaVersion": 2, "normalizationVersion": NORMALIZATION_VERSION, "generatedFrom": metadata,
  "variants": {"debug": {"warnings": records(args.debug_report, "debug", False)}, "release": {"warnings": records(args.release_report, "release", False)}},
  "externalAdvisories": {"debug": {"warnings": records(args.debug_report, "debug", True)}, "release": {"warnings": records(args.release_report, "release", True)}}, "exceptions": []}
args.output.parent.mkdir(parents=True, exist_ok=True)
args.output.write_text(json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
