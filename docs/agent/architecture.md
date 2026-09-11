---
type: Architecture
version: recovery-audit-2026-09
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

## Workout detail

`TabRoute.WorkoutDetail` carries only `workoutId`. Its Owner resolves the current active routine through the existing use case, so it has no duplicate API contract, repository mutation or deep-link dependency. The ViewModel exposes loading, content, missing and error states; composables remain pure.

## SDD ownership

`sdd/PROJECT.md` y `sdd/wip/20260911-goodlife-android-revival/` son el contrato de recovery Android versionado. Repo-minology feature 008 conserva la intención de producto transversal; el SDD externo del Desktop es solo insumo histórico.

Room para user/daily cache; TokenManager usa Android storage/crypto según implementación. DateProvider evita reloj directo.

## Evitar

No llamar `stringResource` en screens ni inyectar ViewModel en components. No afirmar KMP: es app Android con capas KMP-ready.

