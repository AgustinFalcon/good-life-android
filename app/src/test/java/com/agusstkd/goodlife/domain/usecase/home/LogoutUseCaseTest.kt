package com.agusstkd.goodlife.domain.usecase.home

import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.home.result.LogoutResult
import com.agusstkd.goodlife.fake.FakeAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LogoutUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeAuthRepository
    private lateinit var useCase: LogoutUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeAuthRepository()
        useCase = LogoutUseCase(repository, TestDispatcherProvider(dispatcher))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `success calls repository logout`() = runTest(dispatcher) {
        repository.logoutResult = Result.Success(Unit)

        val result = useCase()

        assertEquals(LogoutResult.Success, result)
        assertEquals(1, repository.logoutCallCount)
    }

    @Test
    fun `error still calls repository logout`() = runTest(dispatcher) {
        val failure = Result.Error(Exception("session revoke failed"))
        repository.logoutResult = failure

        val result = useCase()

        assertEquals(LogoutResult.Failed, result)
        assertEquals(1, repository.logoutCallCount)
    }
}
