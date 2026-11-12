# Especificación técnica — Alinear tabs al diseño Stitch

Brownfield. Mismo stack: Compose + MVVM + Koin + Retrofit. No hay Fury/Everest. Tokens visuales: `4-implementation/artifacts/stitch/vitality_flow/DESIGN.md` mapeados al tema Material 3 vigente (`GoodLifeTypography`). Plus Jakarta Sans no se carga en este feature.

Implementación por PRs: **fase 1 → 2 → 3**. Fase 4 solo queda contratada aquí.

Fuente visual: `4-implementation/artifacts/HTML-TO-COMPOSE-REPORT.md`. Contrato de datos: `1-functional/spec.md`.

## Arquitectura (sin capas nuevas)

```
Tab Owner (NavHost ya existente)
  → Screen pura (modifier, uiState, onAction)
  → Tab ViewModel
  → UseCase existente (GetActiveRoutine / GetMealPlans / GetCurrentUser / Logout / FinishWorkout)
  → Repository → RemoteDataSource → Retrofit
```

No `NavController` ni `Context` en el VM. Navigation effects / `ComposeNavigationController` como hoy. No segundo módulo Koin. No reescribir wizards Create Routine / Create Meal Plan / Daily.

## Fase 1 — Entrenar (primer PR)

### Dominio: dejar de tratar `WorkoutResponse.dayOfWeek` como ISO

Hoy `WorkoutResponse.toDomain` usa `dayOfWeek.toDayOfWeekOrNull()` y `RoutineResponseTest` afirma `1 → MONDAY`. Eso es el bug (el int es `dayOrder`).

Cambios:

- `Workout`: agregar `dayOrder: Int?` (null si el wire no manda el int). **Quitar** `dayOfWeek: DayOfWeek?` del workout de GET (ese campo era el bug). Create Routine / `WorkoutDraft` no se tocan. En `WorkoutDetail`, el `dayLabel` que hoy sale de `workout.dayOfWeek` se **omite** (el weekday vive en la tab via pairing, no en el workout).
- `Routine`: conservar `daysOfWeek: Set<DayOfWeek>` para membresía (**Create Routine / `WorkoutDraft` no se tocan**; el draft no tiene `dayOfWeek`). Agregar `daysOfWeekOrdered: List<Int>` = ints ISO **en el orden del JSON** (1–7; inválidos omitidos; duplicados conservados). El emparejamiento usa esta lista, no el Set.
- Mapper: `WorkoutResponse.toDomain` guarda `dayOrder = dayOfWeek` (el int crudo). No pasar por `toDayOfWeekOrNull`.
- `GoalType`: agregar `GENERAL_HEALTH` a `selectable` + `fromWire`. Copy nueva `CreateRoutineTexts.goalGeneralHealth` ES/EN/PT. `WorkoutsTabViewModel.resolveGoalLabel` y `CreateRoutineViewModel` cubren el caso (exhaustive `when`).

Función pura (dominio o `data` mapper testable, **no** en la Screen):

```kotlin
fun workoutForIsoDay(
    daysOfWeekOrdered: List<Int>, // ISO 1–7, orden wire
    workouts: List<Workout>,      // con dayOrder
    isoDay: Int,
): Workout?
```

Regla = `DailyLogService.generateWorkoutItems`:

1. Si `isoDay !in daysOfWeekOrdered` → null (descanso).
2. `index = daysOfWeekOrdered.indexOf(isoDay)`.
3. `sorted = workouts.sortedBy { it.dayOrder ?: Int.MAX_VALUE }`.
4. Si `sorted.isEmpty()` → null.
5. `sorted[index % sorted.size]`.

También filtrar rutina por `startDate`/`endDate` vs **hoy** (igual que Diario). Si hoy está fuera del rango → empty de tab = tratar como sin sesión de hoy; la rutina igual se muestra si GET active la devuelve (el server ya la marca activa).

### GET / pairing vs UI

`GetActiveRoutineUseCase` no cambia de puerto (remote-only, como finish-workout). El VM:

- `OnRefresh` / load → mismo UseCase. Screen **agrega** `PullToRefreshBox` (la action ya existe; Daily ya tiene el box).
- `Success` deja de poner `dayLabel` con `workout.dayOfWeek.isoDayNumber`.
- Nuevo: `selectedIsoDay: Int` (default = ISO de `DateProvider.today()`).
- Nuevo: `todayWorkoutId: Long?` = `workoutForIsoDay(..., isoHoy)?.id`.
- Cards: para cada weekday en `daysOfWeekOrdered`, el workout emparejado. CTA **Empezá tu sesión ahora** solo si `isoDay == isoHoy` y hay workout. Resto de días de entreno: CTA **Ver rutina** → `OnWorkoutClick(id)` igual. Descanso: no card de “empezar”.
- Ambos CTAs navegan al `WorkoutDetail` vigente. La tab **no** pinta timer. El detalle **sí** arranca el reloj de sesión al Content (freeze finish: `durationMinutes`). Eso no es un “timer de hoy” en Entrenar; es el mismo reloj de siempre, también si se abrió con Ver rutina.
- **No** pintar 60 min, calentamiento, “Sincronizado”, campana, badge XP.
- “Semana X de Y”: solo si `startDate != null && endDate != null`. `weekIndex = ((today - start).days / 7) + 1`, `weekTotal = ((end - start).days / 7) + 1`. Si `weekIndex` fuera de `1..weekTotal`, ocultar.
- `scheduledTime` de rutina: mostrar si no null.

