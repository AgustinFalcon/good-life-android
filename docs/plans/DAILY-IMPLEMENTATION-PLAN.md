# 📋 Plan de Implementación: Daily Screen

**Fecha:** 2026-02-04  
**Responsable:** Usuario (con guía del asistente)  
**Estimación:** 3-5 sesiones de trabajo

---

## 🎯 Objetivo

Implementar la pantalla **Daily** que muestra:
- **Tareas** programadas para el día
- **Hábitos** con progreso
- **Entrenamientos** de la rutina activa
- **Comidas** planificadas

Con soporte para:
- ✅ Navegación entre días (← ayer | hoy | mañana →)
- ✅ Cache local con Room (offline-first)
- ✅ Actualización de status (PENDING → COMPLETED/SKIPPED)
- ✅ Pull-to-refresh

---

## 📐 Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER                     │
│                                                          │
│  DailyScreen (UI pura)                                   │
│       ↓ onAction                                         │
│  DailyTabViewModel                                       │
│       • currentDate: LocalDate                           │
│       • loadItems(date)                                  │
│       • updateItemStatus(itemId, status)                 │
└──────────────────────────────────────────────────────────┘
                         ↓ UseCase
┌──────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                         │
│                                                          │
│  GetDailyItemsUseCase(date: LocalDate)                   │
│  UpdateItemStatusUseCase(itemId, status)                 │
└──────────────────────────────────────────────────────────┘
                         ↓ Repository
┌──────────────────────────────────────────────────────────┐
│                      DATA LAYER                          │
│                                                          │
│  DailyRepositoryImpl                                     │
│       ├── Remote (ApiService)                            │
│       │   └── GET /api/v1/daily-logs/{date}             │
│       └── Local (RoomDao)                                │
│           └── daily_logs + daily_log_items              │
└──────────────────────────────────────────────────────────┘
```

---

## 📦 Módulos a Crear

### 1️⃣ Domain Layer

#### 1.1 Models

```kotlin
// domain/model/daily/DailyLog.kt
data class DailyLog(
    val id: Long,
    val userId: Long,
    val date: LocalDate,
    val completionRate: Double,
    val items: List<DailyItem>
)

// domain/model/daily/DailyItem.kt
data class DailyItem(
    val id: Long,
    val type: DailyItemType,
    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: ItemStatus,
    val title: String,
    val description: String?
)

// domain/model/daily/DailyItemType.kt
enum class DailyItemType {
    TASK, HABIT, WORKOUT, MEAL
}

// domain/model/daily/ItemStatus.kt
enum class ItemStatus {
    PENDING, COMPLETED, SKIPPED
}
```

#### 1.2 Repository Interface

```kotlin
// domain/repository/DailyRepository.kt
interface DailyRepository {
    suspend fun getDailyLog(date: LocalDate): Result<DailyLog>
    suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLog>
}
```

#### 1.3 Use Cases

```kotlin
// domain/usecase/daily/GetDailyItemsUseCase.kt
class GetDailyItemsUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {
    suspend operator fun invoke(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            repository.getDailyLog(date)
        }
    }
}

// domain/usecase/daily/UpdateItemStatusUseCase.kt
class UpdateItemStatusUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {
    suspend operator fun invoke(itemId: Long, status: ItemStatus): Result<DailyLog> {
        return withContext(dispatcher.io) {
            repository.updateItemStatus(itemId, status)
        }
    }
}
```

---

### 2️⃣ Data Layer

#### 2.1 DTOs (Remote)

```kotlin
// data/remote/dto/response/DailyLogDto.kt
@Serializable
data class DailyLogDto(
    val id: Long,
    val userId: Long,
    val date: String,  // "2026-02-03"
    val completionRate: Double,
    val items: List<DailyLogItemDto>
)

// data/remote/dto/response/DailyLogItemDto.kt
@Serializable
data class DailyLogItemDto(
    val id: Long,
    val itemType: String,  // "TASK", "HABIT", "WORKOUT", "MEAL"
    val referenceId: Long,
    val scheduledTime: String?,  // "08:00:00"
    val status: String,  // "PENDING", "COMPLETED", "SKIPPED"
    val task: TaskSummaryDto? = null,
    val habitLog: HabitLogSummaryDto? = null,
    val workout: WorkoutSummaryDto? = null,
    val meal: MealSummaryDto? = null
)

