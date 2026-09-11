# R-03 — Workout detail contract

## Scope

Selecting a workout card opens `TabRoute.WorkoutDetail(workoutId)`. The screen is read-only and resolves that ID from the authenticated user's existing `GET /api/v1/routines/active` response.

## Observable states

- `Loading` while the active routine is read.
- `Content` with workouts, exercises, optional notes, and target sets, sorted by their backend order fields.
- `NotFound` when the route ID no longer belongs to the active routine.
- `Error` for network or server failures; a retry repeats the read.

## Boundaries

This slice introduces no endpoint, persistence, deep link, routine mutation, or workout logging. It only makes the existing typed route and existing active-routine contract useful.

## Verification

Unit tests cover sorted mapping, target-weight formatting, missing route IDs, server errors, and the typed navigation action emitted by the tab ViewModel. The Android CI gate remains the acceptance evidence.
