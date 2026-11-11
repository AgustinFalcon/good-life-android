package com.agusstkd.goodlife.domain.usecase.session

import com.agusstkd.goodlife.fake.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckSessionUseCaseTest {

    @Test
    fun `tokens without local user are not a session`() = runTest {
        val repository = FakeAuthRepository().apply { loggedIn = false }
        val useCase = CheckSessionUseCase(authRepository = repository)

        assertFalse(useCase())
    }

    @Test
    fun `tokens and local user are a session`() = runTest {
        val repository = FakeAuthRepository().apply { loggedIn = true }
        val useCase = CheckSessionUseCase(authRepository = repository)

        assertTrue(useCase())
    }
}
