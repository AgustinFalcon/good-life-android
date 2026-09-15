#!/usr/bin/env python3
"""Generate and fail-closed-check the unused-resource inventory from lint schema v2."""
from __future__ import annotations
import argparse, json, re
from collections import Counter
from pathlib import Path

MESSAGE_RE = re.compile(r"^The resource `R\.([a-z][a-z0-9_]*)\.([a-z][a-z0-9_]*)` appears to be unused$")
PATH_RE = re.compile(r"^app/src/main/res/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+\.(?:xml|png|webp|jpg|jpeg|gif|9\.png|avif)$")
RESOURCE_TYPES = {"array", "attr", "bool", "color", "dimen", "drawable", "fraction", "id", "integer", "layout", "menu", "mipmap", "plurals", "raw", "string", "style", "styleable", "transition", "xml"}
REQUIRED_FIELDS = {"fingerprint", "variant", "ruleId", "relativePath", "message", "classification", "owner", "justification", "linkedIssueOrPr", "createdOn"}
ALLOWED_CLASSIFICATIONS = {"investigate", "retain_compat", "retain_launcher", "needs_owner", "split_upgrade"}
SCHEMA_VERSION = 2

def fail(message: str) -> None:
    raise SystemExit(f"resource inventory: {message}")

def warnings(payload: dict, variant: str) -> list[dict]:
    if payload.get("schemaVersion") != SCHEMA_VERSION:
        fail("expected lint baseline schemaVersion 2")
    variants = payload.get("variants")
    if not isinstance(variants, dict) or variant not in variants:
        fail(f"missing variant {variant}")
    entries = variants[variant].get("warnings") if isinstance(variants[variant], dict) else None
    if not isinstance(entries, list):
        fail(f"invalid warnings container for {variant}")
    return entries

def validate_row(row: object, variant: str) -> tuple[str, str, str]:
    if not isinstance(row, dict):
        fail(f"{variant} contains a non-object warning")
    if set(row) != REQUIRED_FIELDS:
        fail(f"unexpected record shape in {variant}")
    for field in REQUIRED_FIELDS:
        if not isinstance(row[field], str) or not row[field].strip():
            fail(f"{variant} has an empty/non-string {field}")
    if row["variant"] != variant or row["ruleId"] != "UnusedResources":
        fail(f"wrong variant/rule on record in {variant}")
    if row["classification"] not in ALLOWED_CLASSIFICATIONS:
        fail(f"unsupported classification in {variant}: {row['classification']!r}")
    if not re.fullmatch(r"[0-9a-f]{64}", row["fingerprint"]):
        fail(f"invalid fingerprint in {variant}")
    if not re.fullmatch(r"#[0-9]+", row["linkedIssueOrPr"]):
        fail(f"invalid issue/PR reference in {variant}")
    if not re.fullmatch(r"20[0-9]{2}-[0-9]{2}-[0-9]{2}", row["createdOn"]):
        fail(f"invalid createdOn in {variant}")
    match = MESSAGE_RE.fullmatch(row["message"])
    if not match:
        fail(f"unknown UnusedResources message in {variant}: {row['message']!r}")
    resource_type, resource_name = match.groups()
    if resource_type not in RESOURCE_TYPES:
        fail(f"unsupported Android resource type in {variant}: {resource_type}")
    path = row["relativePath"]
    if "\\" in path or not PATH_RE.fullmatch(path) or any(part in {"", ".", ".."} for part in path.split("/")):
        fail(f"unsafe or invalid Android repo-relative path in {variant}: {path!r}")
    if not (Path(__file__).resolve().parents[2] / Path(path)).is_file():
        fail(f"Android resource path does not exist in {variant}: {path}")
    family = path.split("/")[4]
    if family != "values" and not (family == resource_type or family.startswith(resource_type + "-")):
        fail(f"resource symbol/path family mismatch in {variant}: {resource_type}/{resource_name} -> {path}")
    return resource_type, resource_name, row["message"]
def extract(payload: dict) -> dict[str, dict]:
    result: dict[str, dict] = {}
    per_variant: dict[str, Counter] = {}
    for variant in ("debug", "release"):
        selected = [row for row in warnings(payload, variant) if isinstance(row, dict) and row.get("ruleId") == "UnusedResources"]
        if not selected:
            fail(f"{variant} has no UnusedResources entries")
        counter: Counter = Counter()
        for row in selected:
            resource_type, resource_name, message = validate_row(row, variant)
            key = (row["ruleId"], f"{resource_type}/{resource_name}", message)
            counter[key] += 1
            if counter[key] != 1:
                fail(f"UnusedResources multiplicity must be exactly D×1/R×1 in {variant}: {key}")
            result.setdefault(key[1], {"type": resource_type, "name": resource_name, "ruleId": key[0], "message": message, "variants": {}})
            result[key[1]]["variants"][variant] = {"fingerprint": row["fingerprint"], "relativePath": row["relativePath"], "occurrences": 1}
        per_variant[variant] = counter
    if per_variant["debug"] != per_variant["release"]:
        missing = list((per_variant["debug"] - per_variant["release"]).elements())
        extra = list((per_variant["release"] - per_variant["debug"]).elements())
        fail(f"cross-variant semantic multiset mismatch: missing={missing}, extra={extra}")
    if any(set(item["variants"]) != {"debug", "release"} for item in result.values()):
        fail("symbol missing from one variant")
    return dict(sorted(result.items()))
