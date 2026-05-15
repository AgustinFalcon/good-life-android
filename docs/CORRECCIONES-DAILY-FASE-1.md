# 🔧 Correcciones Realizadas: Daily - FASE 1

**Fecha:** 2026-02-05  
**Objetivo:** Corregir todos los problemas identificados en la implementación de Daily (FASE 1)

---

## 📝 Resumen

El usuario identificó múltiples problemas durante la implementación de FASE 1:

1. ❌ `GoodLifeApiService.kt` tenía endpoints duplicados y mal definidos
2. ❌ `DailyRemoteDataSource.kt` tenía método `getDailyLogToday()` innecesario
3. ❌ `DailyTabViewModel` estaba registrado DUPLICADO (appModule + dailyModule)
4. ❌ `DailyTabViewModel` NO llamaba a los UseCases (solo manejaba navegación de fechas)
5. ❌ `DailyUiState` era un simple data class (faltaba Loading/Error states)
6. ❌ Plan de implementación asumía Domain Models inexistentes
7. ❌ Dudas sobre arquitectura (ApiExt, validaciones backend, Response vs Dto)

---

## ✅ Correcciones Aplicadas

### 1. GoodLifeApiService.kt

**Problema:** Endpoints duplicados, mal documentados, y mezclando Response/Dto.

**Solución:**
- ✅ Eliminados endpoints duplicados
- ✅ Solo quedaron 2 endpoints:
  - `GET /api/v1/daily-logs/{date}` → obtener daily log por fecha
  - `PATCH /api/v1/daily-logs/items/{itemId}/status` → actualizar status
- ✅ Ambos devuelven `BaseResponse<DailyLogResponse>` (no Dto)
- ✅ KDoc completo con formato de fecha, query params, etc.

```kotlin
@GET("api/v1/daily-logs/{date}")
suspend fun getDailyLogByDate(
    @Path("date") date: String  // ISO-8601: "2026-02-05"
): BaseResponse<DailyLogResponse>

@PATCH("api/v1/daily-logs/items/{itemId}/status")
suspend fun updateItemStatus(
    @Path("itemId") itemId: Long,
    @Query("status") status: String  // "COMPLETED", "SKIPPED", "PENDING"
): BaseResponse<DailyLogResponse>
```

**Eliminados:**
- ❌ `getDailyLogToday()` (innecesario, usar `getDailyLogByDate(today)`)
- ❌ Endpoints con `@POST` incorrectos
- ❌ Endpoints con `@Field` en GET/PATCH

---

### 2. DailyRemoteDataSource.kt

**Problema:** Método `getDailyLogToday()` innecesario.

**Solución:**
- ✅ Eliminado `getDailyLogToday()`
- ✅ KDoc completo para cada método
- ✅ Conversión correcta `ItemStatus.name` → String para el backend

```kotlin
suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLogResponse> {
    return executeApiCall {
        apiService.updateItemStatus(
            itemId = itemId,
            status = status.name  // ItemStatus.COMPLETED → "COMPLETED"
        )
    }
}
```

---

### 3. DailyRepository y DailyRepositoryImpl

**Problema:** Tipo de retorno incorrecto (usaba Response en vez de Domain Model).

**Aclaración:** En esta arquitectura, **NO hay Domain Models para Daily**.
Se usa directamente `DailyLogResponse` (Response del backend).

**Solución:**
- ✅ `DailyRepository` devuelve `Result<DailyLogResponse>`
- ✅ `DailyRepositoryImpl` usa `DailyRemoteDataSource` (no `apiService` directamente)
- ✅ KDoc completo con notas sobre FASE 1 y FASE 2

```kotlin
interface DailyRepository {
    suspend fun getDailyLog(date: LocalDate): Result<DailyLogResponse>
    suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLogResponse>
}

class DailyRepositoryImpl(
    private val remoteDataSource: DailyRemoteDataSource
) : DailyRepository {
    override suspend fun getDailyLog(date: LocalDate): Result<DailyLogResponse> {
        return remoteDataSource.getDailyLogByDate(date.toString())  // ISO-8601
    }
    // ...
}
```

---

### 4. GetDailyItemsUseCase y UpdateItemStatusUseCase

**Problema:** GetDailyItemsUseCase tenía comentarios con dudas sobre manejo de errores.

**Aclaración:** El UseCase NO debe interpretar errores. Solo pasa el Result al ViewModel.

**Solución:**
- ✅ UseCase devuelve `Result<DailyLogResponse>` tal cual
- ✅ ViewModel es responsable de mapear Result.Error → UiState.Error
- ✅ KDoc completo explicando responsabilidades

