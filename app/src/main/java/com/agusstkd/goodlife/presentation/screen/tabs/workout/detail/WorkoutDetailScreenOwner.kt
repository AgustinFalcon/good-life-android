package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun WorkoutDetailScreenOwner(
    workoutId: Long,
    onNavigateUp: () -> Unit,
    viewModel: WorkoutDetailViewModel = koinViewModel(parameters = { parametersOf(workoutId) }),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val texts = viewModel.texts
    when (val currentState = state) {
        WorkoutDetailUiState.Loading -> WorkoutDetailMessage(
            message = texts.loading,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        is WorkoutDetailUiState.Content -> WorkoutDetailScreen(
            state = currentState,
            texts = texts,
            onNavigateUp = onNavigateUp,
            onAction = viewModel::onAction,
        )
        WorkoutDetailUiState.NotFound -> WorkoutDetailMessage(
            message = texts.workoutNotFound,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
        )
        is WorkoutDetailUiState.Error -> WorkoutDetailMessage(
            message = currentState.message,
            backLabel = texts.back,
            onNavigateUp = onNavigateUp,
            actionLabel = texts.retry,
            onAction = viewModel::onAction,
        )
    }
}
