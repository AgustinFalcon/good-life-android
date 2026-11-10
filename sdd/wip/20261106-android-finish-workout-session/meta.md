# Feature Meta — android-finish-workout-session

**feature_name**: android-finish-workout-session
**date_prefix**: 20261106
**created_at**: 2026-11-06
**project_mode**: brownfield
**execution_mode**: express
**project_type**: production
**spec_language**: es
**stack_profile**: mobile
**application**: good-life-android

## Status

| Phase | Status |
|-------|--------|
| 1 — Functional Spec | ✅ Approved |
| 2 — Technical Spec | ✅ Approved |
| 3 — Tasks | ✅ Approved (express sequential) |
| 4 — Implementation | ✅ Built (PR `feature/android-finish-workout`; `/sdd.finish` archive pending) |

stages:
  functional:
    status: approved
    approved_by: Agustin
    approved_at: 2026-11-06T17:11:21Z
  technical:
    status: approved
    approved_by: Agustin
    approved_at: 2026-11-06T17:11:21Z

## Context

Delta de R-03: el detalle de workout ya puede POST `/api/v1/workouts/log/finish` con sets **planificados**, `notes` de sesión, `durationMinutes = 0` y `startTime == endTime`. Este feature documenta la sesión **real**: reloj de inicio/fin, duración en minutos, RPE y sets editables (peso/reps), más robustez del POST (sin doble tap, sin borrar Content al fallar).

Kit usado: Desktop `C:\Users\agustin\Desktop\skills\sdd-kit` (v1.8.0). `~/.sdd-kit` no está instalado; `detect-stack.sh` no existe en ese kit. `check-frontend-skill.sh` se ejecutó (BOM en shebang; tras strip, exit 0). `/sdd.spec --approve` 2026-11-06: `validate-functional.sh` / `validate-technical.sh` ausentes en este kit; `validate-spec-conflicts.sh` advisory (false-positive `NavController` vs daily-meals, dismissed).

## Brownfield Context

- Project conventions: `sdd/PROJECT.md` (Android). Desktop `../../sdd` es histórico; CU-19 se **reconfirma** aquí.
- Related: `sdd/wip/20260911-goodlife-android-revival/2-technical/R-03-workout-detail.md`
- Related: `sdd/wip/20260912-localize-workout-server-errors`
- Related: `sdd/wip/20260918-goodlife-workouts-empty-contract`
- Repo docs: `docs/agent/architecture.md`, `docs/agent/contracts.md`, `CHANGELOG.md`
- API: `POST /api/v1/workouts/log/finish` (`FinishWorkoutRequest`)

## Conflict resolution (recorded)

| Source | Claim | This spec |
|--------|--------|-----------|
| R-03 vigente | POST usa **planned sets** del `Workout` cargado; notes de UiState | Conserva notes, Lottie, `onNavigateUp`, ids de dominio. **Sustituye** “planned sets as POST body” por **targets como valor inicial editable**. |
| CU-19 histórico (`../../sdd/specs/functional-spec.md`) | POST con peso, reps y RPE **reales** | Reconfirmado como objetivo de producto Android. |
| DTO backend | `startTime`, `endTime`, `durationMinutes`, `rpe` | Campos obligatorios del request; el reloj de UI alimenta los tres de tiempo. |
| R-03 `Error` | Fallo de **lectura** de rutina; Retry relee | Se mantiene. Fallo de **POST finish** no usa ese `Error`. |
| `GetActiveRoutineUseCase` | GET remoto cada `load()` | Caché = `loadedWorkout` / `loadedRoutineId` en el VM, no el UseCase. |
| `FinishSetRequest.weight/reps` | Double/Int no null | Semilla UI desde targets; parseo de Content; blank → `0.0`/`0` (POST vigente). |

## Relationship check

relationship_check:
  dismissed_conflicts:
    - sha256:7ec910de9d0f26a94a3a44984a30223f4432f0663910d2d1456ee5ecfd3adcb2

## Sensitive-data policy

Sin credenciales, tokens, ni logs de sesión en evidencia futura.
