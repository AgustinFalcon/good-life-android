# Validación de UR-02a — colores legacy propios

Fecha: 2026-09-16
Base: `origin/master` `0e56c57`
Rama: `chore/android-remove-legacy-colors`
Issue: #38 (se mantiene abierta hasta merge)

## Alcance ejecutado

- Eliminados únicamente `black`, `white`, `purple_200`, `purple_500`, `purple_700`, `teal_200` y `teal_700` de `app/src/main/res/values/colors.xml`; el archivo quedó eliminado por completo.
- No se tocaron strings, recursos `core_compat`, familias launcher, Gradle, manifest, packaging ni UI.
- La búsqueda estática con `rg` para `@color/...` y `R.color...` no encontró referencias en `app/src`.
- El manifiesto `.github/scripts/manifests/legacy-colors-38.json` autoriza exactamente 14 fingerprints (7 símbolos × debug/release).
- El reducer `.github/scripts/remove_lint_baseline_entries.py` actualizó la baseline; no se editó el JSON manualmente y preservó `generatedFrom`, `externalAdvisories` y `exceptions`.
- `remediation/legacy-colors-38.json` conserva owner, issue, rationale, trigger, fingerprints before, estado after y remaining=false.

## Gates

- `python -m unittest discover -s .github/tests -p 'test_*.py'`: OK, 39 tests.
- `python .github/scripts/resource_inventory.py ... --check`: OK, 28 símbolos; multiset debug/release coherente.
- `python .github/scripts/verify_lint_baseline.py --baseline config/lint-baseline.json --debug-report app/build/reports/lint-results-debug.xml --release-report app/build/reports/lint-results-release.xml --repo-root .`: OK; UnusedResources 35 → 28 y warnings determinísticos totales 41 → 34, sin nuevos determinísticos.
- `.\gradlew.bat :app:lintDebug :app:lintRelease --no-daemon`: OK, BUILD SUCCESSFUL.
- `.\gradlew.bat :app:assembleDebug :app:assembleRelease --no-daemon`: OK, BUILD SUCCESSFUL.
- `git diff --check`: OK en el worktree antes de commit.
- `verify-governance.sh`: OK contra la estructura vigente; se repetirá sobre el commit del PR.

La eliminación queda en revisión; no se declara cerrada hasta aprobación de reviewers, CI verde y merge.
