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

# Validación de UR-02b — strings auth/login

Fecha: 2026-09-16
Base: `origin/master` `25d068ab8758203f182158240c344adede7c0d05` (PR #42)
Rama: `chore/android-remove-auth-strings`
Issue: #38 (se mantiene abierta hasta review, CI y merge)

## Alcance ejecutado

- Eliminados únicamente 13 strings legacy autorizados de `app/src/main/res/values/strings.xml`: cinco de biometría y ocho de login, sin modificar Kotlin, Compose, navegación, manifest, launcher, compatibilidad ni Gradle.
- La búsqueda estática cubre `@string/...`, `R.string...`, `stringResource`, reflexión y lookups dinámicos para el conjunto autorizado; no encontró referencias en `app/src`. La presentación vigente suministra copy por `UiTexts`/contratos de dominio.
- `.github/scripts/manifests/auth-strings-38.json` autoriza exactamente 26 fingerprints (13 símbolos × debug/release), generados desde la baseline previa.
- El reducer versionado acepta scopes inmutables `legacy-colors` y `auth-strings`; para auth retira exactamente 13 entradas por variante y preserva `generatedFrom`, `externalAdvisories` y `exceptions`.
- `resource-inventory.json` y `resource-inventory.md` se regeneraron: 28 → 15 símbolos restantes por variante. El ledger `remediation/auth-strings-38.json` conserva before/after/remaining por cada símbolo.

## Gates ejecutados

- `python -m unittest discover -s .github/tests -p 'test_*.py'`: OK, 42 tests.
- `python .github/scripts/resource_inventory.py ... --check`: OK, 15 símbolos y multiset debug/release coherente.
- `python .github/scripts/verify_lint_baseline.py ...`: OK para debug/release; no hay drift determinístico ni de advisories.
- `./gradlew.bat :app:lintDebug :app:lintRelease :app:assembleDebug :app:assembleRelease :app:logicDebugUnitTestCoverageVerification --no-daemon`: OK, generó APK debug y release no firmado; cobertura lógica verificada.

# Validación de cierre UR-02c a UR-05 — recursos restantes

Fecha: 2026-09-17
Base: `origin/master` `283d2cbb98b976968f606f305b2f1880357910ec` (PR #44)
Issue: #38

## Resultado

- PRs #42, #43 y #44 están mergeadas; sus dos checks de Android CI concluyeron `SUCCESS` en la última remediación.
- El inventario versionado y su verificador declaran seis símbolos `UnusedResources`: cuatro `retain_compat` de AndroidX y dos `retain_launcher` protegidos por manifest/qualifiers.
- Los tests de guardrail, inventario multivariante y baseline debug/release se reejecutan en la rama de cierre antes de su commit; la evidencia de producto permanece en las PRs #42–#44. No se eliminan recursos retenidos, no se actualizan dependencias, ni se altera UI o comportamiento.
- El cierre de #38 no reemplaza el smoke autenticado #5 ni el plan de compatibilidad #39.

- La baseline clasifica explícitamente los seis recursos restantes: cuatro retain_compat con seguimiento #39 y dos retain_launcher con seguimiento #38. El test de inventario verifica la clasificación y el enlace en debug/release.