// data/remote/dto/response/TaskSummaryDto.kt
@Serializable
data class TaskSummaryDto(
    val id: Long,
    val title: String,
    val description: String?
)

// Similar para HabitLogSummaryDto, WorkoutSummaryDto, MealSummaryDto
```

#### 2.2 Entities (Local - Room)

```kotlin
// data/local/entity/DailyLogEntity.kt
@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey val date: String,  // "2026-02-03"
    val id: Long,
    val userId: Long,
    val completionRate: Double
    // ✅ SIN cachedAt: Usamos Stale-While-Revalidate (SPEC-006)
    // El backend es siempre la fuente de verdad
)

// data/local/entity/DailyLogItemEntity.kt
@Entity(
    tableName = "daily_log_items",
    foreignKeys = [
        ForeignKey(
            entity = DailyLogEntity::class,
            parentColumns = ["date"],
            childColumns = ["dailyLogDate"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DailyLogItemEntity(
    @PrimaryKey val id: Long,
    val dailyLogDate: String,  // FK
    val itemType: String,
    val referenceId: Long,
    val scheduledTime: String?,
    val status: String,
    val title: String,
    val description: String?
)
```

#### 2.3 DAO

```kotlin
// data/local/dao/DailyDao.kt
@Dao
interface DailyDao {
    
    @Query("SELECT * FROM daily_logs WHERE date = :date")
    suspend fun getDailyLog(date: String): DailyLogEntity?
    
    @Query("SELECT * FROM daily_log_items WHERE dailyLogDate = :date ORDER BY scheduledTime ASC")
    suspend fun getDailyLogItems(date: String): List<DailyLogItemEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(dailyLog: DailyLogEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLogItems(items: List<DailyLogItemEntity>)
    
    @Transaction
    suspend fun insertDailyLogWithItems(
        dailyLog: DailyLogEntity,
        items: List<DailyLogItemEntity>
    ) {
        // Room ejecuta en transacción automáticamente
        insertDailyLog(dailyLog)
        insertDailyLogItems(items)
    }
    
    @Query("UPDATE daily_log_items SET status = :status WHERE id = :itemId")
    suspend fun updateItemStatus(itemId: Long, status: String)
    
    @Query("SELECT * FROM daily_log_items WHERE id = :itemId")
    suspend fun getDailyLogItemById(itemId: Long): DailyLogItemEntity
    
    @Query("DELETE FROM daily_logs WHERE date < :oldestDate")
    suspend fun deleteOldLogs(oldestDate: String)
}
```

#### 2.4 ApiService

```kotlin
// data/remote/api/GoodLifeApiService.kt
interface GoodLifeApiService {
    
    @GET("api/v1/daily-logs/today")
    suspend fun getTodayDailyLog(): BaseResponse<DailyLogDto>
    
    @GET("api/v1/daily-logs/{date}")
    suspend fun getDailyLog(
        @Path("date") date: String  // "2026-02-03"
    ): BaseResponse<DailyLogDto>
    
    @PATCH("api/v1/daily-logs/items/{itemId}/status")
    suspend fun updateItemStatus(
        @Path("itemId") itemId: Long,
        @Query("status") status: String  // "COMPLETED", "SKIPPED", "PENDING"
    ): BaseResponse<DailyLogDto>
}
```

#### 2.5 Mappers

```kotlin
// data/remote/mapper/DailyLogMapper.kt

fun DailyLogDto.toDomain(): DailyLog {
    return DailyLog(
        id = id,
        userId = userId,
        date = LocalDate.parse(date),  // "2026-02-03" → LocalDate
        completionRate = completionRate,
        items = items.map { it.toDomain() }
    )
}

fun DailyLogItemDto.toDomain(): DailyItem {
    return DailyItem(
        id = id,
        type = DailyItemType.valueOf(itemType),
        referenceId = referenceId,
        scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
        status = ItemStatus.valueOf(status),
        title = extractTitle(),
        description = extractDescription()
    )
}

private fun DailyLogItemDto.extractTitle(): String {
    return when (itemType) {
        "TASK" -> task?.title ?: "Tarea sin título"
        "HABIT" -> habitLog?.habitName ?: "Hábito sin nombre"
        "WORKOUT" -> workout?.name ?: "Entrenamiento"
        "MEAL" -> meal?.mealName ?: "Comida"
        else -> "Item desconocido"
    }
}

private fun DailyLogItemDto.extractDescription(): String? {
    return when (itemType) {
        "TASK" -> task?.description
        "HABIT" -> "${habitLog?.currentValue ?: 0} / ${habitLog?.targetValue ?: 0} ${habitLog?.unit ?: ""}"
        else -> null
    }
}

// data/local/mapper/DailyLogEntityMapper.kt

fun DailyLogEntity.toDomain(items: List<DailyLogItemEntity>): DailyLog {
    return DailyLog(
        id = id,
        userId = userId,
        date = LocalDate.parse(date),
        completionRate = completionRate,
        items = items.map { it.toDomain() }
    )
}

fun DailyLogItemEntity.toDomain(): DailyItem {
    return DailyItem(
        id = id,
        type = DailyItemType.valueOf(itemType),
        referenceId = referenceId,
        scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
        status = ItemStatus.valueOf(status),
        title = title,
        description = description
    )
}

fun DailyLog.toEntity(): DailyLogEntity {
    return DailyLogEntity(
        date = date.toString(),  // LocalDate → "2026-02-03"
        id = id,
        userId = userId,
        completionRate = completionRate
    )
}

fun DailyItem.toEntity(dailyLogDate: String): DailyLogItemEntity {
    return DailyLogItemEntity(
        id = id,
        dailyLogDate = dailyLogDate,
        itemType = type.name,
        referenceId = referenceId,
        scheduledTime = scheduledTime?.toString(),
        status = status.name,
        title = title,
        description = description
    )
}
```

#### 2.6 Repository Implementation (Stale-While-Revalidate)

```kotlin
// data/repository/DailyRepositoryImpl.kt
class DailyRepositoryImpl(
    private val apiService: GoodLifeApiService,
    private val dailyDao: DailyDao,
    private val dateProvider: DateProvider,
    private val dispatcher: DispatcherProvider
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                val dateString = date.toString()
                val today = dateProvider.today()

                // 🎯 OPTIMIZACIÓN: Días muy viejos (> 14 días) → cache only
                if (date < today.minus(14, DateTimeUnit.DAY)) {
                    val cached = getCachedDailyLog(dateString)
                    if (cached != null) {
                        return@suspendResultOf cached
                    }
                }

                // 🔥 ESTRATEGIA PRINCIPAL: Stale-While-Revalidate (SPEC-006)
                fetchDailyLogWithSWR(dateString)
            }
        }
    }

    /**
     * Stale-While-Revalidate Pattern (SPEC-006).
     * 
     * 1. Devuelve cache si existe (instantáneo)
     * 2. Consulta backend SIEMPRE (si hay internet)
     * 3. Actualiza cache con response fresca
     * 
     * Beneficios:
     * - UI instantánea (0ms)
     * - Backend como source of truth
     * - Funciona offline
     */
    private suspend fun fetchDailyLogWithSWR(dateString: String): DailyLog {
        // PASO 1: Obtener cache (si existe)
        val cached = getCachedDailyLog(dateString)

        // PASO 2: Intentar backend SIEMPRE (si hay internet)
        try {
            val response = apiService.getDailyLog(dateString)

            when {
                HttpCode.isSuccess(response.code) -> {
                    val fresh = response.data?.toDomain()
                        ?: throw ApiException.NotFoundException("Daily log no encontrado")

                    // ✅ Guardar en cache (revalidation)
                    saveDailyLogToCache(fresh)

                    // ✅ Devolver data fresca
                    return fresh
                }
                else -> {
                    // Backend error → usar cache si existe
                    if (cached != null) {
                        return cached
                    } else {
                        throw ApiException.fromCode(response.code, response.message)
                    }
                }
            }
        } catch (e: IOException) {
            // 🔴 SIN INTERNET → Modo offline
            if (cached != null) {
                return cached
            } else {
                throw NoDataAvailableException("Sin internet y sin cache", e)
            }
        } catch (e: Exception) {
            // Otro error → Fallback a cache
            if (cached != null) {
                return cached
            } else {
                throw e
            }
        }
    }

    private suspend fun getCachedDailyLog(dateString: String): DailyLog? {
        val logEntity = dailyDao.getDailyLog(dateString) ?: return null
        val itemEntities = dailyDao.getDailyLogItems(dateString)
        return logEntity.toDomain(itemEntities)
    }

    override suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                val statusString = status.name

                try {
                    // PASO 1: Intentar backend primero
                    val response = apiService.updateItemStatus(itemId, statusString)

                    when {
                        HttpCode.isSuccess(response.code) -> {
                            val dailyLog = response.data?.toDomain()
                                ?: throw ApiException.NotFoundException()

                            // PASO 2: Actualizar cache con respuesta
                            saveDailyLogToCache(dailyLog)

                            dailyLog
                        }
                        else -> {
                            throw ApiException.fromCode(response.code, response.message)
                        }
                    }
                } catch (e: IOException) {
                    // 🔴 OFFLINE: Actualizar solo cache (optimistic)
                    dailyDao.updateItemStatus(itemId, statusString)

                    // TODO FASE 2: Marcar como pendiente de sincronización
                    // syncQueueDao.enqueue(PendingSync(itemId, status))

                    // Devolver cache actualizado
                    val date = getDateForItem(itemId)
                    getCachedDailyLog(date.toString())
                        ?: throw NoDataAvailableException("No se pudo actualizar offline")
                }
            }
        }
    }

    private suspend fun getDateForItem(itemId: Long): LocalDate {
        val item = dailyDao.getDailyLogItemById(itemId)
        return LocalDate.parse(item.dailyLogDate)
    }
    
    private suspend fun saveDailyLogToCache(dailyLog: DailyLog) {
        val dateString = dailyLog.date.toString()
        val logEntity = dailyLog.toEntity()
        val itemEntities = dailyLog.items.map { it.toEntity(dateString) }
        
        // Room es rápido (~7ms para 10 items), no bloquea
        dailyDao.insertDailyLogWithItems(logEntity, itemEntities)
    }
}
```

---

### 3️⃣ Presentation Layer

#### 3.1 UiState

```kotlin
// presentation/screen/tabs/daily/model/DailyUiState.kt
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
    
    data class Error(
        val message: String
    ) : DailyUiState
}