```kotlin
class GetDailyItemsUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {
    suspend operator fun invoke(date: LocalDate): Result<DailyLogResponse> {
        return withContext(dispatcher.io) {
            repository.getDailyLog(date)
        }
    }
}
```

---

### 5. DailyUiState y DailyUiAction

**Problema:** 
- `DailyUiState` era un simple data class (sin Loading/Error)
- `DailyUiAction` solo tenía navegación (faltaba OnRefresh, OnItemClick, etc.)

**Solución:**
- ✅ `DailyUiState` convertido a sealed interface con 3 estados:
  - `Loading`: Skeleton/shimmer
  - `Success`: Datos cargados (con items, completionRate, etc.)
  - `Error`: Mensaje de error
- ✅ `DailyUiAction` agregadas acciones faltantes:
  - `OnRefresh`: Pull-to-refresh
  - `OnItemClick`: Click en un item
  - `OnItemStatusChange`: Cambiar status (checkbox, skip, etc.)

```kotlin
sealed interface DailyUiState {
    data object Loading : DailyUiState
    
    data class Success(
        val date: LocalDate,
        val dayNumber: Int,
        val headerText: String,
        val monthYear: String,
        val showFullDate: Boolean,
        val completionRate: Double,
        val items: List<DailyItemUiModel>,
        val isRefreshing: Boolean = false
    ) : DailyUiState
    
    data class Error(val message: String) : DailyUiState
}

data class DailyItemUiModel(
    val id: Long,
    val type: DailyItemType,
    val title: String,
    val description: String?,
    val scheduledTime: String?,
    val status: ItemStatus
)
```

---

### 6. DailyTabViewModel

**Problema:** ViewModel antiguo solo manejaba navegación de fechas, NO cargaba datos.

**Solución:**
- ✅ Inyección de UseCases (`GetDailyItemsUseCase`, `UpdateItemStatusUseCase`)
- ✅ `loadItems()` que llama al UseCase y mapea Result → UiState
- ✅ `updateItemStatus()` que actualiza status y recarga datos
- ✅ `buildSuccessState()` que formatea fechas y mapea Response → UiModel
- ✅ `toUiModel()` que extrae título correcto según el tipo de item
- ✅ `mapErrorToUserMessage()` que convierte ApiException → mensaje de usuario

```kotlin
private fun loadItems() {
    viewModelScope.launch {
        _uiState.value = DailyUiState.Loading

        when (val result = getDailyItemsUseCase(currentDate)) {
            is Result.Success -> {
                _uiState.value = buildSuccessState(result.data)
            }
            is Result.Error -> {
                _uiState.value = DailyUiState.Error(
                    message = mapErrorToUserMessage(result.exception)
                )
            }
            is Result.Loading -> { /* ya está en Loading */ }
        }
    }
}

private fun DailyItemResponse.toUiModel(): DailyItemUiModel {
    val (title, description) = when (type) {
        DailyItemType.TASK -> task?.title to task?.description
        DailyItemType.HABIT -> habitLog?.habitName to null
        DailyItemType.WORKOUT -> workout?.name to "Rutina: ${workout?.routineName}"
        DailyItemType.MEAL -> mealPlan?.mealName to "${mealPlan?.calories} kcal"
    }
    // ...
}
```

---

### 7. DI: AppModule y DailyModule

**Problema:** `DailyTabViewModel` estaba registrado DUPLICADO en ambos módulos.

**Solución:**
- ✅ Eliminado `DailyTabViewModel` de `appModule`
- ✅ `dailyModule` actualizado con:
  - `DailyRemoteDataSource` (factory)
  - `DailyRepository` (singleton)
  - `GetDailyItemsUseCase` (factory)
  - `UpdateItemStatusUseCase` (factory)
  - `DailyTabViewModel` (viewModel)
- ✅ `dailyModule` ya estaba registrado en `GoodLifeApp.kt`

```kotlin
val dailyModule = module {
    factory { DailyRemoteDataSource(apiService = get()) }
    single<DailyRepository> { DailyRepositoryImpl(remoteDataSource = get()) }
    factory { GetDailyItemsUseCase(repository = get(), dispatcher = get()) }
    factory { UpdateItemStatusUseCase(repository = get(), dispatcher = get()) }
    viewModel { 
        DailyTabViewModel(
            dateProvider = get(),
            language = get(),
            getDailyItemsUseCase = get(),
            updateItemStatusUseCase = get()
        )
    }
}
```

---

### 8. Response DTOs: DailyLogResponse y DailyItemResponse

**Problema:** Faltaban campos opcionales (task, habitLog, workout, mealPlan) y @Serializable.

