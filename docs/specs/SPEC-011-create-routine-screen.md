# SPEC-011 — Create Routine Screen (Android)

**Versión:** 1.0  
**Estado:** ✅ Implementado  
**Última actualización:** 11 de Marzo, 2026  
**Relacionado con:** backend `training.spec.md`, mockups en `assets/`

---

## 1. Descripción General

Wizard multi-paso para crear una rutina de entrenamiento completa.
Accesible desde el FAB (+) del `MainScaffold` → QuickAction "Rutina".

La rutina se crea en **una sola llamada** (`POST /api/v1/routines`) con toda la estructura anidada:
Routine → Workouts → Exercises → Sets.

Al crearla, opcionalmente se puede activar (`PATCH /api/v1/routines/{id}/activate`).
La rutina activa genera items WORKOUT en el Daily Log para los días configurados.

### Complejidad vs Create Task/Habit

| Feature | Create Task | Create Habit | Create Routine |
|---------|-------------|--------------|----------------|
| Formulario | 1 nivel | 1 nivel | 4 niveles anidados |
| Pantallas | 1 | 1 | 4 (wizard) |
| Endpoints consumidos | 1 (POST) | 1 (POST) | 3 (GET catálogo + POST rutina + PATCH activar) |
| Paginación | No | No | Sí (catálogo de ejercicios) |
| Estado en memoria | Simple | Simple | Complejo (lista de workouts con ejercicios y sets) |

---

## 2. Backend API — Endpoints Consumidos

### 2.1 GET /api/v1/muscle-groups — Catálogo de grupos musculares

**Auth:** Bearer token  
**Response:** `200 OK`

```json
{
  "code": 200,
  "data": [
    { "id": 1, "code": "CHEST", "name": "Pecho", "iconUrl": "https://..." },
    { "id": 2, "code": "BACK", "name": "Espalda", "iconUrl": null }
  ]
}
```

### 2.2 GET /api/v1/exercises — Catálogo de ejercicios (paginado)

**Auth:** Bearer token  
**Query params:**

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `muscleGroupId` | `Long?` | null | Filtrar por grupo muscular |
| `search` | `String?` | null | Búsqueda por nombre |
| `page` | `Int` | 0 | Página (0-indexed) |
| `size` | `Int` | 20 | Items por página |

**Response:** `200 OK` — Spring Data Page

```json
{
  "code": 200,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Press de Banca",
        "description": "Ejercicio compuesto de pecho",
        "imageUrl": "https://...",
        "muscleGroupId": 1,
        "muscleGroupName": "Pecho",
        "score": 8
      }
    ],
    "totalElements": 50,
    "totalPages": 3,
    "size": 20,
    "number": 0,
    "first": true,
    "last": false
  }
}
```

### 2.3 POST /api/v1/routines — Crear rutina completa

**Auth:** Bearer token  
**Response exitosa:** `201 Created`

**Request body:**
```json
{
  "name": "Push Pull Legs",
  "description": "Rutina de 6 días para hipertrofia",
  "difficultyLevel": "INTERMEDIATE",
  "goalType": "MUSCLE_GAIN",
  "scheduledTime": "07:00:00",
  "daysOfWeek": [1, 2, 3, 4, 5, 6],
  "startDate": "2026-03-10",
  "endDate": null,
  "workouts": [
    {
      "name": "Push Day",
      "exercises": [
        {
          "exerciseId": 1,
          "orderIndex": 1,
          "notes": "Usar agarre medio",
          "sets": [
            { "setNumber": 1, "targetReps": 12, "targetWeight": 60.0 },
            { "setNumber": 2, "targetReps": 10, "targetWeight": 65.0 },
            { "setNumber": 3, "targetReps": 8, "targetWeight": 70.0 }
          ]
        }
      ]
    }
  ]
}
```

