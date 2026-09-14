package com.agusstkd.goodlife.presentation.screen.tabs.daily.detail

import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.fake.FakeDailyRepository
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.model.DailyDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DailyDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val date = LocalDate(2026, 9, 12)
    private lateinit var repository: FakeDailyRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeDailyRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(itemId: Long = 10L, dateIso: String = date.toString()) = DailyDetailViewModel(
        itemId = itemId,
        dateIso = dateIso,
        language = Spanish,
        getDailyItemsUseCase = GetDailyItemsUseCase(repository, TestDispatcherProvider(testDispatcher)),
    )

    @Test
    fun `non-positive id is invalid and does not query data`() = runTest(testDispatcher) {
        val viewModel = createViewModel(itemId = 0L)

        assertEquals(DailyDetailUiState.InvalidRoute, viewModel.uiState.value)
        assertEquals(0, repository.getDailyLogCallCount)
    }

    @Test
    fun `non ISO route date is invalid and does not query data`() = runTest(testDispatcher) {
        val viewModel = createViewModel(dateIso = "12-09-2026")

        assertEquals(DailyDetailUiState.InvalidRoute, viewModel.uiState.value)
        assertEquals(0, repository.getDailyLogCallCount)
    }

    @Test
    fun `found item maps all visible fields to localized content`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Success(
            DailyLog(
                id = 1L,
                date = date,
                completionRate = 0.5,
                items = listOf(
                    DailyItem(
                        id = 10L,
                        type = DailyItemType.HABIT,
                        referenceId = 9L,
                        scheduledTime = LocalTime(8, 30),
                        status = DailyItemStatus.IN_PROGRESS,
                        title = "Hydrate",
                        description = "Drink water",
                    ),
                ),
            ),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            DailyDetailUiState.Content(
                title = "Hydrate",
                type = Spanish.dailyTexts.habit,
                status = "En progreso",
                scheduledTime = "08:30",
                description = "Drink water",
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `missing item becomes NotFound`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Success(
            DailyLog(id = 1L, date = date, completionRate = 0.0, items = emptyList()),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DailyDetailUiState.NotFound, viewModel.uiState.value)
    }

    @Test
    fun `network detail error stays localized`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(java.io.IOException("offline"))
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(DailyDetailUiState.Error(Spanish.errorTexts.connectionError), viewModel.uiState.value)
    }
    @Test
    fun `server detail is not exposed and retry keeps route parameters`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(ApiException.ServerException("private backend trace"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        val initialState = viewModel.uiState.value as DailyDetailUiState.Error
        assertEquals(Spanish.errorTexts.dataLoadError, initialState.message)
        assertTrue(!initialState.message.contains("private backend trace"))

        repository.getDailyLogResult = Result.Error(ApiException.NotFoundException("not found"))
        viewModel.retry()
        advanceUntilIdle()

        assertEquals(DailyDetailUiState.NotFound, viewModel.uiState.value)
        assertEquals(date, repository.lastRequestedDate)
        assertEquals(2, repository.getDailyLogCallCount)
    }
}