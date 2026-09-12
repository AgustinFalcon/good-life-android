# GoodLife Android — lint baseline

- Feature id: `20260911-goodlife-lint-baseline`
- Estado: `spec_review`.
- Issue: #10.
- Rama: `chore/android-lint-baseline-spec`.
- Idioma: español (es-AR).

## Evidencia observada

El reporte local `lintDebug` del 2026-09-11 sobre el código equivalente al baseline de trabajo registró 60 advertencias y 0 errores. Es evidencia de triage, no un baseline reproducible: LB-01 debe regenerar desde el SHA de implementación, en CI Linux, y versionar el inventario derivado junto con SHA base, Gradle wrapper, AGP, SDK/JDK relevantes, comando, conteos por regla y fingerprints relativos. Clasificación por regla:

| Regla | Cantidad | Tratamiento propuesto |
|---|---:|---|
| `UnusedResources` | 37 | Inventariar y eliminar sólo recursos propios confirmados; investigar recursos de compatibilidad antes de tocar. |
| `GradleDependency` | 11 | Plan de upgrades por familia, fuera de fixes visuales. |
| `ModifierParameter` | 3 | Fix mecánico con revisión Compose. |
| `IconLocation` | 2 | Reubicar assets con verificación visual. |
| `AndroidGradlePluginVersion` | 1 | Upgrade aislado y compatible, no en esta feature. |
| `DataExtractionRules` | 1 | Corregir por privacidad/backup con revisión de manifest. |
| `MonochromeLauncherIcon` | 1 | Completar icono adaptativo y verificar launcher. |
| `ObsoleteSdkInt` | 1 | Migrar recurso v26 coherentemente con minSdk. |
| `OldTargetApi` | 1 | Decisión de upgrade de target/SDK con pruebas. |
| `RedundantLabel` | 1 | Fix mecánico de manifest. |
| `UseTomlInstead` | 1 | Migración acotada de dependencia a version catalog. |

La evidencia no contiene rutas locales, usuarios ni datos de runtime. Hasta que LB-01 produzca esa medición revisada, el número 60 no se usa como gate ni como afirmación del inventario definitivo.