**Campos:**

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `name` | String | Sí | No vacío |
| `description` | String? | No | |
| `difficultyLevel` | Enum | Sí | BEGINNER, INTERMEDIATE, ADVANCED |
| `goalType` | Enum | Sí | MUSCLE_GAIN, WEIGHT_LOSS, STRENGTH, ENDURANCE, FLEXIBILITY |
| `scheduledTime` | LocalTime? | No | Formato "HH:mm:ss" |
| `daysOfWeek` | List\<Int\> | Sí | No vacío, valores 1-7 |
| `startDate` | LocalDate? | No | |
| `endDate` | LocalDate? | No | |
| `workouts` | List | Sí | Al menos 1 workout |
| `workouts[].name` | String | Sí | |
| `workouts[].exercises` | List | Sí | Al menos 1 ejercicio |
| `workouts[].exercises[].exerciseId` | Long | Sí | Ref al catálogo |
| `workouts[].exercises[].orderIndex` | Int | Sí | Orden del ejercicio |
| `workouts[].exercises[].notes` | String? | No | |
| `workouts[].exercises[].sets` | List | Sí | Al menos 1 set |
| `workouts[].exercises[].sets[].setNumber` | Int | Sí | |
| `workouts[].exercises[].sets[].targetReps` | Int | Sí | |
| `workouts[].exercises[].sets[].targetWeight` | Double? | No | En kg |

**Validaciones del backend:**

| Regla | Error |
|-------|-------|
| `name` vacío | 400 |
| `daysOfWeek` vacío | 400 |
| `daysOfWeek` con valores fuera de [1-7] | 400 |
| `endDate` < `startDate` | 400 |
| `workouts` vacío | 400 |
| workout sin `exercises` | 400 |
| exercise sin `sets` | 400 |
| `exerciseId` inexistente | 400 |

**Response body:** `RoutineResponse` completa con IDs generados.

### 2.4 PATCH /api/v1/routines/{id}/activate — Activar rutina

**Auth:** Bearer token  
**Response:** `200 OK`  
**Regla:** Solo una rutina activa a la vez. Al activar una, la anterior se desactiva automáticamente.

---

## 3. Flujo de Pantallas (Wizard)

### Diagrama de navegación

```
FAB (+) → QuickAction "Rutina"
    │
    ▼
┌─────────────────────┐
│  PASO 1              │  Datos de la rutina
│  Nombre, descripción │  (nombre, dificultad, objetivo,
│  Dificultad, objetivo│   días, hora, fechas)
│  Días, hora, fechas  │
│         [Siguiente →]│
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│  PASO 2              │  Lista de workouts
│  Workouts de la      │  (agregar, editar, eliminar)
│  rutina              │
│  [← Atrás] [Sig. →] │
└─────────┬───────────┘
          │ (tocar workout o "+")
          ▼
┌─────────────────────┐
│  PASO 3              │  Ejercicios del workout
│  "Tus Ejercicios" +  │  (buscar en catálogo, agregar,
│  Catálogo paginado   │   configurar sets)
│         [← Guardar]  │
└─────────┬───────────┘
          │ (tocar "Agregar" o editar)
          ▼
┌─────────────────────┐
│  BOTTOM SHEET        │  Configurar sets
│  Tabla: # | Reps |   │  (setNumber, targetReps,
│  Peso | Notas        │   targetWeight, notes)
│  [Cancelar][Confirm.] │
└─────────┬───────────┘
          │ (volver a paso 2, todos los workouts listos)
          ▼
┌─────────────────────┐
│  PASO 4              │  Resumen
│  Resumen de rutina   │  (vista completa + toggle
│  Toggle activar      │   "Activar esta rutina")
│  [Crear rutina]      │
└─────────────────────┘
```

### Paso 1 — Datos de la rutina

