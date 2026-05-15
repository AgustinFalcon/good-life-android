# ➕ SPEC-009 — Add to Daily: Crear Task y Hábito desde el Daily Tab

**Versión:** 1.1  
**Estado:** ✅ Implementado en app (FAB + modal + navegación a flujos de creación); pendientes menores (OTHER, meal options, checklist US)  
**Última actualización:** 14 de Mayo, 2026  
**Relacionado con:** `SPEC-003-main-scaffold.md`, backend `tasks.spec.md`, backend `habits.spec.md`

---

## 1. Descripción General

El botón **FAB (+)** del `MainScaffold` abre un bottom sheet con acciones rápidas.
Desde el **Daily tab**, las dos acciones más importantes son:

- **Crear Tarea** → llama `POST /api/v1/tasks` → la tarea aparece en el Daily Log del día
- **Crear Hábito** → llama `POST /api/v1/habits` → el hábito aparece en el Daily Log desde hoy

El Daily Log **no tiene endpoint de creación propio** — los ítems se generan automáticamente
cuando el backend construye el log de un día y encuentra tareas o hábitos activos para esa fecha.

```
Usuario → FAB (+) → BottomSheet
                         ├── "Tarea"   → CreateTaskScreen  → POST /api/v1/tasks   → Daily Log se regenera
                         ├── "Hábito"  → CreateHabitScreen → POST /api/v1/habits  → Daily Log se regenera
                         ├── (futuro) "Rutina"  → Ver training.spec.md
                         └── (futuro) "Comida"  → Ver nutrition.spec.md
```

---

## 2. User Stories

### US-ADD-001: Crear Tarea desde Daily
```
COMO usuario
QUIERO crear una nueva tarea directamente desde el tab Daily
PARA que aparezca en mi lista del día sin tener que salir de la pantalla
```

**Criterios de Aceptación:**
- [ ] Puedo acceder al formulario desde el FAB (+) del Daily tab
- [ ] Puedo elegir entre tarea puntual o recurrente
- [ ] **Puntual:** La fecha se pre-carga con el día que estoy viendo en el Daily
- [ ] **Recurrente:** Elijo días de la semana + fecha inicio (endDate opcional)
- [ ] Puedo agregar título (obligatorio), descripción y hora (opcionales)
- [ ] Al guardar, el Daily Log del día se recarga automáticamente
- [ ] La nueva tarea aparece en la lista sin necesidad de pull-to-refresh manual

### US-ADD-002: Crear Hábito desde Daily
```
COMO usuario
QUIERO crear un nuevo hábito desde el tab Daily
PARA que empiece a aparecer en mi log diario desde hoy
```

**Criterios de Aceptación:**
- [ ] Puedo acceder al formulario desde el FAB (+) del Daily tab
- [ ] Puedo escribir nombre del hábito (obligatorio)
- [ ] Puedo seleccionar categoría (HYDRATION, MEDITATION, READING, etc.)
- [ ] Puedo definir meta numérica: `targetValue` + `unit` (ej: "8 vasos")
- [ ] La frecuencia por defecto es todos los días `[1,2,3,4,5,6,7]`
- [ ] Puedo personalizar los días de la semana
- [ ] La `startDate` se pre-carga con hoy
- [ ] Al guardar, el Daily Log se recarga y el nuevo hábito aparece

---

## 3. Flujo de Navegación

```
DailyScreenOwner
    │
    └─ FAB (+) presionado
           │
           ▼
    AddToDailyBottomSheet          ← bottom sheet con 2 opciones iniciales
           ├── [Tarea]
           │       └─► CreateTaskScreen (modal / nueva pantalla)
           │                   └─► POST /api/v1/tasks
           │                           └─► DailyTabViewModel.onAction(OnRefresh)
           │
           └── [Hábito]
                   └─► CreateHabitScreen (modal / nueva pantalla)
                               └─► POST /api/v1/habits
                                       └─► DailyTabViewModel.onAction(OnRefresh)
```

**Nota de navegación:** La pantalla de creación puede implementarse como:
- (a) `ModalBottomSheet` de Compose (UX más fluida, sin nueva ruta)
- (b) Pantalla de `NavController` (más simple de implementar)

