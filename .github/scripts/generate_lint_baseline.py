#!/usr/bin/env python3
"""Generate a reproducible lint baseline or atomically refresh only external advisories."""
from __future__ import annotations

import argparse
import copy
import json
import os
import re
import tempfile
from datetime import date
from pathlib import Path

from lint_baseline_common import NORMALIZATION_VERSION, VOLATILE_RULES, baseline_records, multiset, parse_report, validate_baseline

parser = argparse.ArgumentParser()
parser.add_argument("--source-revision")
parser.add_argument("--debug-report", type=Path, required=True)
parser.add_argument("--release-report", type=Path, required=True)
parser.add_argument("--output", type=Path, required=True)
parser.add_argument("--repo-root", default=str(Path.cwd()))
parser.add_argument("--report-root")
parser.add_argument("--refresh-external-from", type=Path)
parser.add_argument("--linked-issue-or-pr")
parser.add_argument("--gradle-wrapper", default="8.12.1")
parser.add_argument("--agp", default="8.10.0")
parser.add_argument("--jdk", default="17")
parser.add_argument("--sdk", default="35")
args = parser.parse_args()

if args.refresh_external_from and not re.fullmatch(r"#[1-9][0-9]*", args.linked_issue_or_pr or ""):
    raise SystemExit("--refresh-external-from requires --linked-issue-or-pr in #N form")
if not args.refresh_external_from and not re.fullmatch(r"[0-9a-f]{7,64}", args.source_revision or ""):
    raise SystemExit("Initial generation requires --source-revision as a commit SHA")
root = str(Path(args.repo_root).resolve())


def observed(report: Path, variant: str, external: bool) -> list[dict]:
    return [row for row in parse_report(report, variant, root, args.report_root) if (row["ruleId"] in VOLATILE_RULES) == external]


def records(report: Path, variant: str, external: bool, linked_issue_or_pr: str) -> list[dict]:
    classification = "split_upgrade" if external else "investigate"
    justification = "External dependency advisory snapshot." if external else "Pre-existing deterministic lint debt measured for issue #10."
    return [{**row, "classification": classification, "owner": "android-maintainers", "justification": justification, "linkedIssueOrPr": linked_issue_or_pr, "createdOn": str(date.today())} for row in observed(report, variant, external)]


def fail_deterministic_drift(baseline: dict) -> None:
    for variant, report in (("debug", args.debug_report), ("release", args.release_report)):
        current = observed(report, variant, False)
        expected = baseline_records(baseline, "deterministic", variant)
        if multiset(current) != multiset(expected):
            raise SystemExit(f"Deterministic lint drift prevents external advisory refresh for {variant}")


def write_atomically(payload: dict) -> None:
    args.output.parent.mkdir(parents=True, exist_ok=True)
    encoded = (json.dumps(payload, ensure_ascii=False, indent=2) + "\n").encode("utf-8")
    temporary_path = None
    try:
        with tempfile.NamedTemporaryFile(mode="wb", prefix=".lint-baseline-", suffix=".tmp", dir=args.output.parent, delete=False) as temporary:
            temporary.write(encoded)
            temporary.flush()
            os.fsync(temporary.fileno())
            temporary_path = Path(temporary.name)
        os.replace(temporary_path, args.output)
    finally:
        if temporary_path and temporary_path.exists():
            temporary_path.unlink()


if args.refresh_external_from:
    try:
        original = validate_baseline(json.loads(args.refresh_external_from.read_text(encoding="utf-8")))
    except (OSError, json.JSONDecodeError, ValueError) as exc:
        raise SystemExit(f"Invalid refresh baseline: {exc}")
    fail_deterministic_drift(original)
    payload = copy.deepcopy(original)
    payload["externalAdvisories"] = {
        "debug": {"warnings": records(args.debug_report, "debug", True, args.linked_issue_or_pr)},
        "release": {"warnings": records(args.release_report, "release", True, args.linked_issue_or_pr)},
    }
else:
    metadata = {
        "sourceRevision": args.source_revision,
        "commands": [":app:lintDebug", ":app:lintRelease"],
        "toolchain": {"gradleWrapper": args.gradle_wrapper, "agp": args.agp, "jdk": args.jdk, "sdk": args.sdk},
        "authority": "CI validates PR HEAD; the evaluated SHA is not versioned here.",
    }
    payload = {
        "schemaVersion": 2,
        "normalizationVersion": NORMALIZATION_VERSION,
        "generatedFrom": metadata,
        "variants": {
            "debug": {"warnings": records(args.debug_report, "debug", False, "#10")},
            "release": {"warnings": records(args.release_report, "release", False, "#10")},
        },
        "externalAdvisories": {
            "debug": {"warnings": records(args.debug_report, "debug", True, "#10")},
            "release": {"warnings": records(args.release_report, "release", True, "#10")},
        },
        "exceptions": [],
    }
try:
    validate_baseline(payload)
except ValueError as exc:
    raise SystemExit(f"Generated lint baseline is invalid: {exc}")
write_atomically(payload)
