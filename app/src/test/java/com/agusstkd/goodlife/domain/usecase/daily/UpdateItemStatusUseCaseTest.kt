package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.usecase.daily.result.UpdateItemStatusResult
import com.agusstkd.goodlife.fake.FakeDailyRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateItemStatusUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeDailyRepository
    private lateinit var useCase: UpdateItemStatusUseCase

    @Before
    fun setUp() {
        repository = FakeDailyRepository()
        useCase = UpdateItemStatusUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher)
        )
    }

    @Test
    fun `success returns updated DailyLog`() = runTest(testDispatcher) {
        val updatedLog = DailyLog(1, LocalDate(2026, 3, 9), 1.0, emptyList())
        repository.updateItemStatusResult = Result.Success(updatedLog)

        val result = useCase(itemId = 42L, status = DailyItemStatus.COMPLETED)

        assertTrue(result is UpdateItemStatusResult.Success)
        assertEquals(1.0, (result as UpdateItemStatusResult.Success).dailyLog.completionRate, 0.001)
        assertEquals(1, repository.updateItemStatusCallCount)
    }

    @Test
    fun `not found maps to NotFound`() = runTest(testDispatcher) {
        repository.updateItemStatusResult = Result.Error(
            ApiException.NotFoundException("Item not found")
        )

        val result = useCase(itemId = 99L, status = DailyItemStatus.COMPLETED)

        assertTrue(result is UpdateItemStatusResult.NotFound)
    }

    @Test
    fun `server error maps to ServerError`() = runTest(testDispatcher) {
        repository.updateItemStatusResult = Result.Error(
            ApiException.ServerException("DB error")
        )

        val result = useCase(itemId = 1L, status = DailyItemStatus.SKIPPED)

        assertTrue(result is UpdateItemStatusResult.ServerError)
    }

    @Test
    fun `network error maps to NetworkError`() = runTest(testDispatcher) {
        repository.updateItemStatusResult = Result.Error(java.io.IOException("timeout"))

        val result = useCase(itemId = 1L, status = DailyItemStatus.PENDING)

        assertTrue(result is UpdateItemStatusResult.NetworkError)
    }
}
