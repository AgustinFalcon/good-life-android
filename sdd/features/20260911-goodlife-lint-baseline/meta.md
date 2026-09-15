# GoodLife Android — lint baseline

- Feature id: `20260911-goodlife-lint-baseline`
- Estado: `complete` (implementado por PR #37, CI Linux verde y revisión independiente aprobada).
- Issue: #10.
- Rama de implementación: `chore/android-lint-baseline` (mergeada; cierre SDD en `chore/android-lint-baseline-closeout`).
- Baseline de implementación: `origin/master` `5a7be32`.
- Idioma: español (es-AR).

## Evidencia observada

El reporte local `lintDebug` regenerado el 2026-09-15 sobre `5a7be32` registra 53 warnings y 0 errores. Es triage, no baseline: la autoridad vigente es el inventario de debug/release verificado por CI Linux en LB-01/PR #37. PR #37 verificó 41 warnings deterministas y el snapshot de 12 advisories externos por variante, sin errores, con tests, governance y CI verde; Linux observó además una `GradleDependency` externa por variante y la expuso como delta no bloqueante. Las correcciones de manifest, icono, Compose y TOML ya integradas explican por qué el conteo histórico de 60 no puede mantenerse como contrato.

La evidencia no contiene rutas locales, usuarios ni datos de runtime. El inventario derivado versionará sólo rutas repo-relativas, fingerprints y metadatos de herramienta necesarios.
