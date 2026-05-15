# ✅ SPEC-010 — Create Task Screen (Android)

**Versión:** 1.1  
**Estado:** ✅ Completado  
**Última actualización:** 09 de Marzo, 2026  
**Relacionado con:** `SPEC-009-add-to-daily.md`, backend `tasks.spec.md`

---

## 1. Descripción General

Pantalla / ModalBottomSheet para crear una nueva tarea.
Accesible desde el FAB (+) del `MainScaffold` cuando el usuario está en el tab Daily.

La tarea creada aparece automáticamente en el Daily Log del día/días configurados
porque el backend re-genera los ítems del log al recibir la nueva tarea.

---

## 2. Modos de Programación (Scheduling)

El backend distingue dos modos **mutuamente exclusivos** (`scheduledDate` XOR `daysOfWeek`):

### Modo A — Una vez (Puntual)
```
Envía: scheduledDate = "2026-03-05"
       scheduledTime = "10:00:00"  (opcional)
       daysOfWeek = null
       startDate = null
       endDate = null
```
Caso de uso: "Ir a comprar este jueves"

### Modo B — Se repite (Recurrente)
```
Envía: scheduledDate = null
       daysOfWeek = [1, 3, 5]      (días activos)
       startDate = "2026-03-01"    (requerido)
       endDate = "2026-03-31"      (opcional — null = sin fin)
       scheduledTime = "07:00:00"  (opcional)
```
Casos de uso:
- "Todos los lunes mientras vaya a la facu" (con endDate)
- "Ir al gimnasio los lunes, miércoles y viernes" (sin endDate)
- "Cualquier día que seleccione" (eligiendo 1+ días de la semana)

---

## 3. Backend API — Resumen

**Endpoint:** `POST /api/v1/tasks`  
**Auth:** Bearer token (requerido)  
**Response exitosa:** `201 Created`

### Request body:
```json
{
  "title": "string (requerido, no vacío)",
  "description": "string | null",
  "scheduledDate": "YYYY-MM-DD | null",
  "scheduledTime": "HH:mm:ss | null",
  "daysOfWeek": "[1-7] | null",
  "startDate": "YYYY-MM-DD | null",
  "endDate": "YYYY-MM-DD | null"
}
```

### Validaciones del backend:
| Regla | Error |
|-------|-------|
| `title` vacío | 400 |
| `scheduledDate` + `daysOfWeek` a la vez | 400 |
| Ni `scheduledDate` ni `daysOfWeek` | 400 |
| `daysOfWeek` sin `startDate` | 400 |
| `endDate` < `startDate` | 400 |
| `daysOfWeek` con valores fuera de [1-7] | 400 |

---

## 4. UI/UX — Estructura de la Pantalla

La pantalla se presenta como un **ModalBottomSheet** expandible (full screen en dispositivos pequeños).

```
┌─────────────────────────────────────────────┐
│  ───  (drag handle)                          │
│                                             │
│  [TAREA]  Nueva tarea           [×]         │ ← header con badge tipo + close
├─────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────┐│
│  │ Título *                                ││ ← TextField obligatorio
│  │ Ej: Comprar verduras                    ││
│  └─────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────┐│
│  │ Descripción (opcional)                  ││ ← TextField expandible
│  └─────────────────────────────────────────┘│
├─────────────────────────────────────────────┤
│  ¿Cuándo?                                   │
│                                             │
│  ┌──────────┐  ┌──────────────┐             │
│  │ Una vez  │  │  Se repite   │             │ ← Toggle segmentado
│  └──────────┘  └──────────────┘             │
│                                             │
│  [MODO A - Una vez]                         │
│  ┌─────────────────────────────────────────┐│
│  │ 📅  Mié, 04 de marzo de 2026    [ › ]  ││ ← botón → abre DatePicker BottomSheet
│  └─────────────────────────────────────────┘│
│                                             │
│  [MODO B - Se repite]                       │
│  Días de la semana:                         │
│  ┌────┐┌────┐┌────┐┌────┐┌────┐┌────┐┌────┐│
│  │ L  ││ Ma ││ Mi ││ Ju ││ Vi ││ Sá ││ Do ││ ← chips seleccionables
│  └────┘└────┘└────┘└────┘└────┘└────┘└────┘│
│                                             │
│  Desde:  ┌──────────────────────────┐       │
│          │ 📅  28 de febrero 2026   │       │ ← DatePicker
│          └──────────────────────────┘       │
│                                             │
│  Hasta:  ○ Sin fecha de fin                 │ ← Toggle
│          ● Con fecha de fin                 │
│          ┌──────────────────────────┐       │
│          │ 📅  31 de marzo 2026     │       │ ← (visible solo si toggle activo)
│          └──────────────────────────┘       │
├─────────────────────────────────────────────┤
│  Hora (opcional)                            │
│  ○ Sin hora específica                      │ ← toggle default off
│  ● A las:  ┌──────────────────┐             │
│            │ 🕐  10:00 AM     │             │ ← (visible solo si toggle activo)
│            └──────────────────┘             │
├─────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────┐│
│  │          GUARDAR TAREA                  ││ ← botón gradiente DarkGreen→LightGreen
│  └─────────────────────────────────────────┘│
└─────────────────────────────────────────────┘
```

