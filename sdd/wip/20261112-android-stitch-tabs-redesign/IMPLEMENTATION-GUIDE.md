# Guía para implementar — tabs Stitch

Leé esto antes de tocar Kotlin. Specs aprobadas:

- Funcional: `1-functional/spec.md`
- Técnica: `2-technical/spec.md`
- Tareas: `3-tasks/tasks.json`
- Visual: `4-implementation/artifacts/HTML-TO-COMPOSE-REPORT.md`
- HTML/PNG: `4-implementation/artifacts/stitch/<carpeta>/`

Sistema visual: `stitch/vitality_flow/DESIGN.md` (menta `#1EC691`). Mapear al tema Material 3 actual. **No** cargar Plus Jakarta Sans. **No** usar `vitality_core` (naranja).

## GitFlow (este repo)

No hay `develop`. Flujo: `feature/*` → PR → `master`.

| Rama | Qué codeás | Tareas |
|------|------------|--------|
| `feature/android-entrenar-stitch-tab` | **Empezá acá** | TASK-001 … TASK-006 |
| `feature/android-comida-stitch-tab` | Después del merge de Entrenar | TASK-007 … TASK-010 |
| `feature/android-mas-settings-hub` | Después del merge de Comida | TASK-011 … TASK-013 |

Comida y Más nacen del mismo commit de SDD. Cuando Entrenar mergee a `master`:

```bash
git checkout feature/android-comida-stitch-tab
git rebase origin/master
```

Fase 4 (agua / XP / rachas / PRs): **sin rama** hasta tener subpantallas Stitch.

Cada PR: issue + CHANGELOG Unreleased + tests. No bump de versión. No campana. No FAB.

## Cómo arrancar (árbol sucio en master)

`master` local tiene ruido CRLF + cambios que **no** van en este feature (PopupResult, `.idea`, etc.). Las ramas de feature **no** los incluyen.

```bash
cd front-end/Android
git fetch origin
git checkout feature/android-entrenar-stitch-tab
# si git se queja del working tree:
git stash push -u -m "local-wip-not-stitch"
git checkout feature/android-entrenar-stitch-tab
```

No commitees `.idea/*` ni el zip `stitch_daily_item_component_variants.zip` (el HTML/PNG ya están extraídos).

## Fase 1 — Entrenar (orden de código)

### 1. `GoalType.GENERAL_HEALTH` (TASK-001)

`GoalTypeTest.selectable` hoy espera size **5** → pasa a **6**. Copy `goalGeneralHealth` en ES/EN/PT. El `when` de `CreateRoutineViewModel` y `WorkoutsTabViewModel` tiene que ser exhaustivo.

### 2. El bug `dayOfWeek` (TASK-002) — lo más importante

Backend (`RoutineMapper`): JSON `workouts[].dayOfWeek` = `WorkoutEntity.dayOrder` (1, 2, 3…), **no** ISO.

Hoy Android hace `toDayOfWeekOrNull()` y `RoutineResponseTest` afirma `1 → MONDAY`. **Ese test se reescribe.**

```
Workout.dayOrder: Int?          // el int del JSON
Routine.daysOfWeek: Set<DayOfWeek>          // no tocar (Create Routine)
Routine.daysOfWeekOrdered: List<Int>        // orden del JSON, ISO 1–7
```

Callers de `Workout(..., dayOfWeek = DayOfWeek.MONDAY, ...)` en tests se rompen a propósito. Fixtures: `dayOrder = 1`.

`WorkoutDetailViewModel.content` hoy:

```kotlin
dayLabel = workout.dayOfWeek?.let { texts.dayFormat.formatArgs(it.isoDayNumber) }
```

Eso **se omite**. El test que espera `"Día 1"` se actualiza. El weekday vive en la tab (pairing), no en el workout.

### 3. Pairing = Diario (TASK-003)

Copia la regla de `DailyLogService.generateWorkoutItems`:

1. `iso !in daysOfWeekOrdered` → descanso (`null`)
2. `index = daysOfWeekOrdered.indexOf(iso)`
3. `workouts.sortedBy { dayOrder ?: Int.MAX_VALUE }[index % size]`

PPL Lun/Mie/Vie + dayOrder 1,2,3 → lun=w1, mie=w2, vie=w3. Martes = null.