```
┌──────────────────────────────────────────┐
│  [×]   Nueva rutina        [RUTINA]      │
│  Paso 1 de 4              ████░░░░  25%  │
├──────────────────────────────────────────┤
│                                          │
│  Nombre de la rutina                     │
│  ┌──────────────────────────────────────┐│
│  │ Ej. Empuje Hipertrofia              ││
│  └──────────────────────────────────────┘│
│                                          │
│  Descripción (Opcional)                  │
│  ┌──────────────────────────────────────┐│
│  │ Escribe detalles sobre tu rutina... ││
│  └──────────────────────────────────────┘│
│                                          │
│  Dificultad                              │
│  ┌────────────┐┌──────────┐┌──────────┐ │
│  │Principiante││Intermedio││ Avanzado │ │
│  └────────────┘└──────────┘└──────────┘ │
│                                          │
│  Objetivo principal                      │
│  ┌──────────────────────────────────▼──┐ │
│  │ ⚡ Seleccionar un objetivo          │ │
│  └─────────────────────────────────────┘ │
│                                          │
│  Días de entrenamiento                   │
│  ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐   │
│  │L │ │M │ │X │ │J │ │V │ │S │ │D │   │
│  └──┘ └──┘ └──┘ └──┘ └──┘ └──┘ └──┘   │
│                                          │
│  Recordatorio de hora     [toggle]       │
│  HORA        FECHA INICIO                │
│  08:00 🕐    27/10/2023 📅              │
│                                          │
│  Establecer fecha fin     [toggle]       │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │         SIGUIENTE →                  ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

**Campos:** Nombre*, Descripción, DifficultyLevel*, GoalType*, DaysOfWeek*, ScheduledTime, StartDate, EndDate  
**Componentes reutilizados:** TextFieldComponent, DayChipComponent, TimeSelectorComponent, DateSelectorComponent, SwitchComponent, ButtonComponent  
**Componentes nuevos:** DifficultyChipSelector, GoalTypeDropdown

### Paso 2 — Workouts de la rutina

```
┌──────────────────────────────────────────┐
│  [←]  Workouts de la rutina              │
│  Paso 2 de 4              ████████░░ 50% │
├──────────────────────────────────────────┤
│                                          │
│  Workouts de la rutina                   │
│  (2 de 6 días)                           │
│  Define la rotación de workouts.         │
│  Se asignan a los días en orden.         │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 💪 Día 1: Empuje (Pecho/Tríceps)   ││
│  │    8 ejercicios • 60 mins     ✏️ 🗑 ││
│  └──────────────────────────────────────┘│
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🏋️ Día 2: Tracción (Espalda/Bíceps)││
│  │    7 ejercicios • 55 mins     ✏️ 🗑 ││
│  └──────────────────────────────────────┘│
│                                          │
│  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┐│
│  │        (+) Añadir entrenamiento      ││
│  │        para el día 3                 ││
│  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─┘│
│                                          │
│  ┌────────┐  ┌──────────────────────────┐│
│  │ Atrás  │  │     SIGUIENTE →          ││
│  └────────┘  └──────────────────────────┘│
└──────────────────────────────────────────┘
```

**Datos:** Lista de workouts en memoria, cada uno con nombre + lista de ejercicios  
**Acciones:** Agregar workout (dialog con nombre), Editar (navega a Paso 3), Eliminar (confirmación)  
**Botón Siguiente:** Habilitado solo si hay al menos 1 workout con al menos 1 ejercicio

### Paso 3 — Ejercicios del workout

```
┌──────────────────────────────────────────┐
│  [←]  Push Day  ✏️                       │
│  Paso 3 de 4              ████████████ 75│
├──────────────────────────────────────────┤
│                                          │
│  Tus Ejercicios                  3 total │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🖼 Bench Press                       ││
│  │    3 sets • 12/10/8 reps • 60-70 kg ││
│  │    ✏️ EDITAR                    🗑   ││
│  └──────────────────────────────────────┘│
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🖼 Overhead Press                    ││
│  │    3 sets • 10 reps • 30 kg         ││
│  │    ✏️ EDITAR                    🗑   ││
│  └──────────────────────────────────────┘│
│                                          │
│  ── Catálogo de Ejercicios ──────────── │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🔍 Buscar ejercicios...             ││
│  └──────────────────────────────────────┘│
│                                          │
│  [Todos] [Pecho] [Espalda] [Piernas]... │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │ 🖼 Dumbbell Flyes                   ││
│  │    Pecho • Mancuernas     [Agregar] ││
│  └──────────────────────────────────────┘│
│  ┌──────────────────────────────────────┐│
│  │ 🖼 Lateral Raises                   ││
│  │    Hombro • Mancuernas    [Agregar] ││
│  └──────────────────────────────────────┘│
│  ... (scroll infinito / load more) ...   │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │       ← GUARDAR WORKOUT             ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

