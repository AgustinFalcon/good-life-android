package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model.MealDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Owner for the tab-local, read-only meal-plan detail. */
@Composable
fun MealDetailScreenOwner(
    planId: Long,
    dateIso: String,
    onNavigateUp: () -> Unit,
    viewModel: MealDetailViewModel = koinViewModel(parameters = { parametersOf(planId, dateIso) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val texts = viewModel.texts

    when (val currentState = state) {
        MealDetailUiState.Loading -> MealDetailMessage(
            message = texts.loading,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        is MealDetailUiState.Content -> MealDetailScreen(currentState, texts, onNavigateUp)
        MealDetailUiState.NotFound -> MealDetailMessage(
            message = texts.notFound,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        MealDetailUiState.InvalidRoute -> MealDetailMessage(
            message = texts.invalidRoute,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        is MealDetailUiState.Error -> MealDetailMessage(
            message = currentState.message,
            primaryActionLabel = texts.retry,
            onPrimaryAction = viewModel::retry,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
    }
}
