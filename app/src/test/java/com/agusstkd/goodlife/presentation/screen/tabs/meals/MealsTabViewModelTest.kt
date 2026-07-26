package com.agusstkd.goodlife.presentation.screen.tabs.meals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.FakeDateProvider
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.usecase.nutrition.GetMealPlansUseCase
import com.agusstkd.goodlife.fake.FakeMealPlanRepository
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.meals.model.MealsUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MealsTabViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeMealPlanRepository
    private lateinit var navigation: FakeNavigationController

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeMealPlanRepository(); navigation = FakeNavigationController() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun viewModel(): MealsTabViewModel = MealsTabViewModel(
        dateProvider = FakeDateProvider(LocalDate(2026, 7, 26)),
        language = Spanish,
        getMealPlansUseCase = GetMealPlansUseCase(repository, TestDispatcherProvider(dispatcher)),
        navigationController = navigation,
    )

    @Test fun `subscription loads success totals for today`() = runTest(dispatcher) {
        repository.getResult = Result.Success(listOf(plan(1, MealType.BREAKFAST, 300.0), plan(2, MealType.LUNCH, 700.0)))
        val vm = viewModel()
        backgroundScope.launch { vm.uiState.collect {} }
        vm.refresh()
        advanceUntilIdle()

        val state = vm.uiState.value as MealsUiState.Success
        assertEquals(LocalDate(2026, 7, 26), state.date)
        assertEquals("Hoy", state.headerText)
        assertEquals(1000.0, state.totalCalories, 0.01)
        assertEquals(2, state.mealPlans.size)
    }

    @Test fun `previous and next day reload relative dates`() = runTest(dispatcher) {
        repository.getResult = Result.Success(listOf(plan(1, MealType.BREAKFAST, 300.0)))
        val vm = viewModel(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()

        vm.onAction(MealsUiAction.OnPreviousDay); advanceUntilIdle()
        assertEquals(LocalDate(2026, 7, 25), repository.requestedDates.last())
        val yesterday = vm.uiState.value as MealsUiState.Success
        assertEquals("Ayer", yesterday.headerText)

        vm.onAction(MealsUiAction.OnNextDay); advanceUntilIdle()
        assertEquals(LocalDate(2026, 7, 26), repository.requestedDates.last())
    }

    @Test fun `repository error maps to error state`() = runTest(dispatcher) {
        repository.getResult = Result.Error(Exception("offline"))
        val vm = viewModel(); backgroundScope.launch { vm.uiState.collect {} }; vm.refresh(); advanceUntilIdle()
        assertTrue(vm.uiState.value is MealsUiState.Error)
    }

    @Test fun `create meal plan action navigates to route`() = runTest(dispatcher) {
        val vm = viewModel()
        vm.onAction(MealsUiAction.OnCreateMealPlan)
        val action = navigation.navigatedActions.single() as NavigationAction.NavigateTo<*>
        assertEquals(AppRoute.CreateMealPlan, action.route)
    }

    private fun plan(id: Long, type: MealType, calories: Double) = DailyMealPlanSummary(
        id = id, name = "Plan $id", date = LocalDate(2026, 7, 26), mealType = type,
        scheduledTime = LocalTime(12, 0), mealName = "Meal $id", imageUrl = null,
        totalCalories = calories, totalProtein = 10.0, totalCarbs = 20.0, totalFat = 5.0, isActive = true
    )
}