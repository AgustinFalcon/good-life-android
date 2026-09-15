package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.extensions.toDisplayString
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.result.GetDailyItemsResult
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailTexts
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

/**
 * Loads one Daily item from the authenticated daily log selected by its route date.
 *
 * Route arguments are validated before invoking the use case. The backend's error
 * detail deliberately never reaches UI state.
 */
class DailyDetailViewModel(
    private val itemId: Long,
    private val dateIso: String,
    private val language: AppLanguage,
    private val getDailyItemsUseCase: GetDailyItemsUseCase,
) : ViewModel() {
    private var loadJob: Job? = null
    private var latestRequestId = 0L
    private val routeDate = dateIso.toExactLocalDateOrNull()

    private val _uiState = MutableStateFlow<DailyDetailUiState>(DailyDetailUiState.Loading)
    val uiState: StateFlow<DailyDetailUiState> = _uiState.asStateFlow()
    val texts: DailyDetailTexts = DailyDetailTexts.from(language)

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        val date = routeDate
        if (itemId <= 0L || date == null) {
            _uiState.value = DailyDetailUiState.InvalidRoute
            return
        }

        val requestId = ++latestRequestId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DailyDetailUiState.Loading
            val state = when (val result = getDailyItemsUseCase(date)) {
                is GetDailyItemsResult.Success -> result.dailyLog.items
                    .firstOrNull { it.id == itemId }
                    ?.let(::toContent)
                    ?: DailyDetailUiState.NotFound
                GetDailyItemsResult.NotFound -> DailyDetailUiState.NotFound
                is GetDailyItemsResult.ServerError -> DailyDetailUiState.Error(language.errorTexts.dataLoadError)
                GetDailyItemsResult.NetworkError -> DailyDetailUiState.Error(language.errorTexts.connectionError)
            }
            if (requestId == latestRequestId) {
                _uiState.value = state
            }
        }
    }

    private fun toContent(item: DailyItem): DailyDetailUiState.Content = DailyDetailUiState.Content(
        title = item.title,
        type = typeLabel(item.type),
        status = statusLabel(item.status),
        scheduledTime = item.scheduledTime?.toDisplayString(),
        description = item.description?.takeIf(String::isNotBlank),
    )

    private fun typeLabel(type: DailyItemType): String = when (type) {
        DailyItemType.TASK -> language.dailyTexts.task
        DailyItemType.HABIT -> language.dailyTexts.habit
        DailyItemType.WORKOUT -> language.dailyTexts.workout
        DailyItemType.MEAL -> language.dailyTexts.meal
    }

    private fun statusLabel(status: DailyItemStatus): String = when (status) {
        DailyItemStatus.PENDING -> texts.pending
        DailyItemStatus.IN_PROGRESS -> texts.inProgress
        DailyItemStatus.COMPLETED -> texts.completed
        DailyItemStatus.SKIPPED -> texts.skipped
    }
}

private fun String.toExactLocalDateOrNull(): LocalDate? {
    if (!matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) return null
    return runCatching { LocalDate.parse(this) }
        .getOrNull()
        ?.takeIf { it.toString() == this }
}
