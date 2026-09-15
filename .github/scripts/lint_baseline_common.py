#!/usr/bin/env python3
"""Shared, fail-closed lint XML canonicalization for the GoodLife Android gate."""
from __future__ import annotations

import hashlib
import re
import unicodedata
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path

NORMALIZATION_VERSION = 1
VOLATILE_RULES = {"GradleDependency", "AndroidGradlePluginVersion"}


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


def relative_path(raw_path: str, repo_root: str) -> str:
    """Accept only an XML location inside the explicit root; emit a safe relative path."""
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
    if not relative or relative.startswith("/") or ".." in relative.split("/") or re.match(r"^[A-Za-z]:", relative):
        fail(f"Unsafe relative path: {raw_path}")
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


def parse_report(report: Path, variant: str, repo_root: str) -> list[dict]:
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
            path = relative_path(raw_file, repo_root)
            normalized = canonical_message(rule_id, message)
            findings.append({"fingerprint": fingerprint(variant, rule_id, path, normalized), "variant": variant, "ruleId": rule_id, "relativePath": path, "message": normalized})
    return sorted(findings, key=lambda item: (item["fingerprint"], item["relativePath"], item["ruleId"]))


def multiset(items: list[dict]) -> Counter:
    return Counter(item["fingerprint"] for item in items)
