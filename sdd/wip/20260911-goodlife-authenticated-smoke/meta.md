# GoodLife Android — authenticated smoke

- Feature id: `20260911-goodlife-authenticated-smoke`
- Estado: `spec_review`.
- Issue: #5.
- Rama: `chore/android-auth-smoke-spec`.
- Idioma: español (es-AR).

## Evidencia observada

La definición se basa en `origin/master` `3dcedfc` y en los contratos versionados del repositorio:

- `SplashViewModel` restaura una sesión válida hacia Main y sin sesión permite continuar a Login.
- `GoodLifeAuthenticator` es el responsable de refresh frente a una respuesta 401.
- `DailyApiService` lee y actualiza el daily log por fecha.
- El proyecto tiene runner instrumentado, pero no hay smoke autenticado redacted registrado.

No se incluyen cuentas, contraseñas, tokens, direcciones de red, identificadores de dispositivo ni datos de usuarios. La API y el dispositivo se tratan como dependencias externas verificadas en el momento de ejecución.
