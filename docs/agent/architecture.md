---
type: Architecture
version: foundation-2026-11-11c
validated: 2026-11-10
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
| `core/` | Networking, storage, datetime, Result y `SessionCleanup` |
| `data/local/` | Room DAOs/entities y `LocalSessionCleanup` |
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

Los puertos de dominio no importan DTOs Retrofit ni `android.net.Uri`. Meal/ingredient custom se crean con drafts; la foto de perfil usa `LocalImageRef` y `ObserveCurrentUserUseCase`. Wire de enums entra por `fromWire` (`UNKNOWN` no se recablea a un caso válido). El toggle Daily PENDING↔COMPLETED vive en el ViewModel; un GET en vuelo no pisa un PATCH más nuevo. Settings navega a Profile con un efecto one-shot. El Authenticator usa `SessionCleanup` en 401 terminal y `TokenRefresher` (no `AuthApiService`). Un 401 de `/token` con `grantType=password` no emite `SessionExpired`. `CheckSessionUseCase` usa `AuthRepository.isLoggedIn` (tokens + usuario). `ActivateRoutineResult.ServerError` no lleva copy de API; Create Habit/Task validan el submit en UiState.

AppRoute navigation uses a bounded Channel exposed as a one-shot Flow: it bridges Splash-before-host without replaying an already consumed action after host recreation. GoodLifeNavHost collects it only while the host lifecycle is STARTED, so buffered actions wait while backgrounded and are consumed once on return. The controller owns no unmanaged coroutine scope; TabRoute effects remain local rendezvous events.

## Tab details

`TabRoute.DailyDetail` carries `itemId` plus an exact ISO date and `TabRoute.MealDetail` carries `planId` plus an exact ISO date. The tab graph resolves the existing date-backed collection, filters it by ID and never invents an endpoint by ID. Their Owners obtain parameterized Koin ViewModels; pure screens render Content, missing, invalid-route and retryable error states. Navigation effects are tab-local rendezvous events with an anti-double-tap guard, and list Owners retain `LazyListState` across detail/back.

`addDailyTabGraph` and `addMealsTabGraph` are internal typed graph boundaries included by `addTabNavGraph`; instrumentation exercises the same routes with deterministic content and no backend.

Daily and Meals headers open the shared `DatePickerBottomSheetComponent`. `DailyTabViewModel`, `MealsTabViewModel` and `WorkoutsTabViewModel` reload on resume after the first composition (`hasResumedOnce`) so popping a create flow refreshes the list without changing day. Daily `completionRate` is completed items / total on the loaded list. Daily and Meals wrap the list in `PullToRefreshBox`.

## Workout detail

`TabRoute.WorkoutDetail` carries only `workoutId`. Its Owner resolves the current active routine through the existing use case, so it has no duplicate API contract, repository mutation or deep-link dependency. The ViewModel exposes loading, content, missing and error states; composables remain pure.

User intents are `WorkoutDetailUiAction`: `OnRetry`, `OnNotesChange`, `OnSetFieldChange` (`WorkoutSetField` Weight/Reps/Rpe) and `OnFinishWorkout`. Content holds editable session `notes` (seed empty), editable set inputs (seeded from targets, labelled via `WorkoutSetField.label` + `WorkoutTexts` ES/EN/PT), `isFinishing`, `finishError` and `finished`. The ViewModel starts the session clock with `DateProvider.now()` when it publishes Content and ends it on Finish; `durationMinutes` is that interval coerced to at least 1. Finish parses Content inputs into `FinishWorkoutDraft` (RPE via `PerceivedExertion.fromWire`); exercise notes still come from `WorkoutExercise.notes`. GET failures remain `Error`/`NotFound`; POST 400/5xx/unknown stay on Content with `WorkoutTexts.finishFailed`; network is `connectionError`; empty sets are `noExercises`. On `FinishWorkoutResult.Success` the ViewModel sets `Content.finished = true`; the Screen shows the existing `SuccessDialog` Lottie (`R.raw.success_animation`) and then calls Owner `onNavigateUp`. Domain and data are wired (`FinishWorkoutUseCase` → `WorkoutLogRepository` → `POST /api/v1/workouts/log/finish`). The Screen never holds a `NavController`. `GetActiveRoutineUseCase` stays remote-only; cache is `loadedWorkout` / `loadedRoutineId` on the ViewModel.

Daily items keep workout facts (`routineName`, `exerciseCount`, `dayOrder`) on the domain model and Room v3 cache. Copy is applied in the ViewModel with `WorkoutTexts`. `GET /api/v1/meal-plans` has no `date` query; `MealPlanRepositoryImpl` recorta al día.

Contract: `sdd/wip/20261106-android-finish-workout-session/` (CU-19 reconfirmed against R-03).

## SDD ownership

`sdd/PROJECT.md` y `sdd/wip/20260911-goodlife-android-revival/` son el contrato de recovery Android versionado. Repo-minology feature 008 conserva la intención de producto transversal; el SDD externo del Desktop es solo insumo histórico.

Room para user/daily cache; TokenManager usa Android storage/crypto según implementación. DateProvider evita reloj directo.

## Coordinación de solicitudes

- Login mantiene una única autenticación activa entre el formulario y biometría. Un segundo intento se rechaza antes de invocar el caso de uso, para evitar efectos de sesión/tokens en paralelo.
- Daily serializa únicamente las mutaciones con un `Mutex`; las lecturas cancelan el job previo, usan un id monotónico y validan que sigan siendo la solicitud y fecha vigentes antes y después de consultar el caso de uso.
- Meals cancela el job de carga previo y usa el mismo versionado. Una respuesta vieja no puede publicar `UiState`, incluso si la fuente no coopera con la cancelación.
## Evitar

No llamar `stringResource` en screens ni inyectar ViewModel en components. No afirmar KMP: es app Android con capas KMP-ready.

