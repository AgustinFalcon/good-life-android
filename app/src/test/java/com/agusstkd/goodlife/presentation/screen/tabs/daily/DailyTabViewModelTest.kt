package com.agusstkd.goodlife.presentation.screen.tabs.daily

import com.agusstkd.goodlife.core.datetime.FakeDateProvider
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
import com.agusstkd.goodlife.fake.FakeDailyRepository
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.daily.model.DailyUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.datetime.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DailyTabViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fixedDate = LocalDate(2026, 3, 9)
    private lateinit var repository: FakeDailyRepository
    private lateinit var viewModel: DailyTabViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeDailyRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DailyTabViewModel {
        val dispatcher = TestDispatcherProvider(testDispatcher)
        return DailyTabViewModel(
            dateProvider = FakeDateProvider(fixedDate),
            language = Spanish,
            getDailyItemsUseCase = GetDailyItemsUseCase(repository, dispatcher),
            updateItemStatusUseCase = UpdateItemStatusUseCase(repository, dispatcher)
        )
    }

    @Test
    fun `init loads items and shows Success state`() = runTest(testDispatcher) {
        val log = DailyLog(
            id = 1, date = fixedDate, completionRate = 0.75,
            items = listOf(
                DailyItem(1, DailyItemType.TASK, 10, null, DailyItemStatus.COMPLETED, "Task 1", null),
                DailyItem(2, DailyItemType.HABIT, 20, null, DailyItemStatus.PENDING, "Habit 1", "2/5"),
            )
        )
        repository.getDailyLogResult = Result.Success(log)

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DailyUiState.Success)
        val success = state as DailyUiState.Success
        assertEquals(2, success.items.size)
        assertEquals(0.75, success.completionRate, 0.001)
    }

    @Test
    fun `init with not found shows Empty state`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(
            ApiException.NotFoundException("Not found")
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is DailyUiState.Empty)
    }

    @Test
    fun `init with network error shows Error state`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(java.io.IOException("No internet"))

        viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is DailyUiState.Error)
    }

    @Test
    fun `OnNextDay increments date and reloads`() = runTest(testDispatcher) {
        val todayLog = DailyLog(1, fixedDate, 0.5, emptyList())
        val tomorrowLog = DailyLog(2, fixedDate, 1.0, emptyList())
        repository.getDailyLogResult = Result.Success(todayLog)

        viewModel = createViewModel()
        advanceUntilIdle()

        repository.getDailyLogResult = Result.Success(tomorrowLog)
        viewModel.onAction(DailyUiAction.OnNextDay)
        advanceUntilIdle()

        val state = viewModel.uiState.value as DailyUiState.Success
        assertEquals(1.0, state.completionRate, 0.001)
        assertEquals(2, repository.getDailyLogCallCount)
    }

    @Test
    fun `OnPreviousDay decrements date and reloads`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Success(
            DailyLog(1, fixedDate, 0.0, emptyList())
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(DailyUiAction.OnPreviousDay)
        advanceUntilIdle()

        assertEquals(2, repository.getDailyLogCallCount)
    }

    @Test
    fun `refresh reloads items`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Success(
            DailyLog(1, fixedDate, 0.0, emptyList())
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(2, repository.getDailyLogCallCount)
    }

    @Test
    fun `OnItemStatusChange updates item status`() = runTest(testDispatcher) {
        val log = DailyLog(1, fixedDate, 0.0, listOf(
            DailyItem(1, DailyItemType.TASK, 10, null, DailyItemStatus.PENDING, "Task", null)
        ))
        repository.getDailyLogResult = Result.Success(log)

        viewModel = createViewModel()
        advanceUntilIdle()

        val updatedLog = log.copy(completionRate = 1.0)
        repository.updateItemStatusResult = Result.Success(updatedLog)

        viewModel.onAction(DailyUiAction.OnItemStatusChange(1, DailyItemStatus.COMPLETED))
        advanceUntilIdle()

        val state = viewModel.uiState.value as DailyUiState.Success
        assertEquals(1.0, state.completionRate, 0.001)
    }

    @Test
    fun `status updates are serialized and preserve the latest accepted action`() = runTest(testDispatcher) {
        val log = DailyLog(
            id = 1,
            date = fixedDate,
            completionRate = 0.0,
            items = listOf(
                DailyItem(1, DailyItemType.TASK, 10, null, DailyItemStatus.PENDING, "Task", null)
            )
        )
        repository.getDailyLogResult = Result.Success(log)
        viewModel = createViewModel()
        advanceUntilIdle()

        val firstResult = CompletableDeferred<Result<DailyLog>>()
        val secondResult = CompletableDeferred<Result<DailyLog>>()
        repository.updateItemStatusHandler = { itemId, _ ->
            if (itemId == 1L) {
                firstResult.await()
            } else {
                secondResult.await()
            }
        }

        viewModel.onAction(DailyUiAction.OnItemStatusChange(1, DailyItemStatus.COMPLETED))
        runCurrent()
        viewModel.onAction(DailyUiAction.OnItemStatusChange(2, DailyItemStatus.SKIPPED))
        runCurrent()

        assertEquals(1, repository.updateItemStatusCallCount)
        firstResult.complete(Result.Success(log.copy(completionRate = 0.2)))
        runCurrent()
        assertEquals(2, repository.updateItemStatusCallCount)
        secondResult.complete(Result.Success(log.copy(completionRate = 0.8)))
        advanceUntilIdle()

        val state = viewModel.uiState.value as DailyUiState.Success
        assertEquals(0.8, state.completionRate, 0.001)
    }

    @Test
    fun `late daily response does not replace the selected date`() = runTest(testDispatcher) {
        val nextDate = LocalDate(2026, 3, 10)
        var completeInitialRequest: ((Result<DailyLog>) -> Unit)? = null
        val initialRequestStarted = CompletableDeferred<Unit>()
        repository.getDailyLogHandler = { requestedDate ->
            if (requestedDate == fixedDate) {
                initialRequestStarted.complete(Unit)
                suspendCoroutine<Result<DailyLog>> { continuation ->
                    completeInitialRequest = { result -> continuation.resume(result) }
                }
            } else {
                Result.Success(DailyLog(2, nextDate, 1.0, emptyList()))
            }
        }

        viewModel = createViewModel()
        val emittedDates = mutableListOf<LocalDate>()
        backgroundScope.launch {
            viewModel.uiState.collect { state ->
                if (state is DailyUiState.Success) emittedDates += state.date
            }
        }
        runCurrent()
        assertTrue(initialRequestStarted.isCompleted)
        viewModel.onAction(DailyUiAction.OnNextDay)
        runCurrent()

        completeInitialRequest?.invoke(Result.Success(DailyLog(1, fixedDate, 0.0, emptyList())))
        advanceUntilIdle()

        val state = viewModel.uiState.value as DailyUiState.Success
        assertEquals(nextDate, state.date)
        assertEquals(1.0, state.completionRate, 0.001)
        assertTrue(emittedDates.none { it == fixedDate })
    }

    @Test
    fun `header text shows Hoy for today`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Success(
            DailyLog(1, fixedDate, 0.0, emptyList())
        )

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value as DailyUiState.Success
        assertEquals("Hoy", state.headerText)
    }
}
