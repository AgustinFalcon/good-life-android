# HTML → Compose — pre-implementation report

Skill: mobile-screen-from-html. HTML es spec visual, no código a traducir.

Orden de build (producto): Fase 1 Entrenar → Fase 2 Comida → Fase 3 Más sin XP. Hidratación y XP = fase 4.

Confirmación de spec funcional pendiente. No tocar Kotlin hasta `/sdd.spec functional --approve` y luego technical/plan.

Target: Android Jetpack Compose, módulo `:app`, package `com.agusstkd.goodlife.presentation.screen.tabs`.

Design system: tokens de `stitch/vitality_flow/DESIGN.md`. Preferir componentes existentes (`ButtonComponent`, `DatePickerBottomSheet`, `PopupResultComponent`, `DailyItemCard`). No agregar fuentes ni dependencias nuevas sin OK. Plus Jakarta Sans no está en la app; mapear a la type scale Material 3 vigente (`GoodLifeTypography` / tema actual) hasta que se decida cargar la fuente.

Copy: voseo rioplatense. Strings en resources ES/EN/PT, no hardcode.

Datos: todo dinámico salvo chrome del sistema (status bar del HTML se ignora; usa el de Android).

---

## Screen: Entrenar — Rutina Activa

Target: Android Compose  
Existing: `WorkoutsScreen` / `WorkoutsTabViewModel`

Component tree:
- TopAppBar: título "Entrenar" + avatar (navega a Más/Perfil). **Sin campana.**
- Inline sync chip (pull-to-refresh)
- Banner error/offline + Reintentá
- Hero card rutina activa: nombre, frecuencia, chips dificultad/objetivo (**sin** duración 60 min)
- Selector Lun–Dom = `routine.daysOfWeek` (ISO 1–7). Workout de cada día marcado = `workouts.sortedBy(dayOrder)[indexOf(iso) % size]`. El JSON `dayOfWeek` **es** dayOrder, no weekday.
- Sección "Workouts de la semana": card de **hoy** (si el ISO está en `daysOfWeek`) + CTA Empezá; otros días de entreno CTA **Ver rutina** (mismo detalle, sin timer de “hoy”). Día de descanso: no CTA Empezá.
- Empty CTA "Creá tu rutina" (estado NoRoutine ya existe)
- NavBar 4 tabs: Diario / Entrenar / Comida / Más

Design-system mapping:
- Header → TopAppBar / existing tab header
- Hero / workout cards → nuevas cards o evolución de las de `WorkoutsScreen`
- Day selector → Row de chips (no calendario)
- CTA → `ButtonComponent`
- Banner → error container existente
- Refresh → `PullToRefreshBox` (ya en Daily)

Assumptions:
- "Favoritos / más usados" del prompt original no está en este HTML; no inventarlo.
- Avatar del header abre Más o Perfil, no notificaciones.
- Día activo del selector = ISO de hoy o el día que el usuario toca. Fase 1 cambia el mapper: `Workout.dayOrder: Int` + `daysOfWeek` lista JSON. **No** `toDayOfWeekOrNull` sobre el int del workout.
- Calentamiento / skeleton / “Sincronizado al instante”: no implementar.
- Empty = `NoRoutine`. Error de red **y** de servidor tienen copy distinto vigente.

Files to create/update:
- `presentation/screen/tabs/workout/WorkoutsScreen.kt`
- `WorkoutsUiState.kt` / `WorkoutsUiAction.kt` / owner / VM (extender, no duplicar)
- strings ES/EN/PT
- tests de estado vacío / error / rutina activa

---

## Screen: Comida — Vista diaria

Target: Android Compose  
Existing: `MealsScreen`

Component tree:
- TopAppBar "Comida" + avatar
- Date header (ayer / hoy / mañana + abrir `DatePickerBottomSheet`)
- Card macros: calorías **planificadas** (suma de planes `appliesTo`) + 3 píldoras; **sin** denominador 2400 ni “Faltan 260” ni +5 XP
- Lista "Planes de hoy" agrupada por `MealType` de dominio (Unknown se descarta)
- CTA "Registrá comida" → detalle del plan o Create Meal Plan (no hay POST de comida suelta)
- Empty "¿Tenés un día libre o viaje?" + Creá tu plan
- NavBar 4 tabs

Design-system mapping:
- Date header → el mismo patrón de Diario
- Macros → evolución de `MacrosSummaryCard`
- Meal rows → cards con foto + estado Completado / Pendiente
- MealType → tipo cerrado existente, no strings mágicos