// presentation/screen/tabs/daily/model/DailyItemUiModel.kt
data class DailyItemUiModel(
    val id: Long,
    val type: DailyItemType,
    val title: String,
    val description: String?,
    val scheduledTime: String?,  // "08:00" (formateado)
    val status: ItemStatus,
    val iconRes: Int,  // R.drawable.ic_task
    val colorRes: Int  // R.color.task_color
)
```

#### 3.2 UiAction

```kotlin
// presentation/screen/tabs/daily/model/DailyUiAction.kt
sealed interface DailyUiAction {
    data object OnPreviousDay : DailyUiAction
    data object OnNextDay : DailyUiAction
    data object OnRefresh : DailyUiAction
    data class OnItemClick(val itemId: Long) : DailyUiAction
    data class OnItemStatusChange(val itemId: Long, val newStatus: ItemStatus) : DailyUiAction
}
```

#### 3.3 ViewModel (actualizado con API)

```kotlin
// presentation/screen/tabs/daily/DailyTabViewModel.kt
import java.io.IOException

class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage,
    private val getDailyItemsUseCase: GetDailyItemsUseCase,
    private val updateItemStatusUseCase: UpdateItemStatusUseCase
) : ViewModel() {

    private var currentDate: LocalDate = dateProvider.today()

    private val _uiState = MutableStateFlow<DailyUiState>(DailyUiState.Loading)
    val uiState: StateFlow<DailyUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun onAction(action: DailyUiAction) {
        when (action) {
            DailyUiAction.OnPreviousDay -> navigateToPreviousDay()
            DailyUiAction.OnNextDay -> navigateToNextDay()
            DailyUiAction.OnRefresh -> refreshItems()
            is DailyUiAction.OnItemClick -> navigateToDetail(action.itemId)
            is DailyUiAction.OnItemStatusChange -> updateItemStatus(action.itemId, action.newStatus)
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.value = DailyUiState.Loading
            
            getDailyItemsUseCase(currentDate)
                .onSuccess { dailyLog ->
                    _uiState.value = DailyUiState.Success(
                        date = dailyLog.date,
                        dayNumber = dailyLog.date.dayOfMonth,
                        headerText = formatDateHeader(dailyLog.date),
                        monthYear = formatMonthYear(dailyLog.date),
                        showFullDate = !isRelativeDate(dailyLog.date),
                        completionRate = dailyLog.completionRate,
                        items = dailyLog.items.map { it.toUiModel() }
                    )
                }
                .onError { error ->
                    _uiState.value = DailyUiState.Error(
                        message = when (error) {
                            is ApiException.UnauthorizedException -> "Sesión expirada"
                            is ApiException.NotFoundException -> "No hay datos para esta fecha"
                            is ApiException.ServerException -> "Error del servidor. Intenta más tarde"
                            else -> error.message ?: "Error desconocido"
                        }
                    )
                }
        }
    }

    private fun refreshItems() {
        val currentState = _uiState.value
        if (currentState is DailyUiState.Success) {
            _uiState.value = currentState.copy(isRefreshing = true)
        }
        
        viewModelScope.launch {
            getDailyItemsUseCase(currentDate)
                .onSuccess { dailyLog ->
                    _uiState.value = DailyUiState.Success(
                        date = dailyLog.date,
                        dayNumber = dailyLog.date.dayOfMonth,
                        headerText = formatDateHeader(dailyLog.date),
                        monthYear = formatMonthYear(dailyLog.date),
                        showFullDate = !isRelativeDate(dailyLog.date),
                        completionRate = dailyLog.completionRate,
                        items = dailyLog.items.map { it.toUiModel() },
                        isRefreshing = false
                    )
                }
                .onError { error ->
                    _uiState.value = DailyUiState.Error(error.message ?: "Error al refrescar")
                }
        }
    }

    private fun updateItemStatus(itemId: Long, newStatus: ItemStatus) {
        val currentState = _uiState.value as? DailyUiState.Success ?: return

        // 🔥 OPTIMISTIC UI (SPEC-006): Actualizar UI ANTES del request
        val updatedItems = currentState.items.map { item ->
            if (item.id == itemId) {
                item.copy(status = newStatus)
            } else {
                item
            }
        }

        _uiState.value = currentState.copy(
            items = updatedItems,
            completionRate = calculateCompletionRate(updatedItems)
        )

        // Hacer request al backend
        viewModelScope.launch {
            updateItemStatusUseCase(itemId, newStatus)
                .onSuccess { dailyLog ->
                    // Backend confirmó → actualizar con data real
                    _uiState.value = DailyUiState.Success(
                        date = dailyLog.date,
                        dayNumber = dailyLog.date.dayOfMonth,
                        headerText = formatDateHeader(dailyLog.date),
                        monthYear = formatMonthYear(dailyLog.date),
                        showFullDate = !isRelativeDate(dailyLog.date),
                        completionRate = dailyLog.completionRate,
                        items = dailyLog.items.map { it.toUiModel() }
                    )
                }
                .onError { error ->
                    when (error) {
                        is IOException -> {
                            // Offline: mantener cambio optimista
                            // El Repository ya actualizó cache local
                        }
                        else -> {
                            // Error real: revertir cambio optimista
                            _uiState.value = currentState
                            showError(error.message ?: "Error al actualizar")
                        }
                    }
                }
        }
    }

    private fun calculateCompletionRate(items: List<DailyItemUiModel>): Double {
        if (items.isEmpty()) return 0.0
        val completed = items.count { 
            it.status == ItemStatus.COMPLETED || it.status == ItemStatus.SKIPPED 
        }
        return completed.toDouble() / items.size
    }

    private fun navigateToPreviousDay() {
        currentDate = currentDate.minus(1, DateTimeUnit.DAY)
        loadItems()
    }

    private fun navigateToNextDay() {
        currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        loadItems()
    }

    private fun navigateToDetail(itemId: Long) {
        // TODO: navegar a pantalla de detalle
    }

    // Funciones de formateo (igual que antes)
    private fun formatDateHeader(date: LocalDate): String { /* ... */ }
    private fun formatMonthYear(date: LocalDate): String { /* ... */ }
    private fun isRelativeDate(date: LocalDate): Boolean { /* ... */ }
}

