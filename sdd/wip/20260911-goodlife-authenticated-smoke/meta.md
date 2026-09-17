# GoodLife Android — authenticated smoke

- Feature id: `20260911-goodlife-authenticated-smoke`
- Estado: `in_progress`.
- Issue: #5.
- Rama: `chore/android-auth-smoke-evidence`.
- Idioma: español (es-AR).

## Evidencia observada

La definición se basa en `origin/master` `3dcedfc` y en los contratos versionados del repositorio:

- `SplashViewModel` restaura una sesión válida hacia Main y sin sesión permite continuar a Login.
- `GoodLifeAuthenticator` es el responsable de refresh frente a una respuesta 401.
- `DailyApiService` lee y actualiza el daily log por fecha.
- El proyecto tiene runner instrumentado, pero no hay smoke autenticado redacted registrado.

No se incluyen cuentas, contraseñas, tokens, direcciones de red, identificadores de dispositivo ni datos de usuarios. La API y el dispositivo se tratan como dependencias externas verificadas en el momento de ejecución.


## Ejecución parcial 2026-09-17

La evidencia redactada en 4-implementation/evidence-20260917.md confirma instalación y arranque en dispositivo físico. El smoke queda bloqueado/incompleto: no se afirmaron login final, Daily, refresh ni logout sin evidencia controlada.