Assumptions:
- Filtro cliente = misma regla que Diario: weekday ∈ `daysOfWeek` **y** rango fechas. `daysOfWeek` vacío **no aplica** (Create `@NotEmpty`). El `appliesTo` vigente (vacío = todos los días) es un bug a corregir en fase 2.
- Completado = Daily `MEAL` `referenceId=planId`; PATCH `.../items/{id}/status?status=`. Si Daily falla, lista de planes igual.
- Fotos: URL del plan; placeholder si falta.
- Ingredientes/pasos de Lote C = fase 2b (`GET /meals/{id}` no está en Retrofit).

Files to create/update:
- `presentation/screen/tabs/meals/MealsScreen.kt` (+ UiState/UiAction/owner/VM)
- strings
- tests de macros y agrupación por MealType

---

## Screen: Entrenar — Sesión En Curso

Target: Android Compose  
Existing: `WorkoutDetailScreen` (finish freeze en master: sets + `notes` + RPE **por set**)

Component tree:
- AppBar back + "Sesión En Curso" + avatar
- Chip día/semana + timer (`DateProvider`)
- Banner error de **POST finish**, no de GET ni de “POST serie”
- Por ejercicio: notas, filas set (serie / peso / reps / RPE 1–10 / check local). **Sin** chip músculo.
- "Agregá serie" = set extra en memoria; viaja en el mismo finish (`setNumber` correlativo)
- Card "¿Cómo te sentiste hoy?" → `FinishWorkoutRequest.notes` (string, siempre; `""` si vacío). **No** es RPE de sesión.
- Bottom bar: **solo** Terminar workout. HTML **no** tiene Guardar borrador. **Sin** badge +50 XP.

Design-system mapping:
- Sets → `WorkoutSetField` vigente (peso/reps/RPE)
- Terminar → POST finish freeze + `SuccessDialog` Lottie (no PopupResult en esta pantalla)
- Timer → start al Content, end al Terminar; `durationMinutes` derivado

Assumptions:
- Checks de serie = UI local. El POST manda **todas** las series (blank → `0.0`/`0`). No POST por serie.
- RPE entero 1–10 o ausente (`PerceivedExertion`). No 9.5. No RPE de sesión.
- `show_chart` / historial por ejercicio = fase 4.
- No reescribir el body del POST finish.

Files to create/update:
- `presentation/screen/tabs/workout/detail/WorkoutDetailScreen.kt` (+ UiState/UiAction)
- tests de set completado vs en curso vs error de serie

---

## Screen: Más — Hub de Ajustes

Target: Android Compose  
Existing: `SettingsScreen` (perfil + logout) y `ProfileScreen` (foto)

Component tree (fase 3):
- TopAppBar "Más" + avatar
- Card perfil: foto, **username** (`User.name` de Room, viene de `/me.username`), email. **Sin** chip nivel/XP.
- Preferencias: **agregar** idioma (persistencia nueva; hoy `AppLanguage` es locale del dispositivo) y **agregar** biometría (hoy el toggle está en Login).
- **No agregar** Ranking/XP/Rachas/PRs ni “Exportar mis datos”.
- Cuenta: Cerrar sesión (`POST /logout`; si falla, igual se limpia sesión local — vigente)
- NavBar 4 tabs

Design-system mapping:
- Toggle biometría → mover/reusar el de Login
- Logout → acción existente
- Foto: `POST /media/upload` **no** escribe `profileImageUrl` en user (no hay PATCH `/me`). No prometer persistencia.

Assumptions:
- Settings lee Room, no revalida `GET /me` en cada visita.
- Campana ausente a propósito.
- Idioma persistido ≠ singleton de locale del sistema; hay que diseñar almacenamiento en técnico.
- Filas XP = fase 4 + Stitch de subpantallas; `GET /users/me/workout-stats` exige `from` y `to`.

Files to create/update (fase 3):
- `presentation/screen/tabs/settings/SettingsScreen.kt` (+ UiState/UiAction/owner/VM)
- persistencia de idioma + fila biometría
- strings
- tests de filas y logout
- **No** use cases de experience/streaks/PRs en fase 3

---

## Fuera de esta pasada (ya extraído, no implementar ahora)

- `daily_tasks_overview_12` Drink Water (fase 4: PATCH habit-logs; conservar `habitLog.id` en `DailyItem`)
- Catálogo / wizards Create Meal y Create Routine (ya en app). `estados_del_cat_logo` = empty de **ejercicios**.
- Detalle receta Stitch (`detalle_*`) = fase 2b cuando exista Retrofit `GET /meals/{id}`
- Overlay éxito (`xito_plan_creado`) — PopupResult ya sigue Stitch; finish-workout sigue `SuccessDialog`

## No hacer

- Copiar CSS/Tailwind a Compose
- Status bar fake del HTML
- Material Symbols como dependencia nueva si ya hay iconos Material
- Rediseñar Diario en este feature
