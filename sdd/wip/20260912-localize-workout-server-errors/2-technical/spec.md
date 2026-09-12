# Especificación técnica

- La frontera de sanitización permanece en el ViewModel: `GetActiveRoutineResult.ServerError.message` es detalle técnico y se mapea a `AppLanguage.errorTexts.dataLoadError`.
- No se modifica `GetActiveRoutineUseCase`, Retrofit ni modelos de UI; ambos `Error(message)` siguen representando copy listo para renderizar.
- Workouts no cambia su semántica de carga; Workout Detail conserva cancelación del `loadJob` y el guard `latestRequest`.
- Las pruebas usan `ApiException.ServerException("detalle sensible")` en `FakeRoutineRepository` y afirman igualdad con `Spanish.errorTexts.dataLoadError` y desigualdad con el detalle sensible.
- Se actualiza el KDoc del resultado de dominio para indicar que el mensaje remoto no cruza sin sanitizar a UI.