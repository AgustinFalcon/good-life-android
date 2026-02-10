package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyLog
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
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import com.agusstkd.goodlife.core.result.Result

/**
 * ViewModel del tab Daily (tareas diarias).
 *
 * ## Responsabilidades:
 * - Mantener fecha navegada ([currentDate])
 * - Cargar items del backend usando [GetDailyItemsUseCase]
 * - Actualizar status de items usando [UpdateItemStatusUseCase]
 * - Formatear fechas para UI (headerText, monthYear)
 * - Mapear Response → UiModel
 * - Interpretar errores del backend
 *
 * ## Flujo de datos:
 * ```
 * loadItems()
 *     ↓
 * GetDailyItemsUseCase(date)
 *     ↓
 * DailyRepository.getDailyLog(date)
 *     ↓
 * DailyRemoteDataSource.getDailyLogByDate(date)
 *     ↓
 * GoodLifeApiService.getDailyLogByDate(date)
 *     ↓
 * Backend → BaseResponse<DailyLogResponse>
 *     ↓
 * executeApiCall() → Result<DailyLogResponse>
 *     ↓
 * ViewModel mapea Result
 *     ↓
 * UiState.Success | UiState.Error
 * ```
 *
 * ## Filosofía:
 * TODO el formateo de fechas y mapeo Domain→UiModel se hace aquí.
 * La UI recibe strings y UI models listos para renderizar.
 *
 * ## Nota sobre mapeo:
 * El mapeo Response→Domain (DailyLogResponse→DailyLog) se hace en el Repository.
 * Este ViewModel solo mapea Domain→UiModel (DailyItem→DailyItemUiModel).
 */
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage,
    private val getDailyItemsUseCase: GetDailyItemsUseCase,
    private val updateItemStatusUseCase: UpdateItemStatusUseCase
) : ViewModel() {

    // Fecha actualmente navegada (puede ser hoy, ayer, mañana, etc.)
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

    /**
     * Carga los items del daily log del backend.
     *
     * Flujo:
     * 1. Muestra Loading
     * 2. Llama al UseCase
     * 3. Mapea Result.Success → buildSuccessState()
     * 4. Mapea Result.Error → UiState.Error con mensaje de usuario
     */
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
                is Result.Loading -> {
                    // Ya está en Loading
                }
            }
        }
    }

    /**
     * Actualiza el status de un item del daily log.
     *
     * El backend recalcula automáticamente el completionRate del daily log.
     *
     * @param itemId ID del item a actualizar
     * @param newStatus Nuevo status (COMPLETED, SKIPPED, PENDING)
     */
    private fun updateItemStatus(itemId: Long, newStatus: ItemStatus) {
        viewModelScope.launch {
            when (val result = updateItemStatusUseCase(itemId, newStatus)) {
                is Result.Success -> {
                    _uiState.value = buildSuccessState(result.data)
                }
                is Result.Error -> {
                    _uiState.value = DailyUiState.Error(
                        message = mapErrorToUserMessage(result.exception)
                    )
                }
                is Result.Loading -> {
                    // Mantener estado actual con isRefreshing=true si es Success
                    val currentState = _uiState.value
                    if (currentState is DailyUiState.Success) {
                        _uiState.value = currentState.copy(isRefreshing = true)
                    }
                }
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

    /**
     * Construye el UiState.Success con todos los campos formateados.
     *
     * Mapea DailyLog (Domain Model) → DailyUiState.Success (UI Model)
     *
     * @param dailyLog Domain Model del daily log
     * @return UiState.Success con datos listos para renderizar
     */
    private fun buildSuccessState(dailyLog: DailyLog): DailyUiState.Success {
        val today = dateProvider.today()
        val yesterday = dateProvider.yesterday()
        val tomorrow = dateProvider.tomorrow()

        val isRelativeDate = currentDate == today || currentDate == yesterday || currentDate == tomorrow

        val headerText = when (currentDate) {
            today -> language.relativeTexts.today
            yesterday -> language.relativeTexts.yesterday
            tomorrow -> language.relativeTexts.tomorrow
            else -> {
                // Formato: "Lun, 05 feb"
                val dayOfWeek = language.daysOfWeek.short[currentDate.dayOfWeek.ordinal]
                val monthName = language.monthNames.short[currentDate.monthNumber - 1]
                "$dayOfWeek, ${currentDate.dayOfMonth} $monthName"
            }
        }

        val monthYear = "${language.monthNames.names[currentDate.monthNumber - 1]} ${currentDate.year}"

        return DailyUiState.Success(
            date = currentDate,
            dayNumber = currentDate.dayOfMonth,
            headerText = headerText,
            monthYear = monthYear,
            showFullDate = !isRelativeDate,
            completionRate = dailyLog.completionRate,
            items = dailyLog.items.map { it.toUiModel() },
            isRefreshing = false
        )
    }

    /**
     * Mapea DailyItem (Domain Model) → DailyItemUiModel (UI Model).
     *
     * El título y descripción ya vienen extraídos en el Domain Model
     * (el mapeo Response→Domain se hizo en el Repository).
     *
     * Este método solo formatea la hora para la UI.
     */
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

    /**
     * Mapea ApiException → Mensaje de usuario.
     *
     * El backend envía el mensaje correcto en response.message,
     * pero si llega una excepción de red o timeout, mapeamos acá.
     */
    private fun mapErrorToUserMessage(throwable: Throwable): String {
        return when (throwable) {
            is ApiException.UnauthorizedException -> "Sesión expirada. Ingresá de nuevo"
            is ApiException.NotFoundException -> "No hay datos para esta fecha"
            is ApiException.ServerException -> "Error del servidor. Intentá más tarde"
            else -> throwable.message ?: "Error al cargar datos"
        }
    }
}