**Solución:**
- ✅ `DailyLogResponse` con `@Serializable` y KDoc completo
- ✅ `DailyItemResponse` con campos opcionales según tipo
- ✅ `@SerialName("itemType")` para mapear JSON del backend
- ✅ `WorkoutSummaryDto` y `MealSummaryDto` completados con campos del backend

```kotlin
@Serializable
data class DailyItemResponse(
    val id: Long,
    @SerialName("itemType")
    val type: DailyItemType,
    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: ItemStatus,
    
    // Solo uno será no-null según el tipo
    val task: TaskSummaryDto? = null,
    val habitLog: HabitLogSummaryDto? = null,
    val workout: WorkoutSummaryDto? = null,
    val mealPlan: MealSummaryDto? = null
)
```

---

### 9. ApiExt.kt - Aclaración de Arquitectura

**Problema:** Usuario preguntó si debería usar BaseDataSource u otro patrón.

**Solución:**
- ✅ Agregado KDoc extenso explicando por qué Extension Functions es el mejor patrón
- ✅ Comparación con BaseDataSource (herencia vs composición)
- ✅ Otras alternativas profesionales (Result builders, Adapter Pattern)
- ✅ Conclusión: Extension Functions es el más recomendado para KMP

```kotlin
/**
 * ## ¿Por qué Extension Functions y no BaseDataSource?
 *
 * ### ✅ CORRECTO (actual):
 * - No requiere herencia (composición > herencia)
 * - Fácil de testear (mock del apiService)
 * - Flexible y funcional
 * - Sigue principio DRY
 *
 * ### ❌ INCORRECTO (BaseDataSource):
 * - Requiere herencia (acoplamiento)
 * - Menos flexible
 * - Más verboso
 */
```

---

### 10. RegisterRequest - Aclaración sobre Validaciones

**Problema:** Usuario pensó que las validaciones del backend eran "estúpidas".

**Solución:**
- ✅ KDoc extenso explicando por qué las validaciones del backend SON NECESARIAS
- ✅ Responsabilidades correctas: Frontend valida ANTES, Backend valida SIEMPRE
- ✅ Ejemplo de flujo correcto con UseCases de validación
- ✅ Ejemplo de ataque (Postman) y cómo el backend protege

```kotlin
/**
 * ## ¿Las validaciones del backend son "estúpidas"?
 * **NO.** Las validaciones del backend SON NECESARIAS por seguridad.
 *
 * ### Responsabilidades correctas:
 * 
 * #### Frontend (tu responsabilidad):
 * - Validar ANTES de hacer el request
 * - Mostrar errores en la UI
 * - Evitar requests innecesarios
 * 
 * #### Backend (responsabilidad del servidor):
 * - Validar SIEMPRE los datos recibidos
 * - Proteger contra ataques (Postman, curl, etc.)
 * - Mantener integridad de la base de datos
 */
```

---

## 📐 Arquitectura REAL (corregida)

### Flujo de datos completo:

```
UI (DailyScreen)
    ↓
ViewModel (DailyTabViewModel)
    - Usa DailyLog (Domain Model)
    - Mapea DailyItem → DailyItemUiModel
    ↓
UseCase (GetDailyItemsUseCase / UpdateItemStatusUseCase)
    - Devuelve Result<DailyLog>
    ↓
Repository (DailyRepositoryImpl)
    - Llama DataSource
    - Mapea Response → Domain: .map { it.toDomain() }
    - Devuelve Result<DailyLog>
    ↓
DataSource (DailyRemoteDataSource)
    - Llama ApiService
    - Usa executeApiCall()
    - Devuelve Result<DailyLogResponse>
    ↓
ApiService (GoodLifeApiService)
    - Retrofit parsea JSON → DailyLogResponse
    ↓
Backend → BaseResponse<DailyLogResponse>
    ↓
executeApiCall() → Result<DailyLogResponse>
    ↓
Repository .map { it.toDomain() } → Result<DailyLog>
    ↓
UseCase devuelve Result<DailyLog>
    ↓
ViewModel mapea Result<DailyLog> → UiState
    ↓
UI renderiza UiState
```

### Capas de mapeo:

```
Backend JSON
    ↓ (Retrofit + @Serializable)
DailyLogResponse (Data Layer)
    ↓ (.toDomain() en Repository)
DailyLog (Domain Layer)
    ↓ (.toUiModel() en ViewModel)
DailyItemUiModel (Presentation Layer)
    ↓
UI renderiza
```

### Capas:

