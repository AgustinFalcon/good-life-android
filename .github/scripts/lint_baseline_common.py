#!/usr/bin/env python3
"""Shared, fail-closed lint XML canonicalization for the GoodLife Android gate."""
from __future__ import annotations

import hashlib
import re
import unicodedata
import xml.etree.ElementTree as ET
from collections import Counter
from datetime import date
from pathlib import Path

NORMALIZATION_VERSION = 1
VOLATILE_RULES = {"GradleDependency", "AndroidGradlePluginVersion"}
TOP_KEYS = {"schemaVersion", "normalizationVersion", "generatedFrom", "variants", "externalAdvisories", "exceptions"}
BASE_KEYS = {"fingerprint", "variant", "ruleId", "relativePath", "message", "classification", "owner", "justification", "linkedIssueOrPr", "createdOn"}
DETERMINISTIC_CLASSES = {"fix_now", "split_upgrade", "investigate", "retain_compat", "retain_launcher"}


def fail(message: str) -> None:
    raise ValueError(message)


def canonical_message(rule_id: str, message: str) -> str:
    text = " ".join(unicodedata.normalize("NFC", message).split())
    if rule_id in VOLATILE_RULES:
        match = re.match(r"^(A newer version of .+? than [^ ]+ is available): .+$", text)
        if not match:
            fail(f"Unsupported volatile message format for {rule_id}")
        return match.group(1)
    return text


def normalized_report_root(report_root: str) -> str:
    """Validate a lexical POSIX root used only to map remote XML locations."""
    if not isinstance(report_root, str) or not report_root or "\\" in report_root:
        fail("Report root must be a non-empty POSIX absolute path")
    if report_root == "/" or not report_root.startswith("/") or report_root.startswith("//") or report_root.startswith("/??/") or report_root.startswith("/./"):
        fail("Unsafe report root")
    if report_root.endswith("/"):
        fail("Report root must not have a trailing slash")
    segments = report_root.split("/")[1:]
    if not segments or any(segment in {"", ".", ".."} for segment in segments):
        fail("Unsafe report root segments")
    if any(re.match(r"^[A-Za-z]:$", segment) for segment in segments):
        fail("Windows drive is not a POSIX report root")
    return report_root


def safe_relative(relative: str, raw_path: str) -> str:
    if not relative or relative.startswith("/") or any(segment in {"", ".", ".."} for segment in relative.split("/")) or re.match(r"^[A-Za-z]:", relative):
        fail(f"Unsafe relative path: {raw_path}")
    return relative


def local_candidate(relative: str, repo_root: str, raw_path: str) -> None:
    try:
        root_path = Path(repo_root).resolve(strict=True)
        candidate = (root_path / Path(*relative.split("/"))).resolve(strict=True)
        candidate.relative_to(root_path)
    except (OSError, ValueError):
        fail(f"Path resolves outside explicit repo root: {raw_path}")


def relative_path(raw_path: str, repo_root: str, report_root: str | None = None) -> str:
    """Accept only a safe XML location rooted locally or in an explicit remote root."""
    if not isinstance(raw_path, str) or not raw_path:
        fail("Lint XML location is missing")
    if report_root is not None:
        root = normalized_report_root(report_root)
        if "\\" in raw_path or raw_path.startswith("//") or raw_path.startswith("/??/") or raw_path.startswith("/./"):
            fail(f"UNC or device path rejected: {raw_path}")
        if raw_path == root:
            fail(f"Report root cannot be a lint file location: {raw_path}")
        if not raw_path.startswith(root + "/"):
            fail(f"Lint XML location is outside explicit report root: {raw_path}")
        relative = safe_relative(raw_path[len(root) + 1:], raw_path)
        local_candidate(relative, repo_root, raw_path)
        return relative

    path = raw_path.replace("\\", "/")
    root = repo_root.replace("\\", "/").rstrip("/")
    if path.startswith("//") or path.startswith("/??/") or path.startswith("/./"):
        fail(f"UNC or device path rejected: {raw_path}")
    if path.lower().startswith(root.lower() + "/"):
        relative = path[len(root) + 1:]
    elif path.startswith("/") or re.match(r"^[A-Za-z]:/", path):
        fail(f"Absolute path outside explicit repo root rejected: {raw_path}")
    else:
        fail(f"Lint XML location must be rooted in explicit repo root: {raw_path}")
    relative = safe_relative(relative, raw_path)
    try:
        root_path = Path(repo_root).resolve(strict=True)
        candidate = Path(raw_path).resolve(strict=True)
        candidate.relative_to(root_path)
    except (OSError, ValueError):
        if Path(raw_path).exists() or Path(repo_root).exists():
            fail(f"Path resolves outside explicit repo root: {raw_path}")
    return relative


def fingerprint(variant: str, rule_id: str, path: str, message: str) -> str:
    payload = "\0".join((str(NORMALIZATION_VERSION), variant, rule_id, path, message))
    return hashlib.sha256(payload.encode("utf-8")).hexdigest()