`WorkoutsUiAction` existente + `OnSelectDay(iso: Int)` (selector Lun–Dom del HTML E1) + `OnAvatarClick` → **effect nuevo** a tab Más (Daily no tiene avatar). Empty = `NoRoutine`. Error red vs servidor: copy vigente (`connectionError` / `dataLoadError`).

Collections: esta tab usa `List`/`Set` hoy. **No** migrar a `ImmutableList` en este PR (convenio del archivo).

### E2 — Sesión en curso (visual, finish freeze)

No cambiar `FinishWorkoutRequest` / UseCase / `PerceivedExertion`.

- Card “¿Cómo te sentiste hoy?” → `OnNotesChange` ya existente (`notes` de sesión).
- RPE por set 1–10; no 9.5; no RPE de sesión.
- Checks de serie: estado local de UI (no viajan). POST manda todas las series (blank → 0.0/0).
- **Agregá serie** (HTML E2; hoy **no** hay `UiAction`): `OnAddSet(exerciseId)` donde `exerciseId` = `WorkoutExerciseUiModel.id`. Append `WorkoutSetUiModel` con `setNumber = max existente + 1`, inputs vacíos, `id` local negativo (no chocar con ids de server). El finish incluye esas series con `setNumber` correlativo. No POST hasta Terminar.
- `scheduledTime` de la rutina: si el detalle ya tiene la rutina cargada y el campo no es null, mostrarlo; si null, omitir.
- Sin chip músculo, sin +50 XP, sin Guardar borrador, sin POST por serie.
- Éxito = `SuccessDialog` Lottie vigente. **No** `PopupResultComponent` aquí.
- Banner = `finishError` de POST, no `UiState.Error` de GET.
- `show_chart` no se implementa (fase 4).

HTML → Compose: layout de `entrenar_detalle_de_sesi_n` sobre `WorkoutDetailScreen` / `WorkoutSetField`.

### Tests fase 1 (proporcionales, gate 80%)

- `workoutForIsoDay`: PPL `[1,3,5]` + dayOrder 1,2,3 → lun=w1, mie=w2, vie=w3; mar=null. `index % size` si más días que workouts. Lista vacía → null.
- `RoutineResponse.toDomain`: JSON `dayOfWeek: 1` → `dayOrder == 1`, **no** `DayOfWeek.MONDAY`. `9` → `dayOrder == 9` (no null por `toDayOfWeekOrNull`).
- `daysOfWeekOrdered` conserva orden `[5,1,3]`.
- `GoalType.fromWire("GENERAL_HEALTH")` → `GENERAL_HEALTH`; `"HYPERTROPHY"` → `UNKNOWN`.
- `RoutineResponseTest` actual (`1 → MONDAY`) se **reescribe**.
- VM: día de descanso no expone `todayWorkoutId`; refresh no cambia el UseCase.
- `OnAddSet`: `setNumber` correlativo; extra set viaja en el mismo finish; segundo add incrementa.
- Tipos cerrados: tests de `fromWire` que ya existen se conservan; fase 1 agrega `GoalType.GENERAL_HEALTH`; fase 2 el filtro Diario (no rehacer `MealType`/`PerceivedExertion` salvo regresión).

### Archivos fase 1

- `domain/model/training/Routine.kt`, `GoalType.kt`
- `data/remote/dto/response/training/RoutineResponse.kt` + test
- nueva función `workoutForIsoDay` + test
- `WorkoutsUiState` / `WorkoutsUiAction` / `WorkoutsTabViewModel` / `WorkoutsScreen`
- `WorkoutDetailScreen` (layout) + `WorkoutDetailUiAction.OnAddSet` + VM
- `CreateRoutineTexts` + ES/EN/PT + `CreateRoutineViewModel` when
- strings de CTA Ver rutina / Empezá (voseo) en `WorkoutTexts`

No tocar finish UseCase, Daily, Create Routine wizard steps, campana, FAB.

## Fase 2 — Comida (segundo PR)

### Filtro = Diario, no el `appliesTo` vigente

