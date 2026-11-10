package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/** Owner for injection and state routing; rendering stays in pure composables. */
@Composable
fun DailyDetailScreenOwner(
    itemId: Long,
    dateIso: String,
    onNavigateUp: () -> Unit,
    viewModel: DailyDetailViewModel = koinViewModel(
        parameters = { parametersOf(itemId, dateIso) },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val currentState = state) {
        DailyDetailUiState.Loading -> DailyDetailMessage(
            message = viewModel.texts.loading,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        is DailyDetailUiState.Content -> DailyDetailScreen(
            state = currentState,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        DailyDetailUiState.NotFound -> DailyDetailMessage(
            message = viewModel.texts.notFound,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        DailyDetailUiState.InvalidRoute -> DailyDetailMessage(
            message = viewModel.texts.invalidRoute,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
        )
        is DailyDetailUiState.Error -> DailyDetailMessage(
            message = currentState.message,
            texts = viewModel.texts,
            onNavigateUp = onNavigateUp,
            actionLabel = viewModel.texts.retry,
            onAction = viewModel::retry,
        )
    }
}