**Decisión para v1.0:** Usar `ModalBottomSheet` multi-paso (step 1 = elegir tipo, step 2 = form).

---

## 4. Backend Reference

### 4.1 Crear Tarea — `POST /api/v1/tasks`

#### Request — Tarea Puntual:
```json
{
  "title": "Comprar verduras",
  "description": "Ir al mercado orgánico",
  "scheduledDate": "2026-02-28",
  "scheduledTime": "10:00:00",
  "daysOfWeek": null,
  "startDate": null,
  "endDate": null
}
```

#### Request — Tarea Recurrente:
```json
{
  "title": "Ir al gimnasio",
  "description": null,
  "scheduledDate": null,
  "scheduledTime": "07:00:00",
  "daysOfWeek": [1, 3, 5],
  "startDate": "2026-02-28",
  "endDate": null
}
```

#### Response (201 Created):
```json
{
  "code": 201,
  "data": {
    "id": 42,
    "title": "Comprar verduras",
    "scheduledDate": "2026-02-28",
    "scheduledTime": "10:00:00",
    "daysOfWeek": null,
    "startDate": null,
    "endDate": null,
    "isActive": true
  }
}
```

#### Reglas de validación del backend:
| Campo | Regla |
|-------|-------|
| `title` | Requerido, no vacío |
| `scheduledDate` XOR `daysOfWeek` | Solo uno de los dos — puntual o recurrente |
| `daysOfWeek` | Valores entre 1 (Lunes) y 7 (Domingo) |
| `endDate` | Debe ser >= `startDate` si se especifica |
| Si `daysOfWeek`, `startDate` es requerido | |

---

### 4.2 Crear Hábito — `POST /api/v1/habits`

#### Request:
```json
{
  "name": "Beber agua",
  "description": "Mantenerse hidratado",
  "category": "HYDRATION",
  "targetValue": 8,
  "unit": "vasos",
  "daysOfWeek": [1, 2, 3, 4, 5, 6, 7],
  "startDate": "2026-02-28",
  "endDate": null,
  "scheduledTime": "08:00:00"
}
```

#### Response (201 Created):
```json
{
  "code": 201,
  "data": {
    "id": 1,
    "name": "Beber agua",
    "category": "HYDRATION",
    "targetValue": 8,
    "unit": "vasos",
    "isActive": true,
    "daysOfWeek": [1, 2, 3, 4, 5, 6, 7],
    "startDate": "2026-02-28"
  }
}
```

#### Categorías disponibles (`HabitCategory`):
```
HYDRATION | MEDITATION | READING | EXERCISE | SLEEP
NUTRITION | LEARNING | MINDFULNESS | SOCIAL | CREATIVITY
PRODUCTIVITY | HEALTH | CUSTOM
```

---

## 5. Arquitectura Frontend

### 5.1 Capas a crear

```
presentation/
  screen/
    add/                              ← nuevo módulo
      task/
        CreateTaskScreen.kt          ← UI pura del formulario
        CreateTaskScreenOwner.kt     ← inyecta VM, conecta estado
        CreateTaskViewModel.kt       ← lógica de formulario + API call
        model/
          CreateTaskUiState.kt       ← Idle | Loading | Success | Error
          CreateTaskUiAction.kt      ← OnTitleChange, OnDateChange, OnSubmit...
      habit/
        CreateHabitScreen.kt
        CreateHabitScreenOwner.kt
        CreateHabitViewModel.kt
        model/
          CreateHabitUiState.kt
          CreateHabitUiAction.kt

data/
  remote/
    api/
      task/
        TaskApiService.kt            ← POST /api/v1/tasks
      habit/
        HabitApiService.kt           ← POST /api/v1/habits
    dto/
      request/
        CreateTaskRequest.kt
        CreateHabitRequest.kt
      response/
        task/
          TaskResponse.kt
        habit/
          HabitResponse.kt
    datasource/
      TaskRemoteDataSource.kt
      HabitRemoteDataSource.kt

domain/
  repository/
    TaskRepository.kt (interface)
    HabitRepository.kt (interface)
  model/
    task/
      Task.kt
    habit/
      Habit.kt
      HabitCategory.kt
  usecase/
    task/
      CreateTaskUseCase.kt
    habit/
      CreateHabitUseCase.kt

di/
  TaskModule.kt
  HabitModule.kt
```

