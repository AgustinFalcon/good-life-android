#!/usr/bin/env bash
set -euo pipefail

# Keep the agent contract usable and require documentation or changelog evidence
# for product changes. The secret scan is intentionally high confidence so
# secret identifiers such as TOKEN_FILE do not create false positives.
required=(AGENTS.md docs/agent/overview.md docs/agent/architecture.md docs/agent/contracts.md docs/agent/runbook.md docs/agent/traps.md CHANGELOG.md)
for file in "${required[@]}"; do
  [[ -s "$file" ]] || { echo "Missing or empty governance artifact: $file" >&2; exit 1; }
done
for document in docs/agent/overview.md docs/agent/architecture.md docs/agent/contracts.md docs/agent/runbook.md docs/agent/traps.md; do
  grep -Fq "$document" AGENTS.md || { echo "AGENTS.md must link to $document" >&2; exit 1; }
done

base_ref="${DOCS_BASE_REF:-}"
if [[ -z "$base_ref" || "$base_ref" =~ ^0+$ ]] || ! git cat-file -e "${base_ref}^{commit}" 2>/dev/null; then
  echo "Governance structure and AGENTS contract passed."
  exit 0
fi
changed="$(git diff --name-only "$base_ref"...HEAD)"
if [[ -z "$changed" ]]; then
  echo "Governance check passed: no changed files."
  exit 0
fi
requires_docs='^(app/src/|app/build\.gradle\.kts$|build\.gradle\.kts$|settings\.gradle\.kts$|gradle/|\.github/workflows/|AndroidManifest\.xml$)'
if grep -Eq "$requires_docs" <<<"$changed" && ! grep -Eq '^(docs/agent/|AGENTS\.md$|CHANGELOG\.md$)' <<<"$changed"; then
  echo "Product, build, manifest, or workflow changes require docs/agent, AGENTS.md, or CHANGELOG.md evidence." >&2
  exit 1
fi
diff_text="$(git diff --no-ext-diff --unified=0 "$base_ref"...HEAD -- . ':!**/build/**' ':!**/.gradle/**')"
if grep -Eiq '(^|[^A-Za-z0-9])(AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9_]{20,}|xox[baprs]-[A-Za-z0-9-]{10,}|-----BEGIN ([A-Z ]+ )?PRIVATE KEY-----)' <<<"$diff_text"; then
  echo "High-confidence credential material was added in this change." >&2
  exit 1
fi
echo "Documentation, AGENTS, changelog, and changed-secret governance passed."
