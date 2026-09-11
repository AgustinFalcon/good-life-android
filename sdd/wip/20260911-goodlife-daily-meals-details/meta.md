# GoodLife Android — Daily y Meal Plan details

- Feature id: `20260911-goodlife-daily-meals-details`
- Estado: `spec_review` — no habilita implementación hasta revisión de #7.
- Issue: #7 `feature(android): complete Daily and Meals detail flows`.
- Rama: `feature/android-daily-meals-detail`.
- Idioma de specs: español (es-AR).

## Evidencia y precedencia

La evidencia de comportamiento se tomó de `origin/master` en `3dcedfc` el 2026-09-11:

- `TabRoute.kt` define `DailyDetail(taskId)` y `MealDetail(mealId)`, aunque sus nombres no coinciden con los IDs que originan las tarjetas.
- `DailyTabViewModel.kt` conserva la fecha seleccionada y sólo puede leer el log por fecha; el click diario sigue siendo un no-op.
- `MealsTabViewModel.kt` conserva la fecha seleccionada y sólo puede leer planes por fecha; el click de un plan sigue siendo un no-op.
- `DailyApiService.kt` y `MealPlanApiService.kt` ofrecen lecturas por fecha, no por ID.

La decisión propuesta evita inferir endpoints, datos de recetas, credenciales, registros personales, hosts o rutas locales. Los artefactos SDD del Escritorio no se copian: este repositorio conserva el contrato versionable.

## Relación

- Extiende R-04 del revival Android.
- Depende conceptualmente de las rutas y reglas SDD de los PR abiertos #11 y #14, pero no modifica su implementación.
- No depende de enlaces públicos ni deep links.
