# 📋 Plan de Implementación: Daily Screen

**Fecha:** 2026-02-05  
**Responsable:** Usuario  
**Enfoque:** Paso a paso → FASE 1 (API REST) → FASE 2 (Room/Cache)

> **Estado del código (2026-05-14):** En el repo ya existe **`DailyRepositoryImpl`** con **`DailyDao`** y estrategia **SWR** (éxito remoto → persiste en Room; error remoto → lectura desde cache). Este documento se conserva como guía y bitácora; si algo contradice `data/repository/DailyRepositoryImpl.kt` o `data/local/dao/DailyDao.kt`, **manda el código**.

---

## 🎯 Objetivo

Implementar la pantalla **Daily** que muestra todas las actividades del día:
- **Tareas** programadas
- **Hábitos** con progreso
- **Entrenamientos** de la rutina activa
- **Comidas** planificadas

---

## 📐 Enfoque: 2 Fases

| Fase | Objetivo | Duración | Cuándo |
|------|----------|----------|--------|
| **FASE 1** | API REST pura (con internet) | 2-3 horas | **AHORA** |
| **FASE 2** | Room/Cache (modo offline) | 2-3 horas | **FUTURO** |

---

# 🚀 FASE 1: API REST PURA (AHORA)

## Arquitectura

```
DailyScreen → DailyTabViewModel → GetDailyItemsUseCase → DailyRepository → ApiService → Backend
                                                                                            ↓
                                                                                      (Fuente de verdad 100%)
```

**Sin:** Room, cache, offline

---

## ✅ Checklist de FASE 1

### Paso 1: Domain Layer ✅
- [x] Crear `domain/model/daily/DailyLog.kt`
- [x] Crear `domain/model/daily/DailyItem.kt`
- [x] Crear `domain/model/daily/DailyItemType.kt`
- [x] Crear `domain/model/daily/DailyItemStatus.kt`
- [x] Crear `domain/repository/DailyRepository.kt`
- [x] Crear `domain/usecase/daily/GetDailyItemsUseCase.kt` — retorna `GetDailyItemsResult` (Option B)
- [x] Crear `domain/usecase/daily/UpdateItemStatusUseCase.kt` — retorna `UpdateItemStatusResult` (Option B)
- [x] Crear `domain/usecase/daily/result/GetDailyItemsResult.kt`
- [x] Crear `domain/usecase/daily/result/UpdateItemStatusResult.kt`

### Paso 2: Data Layer - DTOs ✅
- [x] Crear `data/remote/dto/response/DailyLogDto.kt`
- [x] Crear `data/remote/dto/response/DailyLogItemDto.kt`
- [x] Crear `data/remote/dto/response/TaskSummaryDto.kt`
- [x] Crear `data/remote/dto/response/HabitLogSummaryDto.kt`
- [x] Crear `data/remote/mapper/DailyLogMapper.kt`
- [x] Endpoints en `DailyApiService.kt` (GoodLifeApiService fue splitteada)

### Paso 3: Repository ✅
- [x] Crear `data/repository/DailyRepositoryImpl.kt`

### Paso 4: Presentation ✅
- [x] `DailyUiState.kt` — Success / Empty / Error / Loading + DailyItemHighlight enum
- [x] `DailyUiAction.kt`
- [x] `DailyTabViewModel.kt` — buildSuccessState con highlight logic
- [x] `DailyScreen.kt` con LazyColumn
- [x] `DailyItemCard.kt` — diseño completo con badge straddling, colores por tipo, toggle status

### Paso 5: DI ✅
- [x] `di/DailyModule.kt`
- [x] Registrado en `GoodLifeApp.kt`

### Paso 6: Testing con API Real ⏸️ Pendiente
- [ ] Conectar con backend real (actualmente usa datos de preview)
- [ ] Verificar carga de datos
- [ ] Verificar navegación entre días
- [ ] Verificar actualización de status

### Extras completados (no estaban en el plan original)
- [x] `DailyTexts.nextUp` y `DailyTexts.inProgress` en los 3 idiomas
- [x] `DailyItemStyle.iconCircleBackground` — paleta refinada según spec de diseño
- [x] Badge highlight a caballo del borde: `Box.padding(top) + badge.offset(y = -halfHeight)`
- [x] `HighlightBadge` extraído como composable privado reutilizable

