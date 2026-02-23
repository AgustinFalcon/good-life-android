package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AccessibilityTexts
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
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
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.toUiModel

/**
 * ViewModel del tab Daily (tareas diarias).
 *
 * ## Responsabilidades:
 * - Mantener fecha navegada ([currentDate])
 * - Cargar items del backend usando [GetDailyItemsUseCase]
 * - Actualizar status de items usando [UpdateItemStatusUseCase]
 * - Formatear fechas para UI (headerText, monthYear)
 * - Mapear Domain → UiModel
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
 * Repository mapea Result<DailyLogResponse> → Result<DailyLog>
 *     ↓
 * ViewModel mapea Result<DailyLog>
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
    private val dispatcher: DispatcherProvider,
    private val getDailyItemsUseCase: GetDailyItemsUseCase,
    private val updateItemStatusUseCase: UpdateItemStatusUseCase
) : ViewModel() {

    val dailyTexts get() = language.dailyTexts
    val accessibilityTexts: AccessibilityTexts get() = language.accessibilityTexts

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
     * Muestra Loading (skeleton) mientras se hace la request y,
     * al responder, renderiza únicamente los datos nuevos del backend.
     */
    private fun loadItems() {
        viewModelScope.launch(dispatcher.main) {
            _uiState.value = DailyUiState.Loading

            when (val result = getDailyItemsUseCase(currentDate)) {
                is Result.Success -> {
                    _uiState.value = buildSuccessState(result.data)
                }
                is Result.Error -> handleError(result.exception)
                is Result.Loading -> Unit
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
    private fun updateItemStatus(itemId: Long, newStatus: DailyItemStatus) {
        viewModelScope.launch(dispatcher.main) {
            val currentState = _uiState.value
            if (currentState is DailyUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            }

            when (val result = updateItemStatusUseCase(itemId, newStatus)) {
                is Result.Success -> {
                    _uiState.value = buildSuccessState(result.data)
                }
                is Result.Error -> handleError(result.exception)
                is Result.Loading -> Unit
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
        return DailyUiState.Success(
            date = currentDate,
            dayNumber = currentDate.day,
            headerText = formatHeaderText(currentDate),
            monthYear = formatMonthYear(currentDate),
            showFullDate = !isRelativeDate(currentDate),
            completionRate = dailyLog.completionRate,
            items = dailyLog.items.map { domainItem ->
                domainItem.toUiModel(
                    typeLabel = resolveTypeLabel(domainItem.type)
                )
            },
            isRefreshing = false
        )
    }

    private fun formatHeaderText(date: LocalDate): String {
        val today = dateProvider.today()
        val yesterday = dateProvider.yesterday()
        val tomorrow = dateProvider.tomorrow()

        return when (date) {
            today -> language.relativeTexts.today
            yesterday -> language.relativeTexts.yesterday
            tomorrow -> language.relativeTexts.tomorrow
            else -> {
                date.format(language.formats.dayNameAndDate)
                    .replaceFirstChar { it.uppercase() }
            }
        }
    }

    private fun resolveTypeLabel(type: DailyItemType): String {
        val labels = language.dailyItemLabels
        return when (type) {
            DailyItemType.TASK -> labels.task
            DailyItemType.HABIT -> labels.habit
            DailyItemType.WORKOUT -> labels.workout
            DailyItemType.MEAL -> labels.meal
        }
    }

    private fun formatMonthYear(date: LocalDate): String {
        return "${language.monthNames.names[date.month.ordinal]} ${date.year}"
    }

    private fun isRelativeDate(date: LocalDate): Boolean {
        val today = dateProvider.today()
        return date == today || date == dateProvider.yesterday() || date == dateProvider.tomorrow()
    }

    /**
     * Convierte un error técnico a UiState.Error genérico (code + message).
     *
     * El Owner decide qué UI mostrar según code:
     * - 500 -> escudo
     * - 401 -> sesión expirada
     * - 404 -> empty state
     * - 403 -> sin permisos
     * - 400 -> request inválido
     */
    private fun handleError(throwable: Throwable) {
        val code = (throwable as? ApiException)?.code
        val message = throwable.message ?: language.errorTexts.dataLoadError

        _uiState.value = DailyUiState.Error(
            code = code,
            message = message
        )
    }
}
