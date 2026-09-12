package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface WorkoutDetailUiState {
    @Immutable data object Loading : WorkoutDetailUiState
    @Immutable data class Content(val workoutName: String, val dayLabel: String?, val exercises: List<WorkoutExerciseUiModel>) : WorkoutDetailUiState
    @Immutable data object NotFound : WorkoutDetailUiState
    @Immutable data class Error(val message: String) : WorkoutDetailUiState
}

@Immutable data class WorkoutExerciseUiModel(val id: Long, val name: String, val notes: String?, val sets: List<WorkoutSetUiModel>)
@Immutable data class WorkoutSetUiModel(val label: String, val summary: String)