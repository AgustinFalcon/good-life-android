# GoodLife Android — lint baseline

- Feature id: `20260911-goodlife-lint-baseline`
- Estado: `spec_review` (revisión 2026-09-15 incorpora comparación exacta por variante antes de build).
- Issue: #10.
- Rama de implementación: `chore/android-lint-baseline`.
- Baseline de implementación: `origin/master` `5a7be32`.
- Idioma: español (es-AR).

## Evidencia observada

El reporte local `lintDebug` regenerado el 2026-09-15 sobre `5a7be32` registra 53 warnings y 0 errores. Es triage, no baseline: la autoridad será el inventario de debug/release verificado por CI Linux en LB-01. Las correcciones de manifest, icono, Compose y TOML ya integradas explican por qué el conteo histórico de 60 no puede mantenerse como contrato.

La evidencia no contiene rutas locales, usuarios ni datos de runtime. El inventario derivado versionará sólo rutas repo-relativas, fingerprints y metadatos de herramienta necesarios.