### 5.2 Flujo de datos — Crear Tarea

```
CreateTaskViewModel
    │
    ├── onAction(OnSubmit)
    │       ↓
    │   Validar campos (título requerido, fecha requerida para puntual)
    │       ↓
    │   CreateTaskUseCase(CreateTaskRequest)   ← dispatcher IO
    │       ↓
    │   TaskRepository.createTask()
    │       ↓
    │   TaskRemoteDataSource → POST /api/v1/tasks
    │       ↓
    │   Result<Task> → CreateTaskResult.Success | Error
    │       ↓
    └── _uiState = Success → callback a DailyTabViewModel.onAction(OnRefresh)
```

### 5.3 Conexión con DailyTabViewModel

Cuando la creación es exitosa, el Daily necesita recargarse.
Opciones:

- **(A) Callback lambda:** `CreateTaskScreenOwner` recibe `onSuccess: () -> Unit` desde `DailyScreenOwner`, que internamente llama `viewModel.onAction(OnRefresh)`. **← Recomendado para v1.0, simple y sin acoplamiento entre VMs.**
- **(B) SharedFlow de eventos:** Un bus de eventos entre VMs. Más complejo, para v2.0.

---

## 6. UI del Formulario

### 6.1 CreateTask — Campos

| Campo | Tipo | Obligatorio | Notas |
|-------|------|-------------|-------|
| Título | TextField | ✅ | max 255 chars |
| Descripción | TextField multiline | ❌ | |
| Tipo | Toggle: Puntual / Recurrente | ✅ | |
| **Si Puntual:** Fecha | DatePicker | ✅ | Pre-cargada con día actual del Daily |
| **Si Puntual:** Hora | TimePicker | ❌ | |
| **Si Recurrente:** Días | Chips seleccionables Lun-Dom | ✅ | min 1 día |
| **Si Recurrente:** Fecha inicio | DatePicker | ✅ | Pre-cargada con hoy |
| **Si Recurrente:** Fecha fin | DatePicker | ❌ | |
| **Si Recurrente:** Hora | TimePicker | ❌ | |

### 6.2 CreateHabit — Campos

| Campo | Tipo | Obligatorio | Notas |
|-------|------|-------------|-------|
| Nombre | TextField | ✅ | |
| Descripción | TextField multiline | ❌ | |
| Categoría | Grid de chips con iconos | ✅ | Ver HabitCategory enum |
| Meta (targetValue) | NumberField | ✅ | solo enteros positivos |
| Unidad (unit) | TextField | ✅ | ej: "vasos", "minutos", "páginas" |
| Días de la semana | Chips seleccionables Lun-Dom | ✅ | default: todos |
| Hora | TimePicker | ❌ | |

---

## 7. Validaciones Frontend (pre-submit)

### 7.1 Tarea
```kotlin
sealed interface CreateTaskValidationError {
    data object TitleEmpty : CreateTaskValidationError
    data object NoSchedulingMode : CreateTaskValidationError    // ni puntual ni recurrente
    data object PunctualNeedsDate : CreateTaskValidationError
    data object RecurrentNeedsDays : CreateTaskValidationError
    data object RecurrentNeedsStartDate : CreateTaskValidationError
    data object EndDateBeforeStart : CreateTaskValidationError
}
```

### 7.2 Hábito
```kotlin
sealed interface CreateHabitValidationError {
    data object NameEmpty : CreateHabitValidationError
    data object NoCategorySelected : CreateHabitValidationError
    data object InvalidTargetValue : CreateHabitValidationError  // <= 0
    data object UnitEmpty : CreateHabitValidationError
    data object NoDaysSelected : CreateHabitValidationError
}
```

---

## 8. UiState

