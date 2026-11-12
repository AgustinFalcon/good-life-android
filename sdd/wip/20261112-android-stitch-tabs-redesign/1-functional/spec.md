# Functional Specification — Alinear tabs al diseño Stitch

## Problem Statement

Las tabs Entrenar, Comida y Más no coinciden con el diseño Stitch ni muestran con claridad rutina, macros del día ni perfil. El canvas Stitch [Daily Item Component Variants](https://stitch.google.com/projects/9630221387926338009) ya tiene **todas** las pantallas de producto que vamos a implementar (tabs nuevas + Diario + hidratación + catálogo/detalle de comida + wizards). El HTML/PNG exportado el 2026-11-12 es la fuente visual. Los datos salen solo de `https://good-life.ddns.net/api/v1`. Lo que el diseño muestra y el API no entrega **no se inventa**.

## Objectives

1. Un catálogo cerrado de pantallas: cada una tiene carpeta Stitch, pantalla Android (o “no existe aún”) y origen de datos.
2. Implementar en este orden: **Entrenar → Comida → Más (sin XP)**. Después: hidratación / completar hábitos y XP-rachas-PRs.
3. Reusar wizards y Diario que ya están en la app; Stitch ahí es referencia visual, no un segundo flujo.

## Success Metrics

- Cada superficie de las fases 1–3 se puede señalar a un HTML en `4-implementation/artifacts/stitch/` y a un endpoint (o a “omitido: no hay API”).
- Cero literales de estado en UI: `DifficultyLevel`, `GoalType`, `MealType`, `DailyItemStatus`, `HabitCategory` via `fromWire`.
- Campana ausente. Ranking entre usuarios ausente.

## Scope

### In Scope

Todas las pantallas del export Stitch, clasificadas por fase. Ver [Screen Catalog](#screen-catalog).

### Out of Scope

- Campana / notificaciones.
- FAB “Otros” y atajos Desayuno del modal (rediseño aparte).
- Leaderboard social.
- SQLCipher, cert pinning, guardar password biométrico.
- Calorías objetivo diarias, duración estimada de workout, “Semana X de Y” si no se puede derivar de `startDate`/`endDate`.
- Guardar borrador de sesión: **no hay endpoint y el HTML de E2 no trae ese CTA**.
- iOS / KMP.

## Phased delivery

| Fase | Qué | Cuándo |
|------|-----|--------|
| **1 Entrenar** | Tab `entrenar_rutina_activa` + `entrenar_detalle_de_sesi_n`. Create Routine ya existe: no reescribir el wizard. | Primera implementación |
| **2 Comida** | Tab `comida_vista_diaria` + detalle de comida ya existente. Wizard Create Meal Plan ya existe. | Después de fase 1 |
| **3 Más (cuenta)** | Hub `m_s_hub_de_ajustes`: perfil + **agregar** idioma y biometría. **No agregar** XP/rachas/PRs/exportar (no están en Settings hoy). | Después de fase 2 |
| **4 Después** | Drink Water (`daily_tasks_overview_12`), PATCH habit-logs, filas XP/rachas/PRs y subpantallas. Diario ya está: solo el detalle de hábito numérico. | No en el primer PR |

## Screen Catalog

Fuente visual: `sdd/wip/20261112-android-stitch-tabs-redesign/4-implementation/artifacts/stitch/<carpeta>/code.html` + `screen.png`.  
Sistema: `vitality_flow/DESIGN.md`. No usar `vitality_core` (naranja).

### Contrato wire: `WorkoutResponse.dayOfWeek` es `dayOrder`

El backend (`RoutineMapper`) copia `WorkoutEntity.dayOrder` al campo JSON `dayOfWeek`. **No es ISO weekday.** Diario empareja así (`DailyLogService.generateWorkoutItems`):

1. Filtrar rutina activa por `startDate`/`endDate` (si vienen).
2. Usar `routine.daysOfWeek` como **lista JSON ordenada** (1=lun…7=dom). Si el ISO de hoy no está → **día de descanso**: no hay workout de hoy; no CTA “Empezá”.
3. `index = daysOfWeek.indexOf(isoHoy)`; workouts ordenados por el int `dayOfWeek` del wire (= dayOrder); `workout = workouts[index % workouts.size]`.

Entrenar **debe** usar esa misma regla.

Hoy Android lo rompe: `WorkoutResponse.toDomain` pasa `dayOfWeek` por `toDayOfWeekOrNull()` (1–7 → Monday…; dayOrder>7 → null) y `Routine.daysOfWeek` es `Set<DayOfWeek>` (pierde duplicados; el orden de inserción de un `LinkedHashSet` no es el contrato). Fase 1 **cambia el mapper**: conservar `Workout.dayOrder: Int?` (el int del JSON; null si falta) y `daysOfWeekOrdered` como lista ISO en el orden del wire. No reinterpretar ese int como weekday.

### Fase 1 — Entrenar

| ID | Stitch | Android hoy | Datos | Notas de diseño vs API |
|----|--------|-------------|-------|------------------------|
| E1 | `entrenar_rutina_activa` | `WorkoutsScreen` | `GET /api/v1/routines/active`. Empty `NoRoutine`. Error de red **o** servidor (copy distinto vigente). | Avatar → Más (effect nuevo; Daily no tiene avatar). Sin campana. Selector Lun–Dom = `daysOfWeek` ordenados; el workout de un día marcado = regla dayOrder. **No** “60 min”. “Semana X de Y” solo si hay `startDate` **y** `endDate`. CTA “Empezá tu sesión ahora” solo el workout de **hoy** (si hay). Cards de otros días: CTA **Ver rutina** → el **mismo** `WorkoutDetailScreen`. La tab Entrenar **no** muestra un timer en esas cards. El reloj de sesión vive en el detalle (freeze finish / `durationMinutes`); no es un timer extra de “hoy”. Varios workouts el mismo weekday: zip Diario (un índice); duplicados → `normalizedIndex`. Pull-to-refresh = mismo GET (`OnRefresh` ya existe; la Screen aún no tiene `PullToRefreshBox`). Calentamiento / skeleton / “Sincronizado”: no implementar. |
| E2 | `entrenar_detalle_de_sesi_n` (título **Sesión En Curso**) | `WorkoutDetailScreen` | GET rutina activa; POST `/api/v1/workouts/log/finish` **sin cambiar el contrato de master**. Timer: `DateProvider`. | **Freeze finish** body: `durationMinutes`, `startTime`, `endTime` (sesión real, no estimado del HTML), `notes` (string, nunca omitido) = card “¿Cómo te sentiste hoy?”, `sets[]` con RPE **por set** entero 1–10 o ausente (`PerceivedExertion`). **No** RPE de sesión; **no** 9.5. Checks de serie = UI local; el POST envía **todas** las series (blank → `0.0`/`0`). **No** POST por serie. **No** badge “+50 XP” (el server otorga 50 `WORKOUT_COMPLETED` + 100/PR; el cliente no pinta montos). **No** chip de músculo. `show_chart` = fase 4 `GET /exercises/{id}/history`. “Agregá serie” = set extra en memoria; viaja en el mismo finish (`setNumber` correlativo). Banner = fallo de POST, no de GET. Éxito = `SuccessDialog` Lottie vigente, **no** PopupResult. HTML no tiene “Guardar borrador”. `scheduledTime` de la rutina: mostrar si viene; si null, omitir. |
| E3 | `1._datos_de_la_rutina`, `2._gesti_n_de_workouts`, `3._ejercicios_y_cat_logo`, `ejercicios_del_workout`, `configurar_series`, `resumen_de_rutina`, `4._resumen_y_xito`, `estados_del_cat_logo` | `CreateRoutineScreen` | `GET /exercises`, `GET /muscle-groups`, `POST /routines`, `PATCH /routines/{id}/activate` | Ya construido. No reescribir en fase 1. Overlay crear = `PopupResultComponent`. `estados_del_cat_logo` = empty/error del **catálogo de ejercicios**. `GoalType.GENERAL_HEALTH` existe en backend; Android `fromWire` hoy lo manda a `UNKNOWN` — fase 1 **agrega el caso** al tipo cerrado (chips de E1). |

### Fase 2 — Comida

| ID | Stitch | Android hoy | Datos | Notas |
|----|--------|-------------|-------|-------|
| C1 | `comida_vista_diaria` | `MealsScreen` | `GET /api/v1/meal-plans` **sin** query date; el server ya filtra `isActive=true`. Filtro cliente **igual a Diario** (`DailyLogService.generateMealItems`): ISO weekday ∈ `daysOfWeek` **y** fecha en `[startDate, endDate]` si vienen. `daysOfWeek` vacío **no aplica** (Create ` @NotEmpty`; `contains` de lista vacía nunca matchea). `MealType.fromWire` Unknown se descarta. Macros = suma de esos planes. `scheduledTime` se muestra si viene. `isCustomMeal` se ignora. Completado: `GET /daily-logs/{date}` ítem `MEAL` `referenceId = planId`; PATCH `.../items/{itemId}/status?status=COMPLETED` (query). Si Daily falla, la lista de planes igual (sin chips Completado). | **Bug vigente a corregir en fase 2:** `DailyMealPlanResponseDto.appliesTo` trata lista vacía como “todos los días” — desincroniza Completado vs Diario. No hay meta kcal. No “2.140/2.400” ni “Faltan 260” ni chip “+5 XP” (server otorga 5 `MEAL_*` al PATCH COMPLETED; el cliente no pinta montos). CTA **Registrar comida** → detalle del plan o Create Meal Plan. Agrupar por `MealType` de dominio. |
| C2 | `detalle_de_comida_cl_sico`, `detalle_foco_en_ingredientes`, `detalle_pasos_de_preparaci_n` | `MealDetailScreen` hoy: `GET /meal-plans` + `planId` (macros/foto). **No** hay Retrofit `GET /meals/{id}`. | Backend **sí** tiene `GET /api/v1/meals/{id}` (ingredientes + instrucciones). | Fase 2: detalle de **plan** (macros/foto). Ingredientes/pasos = **fase 2b** + Retrofit `GET /meals/{id}` usando `mealId` del plan. Hoy `DailyMealPlanSummary` **tira** `mealId` (el DTO sí lo trae): conservarlo antes de 2b. No fingir receta. |
| C3 | `paso_1a_cat_logo_de_comidas`, `paso_1b_formulario_de_comida`, `paso_2_agregar_ingredientes`, `paso_2_ingredientes_y_macros`, `paso_3_planificaci_n_de_comidas`, `ajuste_de_cantidad_bottom_sheet`, `crear_ingrediente_bottom_sheet`, `xito_plan_creado` | `CreateMealPlanScreen` | `GET /meals`, `GET /ingredients`, `POST /ingredients`, `POST /meals`, `POST /meal-plans` (`daysOfWeek` no vacío) | Ya construido. Empty/error de catálogo de ejercicios = E3 `estados_del_cat_logo`, no C3. |
| C4 | (sin Stitch) | Android no tiene Retrofit | `GET /favorite-foods`; `POST`/`DELETE /favorite-foods/{ingredientId}` | No fase 2. |

### Fase 3 — Más (sin gamificación)

| ID | Stitch | Android hoy | Datos | Notas |
|----|--------|-------------|-------|-------|
| M1 | `m_s_hub_de_ajustes` | `SettingsScreen` hoy = username + logout. `User` en Room también tiene `email` y `profileImageUrl`. **No** hay filas XP. | `GET /api/v1/me` wire: `id`, `username`→`User.name`, `email`, `profileImageUrl`, `roles`, `createdAt` (roles/createdAt no se muestran). Settings **no** revalida `/me` en cada visita. Logout: `POST /api/v1/logout` (revoca refresh; access hasta expirar; si el POST falla, igual se limpia sesión local — vigente). | Fase 3 **puede mostrar** email/foto de Room. **No agregar** ranking/XP/rachas/PRs ni chip de nivel ni “Exportar mis datos”. **Agregar** idioma persistido (hoy `AppLanguage` = locale del dispositivo) y biometría (hoy el toggle está en **Login**). Foto: Room **sí** guarda la URL local tras `POST /media/upload`; el **servidor no** escribe `UserEntity.profileImageUrl` (no hay PATCH `/me`) → desync al re-login. No promete persistencia de servidor. |
| M2 | (sin Stitch) | `ProfileScreen` | `POST /media/upload` | Se mantiene; no inventar update de perfil. |

### Fase 4 — Después (documentado, no construir ahora)

| ID | Stitch | Datos | Qué falta en Android |
|----|--------|-------|----------------------|
| D1 | `daily_tasks_overview_12` (HTML h1 “Habit Detail” / Drink Water; `<title>` “Habit Progress Detail”) | `GET /daily-logs/{date}` → HABIT. Ítem `referenceId` **ya es** `habitLog.id` (el KDoc de `DailyItem` dice “habitId” y está mal). `currentValue`/`targetValue` del log (`Double?`); `unit` y meta Int en `habitLog.habit`. Guardar: `PATCH /api/v1/daily-logs/habit-logs/{habitLogId}` `{ currentValue, isCompleted, notes? }`. Si `isCompleted` omitido y `currentValue >= targetValue` → complete + Daily COMPLETED + XP server. `isCompleted` explícito gana. | Retrofit **no** tiene este PATCH. Dominio **aplana** progreso a `description` y pierde `currentValue`/`targetValue`/`unit` estructurados (el id del log está en `referenceId`). Conservar esos campos antes de pintar vasos. `HabitCategory`: backend `STUDY`/`WELLNESS`; Android tiene `LEARNING`/`HEALTH`/… — wire desconocido → `UNKNOWN` (no `CUSTOM`). |
| D2 | `daily_item_component_variants`, `daily_tasks_overview_2`, `daily_tasks_overview_3`, `daily_tasks_overview_5`, `daily_tasks_overview_8`, `daily_tasks_overview_11` | `GET /daily-logs/{date}`, `PATCH .../items/{id}/status?status=` | Diario ya existe. |
| D3 | `daily_tasks_overview_6` | DatePicker local | `DatePickerBottomSheet` ya existe. |
| D4 | `daily_tasks_overview_1`, `daily_tasks_overview_4`, `daily_tasks_overview_10` | Detalle Daily | `DailyDetailScreen` existe; labels de `DailyItemType`/`DailyItemStatus`. |
| D5 | `daily_tasks_overview_7`, `daily_tasks_overview_9` | `POST /tasks` | `CreateTaskScreen` ya existe. |
| D6 | (sin Stitch) Create Habit | `POST /habits` | `CreateHabitScreen` ya existe. |
| D7 | Filas XP del hub (diseño en M1, **sin** subpantallas Stitch) | `GET /users/me/experience`, `xp-history`, `level-ups`, `POST level-ups/mark-read`, `GET /levels`, `GET /levels/{category}`, `GET /users/me/streaks`, `GET /habits/{id}/streak`, `GET /users/me/personal-records`, `GET /users/me/workout-stats?from=&to=` (from/to **obligatorios**), `GET /exercises/{id}/history` | No hay Retrofit. Ranking **personal**, no leaderboard. Diseñar subpantallas en Stitch **antes** de codear. |

## User Stories

### Fase 1

- Como usuario con rutina activa cuyos `daysOfWeek` incluyen hoy, veo el workout emparejado por dayOrder y puedo Empezar.
- Como usuario en día de descanso (`daysOfWeek` no incluye hoy), veo la semana pero **no** “Empezá tu sesión ahora”.
- Como usuario sin rutina, veo empty + CTA crear (contrato vigente).
- Como usuario en sesión, edito peso/reps/RPE **por serie** y notas de sesión; Terminar POST freeze de master (todas las series, `notes` siempre). El look sigue E2 **sin** extras HTML (XP, músculo, RPE 9.5, sync por set).

### Fase 2

- Como usuario, elijo el día y veo los planes que pasan el filtro Diario (weekday ∈ lista + rango fechas), con macros sumados, sin meta kcal ni XP.
- Como usuario, abro el detalle del **plan** (macros/foto). Receta (ingredientes/pasos) queda para fase 2b + `GET /meals/{id}`.

### Fase 3

- Como usuario, veo username/email/foto de Room, agrego idioma persistido y biometría, y cierro sesión.
- No veo filas de XP/rachas/PRs (no se implementan).

### Fase 4 (contrato, no build)

- Como usuario con hábito HYDRATION, abro Drink Water, sumo según `habit.unit` (ml vs vaso), PATCH `habit-logs/{referenceId}` (`referenceId` ya es `habitLog.id`) con `currentValue`/`unit` que hay que dejar de aplanar.

## Data origins (cerrado)

| Dato en UI | Origen | Si falta |
|------------|--------|----------|
| Rutina, workouts, ejercicios, sets target | `GET /routines/active` | Empty / error vigentes |
| Dificultad / objetivo | wire → `DifficultyLevel` / `GoalType` | Unknown del tipo |
| Weekdays con entreno | `daysOfWeek` **lista JSON ordenada** (ISO 1–7), no `Set` | Selector vacío / día de descanso |
| Workout asignado a un weekday | `workouts.sortedBy(dayOrder)[indexOf(iso) % size]`; conservar `Workout.dayOrder: Int?` (JSON `dayOfWeek`) | Día de descanso |
| `scheduledTime` rutina/plan | wire `scheduledTime` | Omitir si null |
| `GoalType.GENERAL_HEALTH` | backend enum; agregar caso Android | Hoy cae en UNKNOWN |
| Semana X de Y | `startDate` **y** `endDate` vs hoy | Ocultar |
| Duración 60 min / calentamiento / “Sincronizado” / +50 XP / chip músculo | — | Ocultar |
| Timer de sesión | `DateProvider` local | — |
| Peso/reps hechos | input → POST finish `sets[]` | Semilla = targets; blank → 0.0 / 0 |
| Timer / duración sesión | `DateProvider` → `durationMinutes` + `startTime` + `endTime` | Freeze; no “60 min” de HTML |
| RPE por set | `PerceivedExertion` 1–10 | Ausente = null en POST |
| “¿Cómo te sentiste hoy?” | `FinishWorkoutRequest.notes` (string, siempre) | `""` |
| Planes del día | `GET /meal-plans` (ya `isActive=true`) + weekday ∈ `daysOfWeek` + rango fechas | Empty “Creá tu plan”. Lista vacía de días = no aplica |
| Macros del día | suma de planes que pasan el filtro Diario | 0 si vacía |
| Meta 2400 kcal / +5 XP / +50 XP | XP lo otorga el server (5 meal, 5 habit, 5 task, 50 workout, 100/PR, 25 día perfecto) | No pintar montos en fases 1–3 |
| Completado de comida | Daily `MEAL` `referenceId=planId` | Chip ausente si Daily falla |
| Foto de plan | `mealImageUrl` / `imageUrl` | Placeholder |
| `mealId` del plan | DTO `mealId`; dominio hoy lo tira | Conservar para fase 2b |
| `isCustomMeal` | wire | Ignorar |
| Ingredientes / pasos receta | `GET /meals/{id}` (backend sí, Android no) | Fase 2b; no fingir |
| Perfil | Room (`User.name` = `username` de `/me`) | Placeholder |
| Foto persistida servidor | **no hay** PATCH `/me`; upload no escribe user | No prometer persistencia |
| XP, nivel, rachas, PRs | GET gamification (Android no consume) | Fase 4 + Stitch de subpantallas |
| Vasos de agua | HABIT `referenceId` = `habitLog.id`; current/target/unit hay que dejar de aplanar | Fase 4 |
| Completar ítem diario | `PATCH .../items/{id}/status?status=` | Ya en Daily |
| Catálogo ejercicios | `GET /exercises`, `/muscle-groups` | Create Routine |
| Catálogo comidas/ingredientes | `GET /meals`, `/ingredients` | Create Meal Plan |
| Favoritos | `GET /favorite-foods`; mutaciones `/{ingredientId}` | Fase posterior + Stitch |
| Stats semanales | `GET /users/me/workout-stats?from=&to=` | Fase 4; query obligatorio |

Base URL: `https://good-life.ddns.net/`. Auth: Bearer vigente.

## Business Rules

1. Valor de enumeración desconocido en wire → `Unknown` del tipo; la UI no imprime el string crudo (`MealType`, `DifficultyLevel`, `GoalType`, `PerceivedExertion`, `DailyItemType`, `DailyItemStatus`, `HabitCategory`).
2. MealType cerrado: Desayuno, Almuerzo, Cena, Merienda, Snack + Unknown (se descarta en lista de planes).
3. Completar hábito numérico ≠ PATCH status del Daily item. Fase 4 usa habit-logs; marcar COMPLETED sin `currentValue` no registra vasos.
4. Nutrition streak (backend) exige **todas** las comidas planeadas del día; Android no calcula rachas en fases 1–3.
5. Rest days no rompen training streak (regla de servidor); irrelevante hasta fase 4.
6. Emparejamiento workout↔weekday = `DailyLogService` (lista JSON + dayOrder). Fase 1 deja de usar `toDayOfWeekOrNull` en el int del workout.
7. Finish: freeze master (`durationMinutes`, `startTime`, `endTime`, `notes` siempre, sets completos, RPE por set 1–10). HTML extras no viajan. XP lo aplica el server; el cliente no hardcodea 50/5.
8. `POST /media/upload` no actualiza el user en servidor. Room puede guardar la URL local. Fase 3 no inventa PATCH `/me`.
9. `GET /workout-stats` exige `from` y `to`; no llamar sin rango.
10. `HabitCategory.fromWire`: `HYDRATION` existe. `STUDY`/`WELLNESS` backend → `UNKNOWN` en Android hasta alinear el tipo. No copiar `API_ENDPOINTS.md` obsoleto.
11. Plan de comida: `daysOfWeek` vacío no aplica (Create `@NotEmpty`; Diario `contains`). Fase 2 cambia `appliesTo` para que vacío ≠ todos los días.
12. `GoalType` cerrado incluye `GENERAL_HEALTH` (backend). No dejarlo en UNKNOWN.

## Error handling

- GET tab: banner inline + Reintentá (como el HTML). Pull-to-refresh en Entrenar/Comida.
- POST finish: se queda en Content + banner; no `UiState.Error` de carga.
- Sin conexión: copy voseo “Sin conexión reciente” / “Reintentá”.
- 4xx de validación: mensaje localizado, no “Sin conexión”.
- Logout: si `POST /logout` falla, igual se limpia sesión local (comportamiento vigente a documentar en técnico, no cambiarlo en fase 3 salvo bug).

## Edge Cases

- Sin rutina activa → empty Entrenar, no crash.
- `daysOfWeek` no incluye hoy → descanso; no CTA Empezá; cards de otros días siguen visibles.
- `daysOfWeek.size` ≠ `workouts.size` → `index % workouts.size` (mismo wrap que Diario).
- Workout sin int `dayOfWeek` en wire → `dayOrder` ausente; orden inestable. Fase 1 **agrega** `Workout.dayOrder: Int?` (hoy el dominio solo tiene `dayOfWeek: DayOfWeek?` mal mapeado). No etiquetar “Día {n}” como weekday.
- Meal-plans: filtrar como Diario (weekday ∈ lista **y** rango fechas); no query date. Plan con `daysOfWeek` vacío **no** entra. GET ya viene `isActive=true`.
- Hábito unit “vasos” vs “ml”: Drink Water usa `habit.unit` y `habit.targetValue` (Int) + log `currentValue` (Double). Atajos +100/+250/+500 ml **solo** si unit es volumen; si es vasos, +1.
- Settings: `GET /me` no se revalida en cada visita (Room). Username, no display name.

## Dependencies

- API GoodLife v1 (REST JSON) ya desplegada.
- Stitch export en artifacts (HTML/PNG).
- Features ya en master: Daily SWR, DatePicker, finish-workout, Create Routine/Meal/Habit/Task, PopupResult.

## Risks

- HTML Entrenar/Comida pinta datos que el API no tiene (kcal goal, duración, semana, XP, músculo, RPE 9.5). Mitigación: omitir; freeze finish.
- `WorkoutResponse.dayOfWeek` = dayOrder. Mitigación: regla Diario; tests de emparejamiento.
- Fase 4 XP sin subpantallas Stitch. Mitigación: no codear hasta diseñarlas.
- PATCH habit-logs ausente + `DailyItem` aplana `currentValue`/`targetValue`/`unit` (el id del log ya está en `referenceId`). Mitigación: fase 4, ampliar dominio.
- `POST /media/upload` no persiste foto de perfil. Mitigación: no prometer sync en fase 3.
- Receta Stitch vs Android sin `GET /meals/{id}`. Mitigación: fase 2b explícita.

## Acceptance Criteria

1. Catálogo E1–E3, C1–C4, M1–M2, D1–D7: cada ID mapea a carpeta Stitch (nombre completo) + endpoint real o “omitido/fase posterior”.
2. Fase 1: mapper conserva `dayOrder` + lista ISO; Lun–Dom regla Diario; `GoalType.GENERAL_HEALTH` en el tipo; no campana; no 60 min; no XP pintado; finish freeze (`durationMinutes`/`startTime`/`endTime`/`notes` + RPE por set 1–10).
3. Fase 2: `MealsScreen` filtra como Diario (vacío ≠ todos los días); sin denominador kcal ni +5 XP. Detalle = plan; receta = 2b con `mealId` conservado.
4. Fase 3: Settings **agrega** idioma persistido y biometría; **puede** mostrar email/foto de Room; **no agrega** XP/rachas/PRs/exportar; no promete foto persistida en servidor.
5. Copy ES/EN/PT via `AppLanguage` (fase 3: persistir elección).
6. Tests de tipos cerrados, filtro Diario de meal-plans (vacío ≠ todos los días), y emparejamiento workout↔weekday; no strings mágicos en la vista.

## Review loop (hallazgos ya incorporados)

Rechazos de 2026-11-12 (Android / backend / Stitch) quedan **cerrados** en este documento:

- C2 no afirma Retrofit `GET /meals/{id}` (backend sí, cliente no); `mealId` se conserva para 2b.
- E2 no afirma RPE de sesión; “sentiste” = `notes`; finish incluye `durationMinutes`/`startTime`/`endTime`.
- E1 no trata `workouts[].dayOfWeek` como weekday ISO; dominio guarda `dayOrder` y lista JSON.
- C1 filtra como Diario; `daysOfWeek` vacío **no** es todos los días; `appliesTo` actual es bug.
- M1 no “oculta” filas XP; agrega idioma/biometría; foto Room vs servidor desync.
- C3/D2/D4/D5 usan nombres de carpeta completos; `estados_del_cat_logo` está en E3.
- Favoritos: POST/DELETE `/{ingredientId}`.
- `workout-stats` exige `from`/`to`.
- PATCH item status es query `?status=`.
- HABIT `referenceId` = `habitLog.id`; se pierden valores estructurados, no el id.
- XP montos (50 workout, 5 meal/habit/task, 100/PR, 25 día perfecto) son server-only.

## E2E / LTP

`ltp_enabled: false`. Android no usa LTP Fury. Calidad = unit tests de lógica + compile/lint. Smoke en dispositivo queda fuera de esta spec.