---

## 📦 Código FASE 1

### 1️⃣ Domain Layer

#### 1.1 Models

```kotlin
// domain/model/daily/DailyLog.kt
package com.agusstkd.goodlife.domain.model.daily

import kotlinx.datetime.LocalDate

/**
 * Daily log unificado con todas las actividades del día.
 */
data class DailyLog(
    val id: Long,
    val userId: Long,
    val date: LocalDate,
    val completionRate: Double,
    val items: List<DailyItem>
)
```

```kotlin
// domain/model/daily/DailyItem.kt
package com.agusstkd.goodlife.domain.model.daily

import kotlinx.datetime.LocalTime

/**
 * Item individual en el daily log.
 */
data class DailyItem(
    val id: Long,
    val type: DailyItemType,
    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: ItemStatus,
    val title: String,
    val description: String?
)
```

```kotlin
// domain/model/daily/DailyItemType.kt
package com.agusstkd.goodlife.domain.model.daily

enum class DailyItemType {
    TASK,
    HABIT,
    WORKOUT,
    MEAL
}
```

```kotlin
// domain/model/daily/ItemStatus.kt
package com.agusstkd.goodlife.domain.model.daily

enum class ItemStatus {
    PENDING,
    COMPLETED,
    SKIPPED
}
```

#### 1.2 Repository Interface

```kotlin
// domain/repository/DailyRepository.kt
package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import kotlinx.datetime.LocalDate

/**
 * Repositorio para acceso a daily logs.
 * 
 * FASE 1: Solo backend (fuente de verdad 100%).
 */
interface DailyRepository {
    suspend fun getDailyLog(date: LocalDate): Result<DailyLog>
    suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLog>
}
```

#### 1.3 Use Cases

```kotlin
// domain/usecase/daily/GetDailyItemsUseCase.kt
package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Obtiene los items del daily log para una fecha.
 */
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
```

```kotlin
// domain/usecase/daily/UpdateItemStatusUseCase.kt
package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.coroutines.withContext

/**
 * Actualiza el status de un item del daily log.
 */
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

### 2️⃣ Data Layer - Remote

#### 2.1 DTOs

```kotlin
// data/remote/dto/response/DailyLogDto.kt
package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DailyLogDto(
    val id: Long,
    val userId: Long,
    val date: String,  // "2026-02-05" (ISO-8601)
    val completionRate: Double,
    val items: List<DailyLogItemDto>
)
```

```kotlin
// data/remote/dto/response/DailyLogItemDto.kt
package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.serialization.Serializable

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
```

```kotlin
// data/remote/dto/response/TaskSummaryDto.kt
package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TaskSummaryDto(
    val id: Long,
    val title: String,
    val description: String?
)
```

```kotlin
// data/remote/dto/response/HabitLogSummaryDto.kt
package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class HabitLogSummaryDto(
    val id: Long,
    val habitId: Long,
    val habitName: String,
    val currentValue: Int,
    val targetValue: Int,
    val progress: Double,
    val unit: String
)
```

#### 2.2 Mappers

```kotlin
// data/remote/mapper/DailyLogMapper.kt
package com.agusstkd.goodlife.data.remote.mapper

import com.agusstkd.goodlife.data.remote.dto.response.DailyLogDto
import com.agusstkd.goodlife.data.remote.dto.response.DailyLogItemDto
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun DailyLogDto.toDomain(): DailyLog {
    return DailyLog(
        id = id,
        userId = userId,
        date = LocalDate.parse(date),  // "2026-02-05" → LocalDate
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
        "HABIT" -> {
            val log = habitLog ?: return null
            "${log.currentValue} / ${log.targetValue} ${log.unit}"
        }
        else -> null
    }
}
```

#### 2.3 ApiService

```kotlin
// Agregar a: data/remote/api/GoodLifeApiService.kt

@GET("api/v1/daily-logs/{date}")
suspend fun getDailyLog(
    @Path("date") date: String  // "2026-02-05"
): BaseResponse<DailyLogDto>