Función pura, **no** en la Screen. Archivo sugerido: `domain/model/training/RoutineDayAssignment.kt`.

### 4. Tab Entrenar (TASK-004)

HTML: `artifacts/stitch/entrenar_rutina_activa/code.html` + `screen.png`.

- `OnRefresh` ya existe; **falta** `PullToRefreshBox` en `WorkoutsScreen` (Daily ya lo tiene).
- `OnSelectDay(iso)` + `OnAvatarClick` → effect **nuevo** a tab Más (Daily no tiene avatar).
- CTA **Empezá tu sesión ahora** solo si el ISO de hoy tiene workout.
- Otros días: **Ver rutina** → mismo `OnWorkoutClick` / `WorkoutDetail`.
- La tab **no** pinta timer. El detalle sí (freeze finish / `durationMinutes`).
- No 60 min, no campana, no XP, no calentamiento, no “Sincronizado”.
- Semana X de Y solo con `startDate` **y** `endDate`.
- Esta carpeta usa `List`, no migres a `ImmutableList`.

### 5. Sesión en curso (TASK-005)

HTML: `artifacts/stitch/entrenar_detalle_de_sesi_n/code.html`.

**Freeze** de `POST /api/v1/workouts/log/finish` (master):

- `routineId`, `workoutId`, `name`, `durationMinutes`, `startTime`, `endTime`
- `notes` string **siempre** (`""` si vacío) = card “¿Cómo te sentiste hoy?” (`OnNotesChange`)
- `sets[].rpe` 1–10 o null (`PerceivedExertion`) — **no** RPE de sesión, **no** 9.5
- Checks de serie = UI local; el POST manda **todas** las series (blank → `0.0` / `0`)

Nuevo: `WorkoutDetailUiAction.OnAddSet(exerciseId)` (`exerciseId` = `WorkoutExerciseUiModel.id`). `setNumber = max+1`, `id` local negativo. No POST hasta Terminar.

Éxito = `SuccessDialog` Lottie. **No** `PopupResultComponent` acá.

### Gate fase 1

```bash
./gradlew :app:testDebugUnitTest --tests '*GoalType*' --tests '*RoutineResponse*' --tests '*RoutineDayAssignment*' --tests '*WorkoutsTab*' --tests '*WorkoutDetailViewModel*'
```

## Fase 2 — Comida (después del merge)

Bug vigente: `DailyMealPlanResponseDto.appliesTo` trata `daysOfWeek` vacío como “todos los días”. Diario hace `contains` → vacío nunca entra. Create `@NotEmpty`.

Cambio: `days.isEmpty() → false`. Conservar `mealId` en `DailyMealPlanSummary`.

HTML: `comida_vista_diaria`. Sin 2400 kcal, sin “Faltan”, sin +5 XP.

Detalle = plan (macros/foto). **No** `GET /meals/{id}` en este PR (fase 2b).

## Fase 3 — Más (después de Comida)

HTML: `m_s_hub_de_ajustes`.

- Mostrar email/foto de Room. No revalidar `/me` cada visita.
- **Agregar** idioma persistido (`app_language` en SharedPreferences **no** encrypted; Koin `coreModule`).
- **Agregar** biometría (hoy el toggle está en Login).
- **No agregar** XP / rachas / PRs / exportar.
- `POST /api/v1/media/upload` no escribe el user en servidor. No inventar PATCH `/me`.

## No hacer (ninguna fase de estas ramas)

- Campana / notificaciones
- FAB Otros
- Leaderboard social
- Inventar kcal goal, 60 min, badges XP
- Guardar borrador
- POST por serie
- Reescribir Create Routine / Create Meal Plan / Daily
- SQLCipher, cert pinning, guardar password biométrico

## Fase 4 (documentada, no codear ahora)

Drink Water: `daily_tasks_overview_12`. PATCH `/api/v1/daily-logs/habit-logs/{id}` `{ currentValue, isCompleted, notes? }`. `DailyItem.referenceId` **ya es** `habitLog.id`; hay que dejar de aplanar `currentValue`/`unit`.

XP: montos los pone el server (50 workout, 5 meal/habit/task, 100/PR, 25 día perfecto). El cliente no los pinta hasta tener pantallas Stitch.