**Toda la pantalla es el catálogo** con búsqueda y filtro por grupo muscular  
**Ejercicios ya agregados** se muestran como sección destacada dentro del mismo catálogo (con badge/indicador de "agregado" y resumen de sets)  
**Al tocar "Agregar":** Abre bottom sheet de configuración de sets (3B)  
**Al tocar "EDITAR" (en ejercicio ya agregado):** Abre mismo bottom sheet con datos precargados  
**Componente de catálogo:** Reutilizable para futuro WorkoutsTab

### Bottom Sheet 3B — Configurar sets

```
┌──────────────────────────────────────────┐
│  ──── (drag handle) ────                 │
│                                          │
│  🖼 Press de Banca                  ⋮   │
│     [Pecho]                              │
│                                          │
│  #    REPS        PESO (KG)              │
│  1    [  12  ]    [  60  ]          ✕    │
│  2    [  10  ]    [  65  ]          ✕    │
│  3    [   8  ]    [  70  ]          ✕    │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │      + Agregar set                   ││
│  └──────────────────────────────────────┘│
│                                          │
│  Notas                                   │
│  ┌──────────────────────────────────────┐│
│  │ Ej: Usar agarre cerrado             ││
│  └──────────────────────────────────────┘│
│                                          │
│  ┌────────┐  ┌──────────────────────────┐│
│  │Cancelar│  │      CONFIRMAR           ││
│  └────────┘  └──────────────────────────┘│
└──────────────────────────────────────────┘
```

**Mínimo 1 set requerido** — no se puede eliminar el último  
**targetWeight es opcional** — ejercicios con peso corporal no lo necesitan  
**Al confirmar:** Agrega/actualiza el ejercicio en la lista de "Tus Ejercicios"

### Paso 4 — Resumen y creación

```
┌──────────────────────────────────────────┐
│  [←]  Resumen de Rutina      [RUTINA]    │
│  Paso 4 de 4              ████████████ ✓ │
├──────────────────────────────────────────┤
│                                          │
│  ┌──────────────────────────────────────┐│
│  │         [imagen decorativa]          ││
│  │                                      ││
│  │  Rutina Hipertrofia                  ││
│  │  [Intermedio] [Ganar músculo]        ││
│  │                                      ││
│  │  L  M  X  J  V  S  D                ││
│  │  ●  ●  ●  ○  ●  ○  ○               ││
│  │                                      ││
│  │  🕐 08:00 AM    📅 10/03 - Indef.   ││
│  └──────────────────────────────────────┘│
│                                          │
│  Entrenamientos                          │
│  ┌──────────────────────────────────────┐│
│  │ Push Day                         ▶  ││
│  │ 4 ejercicios • 12 sets totales      ││
│  └──────────────────────────────────────┘│
│  ┌──────────────────────────────────────┐│
│  │ Pull Day                         ▶  ││
│  │ 5 ejercicios • 15 sets totales      ││
│  └──────────────────────────────────────┘│
│                                          │
│  ⚡ Activar esta rutina ahora  [toggle]  │
│                                          │
│  ┌──────────────────────────────────────┐│
│  │         CREAR RUTINA                 ││
│  └──────────────────────────────────────┘│
└──────────────────────────────────────────┘
```

**Toggle "Activar":** Si ON, después del POST se llama PATCH activate  
**Al crear:** SuccessDialog con animación Lottie → navigateUp()  
**Error:** SnackbarComponent con retry

---

## 4. Enums del Backend

