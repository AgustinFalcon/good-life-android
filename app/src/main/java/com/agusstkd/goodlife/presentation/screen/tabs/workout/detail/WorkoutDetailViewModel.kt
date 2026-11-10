package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.DateProvider
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.extensions.formatArgs
import com.agusstkd.goodlife.domain.model.training.FinishExerciseDraft
import com.agusstkd.goodlife.domain.model.training.FinishSetDraft
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft
import com.agusstkd.goodlife.domain.model.training.PerceivedExertion
import com.agusstkd.goodlife.domain.model.training.Workout
import com.agusstkd.goodlife.domain.model.training.WorkoutSet
import com.agusstkd.goodlife.domain.usecase.routine.GetActiveRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.result.GetActiveRoutineResult
import com.agusstkd.goodlife.domain.usecase.workoutlog.FinishWorkoutUseCase
import com.agusstkd.goodlife.domain.usecase.workoutlog.result.FinishWorkoutResult
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutExerciseUiModel
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetField
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetUiModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Loads a workout by route ID from the authenticated user's active routine
 * and registers the session when the user finishes.
 *
 * On [FinishWorkoutResult.Success], [WorkoutDetailUiState.Content.finished] becomes true
 * so the Screen can show the Lottie celebration.
 */
@OptIn(ExperimentalTime::class)
class WorkoutDetailViewModel(
    private val workoutId: Long,
    private val language: AppLanguage,
    private val dateProvider: DateProvider,
    private val getActiveRoutineUseCase: GetActiveRoutineUseCase,
    private val finishWorkoutUseCase: FinishWorkoutUseCase,
) : ViewModel() {
    private var loadJob: Job? = null
    private var latestRequest = 0L
    private val _uiState = MutableStateFlow<WorkoutDetailUiState>(WorkoutDetailUiState.Loading)
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()
    val texts get() = language.workoutTexts

    private var loadedRoutineId: Long? = null
    private var loadedWorkout: Workout? = null
    @OptIn(ExperimentalTime::class)
    private var sessionStart: Instant? = null

    init {
        load()
    }

    fun retry() = load()

    fun onAction(action: WorkoutDetailUiAction) {
        when (action) {
            WorkoutDetailUiAction.OnFinishWorkout -> finishWorkout()
            is WorkoutDetailUiAction.OnNotesChange -> onNotesChange(action.notes)
            is WorkoutDetailUiAction.OnSetFieldChange -> onSetFieldChange(action)
            WorkoutDetailUiAction.OnRetry -> retry()
        }
    }

    private fun onNotesChange(notes: String) {
        val current = _uiState.value as? WorkoutDetailUiState.Content ?: return
        if (current.finished || current.isFinishing) return
        _uiState.value = current.copy(notes = notes)
    }

    private fun onSetFieldChange(action: WorkoutDetailUiAction.OnSetFieldChange) {
        val current = _uiState.value as? WorkoutDetailUiState.Content ?: return
        if (current.finished || current.isFinishing) return
        _uiState.value = current.copy(
            exercises = current.exercises.map { exercise ->
                if (exercise.id != action.exerciseId) exercise
                else exercise.copy(
                    sets = exercise.sets.map { set ->
                        if (set.id != action.setId) set
                        else when (action.field) {
                            WorkoutSetField.Weight -> set.copy(weightInput = action.value)
                            WorkoutSetField.Reps -> set.copy(repsInput = action.value)
                            WorkoutSetField.Rpe -> set.copy(rpeInput = action.value)
                        }
                    },
                )
            },
        )
    }

    private fun load() {
        val request = ++latestRequest
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = WorkoutDetailUiState.Loading
            val state = when (val result = getActiveRoutineUseCase()) {
                is GetActiveRoutineResult.Success -> {
                    val workout = result.routine.workouts.firstOrNull { it.id == workoutId }
                    if (workout == null) {
                        clearSession()
                        WorkoutDetailUiState.NotFound
                    } else {
                        loadedRoutineId = result.routine.id
                        loadedWorkout = workout
                        sessionStart = dateProvider.now()
                        content(workout)
                    }
                }

                GetActiveRoutineResult.NotFound -> {
                    clearSession()
                    WorkoutDetailUiState.NotFound
                }
                is GetActiveRoutineResult.ServerError -> {
                    clearSession()
                    WorkoutDetailUiState.Error(language.errorTexts.dataLoadError)
                }
                GetActiveRoutineResult.NetworkError -> {
                    clearSession()
                    WorkoutDetailUiState.Error(language.errorTexts.connectionError)
                }
            }

            if (request == latestRequest) _uiState.value = state
        }
    }

    private fun clearSession() {
        loadedRoutineId = null
        loadedWorkout = null
        sessionStart = null
    }

    private fun content(workout: Workout): WorkoutDetailUiState.Content =
        WorkoutDetailUiState.Content(
            workoutName = workout.name,
            dayLabel = workout.dayOfWeek?.let { texts.dayFormat.formatArgs(it) },
            exercises = workout.exercises.sortedBy { it.orderIndex }.map { exercise ->
                WorkoutExerciseUiModel(
                    id = exercise.id,
                    exerciseMasterId = exercise.exerciseId,
                    name = exercise.exerciseName,
                    notes = exercise.notes?.takeIf { it.isNotBlank() },
                    sets = exercise.sets.sortedBy { it.setNumber }.map { set ->
                        WorkoutSetUiModel(
                            id = set.id,
                            setNumber = set.setNumber,
                            label = texts.setFormat.formatArgs(set.setNumber),
                            weightInput = formatWeight(set),
                            repsInput = set.targetReps.toString(),
                            rpeInput = "",
                        )
                    },
                )
            },
            notes = "",
        )

    private fun formatWeight(set: WorkoutSet): String {
        val weight = set.targetWeight ?: return ""
        return if (weight % 1.0 == 0.0) weight.toInt().toString() else weight.toString()
    }

    @OptIn(ExperimentalTime::class)
    private fun finishWorkout() {
        val workout = loadedWorkout ?: return
        val content = _uiState.value as? WorkoutDetailUiState.Content ?: return
        if (content.finished || content.isFinishing) return
        val finishing = content.copy(isFinishing = true, finishError = null)
        _uiState.value = finishing
        viewModelScope.launch {
            val startInstant = sessionStart ?: dateProvider.now()
            val endInstant = dateProvider.now()
            val durationMinutes = (endInstant - startInstant).inWholeMinutes.toInt().coerceAtLeast(1)
            val result = finishWorkoutUseCase(
                draft = FinishWorkoutDraft(
                    routineId = loadedRoutineId,
                    workoutId = workoutId,
                    name = workout.name,
                    durationMinutes = durationMinutes,
                    startTime = toLocal(startInstant),
                    endTime = toLocal(endInstant),
                    notes = finishing.notes,
                    exercises = finishing.exercises.mapNotNull { exercise ->
                        val domainExercise = workout.exercises.firstOrNull { it.id == exercise.id }
                            ?: return@mapNotNull null
                        FinishExerciseDraft(
                            exerciseMasterId = exercise.exerciseMasterId,
                            notes = domainExercise.notes?.takeIf { it.isNotBlank() },
                            sets = exercise.sets.map { set ->
                                FinishSetDraft(
                                    setNumber = set.setNumber,
                                    weight = set.weightInput.toDoubleOrNull() ?: 0.0,
                                    reps = set.repsInput.toIntOrNull() ?: 0,
                                    rpe = PerceivedExertion.fromWire(set.rpeInput).toDraftRpe(),
                                )
                            },
                        )
                    },
                )
            )
            val latest = _uiState.value as? WorkoutDetailUiState.Content ?: return@launch
            _uiState.value = when (result) {
                is FinishWorkoutResult.Success -> latest.copy(finished = true, isFinishing = false)
                FinishWorkoutResult.EmptyExercises ->
                    latest.copy(isFinishing = false, finishError = texts.noExercises)
                FinishWorkoutResult.NetworkError ->
                    latest.copy(isFinishing = false, finishError = language.errorTexts.connectionError)
                is FinishWorkoutResult.ServerError,
                FinishWorkoutResult.NotFound ->
                    latest.copy(isFinishing = false, finishError = texts.finishFailed)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun toLocal(instant: Instant) = instant.toLocalDateTime(TimeZone.currentSystemDefault())
}
