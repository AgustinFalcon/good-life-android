package com.agusstkd.goodlife.presentation.screen.tabs.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsNavigationEffect
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiState
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de la pantalla Workouts.
 *
 * Inyecta el ViewModel y conecta el estado con la UI pura.
 *
 * ## Filosofía Owner Pattern:
 * - Owner: Inyección de ViewModel + colectar estado + routing de estados
 * - Screen: UI pura, solo recibe UiState y callbacks
 */
@Composable
fun WorkoutsScreenOwner(
    onNavigateToWorkoutDetail: (Long) -> Unit,
    viewModel: WorkoutsTabViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel, onNavigateToWorkoutDetail) {
        viewModel.navigationEffects.collect { effect ->
            when (effect) {
                is WorkoutsNavigationEffect.NavigateToWorkoutDetail ->
                    onNavigateToWorkoutDetail(effect.workoutId)
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onWorkoutListResumed()
        viewModel.refresh()
        onPauseOrDispose { }
    }

    when (val state = uiState) {
        WorkoutsUiState.Loading -> {
            WorkoutsLoadingState()
        }

        WorkoutsUiState.NoRoutine -> {
            WorkoutsNoRoutineState(
                texts = viewModel.workoutTexts,
                onCreateRoutine = { viewModel.onAction(WorkoutsUiAction.OnCreateRoutine) }
            )
        }

        is WorkoutsUiState.Success -> {
            WorkoutsScreen(
                uiState = state,
                texts = viewModel.workoutTexts,
                onAction = viewModel::onAction
            )
        }

        is WorkoutsUiState.Error -> {
            WorkoutsErrorState(
                message = state.message,
                retryText = viewModel.workoutTexts.retry,
                onRetry = { viewModel.onAction(WorkoutsUiAction.OnRefresh) }
            )
        }
    }
}