### DifficultyLevel
| Valor | Texto ES | Texto EN | Texto PT |
|-------|----------|----------|----------|
| BEGINNER | Principiante | Beginner | Iniciante |
| INTERMEDIATE | Intermedio | Intermediate | Intermediário |
| ADVANCED | Avanzado | Advanced | Avançado |

### GoalType
| Valor | Texto ES | Texto EN | Texto PT |
|-------|----------|----------|----------|
| MUSCLE_GAIN | Ganar músculo | Muscle gain | Ganhar músculo |
| WEIGHT_LOSS | Perder peso | Weight loss | Perder peso |
| STRENGTH | Fuerza | Strength | Força |
| ENDURANCE | Resistencia | Endurance | Resistência |
| FLEXIBILITY | Flexibilidad | Flexibility | Flexibilidade |

---

## 5. Estado UI — UiStates

### CreateRoutineUiState (Paso 1)

```kotlin
@Stable
data class CreateRoutineUiState(
    // Paso actual del wizard
    val currentStep: RoutineWizardStep = RoutineWizardStep.ROUTINE_INFO,

    // ── Paso 1: Datos de la rutina ──
    val name: String = "",
    val description: String = "",
    val difficultyLevel: DifficultyLevel? = null,
    val goalType: GoalType? = null,
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,
    val startDate: LocalDate? = null,
    val startDateDisplay: String = "",
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,
    val endDateDisplay: String = "",

    // ── Paso 2: Workouts ──
    val workouts: List<WorkoutDraft> = emptyList(),

    // ── Paso 3: Ejercicios del workout activo ──
    val activeWorkoutIndex: Int = -1,
    val exerciseCatalog: List<ExerciseCatalogItem> = emptyList(),
    val muscleGroups: List<MuscleGroupItem> = emptyList(),
    val selectedMuscleGroupId: Long? = null,
    val exerciseSearchQuery: String = "",
    val isLoadingCatalog: Boolean = false,
    val catalogPage: Int = 0,
    val catalogHasMore: Boolean = true,

    // ── Paso 4: Resumen ──
    val activateOnCreate: Boolean = false,

    // ── Estado general ──
    val showTimePicker: Boolean = false,
    val activeDatePickerField: RoutineDatePickerField? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

enum class RoutineWizardStep { ROUTINE_INFO, WORKOUTS, WORKOUT_EXERCISES, SUMMARY }
enum class RoutineDatePickerField { START_DATE, END_DATE }
```

### WorkoutDraft (modelo en memoria durante creación)

```kotlin
data class WorkoutDraft(
    val name: String,
    val exercises: List<ExerciseDraft> = emptyList(),
)

data class ExerciseDraft(
    val exerciseId: Long,
    val exerciseName: String,
    val imageUrl: String?,
    val muscleGroupName: String,
    val orderIndex: Int,
    val notes: String? = null,
    val sets: List<SetDraft> = listOf(SetDraft()),
)

data class SetDraft(
    val setNumber: Int = 1,
    val targetReps: Int = 10,
    val targetWeight: Double? = null,
)
```

---

## 6. Acciones UI

