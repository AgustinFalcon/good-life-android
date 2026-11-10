package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

import com.agusstkd.goodlife.core.datetime.FakeDateProvider
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.Workout
import com.agusstkd.goodlife.domain.model.training.WorkoutExercise
import com.agusstkd.goodlife.domain.model.training.WorkoutSet
import com.agusstkd.goodlife.domain.usecase.routine.GetActiveRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.workoutlog.FinishWorkoutUseCase
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import com.agusstkd.goodlife.fake.FakeWorkoutLogRepository
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutSetField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRoutineRepository
    private lateinit var workoutLogRepository: FakeWorkoutLogRepository
    private lateinit var dateProvider: FakeDateProvider

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeRoutineRepository()
        workoutLogRepository = FakeWorkoutLogRepository()
        dateProvider = FakeDateProvider(LocalDate(2026, 10, 29))
    }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun viewModel(workoutId: Long) = WorkoutDetailViewModel(
        workoutId = workoutId,
        language = Spanish,
        dateProvider = dateProvider,
        getActiveRoutineUseCase = GetActiveRoutineUseCase(repository, TestDispatcherProvider(dispatcher)),
        finishWorkoutUseCase = FinishWorkoutUseCase(workoutLogRepository, TestDispatcherProvider(dispatcher)),
    )

    @Test fun `selected workout maps sorted exercises and sets`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        val state = viewModel.uiState.value as WorkoutDetailUiState.Content
        assertEquals("Upper", state.workoutName)
        assertEquals("Día 1", state.dayLabel)
        assertEquals("Respirar entre series", state.notes)
        assertEquals(listOf("Press", "Row"), state.exercises.map { it.name })
        val firstSet = state.exercises.first().sets.first()
        assertEquals("Set 1", firstSet.label)
        assertEquals("80", firstSet.weightInput)
        assertEquals("8", firstSet.repsInput)
        assertEquals("", firstSet.rpeInput)
        assertEquals("70.5", state.exercises.first().sets[1].weightInput)
    }

    @Test fun `notes change updates content and finish passes ui notes`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnNotesChange("Me sentí fuerte"))
        assertEquals("Me sentí fuerte", (viewModel.uiState.value as WorkoutDetailUiState.Content).notes)

        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        val finished = viewModel.uiState.value as WorkoutDetailUiState.Content
        val draft = workoutLogRepository.lastDraft
        assertEquals("Me sentí fuerte", draft?.notes)
        assertEquals(1, workoutLogRepository.finishWorkoutCallCount)
        assertTrue(finished.finished)
        assertTrue((draft?.durationMinutes ?: 0) >= 1)
        assertEquals("Controlled tempo", draft?.exercises?.first()?.notes)
    }

    @Test fun `unknown workout maps to not found`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 999L)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is WorkoutDetailUiState.NotFound)
    }

    @Test fun `server error remains observable`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Error(ApiException.ServerException("backend unavailable"))
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        val state = viewModel.uiState.value as WorkoutDetailUiState.Error
        assertEquals(Spanish.errorTexts.dataLoadError, state.message)
        assertTrue(state.message != "backend unavailable")
    }

    @Test fun `network error preserves localized connection copy`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Error(Exception("offline"))
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        val state = viewModel.uiState.value as WorkoutDetailUiState.Error
        assertEquals(Spanish.errorTexts.connectionError, state.message)
    }

    @Test fun `duration uses advanced now between content and finish`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        dateProvider.advance(5.minutes)
        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        assertEquals(5, workoutLogRepository.lastDraft?.durationMinutes)
    }

    @Test fun `edited weight reps and rpe go to draft`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        val pressId = 2L
        val firstSetId = 1L
        viewModel.onAction(WorkoutDetailUiAction.OnSetFieldChange(pressId, firstSetId, WorkoutSetField.Weight, "82.5"))
        viewModel.onAction(WorkoutDetailUiAction.OnSetFieldChange(pressId, firstSetId, WorkoutSetField.Reps, "7"))
        viewModel.onAction(WorkoutDetailUiAction.OnSetFieldChange(pressId, firstSetId, WorkoutSetField.Rpe, "8"))
        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        val set = workoutLogRepository.lastDraft!!.exercises.first().sets.first()
        assertEquals(82.5, set.weight, 0.0)
        assertEquals(7, set.reps)
        assertEquals(8, set.rpe)
    }

    @Test fun `empty rpe is null on draft`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        assertEquals(null, workoutLogRepository.lastDraft!!.exercises.first().sets.first().rpe)
    }

    @Test fun `null target weight empty input posts zero`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine(includeBodyweightSet = true))
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        val bodyweight = workoutLogRepository.lastDraft!!.exercises[1].sets.first()
        assertEquals(0.0, bodyweight.weight, 0.0)
    }

    @Test fun `second finish while in flight does not call use case twice`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        assertEquals(1, workoutLogRepository.finishWorkoutCallCount)
    }

    @Test fun `finish server error stays on content with finishFailed`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        workoutLogRepository.finishWorkoutResult = Result.Error(ApiException.ServerException("backend"))
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnNotesChange("keep me"))
        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        val state = viewModel.uiState.value as WorkoutDetailUiState.Content
        assertEquals("keep me", state.notes)
        assertEquals(Spanish.workoutTexts.finishFailed, state.finishError)
        assertEquals(false, state.finished)
        assertEquals(false, state.isFinishing)
    }

    @Test fun `finish not found stays on content with finishFailed`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        workoutLogRepository.finishWorkoutResult = Result.Error(ApiException.NotFoundException("missing"))
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        val state = viewModel.uiState.value as WorkoutDetailUiState.Content
        assertEquals(Spanish.workoutTexts.finishFailed, state.finishError)
        assertTrue(viewModel.uiState.value !is WorkoutDetailUiState.NotFound)
    }

    @Test fun `invalid rpe is null on draft`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        viewModel.onAction(WorkoutDetailUiAction.OnSetFieldChange(2L, 1L, WorkoutSetField.Rpe, "11"))
        viewModel.onAction(WorkoutDetailUiAction.OnFinishWorkout)
        advanceUntilIdle()

        assertEquals(null, workoutLogRepository.lastDraft!!.exercises.first().sets.first().rpe)
    }

    private fun routine(includeBodyweightSet: Boolean = false) = Routine(
        id = 5L, name = "Strength", description = null, difficultyLevel = DifficultyLevel.ADVANCED,
        goalType = GoalType.STRENGTH, isActive = true, scheduledTime = null,
        daysOfWeek = setOf(DayOfWeek.MONDAY), startDate = null, endDate = null,
        workouts = listOf(
            Workout(
                id = 40L, name = "Upper", dayOfWeek = 1,
                notes = "Respirar entre series",
                exercises = listOf(
                    WorkoutExercise(
                        1L, 1L, "Row", null, 2, null,
                        if (includeBodyweightSet) listOf(WorkoutSet(3L, 1, 12, null)) else emptyList(),
                    ),
                    WorkoutExercise(2L, 2L, "Press", null, 1, "Controlled tempo", listOf(
                        WorkoutSet(2L, 2, 10, 70.5), WorkoutSet(1L, 1, 8, 80.0),
                    )),
                ),
            ),
        ),
    )
}
