package com.agusstkd.goodlife.presentation.screen.add.routine.model

import androidx.compose.runtime.Immutable
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.MuscleGroup
import com.agusstkd.goodlife.domain.model.training.WorkoutDraft
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Immutable
data class CreateRoutineUiState(
    val currentStep: RoutineWizardStep = RoutineWizardStep.ROUTINE_INFO,

    // ── Paso 1: Datos de la rutina ──
    val name: String = "",
    val description: String = "",
    val difficultyLevel: DifficultyLevel? = null,
    val goalType: GoalType? = null,
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val hasTime: Boolean = false,
    val scheduledTime: LocalTime? = null,
    val startDate: LocalDate? = null,
    val startDateDisplay: String = "",
    val hasEndDate: Boolean = false,
    val endDate: LocalDate? = null,
    val endDateDisplay: String = "",

    // ── Paso 2: Workouts ──
    val workouts: List<WorkoutDraft> = emptyList(),
    val showAddWorkoutDialog: Boolean = false,

    // ── Paso 3: Ejercicios del workout activo ──
    val activeWorkoutIndex: Int = -1,
    val exerciseCatalog: List<ExerciseMaster> = emptyList(),
    val muscleGroups: List<MuscleGroup> = emptyList(),
    val selectedMuscleGroupId: Long? = null,
    val exerciseSearchQuery: String = "",
    val isLoadingCatalog: Boolean = false,
    val catalogPage: Int = 0,
    val catalogHasMore: Boolean = true,
    val showSetEditorForExerciseIndex: Int? = null,

    // ── Paso 4: Resumen ──
    val activateOnCreate: Boolean = false,

    // ── Estado general ──
    val showTimePicker: Boolean = false,
    val activeDatePickerField: RoutineDatePickerField? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
) {
    val activeWorkout: WorkoutDraft?
        get() = workouts.getOrNull(activeWorkoutIndex)

    val stepNumber: Int get() = currentStep.ordinal + 1
    val totalSteps: Int get() = RoutineWizardStep.entries.size

    val canGoNext: Boolean
        get() = when (currentStep) {
            RoutineWizardStep.ROUTINE_INFO ->
                name.isNotBlank()
                        && difficultyLevel != null
                        && goalType != null
                        && selectedDays.isNotEmpty()

            RoutineWizardStep.WORKOUTS ->
                workouts.isNotEmpty() && workouts.all { it.exercises.isNotEmpty() }

            RoutineWizardStep.WORKOUT_EXERCISES ->
                activeWorkout?.exercises?.isNotEmpty() == true

            RoutineWizardStep.SUMMARY -> !isLoading
        }
}

enum class RoutineWizardStep {
    ROUTINE_INFO,
    WORKOUTS,
    WORKOUT_EXERCISES,
    SUMMARY,
}

enum class RoutineDatePickerField {
    START_DATE,
    END_DATE,
}