def generate(payload: dict) -> dict:
    symbols = extract(payload)
    return {"schemaVersion": 1, "sourceSchemaVersion": SCHEMA_VERSION, "sourceRevision": payload["generatedFrom"]["sourceRevision"], "semanticIdentity": ["ruleId", "type/name", "message"], "symbols": symbols}

INVENTORY_COLUMN_COUNT = 12
MARKDOWN_CLASSIFICATIONS = {"needs_owner", "retain_compat", "retain_launcher"}


def fail_markdown_row(number: int, message: str) -> None:
    fail(f"Markdown inventory row {number}: {message}")


def check_markdown(markdown: Path, generated: dict) -> None:
    lines = markdown.read_text(encoding="utf-8").splitlines()
    rows = [line for line in lines if re.match(r"^\| \d+ \|", line)]
    if len(rows) != len(generated["symbols"]):
        fail(f"Markdown row count {len(rows)} != generated symbol count {len(generated['symbols'])}")
    seen: set[str] = set()
    launcher_definitions = {
        "mipmap/ic_launcher": ("mipmap-anydpi/ic_launcher.xml", "mipmap-mdpi/ic_launcher.webp", "mipmap-hdpi/ic_launcher.webp", "mipmap-xhdpi/ic_launcher.webp", "mipmap-xxhdpi/ic_launcher.webp", "mipmap-xxxhdpi/ic_launcher.webp"),
        "mipmap/ic_logo_install_round": ("mipmap-anydpi-v26/ic_logo_install_round.xml", "mipmap-mdpi/ic_logo_install_round.webp", "mipmap-hdpi/ic_logo_install_round.webp", "mipmap-xhdpi/ic_logo_install_round.webp", "mipmap-xxhdpi/ic_logo_install_round.webp", "mipmap-xxxhdpi/ic_logo_install_round.webp"),
    }
    for expected_number, line in enumerate(rows, start=1):
        cells = [cell.strip() for cell in line.strip("|").split("|")]
        if len(cells) != INVENTORY_COLUMN_COUNT:
            fail_markdown_row(expected_number, f"expected {INVENTORY_COLUMN_COUNT} columns, got {len(cells)}")
        if cells[0] != str(expected_number):
            fail_markdown_row(expected_number, "sequence number is invalid")
        symbol = cells[1].strip("`")
        if symbol in seen or symbol not in generated["symbols"]:
            fail_markdown_row(expected_number, f"missing or duplicate symbol {symbol!r}")
        seen.add(symbol)
        definitions, debug_fp, release_fp, semantic, accountable, evidence, linked, classification, rationale, trigger = cells[2:]
        if any(not value.strip() for value in (definitions, debug_fp, release_fp, semantic, accountable, evidence, linked, classification, rationale, trigger)):
            fail_markdown_row(expected_number, "a required triage column is blank")
        item = generated["symbols"][symbol]
        if item["variants"]["debug"]["fingerprint"] != debug_fp.strip("`"):
            fail_markdown_row(expected_number, "debug fingerprint does not match generated inventory")
        if item["variants"]["release"]["fingerprint"] != release_fp.strip("`"):
            fail_markdown_row(expected_number, "release fingerprint does not match generated inventory")
        definition_path = item["variants"]["debug"]["relativePath"].removeprefix("app/src/main/res/")
        if definition_path not in definitions:
            fail_markdown_row(expected_number, f"definitions do not include report path {definition_path}")
        expected_semantic = f"UnusedResources; {symbol}; canonical unused; D×1/R×1"
        if semantic.strip("`") != expected_semantic:
            fail_markdown_row(expected_number, "semantic key or multiplicity is invalid")
        if not re.search(r"#[0-9]+", linked):
            fail_markdown_row(expected_number, "linked issue/PR is invalid")
        normal_classification = classification.strip("`")
        if normal_classification not in MARKDOWN_CLASSIFICATIONS:
            fail_markdown_row(expected_number, f"classification is invalid: {normal_classification!r}")
        if normal_classification == "needs_owner" and ("placeholder" not in accountable.lower() or "not confirmed" not in accountable.lower()):
            fail_markdown_row(expected_number, "needs_owner must explicitly name placeholder accountability")
        if symbol in launcher_definitions and any(token not in definitions for token in launcher_definitions[symbol]):
            fail_markdown_row(expected_number, "launcher family omits a required qualifier definition")
    if seen != set(generated["symbols"]):
        fail("Markdown symbol set mismatch")

def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--baseline", type=Path, required=True)
    parser.add_argument("--output-json", type=Path, required=True)
    parser.add_argument("--markdown", type=Path, required=True)
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    try:
        payload = json.loads(args.baseline.read_text(encoding="utf-8"))
        generated = generate(payload)
        if args.check:
            expected = json.loads(args.output_json.read_text(encoding="utf-8"))
            if expected != generated:
                fail("generated JSON is stale")
        else:
            args.output_json.parent.mkdir(parents=True, exist_ok=True)
            args.output_json.write_text(json.dumps(generated, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        check_markdown(args.markdown, generated)
    except (OSError, json.JSONDecodeError, KeyError, TypeError) as exc:
        fail(str(exc))
    print(f"Resource inventory validated: {len(generated['symbols'])} symbols, debug/release semantic multisets match.")

if __name__ == "__main__":
    main()