@PATCH("api/v1/daily-logs/items/{itemId}/status")
suspend fun updateItemStatus(
    @Path("itemId") itemId: Long,
    @Query("status") status: String  // "COMPLETED", "SKIPPED", "PENDING"
): BaseResponse<DailyLogDto>
```

#### 2.4 Repository SIMPLE (sin Room)

```kotlin
// data/repository/DailyRepositoryImpl.kt
package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.network.HttpCode
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.result.suspendResultOf
import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import com.agusstkd.goodlife.data.remote.mapper.toDomain
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

/**
 * Implementación de [DailyRepository].
 * 
 * FASE 1: Solo consulta backend (fuente de verdad 100%).
 * Backend maneja toda la lógica de generación y cálculo de daily logs.
 */
class DailyRepositoryImpl(
    private val apiService: GoodLifeApiService,
    private val dispatcher: DispatcherProvider
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                // LocalDate.toString() devuelve ISO-8601 ("2026-02-05")
                val dateString = date.toString()
                
                val response = apiService.getDailyLog(dateString)
                
                when {
                    HttpCode.isSuccess(response.code) -> {
                        response.data?.toDomain()
                            ?: throw ApiException.NotFoundException("Daily log no encontrado")
                    }
                    else -> {
                        throw ApiException.fromCode(response.code, response.message)
                    }
                }
            }
        }
    }

    override suspend fun updateItemStatus(
        itemId: Long,
        status: ItemStatus
    ): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                val statusString = status.name  // ItemStatus.COMPLETED → "COMPLETED"
                
                val response = apiService.updateItemStatus(itemId, statusString)
                
                when {
                    HttpCode.isSuccess(response.code) -> {
                        response.data?.toDomain()
                            ?: throw ApiException.NotFoundException("Daily log no encontrado")
                    }
                    else -> {
                        throw ApiException.fromCode(response.code, response.message)
                    }
                }
            }
        }
    }
}
```

---

### 3️⃣ Presentation Layer

#### 3.1 UiState (actualizado)

```kotlin
// presentation/screen/tabs/daily/model/DailyUiState.kt
package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDate

/**
 * Estados de la pantalla Daily.
 */
@Stable
sealed interface DailyUiState {
    
    /**
     * Cargando datos del backend.
     */
    data object Loading : DailyUiState
    
    /**
     * Datos cargados exitosamente.
     */
    data class Success(
        val date: LocalDate,
        val dayNumber: Int,
        val headerText: String,  // "Hoy", "Ayer", "Lun, 05 feb"
        val monthYear: String,   // "Febrero 2026"
        val showFullDate: Boolean,
        val completionRate: Double,
        val items: List<DailyItemUiModel>,
        val isRefreshing: Boolean = false
    ) : DailyUiState
    
    /**
     * Error al cargar datos.
     */
    data class Error(
        val message: String
    ) : DailyUiState
}

/**
 * Modelo UI de un item del daily log.
 */
@Stable
data class DailyItemUiModel(
    val id: Long,
    val type: DailyItemType,
    val title: String,
    val description: String?,
    val scheduledTime: String?,  // "08:00"
    val status: ItemStatus
)
```

#### 3.2 UiAction (actualizado)

```kotlin
// presentation/screen/tabs/daily/model/DailyUiAction.kt
package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import com.agusstkd.goodlife.domain.model.daily.ItemStatus

/**
 * Acciones de usuario en Daily screen.
 */