### 8.1 CreateTaskUiState
```kotlin
data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val isRecurrent: Boolean = false,

    // Puntual
    val scheduledDate: LocalDate? = null,
    val scheduledTime: LocalTime? = null,

    // Recurrente
    val daysOfWeek: Set<Int> = emptySet(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val validationErrors: Set<CreateTaskValidationError> = emptySet()
)
```

### 8.2 CreateHabitUiState
```kotlin
data class CreateHabitUiState(
    val name: String = "",
    val description: String = "",
    val category: HabitCategory? = null,
    val targetValue: String = "",          // String para el TextField, se parsea al submit
    val unit: String = "",
    val daysOfWeek: Set<Int> = setOf(1,2,3,4,5,6,7),  // default: todos
    val scheduledTime: LocalTime? = null,
    val startDate: LocalDate = LocalDate.currentDate(), // Hoy

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val validationErrors: Set<CreateHabitValidationError> = emptySet()
)
```

---

## 9. DTOs de Request

### 9.1 CreateTaskRequest
```kotlin
@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val scheduledDate: LocalDate? = null,          // ISO-8601: "2026-02-28"
    val scheduledTime: LocalTime? = null,          // ISO-8601: "10:00:00"
    val daysOfWeek: List<Int>? = null,             // [1,3,5]
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)
```

### 9.2 CreateHabitRequest
```kotlin
@Serializable
data class CreateHabitRequest(
    val name: String,
    val description: String? = null,
    val category: HabitCategory,
    val targetValue: Int,
    val unit: String,
    val daysOfWeek: List<Int>,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val scheduledTime: LocalTime? = null
)
```

---

## 10. Orden de Implementación

### Fase 1 — Crear Tarea (prioridad alta)
1. `CreateTaskRequest.kt` + `TaskResponse.kt` (DTOs)
2. `TaskApiService.kt` → `POST /api/v1/tasks`
3. `TaskRemoteDataSource.kt`
4. `TaskRepository.kt` (interface) + `TaskRepositoryImpl.kt`
5. `CreateTaskUseCase.kt`
6. `CreateTaskViewModel.kt` + `CreateTaskUiState.kt` + `CreateTaskUiAction.kt`
7. `CreateTaskScreen.kt` (UI del formulario)
8. `CreateTaskScreenOwner.kt`
9. `TaskModule.kt` (Koin)
10. Conectar con `DailyScreenOwner` → callback `onTaskCreated`

### Fase 2 — Crear Hábito
1. `CreateHabitRequest.kt` + `HabitResponse.kt` (DTOs)
2. `HabitApiService.kt` → `POST /api/v1/habits`
3. `HabitRemoteDataSource.kt`
4. `HabitRepository.kt` (interface) + `HabitRepositoryImpl.kt`
5. `HabitCategory.kt` (enum `@Serializable`)
6. `CreateHabitUseCase.kt`
7. `CreateHabitViewModel.kt` + estados
8. `CreateHabitScreen.kt`
9. `CreateHabitScreenOwner.kt`
10. `HabitModule.kt` (Koin)

### Fase 3 — Bottom Sheet unificado
- `AddToDailyBottomSheet.kt` — selector visual entre Tarea / Hábito / (futuro: Rutina / Comida)

---

## 11. Edge Cases

| Caso | Comportamiento |
|------|----------------|
| Crear tarea para hoy que ya fue procesado | Aparece en el Daily con status PENDING |
| Crear hábito con startDate = hoy | Aparece en el Daily del día actual |
| Crear hábito con startDate = mañana | No aparece hoy, aparece mañana |
| Error de red al crear | Mostrar mensaje de error, no navegar |
| Backend retorna 400 (validación) | Mostrar error específico del backend |
| Usuario toca guardar dos veces | El botón se deshabilita mientras `isLoading = true` |

---

## 12. Pendientes (fuera de scope v1.0)

| Feature | Spec |
|---------|------|
| Crear Rutina desde Daily | Ver `training.spec.md` |
| Crear Comida/Meal desde Daily | Ver `nutrition.spec.md` |
| Editar Task / Habit existente | Spec separado |
| Eliminar Task / Habit | Spec separado |

---

*Spec generado el 28/02/2026. Fuente de verdad para el módulo Add-to-Daily.*
