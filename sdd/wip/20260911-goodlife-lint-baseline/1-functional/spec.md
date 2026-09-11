# Especificación funcional — política de lint

## Objetivo

Transformar las 60 advertencias actuales en deuda visible, priorizada y reducible, evitando que cambios nuevos agreguen advertencias sin revisión.

## Política propuesta

- Lint sin errores sigue siendo obligatorio.
- Cada warning existente tiene dueño, clase y plan; no se acepta una baseline opaca que silencie todo.
- Los PRs de producto no mezclan upgrades grandes, borrado masivo de recursos ni cambios de manifest no relacionados.
- Un warning nuevo requiere fix en la misma PR o una excepción explícita, temporal y revisada.

## Priorización

1. **Seguridad/compatibilidad:** `DataExtractionRules`, `OldTargetApi`, icono monocromo y recursos v26.
2. **Correctitud/consistencia UI:** `ModifierParameter`, `IconLocation`, `RedundantLabel`, `UseTomlInstead`.
3. **Higiene de recursos:** `UnusedResources`, empezando por recursos propiedad de la app y excluyendo compatibilidad hasta tener evidencia.
4. **Upgrade de plataforma:** `GradleDependency` y `AndroidGradlePluginVersion`, segmentados por familia con regresión completa.

## Criterios de aceptación

- El baseline declara las 60 advertencias por regla/clase y se recalcula con una tarea reproducible.
- Una PR nueva no puede aumentar el conjunto de warnings sin una decisión explícita y visible.
- Las excepciones tienen justificación, owner y fecha/condición de retiro; no se usan para errores.
- Cada reducción de warning se verifica con lint y, si afecta UI/manifest/build, con la validación proporcional.

## Fuera de alcance

Actualizar todas las dependencias de una vez, introducir un baseline que suprima errores, borrar recursos generados sin trazabilidad o bloquear una feature por deuda no relacionada.
