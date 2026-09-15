#!/usr/bin/env python3
"""Fail-closed verifier for deterministic lint debt plus observable external advisories."""
from __future__ import annotations
import argparse, json, os, re
from collections import Counter
from datetime import date
from pathlib import Path
from lint_baseline_common import NORMALIZATION_VERSION, VOLATILE_RULES, parse_report, fingerprint, fail

parser = argparse.ArgumentParser()
parser.add_argument("--baseline", type=Path, required=True)
parser.add_argument("--debug-report", type=Path, required=True)
parser.add_argument("--release-report", type=Path, required=True)
parser.add_argument("--repo-root", default=str(Path.cwd()))
parser.add_argument("--advisory-delta-out", type=Path)
parser.add_argument("--fail-on-advisory-drift", action="store_true")
args = parser.parse_args()
try: data = json.loads(args.baseline.read_text(encoding="utf-8"))
except (OSError, json.JSONDecodeError) as exc: raise SystemExit(f"Invalid lint baseline: {exc}")
TOP_KEYS = {"schemaVersion", "normalizationVersion", "generatedFrom", "variants", "externalAdvisories", "exceptions"}
if not isinstance(data, dict) or set(data) != TOP_KEYS or data["schemaVersion"] != 2 or data["normalizationVersion"] != NORMALIZATION_VERSION: raise SystemExit("Unsupported lint baseline schema")
generated = data["generatedFrom"]
if not isinstance(generated, dict) or set(generated) != {"sourceRevision", "commands", "toolchain", "authority"}: raise SystemExit("Lint baseline generatedFrom schema is invalid")
if not re.fullmatch(r"[0-9a-f]{7,64}", str(generated["sourceRevision"])) or generated["commands"] != [":app:lintDebug", ":app:lintRelease"] or not isinstance(generated["authority"], str) or not generated["authority"].strip(): raise SystemExit("Lint baseline source metadata is invalid")
toolchain = generated["toolchain"]
if not isinstance(toolchain, dict) or set(toolchain) != {"gradleWrapper", "agp", "jdk", "sdk"} or any(not isinstance(value, str) or not value.strip() for value in toolchain.values()): raise SystemExit("Lint baseline toolchain metadata is invalid")
if not isinstance(data["exceptions"], list): raise SystemExit("Lint baseline exceptions schema is invalid")
BASE_KEYS = {"fingerprint", "variant", "ruleId", "relativePath", "message", "classification", "owner", "justification", "linkedIssueOrPr", "createdOn"}
DETERMINISTIC_CLASSES = {"fix_now", "split_upgrade", "investigate"}

def validate_record(entry, variant, domain):
    if not isinstance(entry, dict) or not BASE_KEYS.issubset(entry): fail("Baseline entry has missing required fields")
    if domain == "exception":
        extra = set(entry) - BASE_KEYS
        if entry.get("classification") != "temporary_exception" or extra not in ({"expiresOn"}, {"removalCondition"}): fail("Exception shape is invalid")
    elif set(entry) != BASE_KEYS: fail("Baseline record has unexpected fields")
    if any(not isinstance(entry.get(key), str) or not entry[key].strip() for key in BASE_KEYS): fail("Baseline entry has blank required fields")
    if entry["variant"] != variant or not re.fullmatch(r"[0-9a-f]{64}", entry["fingerprint"]): fail("Baseline entry variant or fingerprint is invalid")
    if entry["fingerprint"] != fingerprint(variant, entry["ruleId"], entry["relativePath"], entry["message"]): fail("Baseline fingerprint does not match canonical fields")
    if entry["relativePath"].startswith("/") or ".." in entry["relativePath"].split("/") or re.match(r"^[A-Za-z]:", entry["relativePath"]): fail("Baseline path is unsafe")
    try: date.fromisoformat(entry["createdOn"])
    except ValueError: fail("Baseline createdOn is invalid")
    if domain == "deterministic":
        if entry["ruleId"] in VOLATILE_RULES or entry["classification"] not in DETERMINISTIC_CLASSES: fail("Deterministic baseline record is invalid")
    elif domain == "advisory":
        if entry["ruleId"] not in VOLATILE_RULES or entry["classification"] != "split_upgrade": fail("External advisory record is invalid")
    else:
        expiry, condition = entry.get("expiresOn"), entry.get("removalCondition")
        if expiry:
            try:
                if date.fromisoformat(expiry) < date.today(): fail("Exception is expired")
            except ValueError: fail("Exception expiry is invalid")
        elif not isinstance(condition, str) or not condition.strip(): fail("Exception removalCondition is invalid")

def records(domain, variant):
    container = data["variants"] if domain == "deterministic" else data["externalAdvisories"]
    if not isinstance(container, dict) or set(container) != {"debug", "release"}: fail(f"{domain} variants schema is invalid")
    item = container.get(variant)
    if not isinstance(item, dict) or set(item) != {"warnings"} or not isinstance(item["warnings"], list): fail(f"{domain} warnings schema is invalid")
    result = list(item["warnings"])
    for entry in result:
        validate_record(entry, variant, domain)
    if domain != "deterministic":
        return result
    for entry in data["exceptions"]:
        if not isinstance(entry, dict) or entry.get("variant") not in {"debug", "release"}:
            fail("Exception variant is invalid")
        if entry["variant"] == variant:
            validate_record(entry, variant, "exception")
            result.append(entry)
    return result

def count(items): return Counter(item["fingerprint"] for item in items)
def describe(items, fingerprints):
    lookup = {item["fingerprint"]: f"{item['ruleId']}@{item['relativePath']}#{item['fingerprint'][:12]}" for item in items}
    return [lookup.get(value, value[:12]) for value in fingerprints[:5]]

try:
    root, deltas = str(Path(args.repo_root).resolve()), {}
    for variant, report in (("debug", args.debug_report), ("release", args.release_report)):
        observed_all = parse_report(report, variant, root)
        observed = [item for item in observed_all if item["ruleId"] not in VOLATILE_RULES]
        observed_adv = [item for item in observed_all if item["ruleId"] in VOLATILE_RULES]
        expected, expected_adv = records("deterministic", variant), records("advisory", variant)
        extra, stale = list((count(observed) - count(expected)).elements()), list((count(expected) - count(observed)).elements())
        if extra or stale: fail(f"Lint baseline mismatch for {variant}: new={describe(observed, extra)} stale_or_count_changed={describe(expected, stale)}")
        added, removed = list((count(observed_adv) - count(expected_adv)).elements()), list((count(expected_adv) - count(observed_adv)).elements())
        deltas[variant] = {"new": describe(observed_adv, added), "resolvedOrChanged": describe(expected_adv, removed)}
except ValueError as exc: raise SystemExit(f"Lint baseline verification failed: {exc}")
if args.advisory_delta_out:
    args.advisory_delta_out.parent.mkdir(parents=True, exist_ok=True)
    args.advisory_delta_out.write_text(json.dumps(deltas, indent=2) + "\n", encoding="utf-8")
changed = {variant: delta for variant, delta in deltas.items() if delta["new"] or delta["resolvedOrChanged"]}
for variant, delta in changed.items():
    message = f"External lint advisory ({variant}): new={delta['new']} resolved_or_changed={delta['resolvedOrChanged']}"
    print(f"::warning::{message}")
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as handle: handle.write(f"## {message}\nTrack dependency/AGP remediation in an isolated issue/PR.\n")
if args.fail_on_advisory_drift and changed: raise SystemExit("External lint advisory drift detected by scheduled monitor")
print("Deterministic lint baseline verification passed for debug and release.")