---

## 5. DatePicker BottomSheet

Al tocar cualquier campo de fecha, se abre un segundo `ModalBottomSheet` sobre el formulario:

```
┌─────────────────────────────────────────────┐
│  ───  (drag handle)                          │
│  Seleccionar fecha          [Cancelar] [OK]  │
├─────────────────────────────────────────────┤
│                                             │
│       ◀   Marzo 2026   ▶                   │ ← navegación mes
│                                             │
│   Lu  Ma  Mi  Ju  Vi  Sa  Do               │
│                            1                │
│    2   3   4   5   6   7   8               │
│    9  10  11  12  13  14  15               │
│   16  17  18  19  20  21  22               │
│   23  24  25  26  27  28  29               │
│   30  31                                   │
│                                             │
│  Día seleccionado: círculo verde (#1EC691)  │
│  Hoy: borde verde sutil                    │
│  Días pasados: texto gris opaco            │
└─────────────────────────────────────────────┘
```

**Implementación recomendada:** `DatePicker` de Material3 en modo `INPUT` o `CALENDAR` dentro de un `DatePickerDialog` / `ModalBottomSheet`.

---

## 6. Estados de UI

```kotlin
data class CreateTaskUiState(
    // Campos del formulario
    val title: String = "",
    val description: String = "",

    // Modo scheduling
    val schedulingMode: SchedulingMode = SchedulingMode.ONCE,

    // Modo ONCE
    val scheduledDate: LocalDate? = null,            // null = no elegida aún

    // Modo RECURRENT
    val selectedDays: Set<Int> = emptySet(),         // 1=Lun ... 7=Dom
    val startDate: LocalDate = LocalDate.today(),
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,

    // Hora (ambos modos)
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,

    // Estado de UI
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: Set<CreateTaskValidationError> = emptySet(),

    // DatePicker
    val activeDatePickerField: DatePickerField? = null  // cuál campo está editándose
)

enum class SchedulingMode { ONCE, RECURRENT }

enum class DatePickerField { SCHEDULED_DATE, START_DATE, END_DATE }
```

---

## 7. Acciones de UI

```kotlin
sealed interface CreateTaskUiAction {
    data class OnTitleChange(val value: String) : CreateTaskUiAction
    data class OnDescriptionChange(val value: String) : CreateTaskUiAction
    data class OnSchedulingModeChange(val mode: SchedulingMode) : CreateTaskUiAction
    data class OnDateSelected(val field: DatePickerField, val date: LocalDate) : CreateTaskUiAction
    data class OnDayToggled(val day: Int) : CreateTaskUiAction
    data class OnHasEndDateToggled(val active: Boolean) : CreateTaskUiAction
    data class OnHasTimeToggled(val active: Boolean) : CreateTaskUiAction
    data class OnTimeSelected(val time: LocalTime) : CreateTaskUiAction
    data class OnDatePickerOpened(val field: DatePickerField) : CreateTaskUiAction
    data object OnDatePickerDismissed : CreateTaskUiAction
    data object OnSubmit : CreateTaskUiAction
    data object OnDismiss : CreateTaskUiAction
}
```

---

## 8. Validaciones Frontend (pre-submit)

```kotlin
sealed interface CreateTaskValidationError {
    data object TitleEmpty : CreateTaskValidationError
    data object NoDateSelected : CreateTaskValidationError      // modo ONCE sin fecha
    data object NoDaysSelected : CreateTaskValidationError      // modo RECURRENT sin días
    data object EndDateBeforeStart : CreateTaskValidationError
}

fun CreateTaskUiState.validate(): Set<CreateTaskValidationError> = buildSet {
    if (title.isBlank()) add(CreateTaskValidationError.TitleEmpty)
    if (schedulingMode == SchedulingMode.ONCE && scheduledDate == null)
        add(CreateTaskValidationError.NoDateSelected)
    if (schedulingMode == SchedulingMode.RECURRENT && selectedDays.isEmpty())
        add(CreateTaskValidationError.NoDaysSelected)
    if (hasEndDate && endDate != null && endDate < startDate)
        add(CreateTaskValidationError.EndDateBeforeStart)
}
```

---

## 9. Mapeo UiState → CreateTaskRequest

```kotlin
fun CreateTaskUiState.toRequest(): CreateTaskRequest = when (schedulingMode) {
    SchedulingMode.ONCE -> CreateTaskRequest(
        title = title.trim(),
        description = description.trimOrNull(),
        scheduledDate = scheduledDate,
        scheduledTime = if (hasTime) scheduledTime else null,
        daysOfWeek = null,
        startDate = null,
        endDate = null
    )
    SchedulingMode.RECURRENT -> CreateTaskRequest(
        title = title.trim(),
        description = description.trimOrNull(),
        scheduledDate = null,
        scheduledTime = if (hasTime) scheduledTime else null,
        daysOfWeek = selectedDays.sorted(),
        startDate = startDate,
        endDate = if (hasEndDate) endDate else null
    )
}
```

