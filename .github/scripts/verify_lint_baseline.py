#!/usr/bin/env python3
"""Fail-closed verifier for deterministic lint debt plus observable external advisories."""
from __future__ import annotations

import argparse
import json
import os
from pathlib import Path

from lint_baseline_common import VOLATILE_RULES, baseline_records, fail, multiset, parse_report, validate_baseline

parser = argparse.ArgumentParser()
parser.add_argument("--baseline", type=Path, required=True)
parser.add_argument("--debug-report", type=Path, required=True)
parser.add_argument("--release-report", type=Path, required=True)
parser.add_argument("--repo-root", default=str(Path.cwd()))
parser.add_argument("--report-root")
parser.add_argument("--advisory-delta-out", type=Path)
parser.add_argument("--fail-on-advisory-drift", action="store_true")
args = parser.parse_args()

try:
    data = validate_baseline(json.loads(args.baseline.read_text(encoding="utf-8")))
except (OSError, json.JSONDecodeError, ValueError) as exc:
    raise SystemExit(f"Invalid lint baseline: {exc}")


def describe(items: list[dict], fingerprints: list[str]) -> list[str]:
    lookup = {item["fingerprint"]: f"{item['ruleId']}@{item['relativePath']}#{item['fingerprint'][:12]}" for item in items}
    return [lookup.get(value, value[:12]) for value in fingerprints[:5]]


try:
    root, deltas = str(Path(args.repo_root).resolve()), {}
    for variant, report in (("debug", args.debug_report), ("release", args.release_report)):
        observed_all = parse_report(report, variant, root, args.report_root)
        observed = [item for item in observed_all if item["ruleId"] not in VOLATILE_RULES]
        observed_adv = [item for item in observed_all if item["ruleId"] in VOLATILE_RULES]
        expected = baseline_records(data, "deterministic", variant)
        expected_adv = baseline_records(data, "advisory", variant)
        extra = list((multiset(observed) - multiset(expected)).elements())
        stale = list((multiset(expected) - multiset(observed)).elements())
        if extra or stale:
            fail(f"Lint baseline mismatch for {variant}: new={describe(observed, extra)} stale_or_count_changed={describe(expected, stale)}")
        added = list((multiset(observed_adv) - multiset(expected_adv)).elements())
        removed = list((multiset(expected_adv) - multiset(observed_adv)).elements())
        deltas[variant] = {"new": describe(observed_adv, added), "resolvedOrChanged": describe(expected_adv, removed)}
except ValueError as exc:
    raise SystemExit(f"Lint baseline verification failed: {exc}")

if args.advisory_delta_out:
    args.advisory_delta_out.parent.mkdir(parents=True, exist_ok=True)
    args.advisory_delta_out.write_text(json.dumps(deltas, indent=2) + "\n", encoding="utf-8")
changed = {variant: delta for variant, delta in deltas.items() if delta["new"] or delta["resolvedOrChanged"]}
for variant, delta in changed.items():
    message = f"External lint advisory ({variant}): new={delta['new']} resolved_or_changed={delta['resolvedOrChanged']}"
    print(f"::warning::{message}")
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as handle:
            handle.write(f"## {message}\nTrack dependency/AGP remediation in an isolated issue/PR.\n")
if args.fail_on_advisory_drift and changed:
    raise SystemExit("External lint advisory drift detected by scheduled monitor")
print("Deterministic lint baseline verification passed for debug and release.")