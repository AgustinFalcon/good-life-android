---
type: Architecture
version: recovery-audit-2026-09.1
validated: 2026-09-10
update_when: "Cambien capas, wiring, persistencia o entrypoints."
scope:
  - README.md
  - app/build.gradle.kts
  - app/src/main/java/com/agusstkd/goodlife
  - docs
---

# Architecture

| Ruta | Rol |
|---|---|
| `core/` | Networking, storage, datetime y Result |
| `data/local/` | Room DAOs/entities |
| `data/remote/` | Retrofit APIs/DTOs |
| `data/repository/` | Implementaciones |
| `domain/` | Modelos, ports y use cases |
| `presentation/` | Owners/screens/viewmodels/components/navigation |
| `di/` | Módulos Koin |

## Entrypoints

- `GoodLifeApp` inicializa Koin.
- `MainActivity` arranca `GoodLifeNavHost` en `AppRoute.Splash`.
- `TabNavGraph` contiene Daily/Workouts/Meals/Settings.

```text
Compose Owner -> ViewModel -> use case -> repository -> Retrofit/Room
navigation SharedFlow -> GoodLifeNavHost -> type-safe route
network success -> cache; offline read -> Room
```

Koin modules registran APIs, datasources, repositories, use cases y ViewModels. Solo ScreenOwner obtiene ViewModel.

Room para user/daily cache; TokenManager usa Android storage/crypto según implementación. DateProvider evita reloj directo.

## Coordinación de solicitudes

- Login mantiene una única autenticación activa entre el formulario y biometría. Un segundo intento se rechaza antes de invocar el caso de uso, para evitar efectos de sesión/tokens en paralelo.
- Daily serializa cargas y mutaciones con un `Mutex`; cada carga tiene un id monotónico y valida que siga siendo la solicitud y fecha vigentes antes y después de consultar el caso de uso.
- Meals usa el mismo versionado de carga. Las solicitudes ya obsoletas no inician lectura y una respuesta vieja no puede publicar `UiState`.
## Evitar

No llamar `stringResource` en screens ni inyectar ViewModel en components. No afirmar KMP: es app Android con capas KMP-ready.

