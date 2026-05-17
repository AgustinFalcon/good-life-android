package com.agusstkd.goodlife.presentation.screen.tabs.workout.model

import androidx.compose.runtime.Stable

/**
 * Acciones del usuario en la pantalla Workouts.
 *
 * ## Flujo:
 * ```
 * User interaction
 *     ↓
 * WorkoutsUiAction
 *     ↓
 * WorkoutsTabViewModel.onAction(action)
 *     ↓
 * State update → UI recomposition
 * ```
 */
@Stable
sealed interface WorkoutsUiAction {

    /**
     * Usuario hace pull-to-refresh para recargar la rutina activa.
     */
    data object OnRefresh : WorkoutsUiAction

    /**
     * Usuario presiona el botón de "Crear rutina" desde el estado NoRoutine.
     * Navega a la pantalla de creación de rutina.
     */
    data object OnCreateRoutine : WorkoutsUiAction

    /**
     * Usuario hace click en un workout de la lista.
     *
     * @property workoutId ID del workout clickeado
     */
    data class OnWorkoutClick(val workoutId: Long) : WorkoutsUiAction
}