def parse_report(report: Path, variant: str, repo_root: str, report_root: str | None = None) -> list[dict]:
    validated_report_root = normalized_report_root(report_root) if report_root is not None else None
    if not report.is_file() or report.stat().st_size == 0:
        fail(f"Missing or empty lint report: {report}")
    raw = report.read_text(encoding="utf-8")
    if "<!DOCTYPE" in raw.upper() or "<!ENTITY" in raw.upper():
        fail("DTD and entities are forbidden in lint XML")
    try:
        root = ET.fromstring(raw)
    except ET.ParseError as exc:
        fail(f"Unreadable lint XML: {exc}")
    if root.tag != "issues":
        fail("Unexpected lint XML root")
    findings: list[dict] = []
    for issue in root.findall("issue"):
        rule_id, severity, message = issue.get("id"), issue.get("severity"), issue.get("message")
        if not rule_id or severity not in {"Warning", "Error", "Information", "Fatal"} or message is None:
            fail("Lint issue missing required attributes")
        if severity in {"Error", "Fatal"}:
            fail(f"Lint {severity}: {rule_id}")
        locations = issue.findall("location")
        if not locations:
            fail(f"Lint warning lacks location: {rule_id}")
        for location in locations:
            raw_file = location.get("file")
            if not raw_file:
                fail(f"Lint warning lacks location file: {rule_id}")
            path = relative_path(raw_file, repo_root, validated_report_root)
            normalized = canonical_message(rule_id, message)
            findings.append({"fingerprint": fingerprint(variant, rule_id, path, normalized), "variant": variant, "ruleId": rule_id, "relativePath": path, "message": normalized})
    return sorted(findings, key=lambda item: (item["fingerprint"], item["relativePath"], item["ruleId"]))


def validate_record(entry: dict, variant: str, domain: str) -> None:
    if not isinstance(entry, dict) or not BASE_KEYS.issubset(entry):
        fail("Baseline entry has missing required fields")
    if domain == "exception":
        extra = set(entry) - BASE_KEYS
        if entry.get("classification") != "temporary_exception" or extra not in ({"expiresOn"}, {"removalCondition"}):
            fail("Exception shape is invalid")
    elif set(entry) != BASE_KEYS:
        fail("Baseline record has unexpected fields")
    if any(not isinstance(entry.get(key), str) or not entry[key].strip() for key in BASE_KEYS):
        fail("Baseline entry has blank required fields")
    if entry["variant"] != variant or not re.fullmatch(r"[0-9a-f]{64}", entry["fingerprint"]):
        fail("Baseline entry variant or fingerprint is invalid")
    if entry["fingerprint"] != fingerprint(variant, entry["ruleId"], entry["relativePath"], entry["message"]):
        fail("Baseline fingerprint does not match canonical fields")
    if entry["relativePath"].startswith("/") or ".." in entry["relativePath"].split("/") or re.match(r"^[A-Za-z]:", entry["relativePath"]):
        fail("Baseline path is unsafe")
    try:
        date.fromisoformat(entry["createdOn"])
    except ValueError:
        fail("Baseline createdOn is invalid")
    if domain == "deterministic":
        if entry["ruleId"] in VOLATILE_RULES or entry["classification"] not in DETERMINISTIC_CLASSES:
            fail("Deterministic baseline record is invalid")
    elif domain == "advisory":
        if entry["ruleId"] not in VOLATILE_RULES or entry["classification"] != "split_upgrade":
            fail("External advisory record is invalid")
    else:
        expiry, condition = entry.get("expiresOn"), entry.get("removalCondition")
        if expiry:
            try:
                if date.fromisoformat(expiry) < date.today():
                    fail("Exception is expired")
            except ValueError:
                fail("Exception expiry is invalid")
        elif not isinstance(condition, str) or not condition.strip():
            fail("Exception removalCondition is invalid")


def baseline_records(data: dict, domain: str, variant: str) -> list[dict]:
    container = data["variants"] if domain == "deterministic" else data["externalAdvisories"]
    if not isinstance(container, dict) or set(container) != {"debug", "release"}:
        fail(f"{domain} variants schema is invalid")
    item = container.get(variant)
    if not isinstance(item, dict) or set(item) != {"warnings"} or not isinstance(item["warnings"], list):
        fail(f"{domain} warnings schema is invalid")
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


def validate_baseline(data: object) -> dict:
    if not isinstance(data, dict) or set(data) != TOP_KEYS or data["schemaVersion"] != 2 or data["normalizationVersion"] != NORMALIZATION_VERSION:
        fail("Unsupported lint baseline schema")
    generated = data["generatedFrom"]
    if not isinstance(generated, dict) or set(generated) != {"sourceRevision", "commands", "toolchain", "authority"}:
        fail("Lint baseline generatedFrom schema is invalid")
    if not re.fullmatch(r"[0-9a-f]{7,64}", str(generated["sourceRevision"])) or generated["commands"] != [":app:lintDebug", ":app:lintRelease"] or not isinstance(generated["authority"], str) or not generated["authority"].strip():
        fail("Lint baseline source metadata is invalid")
    toolchain = generated["toolchain"]
    if not isinstance(toolchain, dict) or set(toolchain) != {"gradleWrapper", "agp", "jdk", "sdk"} or any(not isinstance(value, str) or not value.strip() for value in toolchain.values()):
        fail("Lint baseline toolchain metadata is invalid")
    if not isinstance(data["exceptions"], list):
        fail("Lint baseline exceptions schema is invalid")
    for domain in ("deterministic", "advisory"):
        for variant in ("debug", "release"):
            baseline_records(data, domain, variant)
    return data


def multiset(items: list[dict]) -> Counter:
    return Counter(item["fingerprint"] for item in items)