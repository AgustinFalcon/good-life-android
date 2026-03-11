package com.agusstkd.goodlife.presentation.screen.add.routine.model

import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.SetDraft
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

sealed interface CreateRoutineUiAction {

    // ── Navegación del wizard ──
    data object OnNextStep : CreateRoutineUiAction
    data object OnPreviousStep : CreateRoutineUiAction
    data object OnDismiss : CreateRoutineUiAction

    // ── Paso 1: Datos de la rutina ──
    data class OnNameChange(val name: String) : CreateRoutineUiAction
    data class OnDescriptionChange(val description: String) : CreateRoutineUiAction
    data class OnDifficultySelected(val level: DifficultyLevel) : CreateRoutineUiAction
    data class OnGoalTypeSelected(val goal: GoalType) : CreateRoutineUiAction
    data class OnDayToggled(val day: DayOfWeek) : CreateRoutineUiAction
    data object OnHasTimeToggle : CreateRoutineUiAction
    data object OnHasEndDateToggle : CreateRoutineUiAction
    data class OnDatePickerOpen(val field: RoutineDatePickerField) : CreateRoutineUiAction
    data object OnDatePickerDismiss : CreateRoutineUiAction
    data class OnDateSelected(val field: RoutineDatePickerField, val date: LocalDate) : CreateRoutineUiAction
    data object OnTimePickerOpen : CreateRoutineUiAction
    data object OnTimePickerDismiss : CreateRoutineUiAction
    data class OnTimeSelected(val time: LocalTime) : CreateRoutineUiAction

    // ── Paso 2: Workouts ──
    data object OnShowAddWorkoutDialog : CreateRoutineUiAction
    data object OnDismissAddWorkoutDialog : CreateRoutineUiAction
    data class OnAddWorkout(val name: String) : CreateRoutineUiAction
    data class OnEditWorkout(val index: Int) : CreateRoutineUiAction
    data class OnDeleteWorkout(val index: Int) : CreateRoutineUiAction

    // ── Paso 3: Ejercicios ──
    data class OnSearchQueryChange(val query: String) : CreateRoutineUiAction
    data class OnMuscleGroupFilter(val muscleGroupId: Long?) : CreateRoutineUiAction
    data object OnLoadMoreExercises : CreateRoutineUiAction
    data class OnAddExercise(val exercise: ExerciseMaster) : CreateRoutineUiAction
    data class OnRemoveExercise(val orderIndex: Int) : CreateRoutineUiAction
    data class OnOpenSetEditor(val exerciseOrderIndex: Int) : CreateRoutineUiAction
    data object OnDismissSetEditor : CreateRoutineUiAction
    data class OnSaveExerciseSets(
        val orderIndex: Int,
        val sets: List<SetDraft>,
        val notes: String?,
    ) : CreateRoutineUiAction
    data object OnSaveWorkout : CreateRoutineUiAction

    // ── Paso 4: Resumen ──
    data class OnActivateToggle(val activate: Boolean) : CreateRoutineUiAction
    data object OnSubmit : CreateRoutineUiAction

    // ── Feedback ──
    data object OnErrorDismissed : CreateRoutineUiAction
    data object OnSuccessAnimationFinished : CreateRoutineUiAction
}
