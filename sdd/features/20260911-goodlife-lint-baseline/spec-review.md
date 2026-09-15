# Spec pre-build review — lint baseline

## Metadata

- Target: `20260911-goodlife-lint-baseline` / issue #10.
- Baseline de implementación: `origin/master` `5a7be32`.
- Evidencia histórica: el conteo previo de 60 warnings es triage no vinculante; la medición local actual de debug es 53/0 y release debe producirse en CI antes de bootstrap.
- Alcance: inventario y gate de deuda, no upgrades ni borrado masivo de recursos.

## Hallazgos resueltos

| Riesgo | Resolución aplicada | Evidencia exigida |
|---|---|---|
| Conteos iguales esconden warnings distintos | Multiset exacto por fingerprint y cardinalidad; falla también por entradas stale. | Fixtures: nuevo, resuelto, duplicado, same-rule replacement y reintroducción. |
| Una variante queda sin gate | `lintDebug` y `lintRelease` tienen inventario/validación explícitos. | Ambos XML se comparan antes de merge a `master`. |
| Bootstrap circular | `sourceRevision` es el padre/base sin el inventario; el SHA de CI no se versiona dentro del propio commit. | CI Linux valida HEAD contra ese inventario. |
| Mensajes con versión externa son inestables | `GradleDependency` y `AndroidGradlePluginVersion` se canonicalizan por coordenada/versión declarada, se guardan en `externalAdvisories` y no contaminan el multiset determinista. | PR: warning/summary/artefacto; monitor semanal/manual en `master` falla por drift; fixture de sugerencia cambiante. |
| Excepciones amplias o XML inseguro | Schema exacto sin wildcard y parser fail-closed sin DTD/entidades/rutas absolutas. | Fixtures de schema, expiry y XML malicioso. |
| Estado histórico obsoleto | LB-02a/b/c se marcan done como fixes ya integrados; se revalidan por inventario. | Baseline actual `5a7be32`. |

## Decisión

**LISTO PARA BUILD.** La re-review final confirma que el parser distingue ruta absoluta de entrada (sólo bajo `--repo-root`) de inventario/salida relativa. El contrato exacto cubre exclusivamente reglas deterministas; el canal de advisories externos preserva visibilidad en PR y detección bloqueante programada, sin hacer depender el merge de feeds de versiones. Fixtures Windows/Unix equivalentes y outside-root son obligatorios. LB-01 es acotado: schema, generador, validador, fixtures, evidencia CI y monitor. LB-03/LB-04 no son parte del cierre: fueron transferidos a los issues #38 y #39 antes del archivo, sin declararlos hechos.

## Revisión de cierre — 2026-09-15

La implementación de LB-01 se integró por PR #37 (`474048f`). El verificador versiona 41 warnings deterministas y un snapshot de 12 advisories externos por variante; sólo los deterministas bloquean PRs por multiset exacto. CI Linux observó una `GradleDependency` adicional por variante y la dejó visible como delta no bloqueante. Los PRs hacia `master` preservan esa evidencia; el monitor semanal/manual sobre `master` está configurado para fallar ante drift, sin ejecución histórica todavía. La re-review independiente aprobó scripts, CI y documentación; CI Linux aprobó ambos jobs.

## Decisión de archivo

**APROBADO PARA ARCHIVO.** La deuda pendiente fue transferida explícitamente a #38 (recursos) y #39 (toolchain/dependencias); no se ocultó ni se entregó como feature de producto.
