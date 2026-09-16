#!/usr/bin/env python3
"""Fail-closed reducer for immutable #38 resource-remediation scopes."""
from __future__ import annotations

import argparse
import copy
import json
import re
from pathlib import Path

VARIANTS = ("debug", "release")
RESOURCE_MESSAGE = re.compile(r"^The resource `R\.([a-z0-9_]+)\.([a-z0-9_]+)` appears to be unused$")
PROTECTED_TOP_LEVEL = ("generatedFrom", "externalAdvisories", "exceptions")
SCOPES = {
    "legacy-colors": frozenset({
        "color/black", "color/white", "color/purple_200", "color/purple_500",
        "color/purple_700", "color/teal_200", "color/teal_700",
    }),
    "auth-strings": frozenset({
        "string/biometric_checkbox_label", "string/biometric_icon_description",
        "string/biometric_prompt_negative_button", "string/biometric_prompt_subtitle",
        "string/biometric_prompt_title", "string/login_button", "string/login_email_placeholder",
        "string/login_forgot_password", "string/login_password_placeholder", "string/login_remember_user",
        "string/login_subtitle", "string/login_success", "string/login_title",
    }),
}


def fail(message: str) -> None:
    raise SystemExit(f"baseline reducer: {message}")


def resource_key(entry: dict) -> str | None:
    if entry.get("ruleId") != "UnusedResources":
        return None
    match = RESOURCE_MESSAGE.fullmatch(str(entry.get("message", "")))
    return f"{match.group(1)}/{match.group(2)}" if match else None


def load_json(path: Path, label: str):
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"cannot read {label}: {exc}")


def load_baseline(path: Path) -> dict:
    value = load_json(path, "baseline")
    if not isinstance(value, dict) or value.get("schemaVersion") != 2:
        fail("expected schemaVersion 2 object")
    variants = value.get("variants")
    if not isinstance(variants, dict) or set(variants) != set(VARIANTS):
        fail("baseline variants must contain exactly debug and release")
    for variant in VARIANTS:
        container = variants[variant]
        if not isinstance(container, dict) or set(container) != {"warnings"} or not isinstance(container["warnings"], list):
            fail(f"{variant} warnings schema is invalid")
    return value


def load_manifest(path: Path) -> tuple[str, list[dict]]:
    value = load_json(path, "manifest")
    expected_fields = {"schemaVersion", "issue", "scope", "resources"}
    if not isinstance(value, dict) or value.get("schemaVersion") != 2 or value.get("issue") != "#38" or set(value) != expected_fields:
        fail("manifest schema is invalid")
    scope = value.get("scope")
    if not isinstance(scope, str) or scope not in SCOPES:
        fail(f"manifest scope must be one of: {', '.join(sorted(SCOPES))}")
    resources = value.get("resources")
    if not isinstance(resources, list) or not resources:
        fail("manifest resources must be a non-empty list")
    symbols = set()
    for item in resources:
        if not isinstance(item, dict) or set(item) != {"symbol", "debugFingerprint", "releaseFingerprint"}:
            fail("manifest resource shape is invalid")
        symbol = item["symbol"]
        if not isinstance(symbol, str) or not re.fullmatch(r"[a-z][a-z0-9_]*/[a-z][a-z0-9_]*", symbol):
            fail(f"invalid manifest symbol: {symbol!r}")
        if symbol in symbols:
            fail(f"duplicate manifest symbol: {symbol}")
        symbols.add(symbol)
        for variant in VARIANTS:
            fingerprint = item[f"{variant}Fingerprint"]
            if not isinstance(fingerprint, str) or not re.fullmatch(r"[0-9a-f]{64}", fingerprint):
                fail(f"invalid {variant} fingerprint for {symbol}")
    expected_symbols = SCOPES[scope]
    if symbols != expected_symbols:
        missing, extra = sorted(expected_symbols - symbols), sorted(symbols - expected_symbols)
        fail(f"#38 manifest symbols must match immutable {scope} scope; missing={missing}, extra={extra}")
    return scope, resources


def canonical(value) -> str:
    return json.dumps(value, indent=2, ensure_ascii=False) + "\n"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--baseline", type=Path, required=True)
    parser.add_argument("--manifest", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    before = load_baseline(args.baseline)
    scope, resources = load_manifest(args.manifest)
    after = copy.deepcopy(before)
    protected_before = {key: canonical(before.get(key)) for key in PROTECTED_TOP_LEVEL}
    for variant in VARIANTS:
        warnings = before["variants"][variant]["warnings"]
        by_symbol: dict[str, list[dict]] = {item["symbol"]: [] for item in resources}
        for entry in warnings:
            if not isinstance(entry, dict):
                fail(f"{variant} contains a non-object warning")
            key = resource_key(entry)
            if key in by_symbol:
                by_symbol[key].append(entry)
        targets = set()
        for item in resources:
            symbol = item["symbol"]
            entries = by_symbol[symbol]
            if len(entries) != 1:
                fail(f"{variant} must contain exactly one UnusedResources entry for {symbol}; found {len(entries)}")
            entry = entries[0]
            if entry.get("variant") != variant:
                fail(f"{variant} entry declares variant {entry.get('variant')!r}")
            actual = entry.get("fingerprint")
            expected = item[f"{variant}Fingerprint"]
            if actual != expected:
                fail(f"{variant} fingerprint mismatch for {symbol}: expected {expected}, found {actual}")
            targets.add(expected)
        after["variants"][variant]["warnings"] = [entry for entry in warnings if entry.get("fingerprint") not in targets]
        if len(after["variants"][variant]["warnings"]) != len(warnings) - len(resources):
            fail(f"{variant} removal count is not exactly {len(resources)} for scope {scope}")
    for key, expected in protected_before.items():
        if canonical(after.get(key)) != expected:
            fail(f"protected top-level section changed: {key}")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(canonical(after), encoding="utf-8")
    print(f"Removed {len(resources)} exact {scope} UnusedResources entries per variant from {args.output}")
    return 0


if __name__ == "__main__":
    main()
