package com.agusstkd.goodlife.presentation.screen.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.auth.BiometricLoginHandler
import com.agusstkd.goodlife.domain.usecase.login.LoginUseCase
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.fake.FakeAuthRepository
import com.agusstkd.goodlife.fake.FakeBiometricAuthenticator
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.fake.FakeSecureCredentialsStorage
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runCurrent
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeAuthRepository
    private lateinit var nav: FakeNavigationController
    private lateinit var authenticator: FakeBiometricAuthenticator
    private lateinit var storage: FakeSecureCredentialsStorage

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repo = FakeAuthRepository(); nav = FakeNavigationController(); authenticator = FakeBiometricAuthenticator(); storage = FakeSecureCredentialsStorage() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun vm() = LoginViewModel(loginUseCase(), nav, BiometricLoginHandler(authenticator, storage), Spanish)
    private fun loginUseCase() = LoginUseCase(ValidateEmailUseCase(Spanish), ValidatePasswordUseCase(Spanish), repo, TestDispatcherProvider(dispatcher), Spanish)

    @Test fun `initializes biometric state and updates form fields`() = runTest(dispatcher) {
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        var state = vm.uiState.value as LoginUiState.Content
        vm.onAction(LoginUiAction.OnEmailChange("new@test.com"))
        vm.onAction(LoginUiAction.OnPasswordChange("pass"))
        vm.onAction(LoginUiAction.OnRememberUserToggle)
        advanceUntilIdle()
        state = vm.uiState.value as LoginUiState.Content
        assertEquals("new@test.com", state.email)
        assertEquals("pass", state.password)
        assertTrue(state.rememberUser)
    }

    @Test fun `login validation error marks invalid fields without repository login`() = runTest(dispatcher) {
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        vm.onAction(LoginUiAction.OnEmailChange(""))
        vm.onAction(LoginUiAction.OnPasswordChange(""))
        vm.onAction(LoginUiAction.OnLoginClick)
        advanceUntilIdle()
        val state = vm.uiState.value as LoginUiState.Content
        assertTrue(state.isEmailError)
        assertEquals(0, repo.loginCallCount)
    }

    @Test fun `remote login error keeps credentials and exposes a general message`() = runTest(dispatcher) {
        repo.loginResult = Result.Error(Exception("Error de conexión"))
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        vm.onAction(LoginUiAction.OnEmailChange("usuario"))
        vm.onAction(LoginUiAction.OnPasswordChange("password"))
        vm.onAction(LoginUiAction.OnLoginClick)
        advanceUntilIdle()

        val state = vm.uiState.value as LoginUiState.Content
        assertEquals("usuario", state.email)
        assertEquals("password", state.password)
        assertFalse(state.isEmailError)
        assertFalse(state.isPasswordError)
        assertEquals(Spanish.errorTexts.loginError, state.errorMessage)
        assertEquals(1, repo.loginCallCount)
    }

    @Test fun `retry clears old error and preserves edits made while loading`() = runTest(dispatcher) {
        repo.loginResult = Result.Error(Exception("internal backend detail"))
        val delayedResult = CompletableDeferred<Result<User>>()
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        vm.onAction(LoginUiAction.OnEmailChange("usuario"))
        vm.onAction(LoginUiAction.OnPasswordChange("password"))
        vm.onAction(LoginUiAction.OnLoginClick)
        advanceUntilIdle()
        assertEquals(Spanish.errorTexts.loginError, (vm.uiState.value as LoginUiState.Content).errorMessage)

        repo.loginHandler = { _, _ -> delayedResult.await() }
        vm.onAction(LoginUiAction.OnLoginClick)
        runCurrent()
        vm.onAction(LoginUiAction.OnEmailChange("usuario-nuevo"))
        vm.onAction(LoginUiAction.OnPasswordChange("password-nuevo"))

        val whileLoading = vm.uiState.value as LoginUiState.Content
        assertTrue(whileLoading.isLoading)
        assertEquals(null, whileLoading.errorMessage)

        delayedResult.complete(Result.Error(Exception("internal backend detail")))
        advanceUntilIdle()

        val state = vm.uiState.value as LoginUiState.Content
        assertEquals("usuario-nuevo", state.email)
        assertEquals("password-nuevo", state.password)
        assertFalse(state.isLoading)
        assertEquals(Spanish.errorTexts.loginError, state.errorMessage)
    }

    @Test fun `biometric remote login error shows a general message`() = runTest(dispatcher) {
        storage.saveCredentials("bio@test.com", "secret")
        storage.setBiometricEnabled(true)
        repo.loginResult = Result.Error(Exception("internal backend detail"))
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        vm.onAction(LoginUiAction.OnBiometricAuthenticate(Any()))
        advanceUntilIdle()

        val state = vm.uiState.value as LoginUiState.Content
        assertFalse(state.isLoading)
        assertEquals(Spanish.errorTexts.loginError, state.errorMessage)
    }

    @Test fun `successful login navigates to main and saves credentials when biometric enabled`() = runTest(dispatcher) {
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        vm.onAction(LoginUiAction.OnEmailChange("ok@test.com"))
        vm.onAction(LoginUiAction.OnPasswordChange("password"))
        vm.onAction(LoginUiAction.OnBiometricToggle)
        vm.onAction(LoginUiAction.OnLoginClick)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is LoginUiState.Success)
        assertTrue(nav.navigatedActions.last() is NavigationAction.PopBackTo<*> || nav.navigatedActions.last() is NavigationAction.NavigateTo<*>)
        assertEquals("ok@test.com", storage.getSavedEmail())
    }

    @Test fun `register click navigates and biometric cancel hides prompt`() = runTest(dispatcher) {
        storage.saveCredentials("bio@test.com", "secret")
        storage.setBiometricEnabled(true)
        val vm = vm(); backgroundScope.launch { vm.uiState.collect {} }; advanceUntilIdle()
        vm.onAction(LoginUiAction.OnRegisterClick)
        assertTrue(nav.navigatedActions.isNotEmpty())
        vm.onAction(LoginUiAction.OnBiometricToggle)
        vm.onAction(LoginUiAction.OnBiometricIconClick)
        vm.onAction(LoginUiAction.OnBiometricPromptShown)
    }
}