```kotlin
sealed interface CreateRoutineUiAction {
    // ── Navegación del wizard ──
    data object OnNextStep : CreateRoutineUiAction
    data object OnPreviousStep : CreateRoutineUiAction
    data object OnDismiss : CreateRoutineUiAction

    // ── Paso 1: Datos de la rutina ──
    data class OnNameChange(val name: String) : CreateRoutineUiAction
    data class OnDescriptionChange(val description: String) : CreateRoutineUiAction
    data class OnDifficultySelected(val level: DifficultyLevel) : CreateRoutineUiAction
    data class OnGoalTypeSelected(val goal: GoalType) : CreateRoutineUiAction
    data class OnDayToggled(val day: DayOfWeek) : CreateRoutineUiAction
    data object OnHasTimeToggle : CreateRoutineUiAction
    data object OnHasEndDateToggle : CreateRoutineUiAction
    data class OnDatePickerOpen(val field: RoutineDatePickerField) : CreateRoutineUiAction
    data object OnDatePickerDismiss : CreateRoutineUiAction
    data class OnDateSelected(val field: RoutineDatePickerField, val date: LocalDate) : CreateRoutineUiAction
    data object OnTimePickerOpen : CreateRoutineUiAction
    data object OnTimePickerDismiss : CreateRoutineUiAction
    data class OnTimeSelected(val time: LocalTime) : CreateRoutineUiAction

    // ── Paso 2: Workouts ──
    data class OnAddWorkout(val name: String) : CreateRoutineUiAction
    data class OnEditWorkout(val index: Int) : CreateRoutineUiAction
    data class OnDeleteWorkout(val index: Int) : CreateRoutineUiAction
    data class OnRenameWorkout(val index: Int, val name: String) : CreateRoutineUiAction

    // ── Paso 3: Ejercicios ──
    data class OnSearchQueryChange(val query: String) : CreateRoutineUiAction
    data class OnMuscleGroupFilter(val muscleGroupId: Long?) : CreateRoutineUiAction
    data object OnLoadMoreExercises : CreateRoutineUiAction
    data class OnAddExercise(val exercise: ExerciseCatalogItem) : CreateRoutineUiAction
    data class OnRemoveExercise(val orderIndex: Int) : CreateRoutineUiAction
    data class OnEditExerciseSets(val orderIndex: Int) : CreateRoutineUiAction
    data class OnSaveExerciseSets(val orderIndex: Int, val sets: List<SetDraft>, val notes: String?) : CreateRoutineUiAction
    data object OnSaveWorkout : CreateRoutineUiAction

    // ── Paso 4: Resumen ──
    data class OnActivateToggle(val activate: Boolean) : CreateRoutineUiAction
    data object OnSubmit : CreateRoutineUiAction

    // ── Feedback ──
    data object OnErrorDismissed : CreateRoutineUiAction
    data object OnSuccessAnimationFinished : CreateRoutineUiAction
}
```

---

## 7. Validaciones Frontend (por paso)

### Paso 1 → Siguiente
| Regla | Condición |
|-------|-----------|
| Nombre requerido | `name.isNotBlank()` |
| Dificultad requerida | `difficultyLevel != null` |
| Objetivo requerido | `goalType != null` |
| Al menos 1 día | `selectedDays.isNotEmpty()` |
| Fecha inicio requerida | `startDate != null` |
| endDate >= startDate | `!hasEndDate \|\| endDate >= startDate` |

### Paso 2 → Siguiente
| Regla | Condición |
|-------|-----------|
| Al menos 1 workout | `workouts.isNotEmpty()` |
| Cada workout con ejercicios | `workouts.all { it.exercises.isNotEmpty() }` |

### Paso 3 → Guardar workout
| Regla | Condición |
|-------|-----------|
| Al menos 1 ejercicio | `activeWorkout.exercises.isNotEmpty()` |
| Cada ejercicio con sets | `exercises.all { it.sets.isNotEmpty() }` |

### Bottom Sheet → Confirmar
| Regla | Condición |
|-------|-----------|
| Al menos 1 set | `sets.isNotEmpty()` |
| Reps > 0 | `sets.all { it.targetReps > 0 }` |

---

## 8. Arquitectura de Archivos

