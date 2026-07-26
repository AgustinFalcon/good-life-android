package com.agusstkd.goodlife.presentation.screen.tabs.workout

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.DifficultyLevel
import com.agusstkd.goodlife.domain.model.training.GoalType
import com.agusstkd.goodlife.domain.model.training.Routine
import com.agusstkd.goodlife.domain.model.training.Workout
import com.agusstkd.goodlife.domain.model.training.WorkoutExercise
import com.agusstkd.goodlife.domain.model.training.WorkoutSet
import com.agusstkd.goodlife.domain.usecase.routine.GetActiveRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.result.GetActiveRoutineResult
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutsTabViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeRoutineRepository

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repo = FakeRoutineRepository() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun vm() = WorkoutsTabViewModel(Spanish, GetActiveRoutineUseCase(repo, TestDispatcherProvider(dispatcher)))

    @Test fun `refresh maps active routine to success labels and workout totals`() = runTest(dispatcher) {
        repo.getActiveRoutineResult = Result.Success(routine())
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()
        val state = vm.uiState.value as WorkoutsUiState.Success
        assertEquals("Strength Plan", state.routineName)
        assertEquals(1, state.workouts.size)
        assertEquals(2, state.workouts[0].totalSets)
        assertTrue(state.difficultyLabel.isNotBlank())
        assertTrue(state.goalLabel.isNotBlank())
    }

    @Test fun `not found maps to no routine`() = runTest(dispatcher) {
        repo.getActiveRoutineResult = Result.Error(com.agusstkd.goodlife.core.network.ApiException.NotFoundException("missing"))
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()
        assertTrue(vm.uiState.value is WorkoutsUiState.NoRoutine)
    }

    @Test fun `repository error maps to error`() = runTest(dispatcher) {
        repo.getActiveRoutineResult = Result.Error(Exception("offline"))
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()
        assertTrue(vm.uiState.value is WorkoutsUiState.Error)
    }

    private fun routine() = Routine(
        id = 5L, name = "Strength Plan", description = "desc", difficultyLevel = DifficultyLevel.ADVANCED,
        goalType = GoalType.STRENGTH, isActive = true, scheduledTime = null,
        daysOfWeek = setOf(DayOfWeek.MONDAY), startDate = null, endDate = null,
        workouts = listOf(Workout(1L, "Upper", 1, listOf(WorkoutExercise(1L, 10L, "Bench", null, 0, null, listOf(WorkoutSet(1L, 1, 8, 80.0), WorkoutSet(2L, 2, 8, 80.0))))))
    )
}