// Extension para mapear DailyItem → DailyItemUiModel
private fun DailyItem.toUiModel(): DailyItemUiModel {
    return DailyItemUiModel(
        id = id,
        type = type,
        title = title,
        description = description,
        scheduledTime = scheduledTime?.let { "${it.hour}:${it.minute.toString().padStart(2, '0')}" },
        status = status,
        iconRes = when (type) {
            DailyItemType.TASK -> R.drawable.ic_task
            DailyItemType.HABIT -> R.drawable.ic_habit
            DailyItemType.WORKOUT -> R.drawable.ic_workout
            DailyItemType.MEAL -> R.drawable.ic_meal
        },
        colorRes = when (type) {
            DailyItemType.TASK -> R.color.task_color
            DailyItemType.HABIT -> R.color.habit_color
            DailyItemType.WORKOUT -> R.color.workout_color
            DailyItemType.MEAL -> R.color.meal_color
        }
    )
}
```

#### 3.4 Screen (actualizado con items)

```kotlin
// presentation/screen/tabs/daily/DailyScreen.kt
@Composable
fun DailyScreen(
    uiState: DailyUiState,
    onAction: (DailyUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is DailyUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        is DailyUiState.Success -> {
            DailySuccessContent(
                uiState = uiState,
                onAction = onAction,
                modifier = modifier
            )
        }
        
        is DailyUiState.Error -> {
            DailyErrorContent(
                message = uiState.message,
                onRetry = { onAction(DailyUiAction.OnRefresh) },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DailySuccessContent(
    uiState: DailyUiState.Success,
    onAction: (DailyUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { onAction(DailyUiAction.OnRefresh) }
    )
    
    Column(modifier = modifier.fillMaxSize()) {
        // Header con navegación de fecha
        DateHeaderComponent(
            dayNumber = uiState.dayNumber,
            headerText = uiState.headerText,
            monthYear = if (uiState.showFullDate) uiState.monthYear else null,
            onPreviousClick = { onAction(DailyUiAction.OnPreviousDay) },
            onNextClick = { onAction(DailyUiAction.OnNextDay) }
        )
        
        // Barra de progreso
        LinearProgressIndicator(
            progress = uiState.completionRate.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Text(
            text = "${(uiState.completionRate * 100).toInt()}% completado",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        // Lista de items
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            if (uiState.items.isEmpty()) {
                EmptyDailyContent(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.items,
                        key = { it.id }
                    ) { item ->
                        DailyItemCard(
                            item = item,
                            onClick = { onAction(DailyUiAction.OnItemClick(item.id)) },
                            onStatusChange = { newStatus ->
                                onAction(DailyUiAction.OnItemStatusChange(item.id, newStatus))
                            }
                        )
                    }
                }
            }
            
            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun DailyItemCard(
    item: DailyItemUiModel,
    onClick: () -> Unit,
    onStatusChange: (ItemStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del tipo de item
            Icon(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                tint = colorResource(item.colorRes),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Contenido
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    item.scheduledTime?.let { time ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                item.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Checkbox de status
            StatusCheckbox(
                status = item.status,
                onStatusChange = onStatusChange
            )
        }
    }
}

@Composable
private fun StatusCheckbox(
    status: ItemStatus,
    onStatusChange: (ItemStatus) -> Unit
) {
    val isChecked = status == ItemStatus.COMPLETED
    
    Checkbox(
        checked = isChecked,
        onCheckedChange = { checked ->
            onStatusChange(if (checked) ItemStatus.COMPLETED else ItemStatus.PENDING)
        }
    )
}

@Composable
private fun EmptyDailyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_calendar_empty),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No hay actividades para este día",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun DailyErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}
```

---

### 4️⃣ Dependency Injection

```kotlin
// di/DailyModule.kt
val dailyModule = module {
    
    // Repository
    single<DailyRepository> {
        DailyRepositoryImpl(
            apiService = get(),
            dailyDao = get(),
            dispatcher = get()
        )
    }
    
    // Use Cases
    factory { GetDailyItemsUseCase(repository = get(), dispatcher = get()) }
    factory { UpdateItemStatusUseCase(repository = get(), dispatcher = get()) }
    
    // ViewModel
    viewModel {
        DailyTabViewModel(
            dateProvider = get(),
            language = get(),
            getDailyItemsUseCase = get(),
            updateItemStatusUseCase = get()
        )
    }
}

// di/DatabaseModule.kt
val databaseModule = module {
    
    single {
        Room.databaseBuilder(
            androidContext(),
            GoodLifeDatabase::class.java,
            "goodlife_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    single { get<GoodLifeDatabase>().dailyDao() }
}

// GoodLifeApp.kt
class GoodLifeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@GoodLifeApp)
            modules(
                coreModule,
                networkModule,
                databaseModule,
                dailyModule,  // ← Nuevo
                // ... otros módulos
            )
        }
    }
}
```

---

## 📝 Checklist de Implementación

### Fase 1: Domain Layer ✅
- [ ] Crear `DailyLog.kt`, `DailyItem.kt`, `DailyItemType.kt`, `ItemStatus.kt`
- [ ] Crear `DailyRepository.kt` (interface)
- [ ] Crear `GetDailyItemsUseCase.kt`
- [ ] Crear `UpdateItemStatusUseCase.kt`

### Fase 2: Data Layer - Remote ✅
- [ ] Crear DTOs: `DailyLogDto.kt`, `DailyLogItemDto.kt`, `TaskSummaryDto.kt`, etc.
- [ ] Agregar endpoints a `GoodLifeApiService.kt`
- [ ] Crear mappers: `DailyLogMapper.kt` (DTO → Domain)

### Fase 3: Data Layer - Local ✅
- [ ] Crear entities: `DailyLogEntity.kt`, `DailyLogItemEntity.kt`
- [ ] Crear `DailyDao.kt`
- [ ] Agregar tablas a `GoodLifeDatabase.kt`
- [ ] Crear mappers: `DailyLogEntityMapper.kt` (Entity ↔ Domain)

### Fase 4: Data Layer - Repository ✅
- [ ] Implementar `DailyRepositoryImpl.kt` con lógica de cache
- [ ] Testear manejo de cache (fresco, viejo, offline)

### Fase 5: Presentation Layer ✅
- [ ] Actualizar `DailyUiState.kt` con Success/Error/Loading
- [ ] Actualizar `DailyUiAction.kt` con acciones de items
- [ ] Actualizar `DailyTabViewModel.kt` con UseCase integration
- [ ] Actualizar `DailyScreen.kt` con LazyColumn de items
- [ ] Crear `DailyItemCard.kt` composable

### Fase 6: Dependency Injection ✅
- [ ] Crear `DailyModule.kt`
- [ ] Registrar Repository, UseCases, ViewModel
- [ ] Agregar `databaseModule` si no existe
- [ ] Actualizar `GoodLifeApp.kt` con módulos

### Fase 7: Testing 🧪
- [ ] Test de Repository (cache, network, offline)
- [ ] Test de UseCases
- [ ] Test de ViewModel
- [ ] Test de Mappers

### Fase 8: Documentación 📚
- [ ] Actualizar `SPEC-005-network-service.md` con endpoints de Daily
- [ ] Documentar DTOs y flows en KDoc
- [ ] Actualizar `CHANGELOG.md`

---

## 🎯 Próximos Pasos (después de Daily)

1. **Tasks CRUD**: Crear, editar, eliminar tareas (con SWR)
2. **Habits CRUD**: Crear, editar hábitos con progreso (con SWR)
3. **Workouts**: Ejecutar entrenamientos de la rutina activa (con SWR)
4. **Meals**: Planificar comidas del día (con SWR)
5. **Pending Sync** (FUTURO): Sincronizar cambios offline cuando vuelva internet

**Importante:** Todos los módulos usarán **Stale-While-Revalidate** (SPEC-006).

---

## 📚 Referencias

- [SPEC-004: DateProvider Pattern](../specs/SPEC-004-date-provider.md)
- [SPEC-005: Network Service](../specs/SPEC-005-network-service.md)
- **[SPEC-006: Offline-First + SWR](../specs/SPEC-006-offline-first-swr.md)** ← Arquitectura base

---

**FIN DEL PLAN**

---

**Autor:** GoodLife Development Team  
**Fecha:** 2026-02-05 (actualizado con SWR)  
**Estado:** 📋 Listo para implementar
