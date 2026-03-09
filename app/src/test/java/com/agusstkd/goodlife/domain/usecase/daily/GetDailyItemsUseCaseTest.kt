package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.usecase.daily.result.GetDailyItemsResult
import com.agusstkd.goodlife.fake.FakeDailyRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDailyItemsUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeDailyRepository
    private lateinit var useCase: GetDailyItemsUseCase

    @Before
    fun setUp() {
        repository = FakeDailyRepository()
        useCase = GetDailyItemsUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher)
        )
    }

    @Test
    fun `success returns DailyLog with items`() = runTest(testDispatcher) {
        val items = listOf(
            DailyItem(1, DailyItemType.TASK, 10, null, DailyItemStatus.PENDING, "Task 1", null),
            DailyItem(2, DailyItemType.HABIT, 20, null, DailyItemStatus.COMPLETED, "Habit 1", "3/5 veces"),
        )
        val log = DailyLog(id = 1, date = LocalDate(2026, 3, 9), completionRate = 0.5, items = items)
        repository.getDailyLogResult = Result.Success(log)

        val result = useCase(LocalDate(2026, 3, 9))

        assertTrue(result is GetDailyItemsResult.Success)
        val success = result as GetDailyItemsResult.Success
        assertEquals(2, success.dailyLog.items.size)
        assertEquals(0.5, success.dailyLog.completionRate, 0.001)
    }

    @Test
    fun `not found exception maps to NotFound`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(
            ApiException.NotFoundException("Daily log not found")
        )

        val result = useCase(LocalDate(2026, 3, 9))

        assertTrue(result is GetDailyItemsResult.NotFound)
    }

    @Test
    fun `server exception maps to ServerError`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(
            ApiException.ServerException("500 Internal Server Error")
        )

        val result = useCase(LocalDate(2026, 3, 9))

        assertTrue(result is GetDailyItemsResult.ServerError)
        assertEquals("500 Internal Server Error", (result as GetDailyItemsResult.ServerError).message)
    }

    @Test
    fun `network error maps to NetworkError`() = runTest(testDispatcher) {
        repository.getDailyLogResult = Result.Error(java.io.IOException("No internet"))

        val result = useCase(LocalDate(2026, 3, 9))

        assertTrue(result is GetDailyItemsResult.NetworkError)
    }

    @Test
    fun `passes correct date to repository`() = runTest(testDispatcher) {
        val date = LocalDate(2026, 6, 15)
        useCase(date)

        assertEquals(date, repository.lastRequestedDate)
        assertEquals(1, repository.getDailyLogCallCount)
    }
}
