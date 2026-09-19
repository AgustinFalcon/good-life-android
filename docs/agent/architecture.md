---
type: Architecture
version: recovery-audit-2026-09.2
validated: 2026-09-14
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
navigation buffered Channel/Flow -> GoodLifeNavHost -> type-safe route
network success -> cache; offline read -> Room
```

Koin modules registran APIs, datasources, repositories, use cases y ViewModels. Solo ScreenOwner obtiene ViewModel.

AppRoute navigation uses a bounded Channel exposed as a one-shot Flow: it bridges Splash-before-host without replaying an already consumed action after host recreation. The controller owns no unmanaged coroutine scope; TabRoute effects remain local rendezvous events.

## Tab details

`TabRoute.DailyDetail` carries `itemId` plus an exact ISO date and `TabRoute.MealDetail` carries `planId` plus an exact ISO date. The tab graph resolves the existing date-backed collection, filters it by ID and never invents an endpoint by ID. Their Owners obtain parameterized Koin ViewModels; pure screens render Content, missing, invalid-route and retryable error states. Navigation effects are tab-local rendezvous events with an anti-double-tap guard, and list Owners retain `LazyListState` across detail/back.

`addDailyTabGraph` and `addMealsTabGraph` are internal typed graph boundaries included by `addTabNavGraph`; instrumentation exercises the same routes with deterministic content and no backend.

## Workout detail

`TabRoute.WorkoutDetail` carries only `workoutId`. Its Owner resolves the current active routine through the existing use case, so it has no duplicate API contract, repository mutation or deep-link dependency. The ViewModel exposes loading, content, missing and error states; composables remain pure.

## SDD ownership

`sdd/PROJECT.md` y `sdd/wip/20260911-goodlife-android-revival/` son el contrato de recovery Android versionado. Repo-minology feature 008 conserva la intención de producto transversal; el SDD externo del Desktop es solo insumo histórico.

Room para user/daily cache; TokenManager usa Android storage/crypto según implementación. DateProvider evita reloj directo.

## Coordinación de solicitudes

- Login mantiene una única autenticación activa entre el formulario y biometría. Un segundo intento se rechaza antes de invocar el caso de uso, para evitar efectos de sesión/tokens en paralelo.
- Daily serializa únicamente las mutaciones con un `Mutex`; las lecturas cancelan el job previo, usan un id monotónico y validan que sigan siendo la solicitud y fecha vigentes antes y después de consultar el caso de uso.
- Meals cancela el job de carga previo y usa el mismo versionado. Una respuesta vieja no puede publicar `UiState`, incluso si la fuente no coopera con la cancelación.
## Evitar

No llamar `stringResource` en screens ni inyectar ViewModel en components. No afirmar KMP: es app Android con capas KMP-ready.

