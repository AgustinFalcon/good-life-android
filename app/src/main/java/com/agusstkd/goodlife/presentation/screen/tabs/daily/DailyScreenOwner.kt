package com.agusstkd.goodlife.presentation.screen.tabs.daily

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.core.datetime.language.AccessibilityTexts
import com.agusstkd.goodlife.presentation.components.bottom.datepicker.DatePickerBottomSheetComponent
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyNavigationEffect
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Daily.
 *
 * Inyecta el ViewModel y conecta el estado con la UI pura.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado + routing de estados
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun DailyScreenOwner(
    onNavigateToDailyDetail: (Long, kotlinx.datetime.LocalDate) -> Unit,
    viewModel: DailyTabViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isCalendarVisible by viewModel.isCalendarVisible.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel, onNavigateToDailyDetail) {
        viewModel.navigationEffects.collect { effect ->
            when (effect) {
                is DailyNavigationEffect.NavigateToDailyDetail -> onNavigateToDailyDetail(effect.itemId, effect.date)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onDailyListResumed()
        onPauseOrDispose { }
    }

    when (val state = uiState) {
        DailyUiState.Loading -> {
            DailyLoadingState()
        }

        is DailyUiState.Empty -> {
            DailyEmptyState(
                dailyTexts = viewModel.dailyTexts,
                dateHeaderParams = dailyHeaderParams(
                    dayNumber = state.dayNumber,
                    headerText = state.headerText,
                    monthYear = state.monthYear,
                    showFullDate = state.showFullDate,
                    accessibility = viewModel.accessibilityTexts,
                ),
                onAction = viewModel::onAction,
            )
        }

        is DailyUiState.Success -> {
            DailyScreen(
                uiState = state,
                dailyTexts = viewModel.dailyTexts,
                listState = listState,
                dateHeaderParams = dailyHeaderParams(
                    dayNumber = state.dayNumber,
                    headerText = state.headerText,
                    monthYear = state.monthYear,
                    showFullDate = state.showFullDate,
                    accessibility = viewModel.accessibilityTexts,
                ),
                onAction = viewModel::onAction
            )
        }

        is DailyUiState.Error -> {
            DailyErrorState(
                message = state.message,
                dailyTexts = viewModel.dailyTexts,
                onRetry = { viewModel.onAction(DailyUiAction.OnRefresh) }
            )
        }
    }

    if (isCalendarVisible) {
        val initialDate = when (val state = uiState) {
            is DailyUiState.Success -> state.date
            is DailyUiState.Empty -> state.date
            else -> null
        }
        DatePickerBottomSheetComponent(
            texts = viewModel.datePickerTexts,
            initialDate = initialDate,
            onDateSelected = { viewModel.onAction(DailyUiAction.OnCalendarDateSelected(it)) },
            onDismiss = { viewModel.onAction(DailyUiAction.OnCalendarDismiss) },
        )
    }
}

private fun dailyHeaderParams(
    dayNumber: Int,
    headerText: String,
    monthYear: String,
    showFullDate: Boolean,
    accessibility: AccessibilityTexts,
) = DateHeaderParams(
    dayNumber = dayNumber,
    headerText = headerText,
    monthYear = if (showFullDate) monthYear else null,
    openCalendarLabel = accessibility.openCalendar,
    previousDayLabel = accessibility.previousDay,
    nextDayLabel = accessibility.nextDay,
    notificationsLabel = accessibility.notifications,
)
