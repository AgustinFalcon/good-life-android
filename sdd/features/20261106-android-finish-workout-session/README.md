# Finish Workout — sesión real

## Resultado

El detalle de workout registra una sesión real al terminar: reloj `DateProvider`, `durationMinutes` ≥ 1, sets editables (peso/reps/RPE vía `PerceivedExertion`), anti doble tap y error de POST en `Content` (`finishFailed`). Reconfirma CU-19.

## Alcance incluido

- Tipo cerrado `PerceivedExertion` (`fromWire` + Absent/Unknown).
- Sets de sesión editables; targets del plan solo como semilla.
- Fallo de GET sigue en `Error`/`NotFound`; fallo de POST no borra Content.
- Copy ES/EN/PT vía `AppLanguage`.

## Evidencia

- PRs [#72](https://github.com/AgustinFalcon/good-life-android/pull/72) y [#73](https://github.com/AgustinFalcon/good-life-android/pull/73) mergeados a `master`.
- Commit de cimientos `36abdd5` (sesión, dominio cerrado, auth/session).
- Unit tests `:app:testDebugUnitTest` en verde antes del push.

## Límite de promoción

Campana, FAB, smoke autenticado instrumentado y firma/distribución siguen fuera de este cierre. No se bumpó versión.
