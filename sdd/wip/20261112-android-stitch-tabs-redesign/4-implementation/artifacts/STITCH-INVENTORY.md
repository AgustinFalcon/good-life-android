# Inventario Stitch — Daily Item Component Variants

Proyecto: https://stitch.google.com/projects/9630221387926338009  
Export: `stitch_daily_item_component_variants.zip` (6.9 MB, 2026-11-12)  
Sistema visual canónico para las 4 tabs nuevas: `stitch/vitality_flow/DESIGN.md` (mint `#1EC691`, Plus Jakarta Sans, 8dp, voseo).  
`vitality_core/DESIGN.md` es un sistema naranja editorial anterior; no usar en las tabs nuevas.

## Respuesta corta: ¿hacen falta más de 4?

Las **4 pantallas nuevas cubren los cascarones de tab** Entrenar / Comida / Más y el detalle de sesión. **No alcanzan solas** para el producto: el mismo canvas ya trae Diario, hidratación, catálogo de comidas, detalle de receta, wizards de crear rutina y crear comida. Esas no hay que rediseñar de cero: hay que **reusarlas**.

Todavía **no están** en Stitch (y sí están en backend/prompts):

| Hueco | Por qué importa |
|-------|-----------------|
| Detalle XP / historial de puntos | `GET /users/me/experience`, `xp-history`, `level-ups` |
| Overlay de subida de nivel | `POST level-ups/mark-read` |
| Lista de rachas y de PRs | filas de Más navegan a pantallas que no existen |
| Selector de idioma | fila Preferencias |
| Confirmar exportar datos / cerrar sesión | filas de Más |
| Ranking social entre usuarios | **no implementar**: el API es ranking personal |
| Campana | fuera de alcance |

## Lote A — 4 pantallas nuevas (2026-11-12)

| Carpeta | Título UI | Rol Android | Estado en app |
|---------|-----------|-------------|---------------|
| `entrenar_rutina_activa` | Entrenar | Tab Entrenamientos | `WorkoutsScreen` existe; hay que alinear al HTML |
| `comida_vista_diaria` | Comida | Tab Comida | `MealsScreen` existe; hay que alinear al HTML |
| `entrenar_detalle_de_sesi_n` | Sesión En Curso | Detalle de workout en curso | `WorkoutDetailScreen` + finish ya en master; hay que alinear visual |
| `m_s_hub_de_ajustes` | Más | Tab Settings hub | `SettingsScreen` es perfil+logout; fase 3 **agrega** idioma persistido y biometría; **no** XP/rachas/PRs |

## Lote B — Diario e hidratación (ya en el MCP)

| Carpeta | Título HTML | Uso |
|---------|-------------|-----|
| `daily_item_component_variants` | Daily Log — cards Task/Habit/Workout/Meal | Variantes de `DailyItemCard` |
| `daily_tasks_overview_2` / `_3` / `_5` / `_8` / `_11` | Daily Tasks Overview | Tab Diario (estados: pending, completed, Drink Water) |
| `daily_tasks_overview_6` | Date Picker Selector | `DatePickerBottomSheet` (ya implementado) |
| `daily_tasks_overview_1` | Workout Log Detail | Detalle diario de entreno |
| `daily_tasks_overview_4` | Meal Completion Detail | Completar comida del día |
| `daily_tasks_overview_10` | Task Status Detail | Detalle de tarea |
| `daily_tasks_overview_7` | Crear Tarea - Una Vez | Create Task (ya existe) |
| `daily_tasks_overview_9` | Nueva Tarea Recurrente | Create Task recurrente |
| **`daily_tasks_overview_12`** | **Habit Detail — Drink Water** | **Completar vasos/ml de agua** (anillo + vaso + − / +250 ml / +100 / +500) |

`daily_tasks_overview_12` es la pantalla que el usuario mencionó: registrar cuánta agua se tomó. No es una de las 4 nuevas; ya estaba en el proyecto.

## Lote C — Comida (catálogo, detalle, wizard)

| Carpeta | Título | Uso |
|---------|--------|-----|
| `paso_1a_cat_logo_de_comidas` | Nueva Comida — catálogo | Elegir comida del catálogo |
| `paso_1b_formulario_de_comida` | Nueva Comida — formulario | Alta custom |
| `paso_2_agregar_ingredientes` | Paso 2 — ingredientes | Wizard |
| `paso_2_ingredientes_y_macros` | Paso 2 — macros | Wizard |
| `paso_3_planificaci_n_de_comidas` | Planificar Comida | Días / tipo de comida |
| `ajuste_de_cantidad_bottom_sheet` | Configurar Cantidad | Bottom sheet gramos |
| `crear_ingrediente_bottom_sheet` | Nuevo Ingrediente | Bottom sheet |
| `detalle_de_comida_cl_sico` | Salmón con Espárragos | Detalle clásico |
| `detalle_foco_en_ingredientes` | Bowl de Quinoa | Detalle ingredientes |
| `detalle_pasos_de_preparaci_n` | Pasta Integral | Pasos de receta |
| `estados_del_cat_logo` | (vacío/error catálogo **de ejercicios**) | E3 empty/error; **no** es Comida |
| `xito_plan_creado` | ¡Plan de comidas creado! | Overlay éxito (ya cubierto por PopupResult) |