```
domain/model/training/
    DifficultyLevel.kt              enum
    GoalType.kt                     enum
    MuscleGroup.kt                  domain model
    ExerciseMaster.kt               domain model
    Routine.kt                      domain model (con workouts anidados)

domain/repository/
    TrainingCatalogRepository.kt    interface (muscle groups + exercises)
    RoutineRepository.kt            interface (create + activate)

domain/usecase/routine/
    GetMuscleGroupsUseCase.kt
    SearchExercisesUseCase.kt       con paginación
    CreateRoutineUseCase.kt         validaciones + POST
    ActivateRoutineUseCase.kt
    result/
        CreateRoutineResult.kt      sealed interface
        SearchExercisesResult.kt    sealed interface

data/remote/api/training/
    TrainingCatalogApiService.kt    GET exercises + muscle-groups
    RoutineApiService.kt            POST routines + PATCH activate

data/remote/dto/request/routine/
    CreateRoutineRequest.kt         con DTOs anidados

data/remote/dto/response/training/
    MuscleGroupResponse.kt
    ExerciseMasterResponse.kt
    ExercisePageResponse.kt         wrapper de paginación Spring
    RoutineResponse.kt              con DTOs anidados

data/remote/datasource/
    TrainingCatalogRemoteDataSource.kt
    RoutineRemoteDataSource.kt

data/repository/
    TrainingCatalogRepositoryImpl.kt
    RoutineRepositoryImpl.kt

presentation/screen/add/routine/
    model/
        CreateRoutineUiState.kt     @Stable + drafts
        CreateRoutineUiAction.kt    sealed interface
    CreateRoutineViewModel.kt       wizard logic + API calls
    steps/
        RoutineInfoStep.kt          Paso 1 - Screen
        WorkoutsStep.kt             Paso 2 - Screen
        WorkoutExercisesStep.kt     Paso 3 - Screen
        RoutineSummaryStep.kt       Paso 4 - Screen
    CreateRoutineScreen.kt          Orchestrador de pasos
    CreateRoutineScreenOwner.kt     Inyecta VM + dialogs

presentation/components/common/
    DifficultyChipSelector.kt       NUEVO - chips para dificultad
    GoalTypeDropdown.kt             NUEVO - dropdown para objetivo
    ExerciseCatalogComponent.kt     NUEVO - búsqueda + lista paginada (reutilizable)
    SetEditorComponent.kt           NUEVO - tabla de sets editable
    WorkoutDraftCard.kt             NUEVO - card de workout con resumen

di/
    RoutineModule.kt                Koin module (catálogo + rutinas en uno)
```

---

## 9. Componentes Reutilizables

### De Create Task/Habit (ya existen)
- `DayChipComponent` → días de la semana
- `TimeSelectorComponent` → hora
- `DateSelectorComponent` → fechas
- `SwitchComponent` → toggles
- `TextFieldComponent` → nombre, descripción, búsqueda
- `ButtonComponent` → navegación, submit
- `SnackbarComponent` → errores
- `SuccessDialog` → feedback de éxito
- `DatePickerBottomSheetComponent` → selección de fecha
- `TimePickerDialogComponent` → selección de hora

### Nuevos (reutilizables en WorkoutsTab)
| Componente | Propósito | Reutilización futura |
|------------|-----------|---------------------|
| `DifficultyChipSelector` | 3 chips horizontales BEGINNER/INTERMEDIATE/ADVANCED | Filtros en WorkoutsTab |
| `GoalTypeDropdown` | Dropdown con los 5 objetivos | Filtros en WorkoutsTab |
| `ExerciseCatalogComponent` | Búsqueda + filtro por grupo muscular + lista paginada | WorkoutsTab, Live Workout |
| `SetEditorComponent` | Tabla editable de sets (reps + peso) | Live Workout (paso de sets reales) |
| `WorkoutDraftCard` | Card con nombre + resumen de ejercicios | WorkoutsTab (vista de rutina) |
| `ExerciseItemCard` | Card de ejercicio con imagen + sets resumidos | WorkoutsTab, detalle de rutina |

---

## 10. Colores y Tokens de Diseño

| Elemento | Color | Hex |
|----------|-------|-----|
| Badge "RUTINA" texto | WorkoutAccent | `#2ECC71` |
| Badge "RUTINA" fondo | WorkoutBackground | `#E8F5E9` |
| Chip dificultad seleccionado | WorkoutAccent | `#2ECC71` |
| Step indicator activo | WorkoutAccent | `#2ECC71` |
| Step indicator inactivo | BorderDefault | `#E0E0E0` |
| Botón "Agregar" ejercicio | WorkoutAccent | `#2ECC71` |
| Botón eliminar | ErrorRed | `#E53935` |
| Fondo card workout | WorkoutBackground | `#E8F5E9` |

---

## 11. Paginación del Catálogo

