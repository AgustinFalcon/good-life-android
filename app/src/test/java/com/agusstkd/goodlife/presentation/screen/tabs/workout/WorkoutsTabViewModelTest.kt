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
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsNavigationEffect
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.workout.model.WorkoutsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
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

    private fun vm(navigation: FakeNavigationController = FakeNavigationController()) = WorkoutsTabViewModel(
        language = Spanish,
        getActiveRoutineUseCase = GetActiveRoutineUseCase(repo, TestDispatcherProvider(dispatcher)),
        navigationController = navigation
    )

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

    @Test fun `successful missing active routine maps to no routine`() = runTest(dispatcher) {
        repo.getActiveRoutineResult = Result.Success(null)
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()

        assertTrue(vm.uiState.value is WorkoutsUiState.NoRoutine)
    }
    @Test fun `network error preserves localized connection copy`() = runTest(dispatcher) {
        repo.getActiveRoutineResult = Result.Error(Exception("offline"))
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()
        assertEquals(Spanish.errorTexts.connectionError, (vm.uiState.value as WorkoutsUiState.Error).message)
    }

    @Test fun `server error does not expose backend detail`() = runTest(dispatcher) {
        val backendDetail = "routine SQL trace"
        repo.getActiveRoutineResult = Result.Error(com.agusstkd.goodlife.core.network.ApiException.ServerException(backendDetail))
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()

        val state = vm.uiState.value as WorkoutsUiState.Error
        assertEquals(Spanish.errorTexts.dataLoadError, state.message)
        assertTrue(state.message != backendDetail)
    }

    @Test fun `workout click emits a tab-local typed detail effect`() = runTest(dispatcher) {
        val navigation = FakeNavigationController()
        val viewModel = vm(navigation)
        val effect = async { viewModel.navigationEffects.first() }
        runCurrent()

        viewModel.onAction(WorkoutsUiAction.OnWorkoutClick(42L))

        assertEquals(WorkoutsNavigationEffect.NavigateToWorkoutDetail(42L), effect.await())
        assertTrue(navigation.navigatedActions.isEmpty())
    }

    @Test fun `double workout click cannot leave a stale detail navigation`() = runTest(dispatcher) {
        val viewModel = vm()
        val firstEffect = async { viewModel.navigationEffects.first() }
        runCurrent()

        viewModel.onAction(WorkoutsUiAction.OnWorkoutClick(42L))
        viewModel.onAction(WorkoutsUiAction.OnWorkoutClick(42L))

        assertEquals(WorkoutsNavigationEffect.NavigateToWorkoutDetail(42L), firstEffect.await())
        viewModel.onWorkoutListResumed()
        val staleEffect = async { withTimeoutOrNull(1) { viewModel.navigationEffects.first() } }
        advanceUntilIdle()
        assertEquals(null, staleEffect.await())
    }
    @Test fun `create routine action navigates to the app creation route`() = runTest(dispatcher) {
        val navigation = FakeNavigationController()
        val vm = vm(navigation)

        vm.onAction(WorkoutsUiAction.OnCreateRoutine)

        assertEquals(
            AppRoute.CreateRoutine,
            (navigation.navigatedActions.single() as NavigationAction.NavigateTo<*>).route
        )
    }
    private fun routine() = Routine(
        id = 5L, name = "Strength Plan", description = "desc", difficultyLevel = DifficultyLevel.ADVANCED,
        goalType = GoalType.STRENGTH, isActive = true, scheduledTime = null,
        daysOfWeek = setOf(DayOfWeek.MONDAY), startDate = null, endDate = null,
        workouts = listOf(Workout(1L, "Upper", 1, listOf(WorkoutExercise(1L, 10L, "Bench", null, 0, null, listOf(WorkoutSet(1L, 1, 8, 80.0), WorkoutSet(2L, 2, 8, 80.0))))))
    )
}
