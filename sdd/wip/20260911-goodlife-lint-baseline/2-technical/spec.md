# Especificación técnica — política de lint

## Fuente de verdad y variantes

La fuente de verdad son los XML producidos por `:app:lintDebug` y `:app:lintRelease` en el SHA revisado. Ambos se validan antes del merge; no se deja una variante fuera del gate. CI Linux es la autoridad de bootstrap y validación. El verificador nunca reemplaza el XML ni usa `lint-baseline.xml`.

## Inventario versionado

El archivo `config/lint-baseline.json` usa `schemaVersion: 2` y contiene:

- `generatedFrom`: `sourceRevision` no autorreferencial, comandos, versión de wrapper, AGP, JDK y SDK relevantes;
- `variants.debug` y `variants.release`, cada una con entradas de warning determinista;
- `externalAdvisories.debug` y `externalAdvisories.release`, snapshots sólo de `GradleDependency` y `AndroidGradlePluginVersion`;
- `exceptions`, únicamente para incorporación temporal y explícita de deuda determinista;
- clasificación de cada warning (`fix_now`, `split_upgrade`, `investigate` o `temporary_exception`).

Cada entrada de warning contiene `fingerprint`, `variant`, `ruleId`, `relativePath`, `message`, `classification`, `owner`, `justification`, `linkedIssueOrPr` y `createdOn`. Sólo una entrada `temporary_exception` dentro de `exceptions` añade exactamente uno de `expiresOn` (ISO `YYYY-MM-DD`) o `removalCondition` medible; `variants.*.warnings` prohíbe esa clasificación y cualquier campo de lifecycle. No se aceptan rutas absolutas, `..`, barras invertidas, campos vacíos, wildcards, IDs de regla sin fingerprint, fechas ambiguas ni valores de owner/enlace vacíos.

El fingerprint canónico es SHA-256 de `normalizationVersion + NUL + variant + NUL + ruleId + NUL + relativePath + NUL + normalizedMessage`, donde la ruta es repo-relativa con `/` y el texto se normaliza UTF-8 NFC, espacios internos compactados. `normalizationVersion: 1` define canonicalizadores por `ruleId`: para `GradleDependency` y `AndroidGradlePluginVersion` se conserva la coordenada y versión declarada, pero se elimina únicamente la versión sugerida externamente cambiante. Estas dos reglas se guardan exclusivamente en `externalAdvisories`; toda regla sin canonicalizador usa el mensaje completo y falla cerrado. Línea y columna no forman parte del fingerprint. Los duplicados se guardan como entradas repetidas o `occurrences`; el validador los compara como multiset exacto.

## Parser y validación fail-closed

El lector acepta sólo XML lint esperado. Rechaza archivo ausente/vacío, parseo inválido, DTD/entidades, atributo o ubicación faltante y severidad desconocida. Las rutas absolutas que emite AGP se aceptan sólo como **entrada** cuando están bajo el `--repo-root` explícito; se resuelven/normalizan, se rechazan symlinks que escapen, UNC/device paths, `..` y roots ajenos, y se convierten a ruta relativa `/`. El inventario y la salida nunca contienen rutas absolutas. Los errores lint fallan inmediatamente. Para cada variante compara el multiset de fingerprints deterministas observado contra el multiset esperado tras sumar sólo excepciones exactas, vigentes y correspondientes a esa variante. Los dos rule IDs externos se comparan contra su snapshot por separado: en PR producen un delta JSON, anotación y summary sin falsear el resultado; en el workflow programado/manual sobre `master` el mismo delta falla cerrado.

Falla por: warning determinista nuevo, multiplicidad mayor o menor, entrada inventariada stale/resuelta, excepción vencida, schema inválido o XML no legible. El drift de advisory externo sólo falla en el monitor independiente, nunca se incorpora al baseline determinista ni se silencia por catálogo o regla completa. Por lo tanto un warning corregido obliga a retirar su inventario en el mismo PR; si se reintroduce luego, falla como nuevo. La salida sólo imprime variant, regla, fingerprint corto y ruta repo-relativa.

## Bootstrap reproducible

La PR inicial genera ambos XML desde la `sourceRevision` (el padre/base que no contiene el inventario ni scripts) y usa una herramienta versionada para emitir el JSON ordenado. El inventario no puede contener el SHA del commit que lo agrega. CI corre los mismos dos comandos en HEAD y el verificador exige igualdad exacta; el `GITHUB_SHA` evaluado queda sólo en logs/evidencia efímera. La primera ejecución verde en Linux constituye la medición autoritativa. La cantidad histórica observada el 2026-09-11/15 es únicamente triage y no se copia como aceptación fija.

## Integración CI

Antes de merge, el workflow para PRs cuya base es `master` corre `:app:lintDebug :app:lintRelease`, luego el verificador con `--advisory-delta-out build/lint-advisory-delta.json`, y por último ensambla la variante release no firmada. El multiset exacto sólo cubre reglas deterministas. Los XML debug/release y el delta JSON se publican siempre como evidencia del job. `lint-advisory-monitor.yml` corre cada lunes y manualmente sobre `master` con `--fail-on-advisory-drift`: el mismo cambio externo que sólo alerta en PR falla allí y requiere su issue/PR de upgrade aislado.

Los fixtures cubren XML Windows/Unix equivalente, DTD rechazado, reporte ausente, error lint, warning determinista nuevo, warning eliminado sin retirar inventario, duplicado idéntico, reemplazo same-rule, reintroducción luego de retirar la entrada, sugerencia externa con objetivo distinto (sin drift), coordenada externa distinta (visible/no bloqueante en PR y bloqueante en monitor), reglas desconocidas, metadata/schema, excepción vencida o malformada y `--repo-root .` normalizado.

## Plan de cambios

1. Implementar generador, schema, inventario de ambas variantes, verificador determinista y fixtures/tests.
2. Activar el paso CI después de ambos lint.
3. Mantener los fixes de bajo riesgo ya integrados como evidencia histórica; no volver a mezclar familias.
4. Crear issues separados para `UnusedResources` y upgrades de toolchain/dependencias antes de reducirlos.
5. Reducir el inventario en PRs pequeños hasta poder evaluar `warningsAsErrors` sólo cuando no quede deuda aceptada.

## Riesgos

Un parser permisivo o una comparación por conteo permitiría falsos verdes; por eso se compara multiset exacto por variante y se rechaza input ambiguo. Los upgrades de dependency/AGP y recursos de compatibilidad se mantienen fuera del bootstrap porque cambian toolchain o packaging.
