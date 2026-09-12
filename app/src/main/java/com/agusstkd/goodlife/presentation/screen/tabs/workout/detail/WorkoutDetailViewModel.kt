package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.extensions.formatArgs
import com.agusstkd.goodlife.domain.model.training.Workout
import com.agusstkd.goodlife.domain.usecase.routine.GetActiveRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.result.GetActiveRoutineResult
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutExerciseUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Loads a workout by route ID from the authenticated user's active routine. */
class WorkoutDetailViewModel(
    private val workoutId: Long,
    private val language: AppLanguage,
    private val getActiveRoutineUseCase: GetActiveRoutineUseCase,
) : ViewModel() {
    private var loadJob: Job? = null
    private var latestRequest = 0L
    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()
    val texts get() = language.workoutTexts

    init { load() }
    fun retry() = load()

    private fun load() {
        val request = ++latestRequest
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = WorkoutDetailUiState.Loading
            val state = when (val result = getActiveRoutineUseCase()) {
                is GetActiveRoutineResult.Success -> result.routine.workouts.firstOrNull { it.id == workoutId }
                    ?.let(::content) ?: WorkoutDetailUiState.NotFound
                GetActiveRoutineResult.NotFound -> WorkoutDetailUiState.NotFound
                is GetActiveRoutineResult.ServerError -> WorkoutDetailUiState.Error(language.errorTexts.dataLoadError)
                GetActiveRoutineResult.NetworkError -> WorkoutDetailUiState.Error(language.errorTexts.connectionError)
            }
            if (request == latestRequest) _uiState.value = state
        }
    }

    private fun content(workout: Workout): WorkoutDetailUiState.Content = WorkoutDetailUiState.Content(
        workoutName = workout.name,
        dayLabel = workout.dayOfWeek?.let { texts.dayFormat.formatArgs(it) },
        exercises = workout.exercises.sortedBy { it.orderIndex }.map { exercise ->
            WorkoutExerciseUiModel(
                id = exercise.id,
                name = exercise.exerciseName,
                notes = exercise.notes?.takeIf { it.isNotBlank() },
                sets = exercise.sets.sortedBy { it.setNumber }.map { set ->
                    val weight = set.targetWeight?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }
                    WorkoutSetUiModel(
                        label = texts.setFormat.formatArgs(set.setNumber),
                        summary = if (weight == null) texts.repsFormat.formatArgs(set.targetReps) else texts.repsAndWeightFormat.formatArgs(set.targetReps, weight),
                    )
                },
            )
        },
    )
}