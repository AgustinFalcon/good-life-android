# Spec pre-build review — recursos no usados

Estado final de revisión: 🟢 LISTO PARA BUILD (sólo el siguiente paso acotado: generar/verificar UR-01 inventory). No es aprobación blanket para borrar recursos.

## Alcance y decisión

La revisión cubre únicamente Plan A/PR #41: spec/inventory y checker/generador. No autoriza borrar recursos en esta PR, modificar dependencias/AGP/packaging, cerrar #38, tocar #39 ni asignar el issue al tablero. #38 puede recibir PRs de remediación posteriores, sujetas a su propia review. El issue #38 permanece abierto; su asignación al tablero no se afirma porque el token actual no tiene `read:project`.

| Revisor | Ángulo | Resultado | Cambio aplicado |
|---|---|---|---|
| Architect | estructura, ownership y límites | Revisar | Se separan inventario, triage y remediaciones; se añade UR-05 y se mantiene #39 fuera de alcance. |
| Critic | fail-closed y evidencia | Revisar | Parseo estricto de mensaje canonicalizado, multiset con multiplicidad, no borrado por location y `needs_owner` bloqueante. |
| Pragmatist | tamaño y secuencia | Revisar | Snapshot inicial explícito; remediation futura dividida por familia/owner con before/after/remaining. |
| Baseline alignment | coherencia con schema v2 | Revisar | El checker/generador versionado deriva la identidad semántica desde schema v2, conserva fingerprints separados y no fija 35/70 después de una remediación. |
| Contracts & resilience | invariantes y reducer | Revisar | Se define reducer focal con lista cerrada de SHA y protección de advisory/exceptions/metadata/policy. |
| Launcher/compat | riesgos de packaging | Revisar | `retain_launcher` exige roots manifest, qualifiers y smoke; `core_compat` queda retain_compat y se investiga sólo en #39. |

## Hallazgos cerrados en la spec

- La tabla dejó de ser una plantilla: contiene 35 familias reales, fingerprints debug/release, multiplicidad, definiciones/qualifiers, responsable, evidencia, issue/PR, clasificación, rationale y trigger.
- El 35/70 se documenta como snapshot inicial histórico; el contrato posterior es un baseline evolutivo con snapshots `before`, `after` y `remaining`.
- No se mezclan advisories externos ni excepciones con determinismo de recursos no usados.
- El checker/generador versionado falla cerrado ante formato de mensaje desconocido, parseo inválido, SHA ausente/duplicado o mismatch del multiset; el reducer de futuras PRs preservará advisory, exceptions, metadata, canonicalizadores y policy.
- UR-05 agrega changelog/docs/Knowledge/issue/PR y finish/archive, condicionado a reviews y gates verdes.

## Pendientes explícitos antes de cualquier borrado

1. Confirmar ownership por fila `needs_owner` y enlazar el issue/PR concreto.
2. Ejecutar búsquedas y evidencia de referencias directas, indirectas, dinámicas, manifest, qualifiers y dependencias.
3. Implementar el reducer focal y sus tests sólo en la PR que vaya a modificar el baseline.
4. Abrir PRs pequeñas por familia; ejecutar lint/verifier/assemble y smoke donde corresponda.
5. Obtener revisión de agentes y humana; documentar /ship, Knowledge y finish/archive.

Conclusión: el siguiente paso acotado —generar y verificar UR-01 con el checker/generador y sus tests— está `LISTO PARA BUILD`. La eliminación de recursos, ownership, compatibilidad y launcher siguen bloqueados y requieren PRs separadas; no hay aprobación blanket de borrado.
