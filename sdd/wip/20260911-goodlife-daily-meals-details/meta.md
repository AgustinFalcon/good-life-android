# GoodLife Android — Daily y Meal Plan details

- Feature id: `20260911-goodlife-daily-meals-details`
- Estado: `in_review` — implementación y gates locales en curso; se requiere aprobación final y CI del PR antes de build-ready.
- Issue: #7 `feature(android): complete Daily and Meals detail flows`.
- Rama: `feature/android-daily-meals-details`.
- Idioma de specs: español (es-AR).
- Baseline de implementación: `origin/master` `7c67804` (2026-09-12).

## Evidencia y precedencia

El backend expone las lecturas autenticadas por fecha ya usadas por ambos tabs; no se inventan lecturas por ID. El detalle filtra el resultado date-backed por ID y preserva la navegación interna del tab. `WorkoutDetail` fusionado es sólo patrón UI/Owner de comparación, no una dependencia contractual.

Los artefactos aquí son la fuente versionable del contrato de #7. No contienen datos de usuario, credenciales, hosts privados ni secretos.

## Evidencia de implementación

- Compilación Kotlin, unit tests, cobertura y lint locales ejecutados en esta rama.
- Gate conectado Gradle en emulador Android API 36: 8/8 tests OK; Daily y Meals (scroll → detalle → back), estados NotFound/InvalidRoute/Retry, Back y fallback accesible.
- El gate CI ejecuta connectedDebugAndroidTest sobre API 35; su resultado se acreditará desde el PR.
