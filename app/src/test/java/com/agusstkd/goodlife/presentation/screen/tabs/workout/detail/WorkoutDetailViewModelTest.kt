package com.agusstkd.goodlife.presentation.screen.tabs.workout.detail

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
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import com.agusstkd.goodlife.presentation.screen.tabs.workout.detail.model.WorkoutDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRoutineRepository

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeRoutineRepository() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun viewModel(workoutId: Long) = WorkoutDetailViewModel(
        workoutId = workoutId,
        language = Spanish,
        getActiveRoutineUseCase = GetActiveRoutineUseCase(repository, TestDispatcherProvider(dispatcher)),
    )

    @Test fun `selected workout maps sorted exercises and sets`() = runTest(dispatcher) {
        repository.getActiveRoutineResult = Result.Success(routine())
        val viewModel = viewModel(workoutId = 40L)
        advanceUntilIdle()

        val state = viewModel.uiState.value as WorkoutDetailUiState.Content
        assertEquals("Upper", state.workoutName)
        assertEquals("Día 1", state.dayLabel)
        assertEquals(listOf("Press", "Row"), state.exercises.map { it.name })
        assertEquals("Set 1", state.exercises.first().sets.first().label)
        assertEquals("8 reps · 80 kg", state.exercises.first().sets.first().summary)
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
    private fun routine() = Routine(
        id = 5L, name = "Strength", description = null, difficultyLevel = DifficultyLevel.ADVANCED,
        goalType = GoalType.STRENGTH, isActive = true, scheduledTime = null,
        daysOfWeek = setOf(DayOfWeek.MONDAY), startDate = null, endDate = null,
        workouts = listOf(
            Workout(
                id = 40L, name = "Upper", dayOfWeek = 1,
                exercises = listOf(
                    WorkoutExercise(1L, 1L, "Row", null, 2, null, emptyList()),
                    WorkoutExercise(2L, 2L, "Press", null, 1, "Controlled tempo", listOf(
                        WorkoutSet(2L, 2, 10, 70.5), WorkoutSet(1L, 1, 8, 80.0),
                    )),
                ),
            ),
        ),
    )
}
