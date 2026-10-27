package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import androidx.compose.runtime.Stable

/**
 * Acciones del usuario en el detalle de un workout.
 *
 * ## Flujo:
 * ```
 * User interaction
 *     ↓
 * WorkoutDetailUiAction
 *     ↓
 * WorkoutDetailViewModel.onAction(action)
 *     ↓
 * State update → UI recomposition
 * ```
 *
 * El back no es una UiAction: el Owner lo resuelve con `onNavigateUp`.
 */
@Stable
sealed interface WorkoutDetailUiAction {

    /**
     * Usuario confirma que terminó el workout.
     * El ViewModel ya tiene el `workoutId` de ruta y el [WorkoutDetailUiState.Content] cargado.
     */
    data object OnFinishWorkout : WorkoutDetailUiAction

    /**
     * Usuario reintenta la carga tras [WorkoutDetailUiState.Error].
     */
    data object OnRetry : WorkoutDetailUiAction
}
