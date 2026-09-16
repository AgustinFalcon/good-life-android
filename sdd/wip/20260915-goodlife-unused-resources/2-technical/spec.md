# Especificación técnica — recursos no usados

## Artefacto y derivación

`4-implementation/resource-inventory.md` es el artefacto versionado de UR-01. Se deriva de `config/lint-baseline.json` schema v2 sin editar el JSON manualmente. El parser acepta el mensaje ya canonicalizado `The resource R.<type>.<name> appears to be unused`, extrae `type/name` y falla cerrado ante cualquier formato desconocido, símbolo no parseable o path que no corresponda a una definición Android esperada.

La comparación entre variantes usa el multiset semántico `ruleId + type/name + normalizedMessage + occurrences`; conserva la multiplicidad, no incluye `variant` ni location y falla si hay mismatch. Los fingerprints crudos permanecen separados porque contienen la variante. La snapshot inicial se espera en 35/35; toda PR posterior usa snapshots `before`, `after` y `remaining` y el delta explícito, sin una cardinalidad fija.

## Reducer focal futuro

Si una remediation PR necesita retirar entradas del baseline, debe usar una herramienta focal/reducer versionada, no editar JSON a mano. Su entrada es una lista cerrada de fingerprints exactos presentes en `before`; debe retirar sólo esos fingerprints de `variants.*.warnings`, rechazar fingerprints ausentes/duplicados y verificar el delta semántico. Debe preservar byte por byte (o mediante hash canónico estable) `externalAdvisories`, `exceptions`, `generatedFrom`, canonicalizadores y policy; si alguna de esas secciones cambia, falla. Sus tests cubren retiro exacto, fingerprint ausente/duplicado, advertencia nueva, advisory/excepción/metadata inmutables y mismatch de multiplicidad. La implementación del reducer queda fuera de Plan A.

## Familias y límites

1. `core_compat.xml` / `notification_template_*`: `retain_compat` en #38. En #38 puede inspeccionarse AAR/merged resources read-only para justificar `retain_compat`; sólo #39 puede producir cambios de dependency/AGP/packaging y define el trigger de reevaluación.
2. Launcher: se trata como familia completa entre `mipmap-*`, `mipmap-anydpi-*`, adaptive foreground/background/monochrome y manifest merged. Nunca se borra sólo una location. Si se propone eliminación exige smoke de instalación, upgrade y launcher antes/después.
3. Strings: se divide por owner (login/biometría, navegación/tabs, Daily) y se remedia en PRs distintos.
4. Colors/drawables propios: se divide como familia legacy; requiere búsqueda Compose/XML/theme/preview/dependencias.

## Procedimiento atómico posterior

1. Capturar `before` y XML debug/release en checkout limpio.
2. Aplicar sólo la familia autorizada por `remove`.
3. Regenerar ambos XML; verificar que el único delta determinista sea exactamente el conjunto esperado, sin warning nuevo, reemplazo de location/qualifier ni cambio de multiplicidad.
4. Ejecutar reducer focal, verifier, lint debug/release, `assembleDebug`, `assembleRelease` y gates lógicos.
5. Registrar `after`/`remaining`, changelog, Knowledge, issue y PR; sólo después de reviews aprobadas usar finish/archive.

`retain_*` registra rationale y trigger de reevaluación; `needs_owner` se registra inicialmente como placeholder con accountable follow-up no confirmado; requiere owner real, issue enlazado y trigger, y bloquea UR-04 hasta resolver o transferir ownership.
