package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface WorkoutDetailUiState {
    @Immutable data object Loading : WorkoutDetailUiState
    /**
     * @property notes Feedback de sesión editable; vacío si el usuario no escribió.
     * @property finished `true` tras [com.agusstkd.goodlife.domain.usecase.workoutlog.result.FinishWorkoutResult.Success];
     * la pantalla muestra la celebración Lottie.
     * @property isFinishing POST de finish en vuelo.
     * @property finishError Copy localizado de un fallo de POST; no usar [Error] de GET.
     */
    @Immutable data class Content(
        val workoutName: String,
        val dayLabel: String?,
        val exercises: List<WorkoutExerciseUiModel>,
        val notes: String = "",
        val finished: Boolean = false,
        val isFinishing: Boolean = false,
        val finishError: String? = null,
    ) : WorkoutDetailUiState {
        val canFinish: Boolean get() = exercises.any { it.sets.isNotEmpty() }
    }
    @Immutable data object NotFound : WorkoutDetailUiState
    @Immutable data class Error(val message: String) : WorkoutDetailUiState
}

@Immutable data class WorkoutExerciseUiModel(
    val id: Long,
    val exerciseMasterId: Long,
    val name: String,
    val notes: String?,
    val sets: List<WorkoutSetUiModel>,
)

@Immutable data class WorkoutSetUiModel(
    val id: Long,
    val setNumber: Int,
    val label: String,
    val weightInput: String,
    val repsInput: String,
    val rpeInput: String,
)
