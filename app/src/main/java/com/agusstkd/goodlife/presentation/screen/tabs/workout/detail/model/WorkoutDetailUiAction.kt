package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import androidx.compose.runtime.Stable

/**
 * Acciones del usuario en el detalle de un workout.
 *
 * El back no es una UiAction: el Owner lo resuelve con `onNavigateUp`.
 */
@Stable
sealed interface WorkoutDetailUiAction {

    data object OnFinishWorkout : WorkoutDetailUiAction

    data class OnNotesChange(val notes: String) : WorkoutDetailUiAction

    /**
     * Usuario edita peso, reps o RPE de un set. El campo es [WorkoutSetField];
     * su label vive en el tipo (`label(WorkoutTexts)`).
     *
     * @param exerciseId [WorkoutExerciseUiModel.id], no el master.
     * @param setId [WorkoutSetUiModel.id]
     */
    data class OnSetFieldChange(
        val exerciseId: Long,
        val setId: Long,
        val field: WorkoutSetField,
        val value: String,
    ) : WorkoutDetailUiAction

    data object OnRetry : WorkoutDetailUiAction
}
