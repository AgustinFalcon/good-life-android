package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail

import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.usecase.nutrition.GetMealPlansUseCase
import com.agusstkd.goodlife.fake.FakeMealPlanRepository
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model.MealDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MealDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val date = LocalDate(2026, 9, 13)
    private lateinit var repository: FakeMealPlanRepository
    @Before fun setUp() { Dispatchers.setMain(dispatcher); repository = FakeMealPlanRepository() }
    @After fun tearDown() = Dispatchers.resetMain()
    private fun vm(id: Long = 4, iso: String = date.toString()) = MealDetailViewModel(id, iso, Spanish, GetMealPlansUseCase(repository, TestDispatcherProvider(dispatcher)))

    @Test fun `invalid route does not query`() = runTest(dispatcher) {
        val viewModel = vm(0); assertEquals(MealDetailUiState.InvalidRoute, viewModel.uiState.value); assertTrue(repository.requestedDates.isEmpty())
    }
    @Test fun `non ISO date is invalid and does not query`() = runTest(dispatcher) {
        val viewModel = vm(4, "13-09-2026")
        assertEquals(MealDetailUiState.InvalidRoute, viewModel.uiState.value)
        assertTrue(repository.requestedDates.isEmpty())
    }
    @Test fun `found plan exposes visible state`() = runTest(dispatcher) {
        repository.getResult = Result.Success(listOf(DailyMealPlanSummary(4, "Plan", date, MealType.LUNCH, null, "Bowl", null, 500.0, 30.0, 60.0, 10.0, true)))
        val viewModel = vm(); advanceUntilIdle(); val state = viewModel.uiState.value as MealDetailUiState.Content
        assertEquals("Plan", state.planName); assertTrue(state.isActive); assertEquals(500.0, state.totalCalories, 0.0)
    }
    @Test fun `missing plan becomes not found`() = runTest(dispatcher) { val viewModel = vm(); advanceUntilIdle(); assertEquals(MealDetailUiState.NotFound, viewModel.uiState.value) }
    @Test fun `network detail error stays localized and retry preserves date`() = runTest(dispatcher) {
        repository.getResult = Result.Error(java.io.IOException("offline"))
        val viewModel = vm()
        advanceUntilIdle()
        assertEquals(MealDetailUiState.Error(Spanish.errorTexts.connectionError), viewModel.uiState.value)
        repository.getResult = Result.Error(ApiException.NotFoundException("missing"))
        viewModel.retry()
        advanceUntilIdle()
        assertEquals(MealDetailUiState.NotFound, viewModel.uiState.value)
        assertEquals(date, repository.requestedDates.last())
    }
    @Test fun `server detail is sanitized`() = runTest(dispatcher) {
        repository.getResult = Result.Error(ApiException.ServerException("private trace")); val viewModel = vm(); advanceUntilIdle()
        val state = viewModel.uiState.value as MealDetailUiState.Error; assertEquals(Spanish.errorTexts.dataLoadError, state.message); assertTrue(!state.message.contains("private trace"))
    }
}