```
┌─────────────────────────────────────────────┐
│ Presentation                                │
│ - DailyScreen.kt (Owner pattern)           │
│ - DailyTabViewModel.kt                     │
│ - DailyUiState.kt (Loading/Success/Error)  │
│ - DailyUiAction.kt                         │
│ - DailyItemUiModel.kt                      │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ Domain                                      │
│ - GetDailyItemsUseCase.kt                  │
│ - UpdateItemStatusUseCase.kt               │
│ - DailyRepository.kt (interface)           │
│ - DailyItemType.kt (enum)                  │
│ - ItemStatus.kt (enum)                     │
│                                             │
│ NO HAY: DailyLog, DailyItem (models)       │
│ Se usa directamente DailyLogResponse        │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ Data                                        │
│ - DailyRepositoryImpl.kt                   │
│ - DailyRemoteDataSource.kt                 │
│ - GoodLifeApiService.kt                    │
│                                             │
│ DTOs/Responses:                             │
│ - DailyLogResponse.kt                      │
│ - DailyItemResponse.kt                     │
│ - TaskSummaryDto.kt                        │
│ - HabitLogSummaryDto.kt                    │
│ - WorkoutSummaryDto.kt                     │
│ - MealSummaryDto.kt                        │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│ Core                                        │
│ - Result.kt (Success/Error/Loading)        │
│ - ApiExt.kt (executeApiCall)               │
│ - ApiException.kt                          │
│ - HttpCode.kt                              │
│ - BaseResponse.kt                          │
└─────────────────────────────────────────────┘
```

---

## ❓ Respuestas a Dudas

### 1. ¿Response o Dto o Domain?

**Respuesta CORREGIDA:** El proyecto SIEMPRE mapea Response → Domain:

**Patrón correcto (igual para Auth y Daily):**
- **Response** = Lo que viene del backend (DailyLogResponse, DailyItemResponse)
  - Tiene `@Serializable` para Retrofit
  - Vive en `data/remote/dto/response`
  - Tiene función `fun toDomain(): DomainModel`
- **Dto** = Sub-objetos dentro de Response (TaskSummaryDto, HabitLogSummaryDto)
- **Domain** = Modelo de negocio (DailyLog, DailyItem)
  - Libre de detalles de serialización
  - Vive en `domain/model`
  - Es lo que usan ViewModels y UseCases

**Flujo completo:**
```
Backend → BaseResponse<DailyLogResponse>
    ↓
DailyLogResponse.toDomain() → DailyLog
    ↓
Repository devuelve Result<DailyLog>
    ↓
UseCase devuelve Result<DailyLog>
    ↓
ViewModel usa DailyLog
```

**Dónde se hace el mapeo:** En el Repository, usando `Result.map { it.toDomain() }`

### 2. ¿UseCase debe manejar errores?

**Respuesta:** NO. El UseCase es un "pasa-manos".

- ✅ **UseCase:** Cambia dispatcher, llama repository, devuelve Result tal cual
- ✅ **ViewModel:** Mapea Result.Error → UiState.Error con mensaje de usuario

### 3. ¿Dónde van las validaciones?

**Respuesta:**
- **Frontend:** Valida ANTES del request (UX instantánea)
- **Backend:** Valida SIEMPRE (seguridad)

Ambas son necesarias.

### 4. ¿ApiExt.kt o BaseDataSource?

**Respuesta:** ApiExt.kt (Extension Functions) es la mejor opción:
- Composición > Herencia
- Más flexible y testeable
- Patrón usado por Google y JetBrains

---

## 🎯 Siguiente Paso

**Pendiente (Usuario):**
- [ ] Crear `DailyScreenOwner.kt` (patrón Owner)
- [ ] Actualizar `DailyScreen.kt` con LazyColumn y render de UiState
- [ ] Crear `DailyItemCard.kt` (componente visual de cada item)

**Ya hecho (Asistente):**
- [x] Corregir ApiService
- [x] Corregir DataSource
- [x] Corregir Repository
- [x] Corregir UseCases
- [x] Corregir ViewModel
- [x] Corregir UiState y UiAction
- [x] Corregir DI
- [x] Actualizar Response DTOs
- [x] Aclarar dudas arquitectónicas

---

## 📚 Referencias

- Patrón Owner: `LoginScreenOwner.kt`, `RegisterScreenOwner.kt`
- Patrón Repository: `AuthRepositoryImpl.kt`
- Patrón DataSource: `AuthRemoteDataSource.kt`
- Patrón UseCase: `LoginUseCase.kt`
- Patrón ViewModel: `LoginViewModel.kt`
- Patrón UiState: `LoginUiState.kt`
- Backend Spec: `back-end/GoodLife-backend-v2/docs/specs/modules/daily-log.spec.md`