La paginación se maneja con **scroll infinito** (load more al llegar al final):

```kotlin
// En el ViewModel
private fun loadExercises(page: Int = 0, resetList: Boolean = false) {
    viewModelScope.launch {
        _uiState.update { it.copy(isLoadingCatalog = true) }

        val result = searchExercisesUseCase(
            muscleGroupId = uiState.value.selectedMuscleGroupId,
            search = uiState.value.exerciseSearchQuery.takeIf { it.isNotBlank() },
            page = page,
            size = 20,
        )

        when (result) {
            is SearchExercisesResult.Success -> {
                val currentList = if (resetList) emptyList() else uiState.value.exerciseCatalog
                _uiState.update {
                    it.copy(
                        exerciseCatalog = currentList + result.exercises,
                        catalogPage = page,
                        catalogHasMore = !result.isLastPage,
                        isLoadingCatalog = false,
                    )
                }
            }
            // error handling...
        }
    }
}
```

---

## 12. Koin Module

```kotlin
// RoutineModule — un solo module para todo el feature (catálogo + rutinas)
val routineModule = module {
    // Data
    factory { TrainingCatalogRemoteDataSource(apiService = get()) }
    factory { RoutineRemoteDataSource(apiService = get()) }
    single<TrainingCatalogRepository> { TrainingCatalogRepositoryImpl(remoteDataSource = get()) }
    single<RoutineRepository> { RoutineRepositoryImpl(remoteDataSource = get()) }

    // UseCases
    factory { GetMuscleGroupsUseCase(repository = get(), dispatcher = get()) }
    factory { SearchExercisesUseCase(repository = get(), dispatcher = get()) }
    factory { CreateRoutineUseCase(repository = get(), dispatcher = get()) }
    factory { ActivateRoutineUseCase(repository = get(), dispatcher = get()) }

    // ViewModel
    viewModel {
        CreateRoutineViewModel(
            navigationController = get(),
            language = get(),
            createRoutineUseCase = get(),
            activateRoutineUseCase = get(),
            getMuscleGroupsUseCase = get(),
            searchExercisesUseCase = get(),
        )
    }
}
```

---

## 13. Orden de Implementación Sugerido

| Fase | Archivos | Descripción |
|------|----------|-------------|
| 1 | Domain models + enums | DifficultyLevel, GoalType, MuscleGroup, ExerciseMaster, Routine |
| 2 | Domain repos + use cases | Interfaces + CreateRoutineUseCase, SearchExercisesUseCase, etc. |
| 3 | Data DTOs | Request/Response para routines + catálogo + paginación |
| 4 | Data layer | ApiServices, DataSources, RepositoryImpls |
| 5 | Localización | CreateRoutineTexts, DifficultyTexts, GoalTypeTexts en 3 idiomas |
| 6 | Componentes nuevos | DifficultyChipSelector, GoalTypeDropdown, ExerciseCatalog, SetEditor |
| 7 | Presentation models | UiState, UiAction, WorkoutDraft, ExerciseDraft, SetDraft |
| 8 | ViewModel | Wizard logic, catálogo paginado, submit |
| 9 | Screens (4 pasos) | RoutineInfoStep, WorkoutsStep, WorkoutExercisesStep, RoutineSummaryStep |
| 10 | Screen + Owner | CreateRoutineScreen (orchestrador) + Owner (dialogs/pickers) |
| 11 | DI + Navigation | Modules, AppRoute, AppGraph, QuickAction |
| 12 | Tests | UseCases + ViewModel tests |

---

*Spec creado el 09/03/2026. Implementado el 11/03/2026.*

### Nota de implementación

- La lógica de `activateRoutineUseCase` post-creación (cuando el toggle está activado en el paso 4) queda pendiente para la próxima iteración.
- El toggle ya existe en la UI (`RoutineSummaryStep`) y el UseCase está creado (`ActivateRoutineUseCase`), solo falta conectar en `submitRoutine()`.
- Se usó `ButtonVariant.OUTLINE` para los botones "Atrás" (no existía `SECONDARY`).
