# Especificación técnica — importación portable y refresh atómico de evidencia CI

## Contrato de rutas

`parse_report` mantiene el contrato actual de raíz local. Se agrega `report_root` opcional, exclusivamente como prefijo léxico para ubicaciones absolutas guardadas en XML remoto; nunca se usa para acceso a filesystem remoto.

Cuando se declara, `report_root` debe ser una ruta POSIX absoluta, no puede ser `/`, vacía, contener `.`/`..`, UNC/device syntax ni unidades Windows. La coincidencia es case-sensitive y por segmento exacto (`report_root + "/"`): se rechazan raíz exacta, prefijos colisionados, case mismatch, entradas no mapeadas y cualquier resultado inseguro. La parte mapeada se valida como ruta relativa segura, se resuelve bajo `repo_root` local y debe existir. Sin `report_root`, el comportamiento fail-closed actual no cambia.

## Refresh externo atómico

El generador incorpora un modo `--refresh-external-from <baseline>` que requiere `--linked-issue-or-pr #N`. En este modo:

1. carga el baseline existente y obtiene los XML con el mismo `--report-root` opcional;
2. compara el multiset determinista observado con `variants` más `exceptions`; ante cualquier diferencia falla sin escribir;
3. preserva por igualdad profunda `generatedFrom`, `variants` y `exceptions`;
4. reemplaza solamente `externalAdvisories`, con metadata de #52;
5. escribe el resultado solo tras superar todas las validaciones.

El modo de generación inicial queda compatible; el vínculo de issue es validado estrictamente en modo refresh. La CLI del verificador también acepta `--report-root`, para probar la misma evidencia importada en modo estricto.

## Evidencia y validación

`4-implementation/evidence.md` registra el run CI fuente, SHA evaluado, nombre de artefacto y digest/identidad pública admisible. No se versionan XML, rutas locales ni datos de usuario.

- Tests E2E de generator y verifier: Linux remoto válido; raíz inválida, case mismatch, prefijo colisionado, traversal, UNC/device, ruta relativa, absoluta sin mapear y raíz exacta rechazados; vínculo `#N` inválido rechazado.
- Tests afirman deep equality de `variants`, `exceptions` y `generatedFrom`; confirman que drift determinista no escribe output.
- Regenerar desde `lint-evidence` del run CI y ejecutar verificador estricto sobre ambos XML.
- Ejecutar tests Python, lint debug/release y CI del PR.
- Rollback: revertir el PR. No hay migración de runtime ni cambio de producto.