Hoy `DailyMealPlanResponseDto.appliesTo`: `daysOfWeek` vacío = todos los días. Diario (`generateMealItems`) hace `contains` → vacío nunca matchea. Create `@NotEmpty`.

Cambiar `appliesTo`:

1. `days.isEmpty()` → `false`.
2. `isoDay !in days` → `false`.
3. `startDate`/`endDate` como ahora.

Test nuevo: `daysOfWeek = emptyList()` + fecha en rango → `toDailySummary` null.

GET `/meal-plans` ya viene `isActive=true`. No query date.

Macros del día = suma de planes que pasan el filtro. Sin denominador 2400, sin “Faltan”, sin +5 XP.

`scheduledTime`: mostrar si viene. `isCustomMeal`: ignorar.

Completado: `GET /daily-logs/{date}` ítem `MEAL` `referenceId = planId`; PATCH `items/{id}/status?status=`. Daily fail → lista igual, sin chip.

`MealType.fromWire` Unknown se descarta (ya).

### C2 / 2b

Fase 2: detalle de **plan** (macros/foto) como `MealDetailScreen` hoy. No fingir ingredientes.

Antes de 2b: `DailyMealPlanSummary.mealId: Long?` (el DTO ya lo trae; el mapper hoy lo tira).

Fase 2b (PR aparte, después de 2): Retrofit `GET /api/v1/meals/{id}` + layouts `detalle_*`. No en el PR de la tab.

### UI Comida

`MealsScreen` + `PullToRefreshBox` (ya tiene PTR). Date header = patrón Diario + `DatePickerBottomSheet`. Agrupar por `MealType` de dominio. CTA Registrar comida → detalle del plan o Create Meal Plan.

### Tests fase 2

- `appliesTo` vacío / fuera de rango / weekday / Unknown MealType.
- `mealId` sobrevive el mapper.
- VM macros = suma filtrada.

## Catálogo cerrado (ID → HOW)

| ID | Stitch | HOW |
|----|--------|-----|
| E1 | `entrenar_rutina_activa` | Fase 1: mapper + `workoutForIsoDay` + PTR + CTAs |
| E2 | `entrenar_detalle_de_sesi_n` | Fase 1: layout + `OnAddSet` + finish freeze |
| E3 | wizard rutina + `estados_del_cat_logo` | **No reescribir.** Solo `GoalType.GENERAL_HEALTH` en el tipo (chips E1 / create). |
| C1 | `comida_vista_diaria` | Fase 2: `appliesTo` + macros + Daily complete |
| C2 | `detalle_*` | Fase 2 plan-only; **2b** `GET /api/v1/meals/{id}` + `mealId` |
| C3 | `paso_1a_cat_logo_de_comidas` … `xito_plan_creado` | **No reescribir** Create Meal Plan |
| C4 | (sin Stitch) | Fuera de fase 2. `GET /api/v1/favorite-foods`; `POST`/`DELETE /api/v1/favorite-foods/{ingredientId}` |
| M1 | `m_s_hub_de_ajustes` | Fase 3: email/foto Room + idioma + biometría; no XP |
| M2 | (sin Stitch) | **Se mantiene** `ProfileScreen` + `POST /api/v1/media/upload`. No PATCH perfil. Desync servidor documentado. |
| D1 | `daily_tasks_overview_12` | Fase 4 later-PR (PATCH habit-logs) |
| D2 | `daily_item_component_variants`, `daily_tasks_overview_2`, `daily_tasks_overview_3`, `daily_tasks_overview_5`, `daily_tasks_overview_8`, `daily_tasks_overview_11` | **Ya existe Daily.** No reescribir. |
| D3 | `daily_tasks_overview_6` | **Ya existe** `DatePickerBottomSheet`. No tocar. |
| D4 | `daily_tasks_overview_1`, `daily_tasks_overview_4`, `daily_tasks_overview_10` | **Ya existe** `DailyDetailScreen`. No tocar. |
| D5 | `daily_tasks_overview_7`, `daily_tasks_overview_9` | **Ya existe** `CreateTaskScreen`. No tocar. |
| D6 | (sin Stitch) | **Ya existe** `CreateHabitScreen`. No tocar. |
| D7 | filas XP de M1 (subpantallas no en Stitch) | Fase 4 **después** de diseñar en Stitch. GETs: `/api/v1/users/me/experience`, `/api/v1/users/me/xp-history`, `/api/v1/users/me/level-ups`, `POST /api/v1/users/me/level-ups/mark-read`, `/api/v1/levels`, `/api/v1/levels/{category}`, `/api/v1/users/me/streaks`, `/api/v1/habits/{id}/streak`, `/api/v1/users/me/personal-records`, `/api/v1/users/me/workout-stats?from=&to=`, `/api/v1/exercises/{id}/history` |

## Fase 3 — Más sin XP (tercer PR)

`SettingsScreen` hoy: username + logout.

