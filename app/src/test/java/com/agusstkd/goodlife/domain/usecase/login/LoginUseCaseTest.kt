package com.agusstkd.goodlife.domain.usecase.login

import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.fake.FakeAuthRepository
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()
    private val language = Spanish
    private lateinit var repository: FakeAuthRepository
    private lateinit var useCase: LoginUseCase

    @Before
    fun setUp() {
        repository = FakeAuthRepository()
        useCase = LoginUseCase(
            validateEmail = ValidateEmailUseCase(language),
            validatePassword = ValidatePasswordUseCase(language),
            authRepository = repository,
            dispatcher = TestDispatcherProvider(testDispatcher),
            language = language
        )
    }

    @Test
    fun `valid credentials return Success`() = runTest(testDispatcher) {
        val result = useCase(email = "test@mail.com", password = "password123")

        assertTrue(result is LoginResult.Success)
        assertEquals(1, repository.loginCallCount)
    }

    @Test
    fun `blank email returns ValidationError with email error`() = runTest(testDispatcher) {
        val result = useCase(email = "", password = "password123")

        assertTrue(result is LoginResult.ValidationError)
        val error = result as LoginResult.ValidationError
        assertNotNull(error.emailError)
        assertNull(error.passwordError)
        assertEquals(0, repository.loginCallCount)
    }

    @Test
    fun `short email returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(email = "ab", password = "password123")

        assertTrue(result is LoginResult.ValidationError)
        assertNotNull((result as LoginResult.ValidationError).emailError)
        assertEquals(0, repository.loginCallCount)
    }

    @Test
    fun `blank password returns ValidationError with password error`() = runTest(testDispatcher) {
        val result = useCase(email = "test@mail.com", password = "")

        assertTrue(result is LoginResult.ValidationError)
        val error = result as LoginResult.ValidationError
        assertNull(error.emailError)
        assertNotNull(error.passwordError)
        assertEquals(0, repository.loginCallCount)
    }

    @Test
    fun `repository error returns Error`() = runTest(testDispatcher) {
        repository.loginResult = Result.Error(Exception("Invalid credentials"))

        val result = useCase(email = "test@mail.com", password = "password123")

        assertTrue(result is LoginResult.Error)
        assertEquals(1, repository.loginCallCount)
    }

    @Test
    fun `invalid email format returns ValidationError`() = runTest(testDispatcher) {
        val result = useCase(email = "invalid@", password = "password123")

        assertTrue(result is LoginResult.ValidationError)
        assertNotNull((result as LoginResult.ValidationError).emailError)
    }
}
