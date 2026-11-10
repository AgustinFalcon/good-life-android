package com.agusstkd.goodlife.presentation.screen.tabs.meals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.components.bottom.datepicker.DatePickerBottomSheetComponent
import com.agusstkd.goodlife.presentation.components.header.DateHeaderComponent
import com.agusstkd.goodlife.presentation.components.header.DateHeaderParams
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsNavigationEffect
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Meals.
 *
 * Inyecta el ViewModel, colecta el estado y hace routing entre estados.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado + routing de estados
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun MealsScreenOwner(
    onNavigateToMealDetail: (Long, String) -> Unit,
    viewModel: MealsTabViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isCalendarVisible by viewModel.isCalendarVisible.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(viewModel, onNavigateToMealDetail) {
        viewModel.navigationEffects.collect { effect ->
            when (effect) {
                is MealsNavigationEffect.NavigateToMealDetail ->
                    onNavigateToMealDetail(effect.planId, effect.dateIso)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onMealListResumed()
        onPauseOrDispose { }
    }

    when (val state = uiState) {
        MealsUiState.Loading -> {
            MealsLoadingState()
        }

        is MealsUiState.Empty -> {
            val a = viewModel.accessibilityTexts
            Column(modifier = Modifier.fillMaxSize()) {
                DateHeaderComponent(
                    params = DateHeaderParams(
                        dayNumber = state.dayNumber,
                        headerText = state.headerText,
                        monthYear = if (state.showFullDate) state.monthYear else null,
                        openCalendarLabel = a.openCalendar,
                        previousDayLabel = a.previousDay,
                        nextDayLabel = a.nextDay,
                        notificationsLabel = a.notifications,
                    ),
                    onPreviousDay = { viewModel.onAction(MealsUiAction.OnPreviousDay) },
                    onNextDay = { viewModel.onAction(MealsUiAction.OnNextDay) },
                    onCalendarClick = { viewModel.onAction(MealsUiAction.OnCalendarClick) },
                )
                MealsEmptyState(
                    texts = viewModel.tabDetailTexts,
                    onCreateMealPlan = { viewModel.onAction(MealsUiAction.OnCreateMealPlan) }
                )
            }
        }

        is MealsUiState.Success -> {
            val a = viewModel.accessibilityTexts
            MealsScreen(
                uiState = state,
                texts = viewModel.tabDetailTexts,
                listState = listState,
                dateHeaderParams = DateHeaderParams(
                    dayNumber = state.dayNumber,
                    headerText = state.headerText,
                    monthYear = if (state.showFullDate) state.monthYear else null,
                    openCalendarLabel = a.openCalendar,
                    previousDayLabel = a.previousDay,
                    nextDayLabel = a.nextDay,
                    notificationsLabel = a.notifications,
                ),
                onAction = viewModel::onAction,
            )
        }

        is MealsUiState.Error -> {
            MealsErrorState(
                message = state.message,
                retryText = viewModel.tabDetailTexts.tapToRetry,
                onRetry = { viewModel.onAction(MealsUiAction.OnRefresh) },
            )
        }
    }

    if (isCalendarVisible) {
        val initialDate = when (val state = uiState) {
            is MealsUiState.Success -> state.date
            is MealsUiState.Empty -> state.date
            else -> null
        }
        DatePickerBottomSheetComponent(
            texts = viewModel.datePickerTexts,
            initialDate = initialDate,
            onDateSelected = { viewModel.onAction(MealsUiAction.OnCalendarDateSelected(it)) },
            onDismiss = { viewModel.onAction(MealsUiAction.OnCalendarDismiss) },
        )
    }
}
