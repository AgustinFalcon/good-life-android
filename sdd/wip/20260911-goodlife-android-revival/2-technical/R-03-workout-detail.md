# R-03 — Workout detail contract

> **Delta de finish (2026-11-06):** la sesión real (reloj, duración, RPE, sets editables) está especificada e implementada en `sdd/wip/20261106-android-finish-workout-session/`. Ese feature reconfirma CU-19 y sustituye el POST de plantilla. Carga, notes, Lottie y navegación de este R-03 siguen vigentes.

## Scope

Selecting a workout card opens `TabRoute.WorkoutDetail(workoutId)`. The screen resolves that ID from the authenticated user's existing `GET /api/v1/routines/active` response. Session feedback lives in `WorkoutDetailUiState.Content.notes` (user input) and is sent as non-null `FinishWorkoutDraft.notes` when the user finishes.

## Observable states

- `Loading` while the active routine is read.
- `Content` with workout name, exercises, target sets, editable session `notes`, and `finished` (false until the log is persisted).
- After `FinishWorkoutResult.Success`, `finished` is true and the Screen shows the shared Lottie success dialog, then navigates up.
- `NotFound` when the route ID no longer belongs to the active routine.
- `Error` for network or server failures; a retry repeats the read.

## Boundaries

Finish workout uses the existing `POST /api/v1/workouts/log/finish` contract. Domain ids come from the loaded `Workout`. Target sets se muestran como semilla editable. El POST envía peso, reps y RPE **de la sesión** (CU-19 reconfirmado), más `startTime`/`endTime`/`durationMinutes` del reloj de pantalla. Session notes come from UiState. Celebration reuses `SuccessDialog` / `R.raw.success_animation`; no new Lottie asset.

## Verification

Unit tests cover sorted mapping, target-weight formatting, missing route IDs, server errors, and the typed navigation action emitted by the tab ViewModel. The Android CI gate remains the acceptance evidence.