- Mostrar `email` y foto de `User` Room (`GetCurrentUserUseCase`). No revalidar `GET /me` en cada visita.
- **No** filas XP/rachas/PRs/exportar.
- **Agregar** idioma persistido: preferencia local (SharedPreferences **no** encrypted; no mezclar con `TokenManager`). Clave `app_language` = `es`/`en`/`pt`. Al arrancar, Koin (`coreModule` en `AppModule.kt`) resuelve `AppLanguage` como: preferencia si existe, si no locale del dispositivo (hoy). Cambio en Settings recrea/re-emite el holder; **no** guardar idioma en EncryptedSharedPreferences de tokens.
- **Agregar** fila biometría: reusar el toggle de Login (`BiometricLoginHandler` / preferencia ya existente). No guardar password.
- Foto: `POST /media/upload` puede escribir URL en Room (`updateProfileImageUrl`). Servidor **no** persiste `profileImageUrl`. Copy no promete sync. No PATCH `/me`.
- Logout vigente: `POST /logout` y si falla igual se limpia sesión.

## Fase 4 — Contratada, no construir

- Drink Water: PATCH `/daily-logs/habit-logs/{habitLogId}`. `DailyItem.referenceId` **ya es** `habitLog.id`. Ampliar dominio con `currentValue`/`targetValue`/`unit` (hoy aplanados a `description`). Atajos ml vs vaso según `habit.unit`.
- Retrofit nuevo solo en esta fase: habit-logs PATCH; GETs de experience/streaks/PRs/`workout-stats?from=&to=` / `exercises/{id}/history`.
- Subpantallas XP: **diseñar en Stitch antes** de codear. Cliente no pinta montos (server: 50 workout, 5 meal/habit/task, 100/PR, 25 día perfecto).
- `HabitCategory`: alinear `STUDY`/`WELLNESS` vs Android `LEARNING`/`HEALTH` en ese PR. Hasta entonces `fromWire` → `UNKNOWN`.

## HTTP (no se inventa)

| Uso | Método | Notas |
|-----|--------|-------|
| Rutina tab | `GET /api/v1/routines/active` | Ya existe |
| Finish | `POST /api/v1/workouts/log/finish` | Freeze vigente: `routineId`, `workoutId`, `name`, `durationMinutes`, `startTime`, `endTime`, `notes` (cliente siempre envía, `""` si vacío), `exercises[]` (`exerciseMasterId`, `sets[]` con `setNumber`/`weight`/`reps`/`rpe` 1–10 o null) |
| Planes | `GET /api/v1/meal-plans` | Sin query date |
| Completar comida | `PATCH /api/v1/daily-logs/items/{id}/status?status=` | Query |
| Receta 2b | `GET /api/v1/meals/{id}` | Backend sí; Android no hasta 2b |
| Perfil | Room / `GET /api/v1/me` en login | `username`→`User.name` |
| Logout | `POST /api/v1/logout` | |
| Upload | `POST /api/v1/media/upload` | No escribe UserEntity servidor |
| Favoritos | `GET /api/v1/favorite-foods`; `POST`/`DELETE /api/v1/favorite-foods/{ingredientId}` | Fuera de fase 2 |
| Stats | `GET /api/v1/users/me/workout-stats?from=&to=` | Fase 4; query obligatorio |
| Agua | `PATCH /api/v1/daily-logs/habit-logs/{id}` | Fase 4. Body `{ currentValue: Double?, isCompleted: Boolean?, notes? }` |

Base: `https://good-life.ddns.net/`. Bearer vigente.

## Seguridad

Sin secretos nuevos en spec ni evidencia. Idioma ≠ credentials store. Biometría no guarda password (regla de proyecto). No SQLCipher / pinning / campana en este feature.

## Accesibilidad / copy

Voseo. ES/EN/PT vía `AppLanguage`. Labels de `DifficultyLevel` / `GoalType` / `MealType` en el tipo o en texts, no strings mágicos en la Screen. `clickable` con role en CTAs nuevos.

## Performance

Listas de workouts/planes = tamaño de rutina/día (pequeño). PTR no dispara loads paralelos: mismo `latestLoadRequestId` que ya tiene Workouts.

## No hacer

- Campana, FAB, leaderboard, kcal goal, 60 min, XP badges, Guardar borrador, POST por serie, PopupResult en finish.
- Everest/Andes/Coil-as-requirement (la app ya carga imágenes como hoy; no cambiar librería).
- Reescribir Daily / Create Routine / Create Meal Plan en fase 1–3.
- Tratar `Workout.dayOrder` como `DayOfWeek`.
- Documentar `appliesTo` vacío = todos los días.
- `ImmutableList` obligatorio en Workouts (el archivo usa `List`).
- `stateIn` / quitar `init { load() }` de Settings (deuda previa, fuera salvo que el PR de fase 3 toque ese VM).