---

## 10. Arquitectura de Archivos

```
presentation/screen/add/task/
    CreateTaskScreen.kt           ← UI pura, recibe UiState + callbacks
    CreateTaskScreenOwner.kt      ← inyecta VM, conecta estado
    CreateTaskViewModel.kt        ← lógica de formulario + API call
    components/
        SchedulingModeToggle.kt   ← segmented button Una vez / Se repite
        DayChipsRow.kt            ← fila Lun Ma Mi Ju Vi Sá Do
        DateFieldButton.kt        ← botón que abre el DatePicker
        TimeFieldButton.kt        ← botón que abre el TimePicker
    model/
        CreateTaskUiState.kt
        CreateTaskUiAction.kt
        CreateTaskValidationError.kt
        SchedulingMode.kt

data/remote/api/task/
    TaskApiService.kt             ← POST /api/v1/tasks

data/remote/dto/request/
    CreateTaskRequest.kt

data/remote/dto/response/task/
    TaskResponse.kt               ← response del backend (id, title, scheduledDate, etc.)

data/remote/datasource/
    TaskRemoteDataSource.kt

data/repository/
    TaskRepositoryImpl.kt

domain/repository/
    TaskRepository.kt

domain/model/task/
    Task.kt                       ← domain model

domain/usecase/task/
    CreateTaskUseCase.kt
    result/
        CreateTaskResult.kt       ← Success(task) | ServerError | NetworkError

di/
    TaskModule.kt
```

---

## 11. Colores y Tokens de Diseño

| Elemento | Color | Hex |
|----------|-------|-----|
| Badge tipo "TAREA" | TaskAccent | `#1976D2` |
| Fondo badge tipo | TaskBackground | `#E3F2FD` |
| Día seleccionado (chip) | LightGreen | `#1EC691` |
| Día no seleccionado | BorderDefault | `#E0E0E0` |
| Fecha seleccionada (calendario) | LightGreen | `#1EC691` |
| Botón guardar (inicio) | DarkGreen | `#003C3C` |
| Botón guardar (fin) | LightGreen | `#1EC691` |
| Toggle activo | LightGreen | `#1EC691` |
| Toggle inactivo | SurfaceCard | `#F8F8F8` |
| Fondo pantalla | SurfaceLight | `#FFFFFF` |
| Error validación | ErrorRed | `#E53935` |

---

## 12. Conexión con DailyTabViewModel

Cuando la creación es exitosa, el Daily debe recargarse:

```kotlin
// En DailyScreenOwner.kt
CreateTaskScreenOwner(
    onSuccess = { viewModel.onAction(DailyUiAction.OnRefresh) },
    onDismiss = { /* cerrar el bottom sheet */ }
)
```

El `CreateTaskViewModel` expone un `isSuccess` en el `UiState`.
El `CreateTaskScreenOwner` observa ese campo y llama `onSuccess()` cuando cambia a `true`.

---

## 13. Koin — TaskModule

```kotlin
val taskModule = module {
    factory { TaskRemoteDataSource(apiService = get<TaskApiService>()) }
    single<TaskRepository> { TaskRepositoryImpl(remoteDataSource = get()) }
    factory { CreateTaskUseCase(repository = get(), dispatcher = get()) }
    viewModel { CreateTaskViewModel(createTaskUseCase = get(), language = get()) }
}
```

---

---

## 14. Notas de Implementación (2026-03-09)

### Diferencias con el diseño original

| Aspecto | Diseño original | Implementación final |
|---------|----------------|---------------------|
| Contenedor | ModalBottomSheet | Full-screen via `AppRoute.CreateTask` |
| Validación de título | `isError` en TextField | Botón "Guardar" disabled si título vacío |
| Selección de días | `FilterChip` | `DayChipComponent` circular personalizado |
| Toggle hora/endDate | Texto `○ / ●` | `SwitchComponent` con icono |
| Componentes de fecha/hora | Inline en CreateTaskScreen | Extraídos a `*Component.kt` reutilizables |
| Feedback de éxito | Genérico | `SuccessDialog` con animación Lottie |
| Feedback de error | Genérico | `SnackbarComponent` con variantes |
| Refresh Daily | `onSuccess` callback | `LifecycleResumeEffect` en `DailyScreenOwner` |

### Componentes reutilizables extraídos

Todos siguen el patrón `data class *Params` + `@Composable fun *Component`:

- `SwitchComponent` / `SwitchParams`
- `DayChipComponent` / `DayChipParams`
- `DateSelectorComponent` / `DateSelectorParams`
- `TimeSelectorComponent` / `TimeSelectorParams`
- `TimePickerDialogComponent` / `TimePickerDialogParams`
- `DatePickerBottomSheetComponent` (renombrado)
- `SnackbarComponent` / `SnackbarParams`
- `SuccessDialog` / `SuccessDialogParams`

### Archivos creados

Ver `CHANGELOG.md` sección `[Unreleased] - 2026-03-09` para listado completo.

---

*Spec generado el 28/02/2026. Actualizado el 09/03/2026 con notas de implementación.*