sealed interface DailyUiAction {
    data object OnPreviousDay : DailyUiAction
    data object OnNextDay : DailyUiAction
    data object OnRefresh : DailyUiAction
    data class OnItemClick(val itemId: Long) : DailyUiAction
    data class OnItemStatusChange(val itemId: Long, val newStatus: ItemStatus) : DailyUiAction
}
```

#### 3.3 ViewModel (actualizado)

```kotlin
// presentation/screen/tabs/daily/DailyTabViewModel.kt
package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.onError
import com.agusstkd.goodlife.core.result.onSuccess
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * ViewModel para Daily tab.
 * 
 * Responsabilidades:
 * - Mantener fecha navegada ([currentDate])
 * - Cargar items del backend
 * - Actualizar status de items
 * - Formatear fechas para UI
 */
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
            DailyUiAction.OnRefresh -> loadItems()
            is DailyUiAction.OnItemClick -> navigateToDetail(action.itemId)
            is DailyUiAction.OnItemStatusChange -> updateItemStatus(action.itemId, action.newStatus)
        }
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.value = DailyUiState.Loading
            
            getDailyItemsUseCase(currentDate)
                .onSuccess { dailyLog ->
                    _uiState.value = buildSuccessState(dailyLog)
                }
                .onError { error ->
                    _uiState.value = DailyUiState.Error(
                        message = when (error) {
                            is ApiException.UnauthorizedException -> "Sesión expirada"
                            is ApiException.NotFoundException -> "No hay datos para esta fecha"
                            is ApiException.ServerException -> "Error del servidor. Intenta más tarde"
                            else -> error.message ?: "Error al cargar datos"
                        }
                    )
                }
        }
    }

    private fun updateItemStatus(itemId: Long, newStatus: ItemStatus) {
        viewModelScope.launch {
            updateItemStatusUseCase(itemId, newStatus)
                .onSuccess { dailyLog ->
                    _uiState.value = buildSuccessState(dailyLog)
                }
                .onError { error ->
                    _uiState.value = DailyUiState.Error(
                        message = error.message ?: "Error al actualizar"
                    )
                }
        }
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
        // TODO: Implementar navegación a detalle
    }

    private fun buildSuccessState(dailyLog: DailyLog): DailyUiState.Success {
        return DailyUiState.Success(
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

    private fun formatDateHeader(date: LocalDate): String {
        val today = dateProvider.today()
        val yesterday = dateProvider.yesterday()
        val tomorrow = dateProvider.tomorrow()

        return when (date) {
            today -> language.relativeTexts.today
            yesterday -> language.relativeTexts.yesterday
            tomorrow -> language.relativeTexts.tomorrow
            else -> {
                // Formato: "Lun, 05 feb"
                val dayOfWeek = language.daysOfWeek.short[date.dayOfWeek.ordinal]
                val monthName = language.monthNames.short[date.monthNumber - 1]
                "$dayOfWeek, ${date.dayOfMonth} $monthName"
            }
        }
    }

    private fun formatMonthYear(date: LocalDate): String {
        val monthName = language.monthNames.names[date.monthNumber - 1]
        return "$monthName ${date.year}"
    }

    private fun isRelativeDate(date: LocalDate): Boolean {
        val today = dateProvider.today()
        return date == today || date == dateProvider.yesterday() || date == dateProvider.tomorrow()
    }

    private fun DailyItem.toUiModel(): DailyItemUiModel {
        return DailyItemUiModel(
            id = id,
            type = type,
            title = title,
            description = description,
            scheduledTime = scheduledTime?.let { 
                "${it.hour}:${it.minute.toString().padStart(2, '0')}" 
            },
            status = status
        )
    }
}
```

#### 3.4 Screen (actualizado)

```kotlin
// presentation/screen/tabs/daily/DailyScreen.kt
package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.presentation.components.header.DateHeaderComponent
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DailyScreen(
    uiState: DailyUiState,
    onAction: (DailyUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is DailyUiState.Loading -> {
            DailyLoadingContent(modifier = modifier)
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
private fun DailyLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DailySuccessContent(
    uiState: DailyUiState.Success,
    onAction: (DailyUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    
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
            modifier = Modifier.padding(horizontal = 16.dp, bottom = 8.dp)
        )
        
        // Lista de items con pull-to-refresh
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { onAction(DailyUiAction.OnRefresh) },
            modifier = Modifier.fillMaxSize()
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
        }
    }
}

@Composable
private fun EmptyDailyContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No hay actividades para este día",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

#### 3.5 DailyItemCard (nuevo)

```kotlin
// presentation/components/daily/DailyItemCard.kt
package com.agusstkd.goodlife.presentation.components.daily

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemUiModel

/**
 * Card para mostrar un item del daily log.
 */
@Composable
fun DailyItemCard(
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
            // Icono según tipo
            Icon(
                imageVector = getIconForType(item.type),
                contentDescription = null,
                tint = getColorForType(item.type),
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

private fun getIconForType(type: DailyItemType): ImageVector {
    return when (type) {
        DailyItemType.TASK -> Icons.Default.Check
        DailyItemType.HABIT -> Icons.Default.FavoriteBorder
        DailyItemType.WORKOUT -> Icons.Default.FitnessCenter
        DailyItemType.MEAL -> Icons.Default.Restaurant
    }
}

@Composable
private fun getColorForType(type: DailyItemType): androidx.compose.ui.graphics.Color {
    return when (type) {
        DailyItemType.TASK -> MaterialTheme.colorScheme.primary
        DailyItemType.HABIT -> MaterialTheme.colorScheme.secondary
        DailyItemType.WORKOUT -> MaterialTheme.colorScheme.tertiary
        DailyItemType.MEAL -> MaterialTheme.colorScheme.error
    }
}
```

---

### 4️⃣ Dependency Injection

```kotlin
// di/DailyModule.kt
package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.repository.DailyRepositoryImpl
import com.agusstkd.goodlife.domain.repository.DailyRepository
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyTabViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dailyModule = module {
    
    // Repository (FASE 1: sin Room)
    single<DailyRepository> {
        DailyRepositoryImpl(
            apiService = get(),
            dispatcher = get()
        )
    }
    
    // Use Cases
    factory { 
        GetDailyItemsUseCase(
            repository = get(),
            dispatcher = get()
        )
    }
    
    factory { 
        UpdateItemStatusUseCase(
            repository = get(),
            dispatcher = get()
        )
    }
    
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
```

```kotlin
// Agregar en: GoodLifeApp.kt
startKoin {
    androidContext(this@GoodLifeApp)
    modules(
        coreModule,
        networkModule,
        databaseModule,
        biometricModule,
        dailyModule,  // ← AGREGAR
        // ... otros módulos
    )
}
```

---

## ✅ FASE 1 Completada

Cuando termines la FASE 1, tu app:
- ✅ Carga datos del backend
- ✅ Muestra daily log del día actual
- ✅ Navega entre días (← ayer | hoy | mañana →)
- ✅ Actualiza status de items
- ✅ Muestra porcentaje de completitud
- ✅ Pull-to-refresh funciona

**Limitaciones esperadas:**
- ❌ No funciona sin internet (requiere conexión)
- ❌ Cada navegación hace request (puede ser lento)

**¿Cuándo pasar a FASE 2?**
- Cuando necesites entrenar sin internet
- Cuando quieras mejor performance (UI instantánea)
- Cuando marques hábitos y se corte conexión

---

---

# 🔮 FASE 2: ROOM + CACHE (FUTURO)

## Objetivo

Agregar soporte offline con **Stale-While-Revalidate** (SPEC-006):
- ✅ UI instantánea (cache first)
- ✅ Backend como fuente de verdad (revalidation)
- ✅ Funciona sin internet (gym, entrenamientos)
- ✅ No pierde progreso si se corta conexión

---

## Qué Agregar en FASE 2

### 1️⃣ Room Entities

```kotlin
// data/local/entity/DailyLogEntity.kt
@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey val date: String,  // "2026-02-05"
    val id: Long,
    val userId: Long,
    val completionRate: Double
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

### 2️⃣ DAO

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

### 3️⃣ Mappers Entity ↔ Domain

```kotlin
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
        date = date.toString(),
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

### 4️⃣ Repository con SWR

```kotlin
// data/repository/DailyRepositoryImpl.kt (actualizado)
class DailyRepositoryImpl(
    private val apiService: GoodLifeApiService,
    private val dailyDao: DailyDao,  // ← AGREGAR
    private val dateProvider: DateProvider,  // ← AGREGAR
    private val dispatcher: DispatcherProvider
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                val dateString = date.toString()
                val today = dateProvider.today()

                // Optimización: Días muy viejos → cache only
                if (date < today.minus(14, DateTimeUnit.DAY)) {
                    val cached = getCachedDailyLog(dateString)
                    if (cached != null) {
                        return@suspendResultOf cached
                    }
                }

                // SWR: Cache first + backend revalidation
                fetchDailyLogWithSWR(dateString)
            }
        }
    }

    private suspend fun fetchDailyLogWithSWR(dateString: String): DailyLog {
        val cached = getCachedDailyLog(dateString)

        try {
            val response = apiService.getDailyLog(dateString)

            when {
                HttpCode.isSuccess(response.code) -> {
                    val fresh = response.data?.toDomain()
                        ?: throw ApiException.NotFoundException()

                    saveDailyLogToCache(fresh)
                    return fresh
                }
                else -> {
                    return cached ?: throw ApiException.fromCode(response.code)
                }
            }
        } catch (e: IOException) {
            return cached ?: throw NoDataAvailableException("Sin internet y sin cache", e)
        }
    }

    override suspend fun updateItemStatus(
        itemId: Long,
        status: ItemStatus
    ): Result<DailyLog> {
        return withContext(dispatcher.io) {
            suspendResultOf {
                try {
                    val response = apiService.updateItemStatus(itemId, status.name)

                    when {
                        HttpCode.isSuccess(response.code) -> {
                            val dailyLog = response.data?.toDomain()
                                ?: throw ApiException.NotFoundException()

                            saveDailyLogToCache(dailyLog)
                            dailyLog
                        }
                        else -> {
                            throw ApiException.fromCode(response.code)
                        }
                    }
                } catch (e: IOException) {
                    // OFFLINE: Actualizar cache local
                    dailyDao.updateItemStatus(itemId, status.name)
                    
                    val date = getDateForItem(itemId)
                    getCachedDailyLog(date.toString())
                        ?: throw NoDataAvailableException("No se pudo actualizar offline")
                }
            }
        }
    }

    private suspend fun getCachedDailyLog(dateString: String): DailyLog? {
        val logEntity = dailyDao.getDailyLog(dateString) ?: return null
        val itemEntities = dailyDao.getDailyLogItems(dateString)
        return logEntity.toDomain(itemEntities)
    }

    private suspend fun saveDailyLogToCache(dailyLog: DailyLog) {
        val dateString = dailyLog.date.toString()
        val logEntity = dailyLog.toEntity()
        val itemEntities = dailyLog.items.map { it.toEntity(dateString) }
        dailyDao.insertDailyLogWithItems(logEntity, itemEntities)
    }

    private suspend fun getDateForItem(itemId: Long): LocalDate {
        val item = dailyDao.getDailyLogItemById(itemId)
        return LocalDate.parse(item.dailyLogDate)
    }
}
```

### 5️⃣ Actualizar DI

```kotlin
// di/DailyModule.kt (actualizado)
val dailyModule = module {
    
    // Repository (FASE 2: con Room)
    single<DailyRepository> {
        DailyRepositoryImpl(
            apiService = get(),
            dailyDao = get(),  // ← AGREGAR
            dateProvider = get(),  // ← AGREGAR
            dispatcher = get()
        )
    }
    
    // ... resto igual
}
```

---

## 📊 Comparación FASE 1 vs FASE 2

| Aspecto | FASE 1 (API only) | FASE 2 (Room/Cache) |
|---------|-------------------|---------------------|
| **Funciona con internet** | ✅ Sí | ✅ Sí |
| **Funciona SIN internet** | ❌ No | ✅ Sí |
| **UI instantánea** | ❌ No (espera red) | ✅ Sí (0ms) |
| **Backend = verdad** | ✅ Sí | ✅ Sí |
| **Complejidad** | 🟢 Simple | 🟡 Media |
| **Archivos adicionales** | 0 | +6 (entities, DAO, mappers) |

---

## 🎯 Cuándo Hacer FASE 2

Implementa FASE 2 cuando:
- ✅ Necesites entrenar sin internet (gym)
- ✅ Marques hábitos y se corte conexión
- ✅ Completes workout sin red
- ✅ Quieras UI más rápida (0ms)

**No antes.** Primero hacé que funcione con backend (FASE 1).

---

## 📚 Referencias

- [SPEC-004: DateProvider Pattern](../specs/SPEC-004-date-provider.md) - Formato ISO-8601
- [SPEC-005: Network Service](../specs/SPEC-005-network-service.md) - BaseResponse, HttpCode
- [SPEC-006: Offline-First + SWR](../specs/SPEC-006-offline-first-swr.md) - Arquitectura FASE 2

---

**Autor:** GoodLife Development Team  
**Fecha creación:** 2026-02-05 | **Última actualización:** 2026-02-24  
**Estado:** ✅ FASE 1 completada — pendiente conectar con API real
