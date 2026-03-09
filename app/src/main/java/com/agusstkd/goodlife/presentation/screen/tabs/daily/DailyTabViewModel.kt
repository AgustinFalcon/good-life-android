package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AccessibilityTexts
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
import com.agusstkd.goodlife.domain.usecase.daily.result.GetDailyItemsResult
import com.agusstkd.goodlife.domain.usecase.daily.result.UpdateItemStatusResult
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyFilter
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyItemHighlight
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.toUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * ViewModel del tab Daily (tareas diarias).
 *
 * ## Responsabilidades:
 * - Mantener fecha navegada ([currentDate])
 * - Cargar items del backend usando [GetDailyItemsUseCase]
 * - Actualizar status de items usando [UpdateItemStatusUseCase]
 * - Formatear fechas para UI (headerText, monthYear)
 * - Mapear Domain → UiModel
 *
 * ## Flujo de datos:
 * ```
 * loadItems()
 *     ↓
 * GetDailyItemsUseCase(date)           ← dueño del dispatcher IO
 *     ↓
 * DailyRepository.getDailyLog(date)
 *     ↓
 * DailyRemoteDataSource → DailyApiService → Backend
 *     ↓
 * GetDailyItemsResult (subtipo semántico)
 *     ↓
 * DailyUiState.Success | DailyUiState.Empty | DailyUiState.Error
 * ```
 *
 * ## Filosofía:
 * Todo el formateo de fechas y mapeo Domain→UiModel se hace aquí.
 * La UI recibe strings y UI models listos para renderizar.
 *
 * ## Sobre el dispatcher:
 * Este ViewModel NO recibe ni usa [DispatcherProvider].
 * El dispatcher es responsabilidad exclusiva del UseCase (decisión arquitectónica #1).
 * El ViewModel solo usa [viewModelScope.launch] sin especificar dispatcher.
 */
class DailyTabViewModel(
    private val dateProvider: DateProvider,
    private val language: AppLanguage,
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

    fun refresh() {
        loadItems()
    }

    fun onAction(action: DailyUiAction) {
        when (action) {
            DailyUiAction.OnPreviousDay -> navigateToPreviousDay()
            DailyUiAction.OnNextDay -> navigateToNextDay()
            DailyUiAction.OnRefresh -> loadItems()
            is DailyUiAction.OnItemClick -> navigateToDetail(action.itemId)
            is DailyUiAction.OnItemStatusChange -> updateItemStatus(action.itemId, action.newStatus)
            is DailyUiAction.OnFilterChange -> filterItems(action.filter)
        }
    }

    private fun filterItems(filter: DailyFilter) {
        val currentState = _uiState.value
        if (currentState is DailyUiState.Success) {
            _uiState.value = currentState.copy(activeFilter = filter)
        }
    }

    /**
     * Carga los items del daily log del backend.
     *
     * Muestra [DailyUiState.Loading] (skeleton) mientras se hace la request.
     */
    private fun loadItems() {
        viewModelScope.launch {
            _uiState.value = DailyUiState.Loading

            when (val result = getDailyItemsUseCase(currentDate)) {
                is GetDailyItemsResult.Success -> _uiState.value =
                    buildSuccessState(result.dailyLog)

                is GetDailyItemsResult.NotFound -> showEmptyState()
                is GetDailyItemsResult.ServerError -> showServerError(result.message)
                is GetDailyItemsResult.NetworkError -> showNetworkError()
            }
        }
    }

    /**
     * Actualiza el status de un item del daily log.
     *
     * Muestra spinner de refresh en el estado actual mientras espera respuesta.
     * El backend recalcula automáticamente el completionRate del daily log.
     *
     * @param itemId ID del item a actualizar
     * @param newStatus Nuevo status (COMPLETED, SKIPPED, PENDING)
     */
    private fun updateItemStatus(itemId: Long, newStatus: DailyItemStatus) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is DailyUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            }

            when (val result = updateItemStatusUseCase(itemId, newStatus)) {
                is UpdateItemStatusResult.Success -> _uiState.value =
                    buildSuccessState(result.dailyLog)

                is UpdateItemStatusResult.NotFound -> loadItems()
                is UpdateItemStatusResult.ServerError -> showServerError(result.message)
                is UpdateItemStatusResult.NetworkError -> showNetworkError()
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
     * Mapea [DailyLog] (Domain Model) → [DailyUiState.Success] (UI Model).
     *
     * @param dailyLog Domain Model del daily log
     * @return [DailyUiState.Success] con datos listos para renderizar
     */
    private fun buildSuccessState(dailyLog: DailyLog): DailyUiState.Success {
        val nextUpId: Long? =
            dailyLog.items.firstOrNull { it.status == DailyItemStatus.PENDING }?.id
        return DailyUiState.Success(
            date = currentDate,
            dayNumber = currentDate.day,
            headerText = formatHeaderText(currentDate),
            monthYear = formatMonthYear(currentDate),
            showFullDate = !isRelativeDate(currentDate),
            completionRate = dailyLog.completionRate,
            items = dailyLog.items.map { domainItem ->
                domainItem.toUiModel(
                    typeLabel = resolveTypeLabel(domainItem.type),
                    highlight = when {
                        domainItem.status == DailyItemStatus.IN_PROGRESS -> DailyItemHighlight.IN_PROGRESS
                        domainItem.id == nextUpId -> DailyItemHighlight.NEXT_UP
                        else -> DailyItemHighlight.NONE
                    }
                )
            }.toImmutableList(),
            isRefreshing = false,
        )
    }

    private fun showEmptyState() {
        _uiState.value = DailyUiState.Empty
    }

    private fun showServerError(message: String) {
        _uiState.value = DailyUiState.Error(message = message)
    }

    private fun showNetworkError() {
        _uiState.value = DailyUiState.Error(message = language.errorTexts.connectionError)
    }

    private fun formatHeaderText(date: LocalDate): String {
        val today = dateProvider.today()
        val yesterday = dateProvider.yesterday()
        val tomorrow = dateProvider.tomorrow()

        return when (date) {
            today -> language.relativeTexts.today
            yesterday -> language.relativeTexts.yesterday
            tomorrow -> language.relativeTexts.tomorrow
            else -> date.format(language.formats.dayNameAndDate)
                .replaceFirstChar { it.uppercase() }
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
}
