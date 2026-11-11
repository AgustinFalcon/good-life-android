package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.routine.result.ActivateRoutineResult
import com.agusstkd.goodlife.fake.FakeRoutineRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ActivateRoutineUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeRoutineRepository
    private lateinit var useCase: ActivateRoutineUseCase

    @Before
    fun setUp() {
        repository = FakeRoutineRepository()
        useCase = ActivateRoutineUseCase(
            repository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher),
        )
    }

    @Test
    fun `success maps to Success`() = runTest(testDispatcher) {
        val result = useCase(1L)

        assertEquals(ActivateRoutineResult.Success, result)
        assertEquals(1, repository.activateRoutineCallCount)
    }

    @Test
    fun `ServerException maps to ServerError without backend copy`() = runTest(testDispatcher) {
        repository.activateRoutineResult = Result.Error(
            ApiException.ServerException("SQLSTATE leaked"),
        )

        val result = useCase(1L)

        assertEquals(ActivateRoutineResult.ServerError, result)
    }

    @Test
    fun `non server error maps to NetworkError`() = runTest(testDispatcher) {
        repository.activateRoutineResult = Result.Error(
            ApiException.UnauthorizedException("token expired"),
        )

        val result = useCase(1L)

        assertTrue(result is ActivateRoutineResult.NetworkError)
    }
}
