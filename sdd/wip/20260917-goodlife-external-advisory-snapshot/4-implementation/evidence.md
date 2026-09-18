# Evidencia de refresh externo — #52

- Fuente: GitHub Actions run `35281521463`, artifact `lint-evidence`.
- Evento: `pull_request`; resultado: `success`.
- SHA evaluado: `fdbede748f4f565276a2305439eb55ab9aa9efc3`.
- Archivos consumidos: `lint-results-debug.xml`, `lint-results-release.xml` y delta JSON del artifact.
- Raíz declarada del XML: `/home/runner/work/good-life-android/good-life-android`.

## Resultado

El refresh atómico vinculó `externalAdvisories` a #52. La comparación profunda contra HEAD confirmó sin cambios `generatedFrom`, `variants` y `exceptions`; el verificador estricto pasó sobre ambos XML importados. La evidencia se usa solo para tooling: no incluye tokens, usuarios, dispositivos, APKs ni datos de aplicación.
## Validación local y límite conocido

- `test_lint_baseline.py`: PASS (22 casos, incluidos imports Linux remotos, rechazos y no-escritura).
- `:app:lintDebug :app:lintRelease`: PASS.
- Verificación estricta contra los XML del artifact CI: PASS.
- Verificación local no estricta: baseline determinista PASS; el resolver local no anunció dos advisories de `GradleDependency` que sí existían en la evidencia CI. Con `--fail-on-advisory-drift` el resultado es FAIL, como debe ser: no se interpreta como éxito ni se borra la evidencia CI para acomodar una fuente distinta. El monitor de GitHub valida el runner autoritativo tras el PR.