## Lote D — Crear rutina (ya hay CreateRoutine en app)

| Carpeta | Título |
|---------|--------|
| `1._datos_de_la_rutina` | Nueva Rutina - Paso 1 |
| `2._gesti_n_de_workouts` | Gestión de workouts |
| `3._ejercicios_y_cat_logo` | Create Routine - Step 3 / Push Day |
| `ejercicios_del_workout` | Workout Builder - Push Day |
| `configurar_series` | Resumen de Entrenamiento / series |
| `resumen_de_rutina` | Resumen de Rutina |
| `4._resumen_y_xito` | Resumen y éxito |

## Snippets sueltos

- `c_digo_jetpack_compose_bot_n_crear_nueva_comida.html`
- `componente_jetpack_compose_createingredientdialog.kt.txt`

No son pantallas. Referencia de copy/Compose generado por Stitch; no copiar literal.

## Orden de implementación (producto)

1. **Entrenar** — Lote A `entrenar_rutina_activa` + `entrenar_detalle_de_sesi_n`. Wizards de rutina (Lote D) ya están en la app.
2. **Comida** — Lote A `comida_vista_diaria` (filtro **igual a Diario**: weekday ∈ `daysOfWeek` + rango fechas; lista vacía no aplica). Detalle = plan. Receta Stitch (`detalle_*`) = fase 2b + `GET /meals/{id}` + `mealId` conservado. Wizard Create Meal Plan ya está.
3. **Más** — hub: perfil + **agregar** idioma persistido y biometría. **No agregar** XP/rachas/PRs/exportar.
4. **Después** — Drink Water (`daily_tasks_overview_12`), `PATCH /daily-logs/habit-logs/{id}` (conservar `habitLog.id` en dominio), y subpantallas XP cuando existan en Stitch.

Catálogo con origen de datos: `1-functional/spec.md` sección Screen Catalog.

## Decisiones vs HTML (no inventar)

| Lo que pinta Stitch | Qué hay en API | Qué hacemos |
|---------------------|----------------|-------------|
| 2.140 / 2.400 kcal, “Faltan 260”, +5 XP | No hay meta kcal. Server otorga 5 XP al COMPLETED de MEAL | Suma de planes que pasan el filtro Diario; no pintar montos. `daysOfWeek` vacío no aplica |
| Workout “60 min”, calentamiento, “Sincronizado” | Workout no trae duración | Ocultar |
| Selector Lun–Dom / “workout de hoy” | JSON `dayOfWeek` = `dayOrder`, no ISO. Emparejar como Diario: `daysOfWeek.indexOf(iso)` + workouts sorted by dayOrder | No mapear `Workout.dayOfWeek` a Monday… |
| Semana 4 de 8 | `startDate` **y** `endDate` | Calcular o ocultar |
| Chip músculo en sesión | Workout exercise no trae muscle group | Ocultar (salvo GET extra `/exercises`, fuera de fase 1) |
| “¿Cómo te sentiste hoy?” | `FinishWorkoutRequest.notes` (string siempre) | No es RPE de sesión |
| RPE 9.5 / RPE de sesión | RPE por set, entero 1–10 o ausente | Freeze finish de master |
| +50 XP en Terminar | XP lo aplica el server; Android no lo pinta | Ocultar badge |
| Check por serie = POST | Finish envía **todas** las series juntas | Checks locales; blank → 0.0/0 |
| Guardar borrador | No hay endpoint (y **no está en el HTML**) | No implementar |
| Ingredientes / pasos receta | Backend `GET /meals/{id}`; Android no tiene Retrofit | Fase 2b; detalle de plan ahora |
| Exportar mis datos | No hay endpoint | No agregar (Settings hoy no lo tiene) |
| Ranking / XP / rachas / PRs en Más | GET experience/streaks/PRs; Android no consume | Fase 4 + Stitch de subpantallas |
| Foto de perfil persistida | `POST /media/upload` no escribe `UserEntity` | No prometer; no hay PATCH `/me` |
| +100 / +250 / +500 ml | `habit.unit` + `habit.targetValue` (Int) + log `currentValue` (Double). PATCH habit-logs | Fase 4; atajos según unidad; conservar `habitLog.id` |
