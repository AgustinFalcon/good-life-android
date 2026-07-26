package com.agusstkd.goodlife.presentation.screen.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.register.RegisterUseCase
import com.agusstkd.goodlife.domain.usecase.validation.*
import com.agusstkd.goodlife.fake.FakeAuthRepository
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiAction
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeAuthRepository
    private lateinit var nav: FakeNavigationController

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repo = FakeAuthRepository(); nav = FakeNavigationController() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun vm() = RegisterViewModel(registerUseCase(), nav, Spanish)
    private fun registerUseCase() = RegisterUseCase(repo, ValidateUserNameUseCase(Spanish), ValidateFullNameUseCase(Spanish), ValidateEmailUseCase(Spanish), ValidatePasswordUseCase(Spanish), ValidatePasswordMatchUseCase(Spanish), TestDispatcherProvider(dispatcher), Spanish)

    @Test fun `field changes update content and terms guard blocks submit`() = runTest(dispatcher) {
        val vm = vm()
        vm.onAction(RegisterUiAction.OnFullNameChange("Agustin Falcon"))
        vm.onAction(RegisterUiAction.OnUserNameChange("agus"))
        vm.onAction(RegisterUiAction.OnEmailChange("a@test.com"))
        vm.onAction(RegisterUiAction.OnPasswordChange("Password1!"))
        vm.onAction(RegisterUiAction.OnConfirmPasswordChange("Different1!"))
        var state = vm.uiState.value as RegisterUiState.Content
        assertTrue(state.isConfirmPasswordError)
        vm.onAction(RegisterUiAction.OnRegisterClick)
        state = vm.uiState.value as RegisterUiState.Content
        assertEquals(0, repo.registerCallCount)
        Assert.assertNotNull(state.errorMessage)
        vm.onAction(RegisterUiAction.OnDismissError)
        Assert.assertNull((vm.uiState.value as RegisterUiState.Content).errorMessage)
    }

    @Test fun `validation error marks field when terms accepted`() = runTest(dispatcher) {
        val vm = vm()
        vm.onAction(RegisterUiAction.OnTermsToggle)
        vm.onAction(RegisterUiAction.OnRegisterClick)
        advanceUntilIdle()
        val state = vm.uiState.value as RegisterUiState.Content
        assertTrue(state.isFullNameError)
        assertEquals(0, repo.registerCallCount)
    }

    @Test fun `successful register navigates to login`() = runTest(dispatcher) {
        val vm = vm()
        fillValid(vm)
        vm.onAction(RegisterUiAction.OnRegisterClick)
        advanceUntilIdle()
        assertTrue(vm.uiState.value is RegisterUiState.Success)
        assertTrue(nav.navigatedActions.last() is NavigationAction.PopBackTo<*> || nav.navigatedActions.last() is NavigationAction.NavigateTo<*>)
        assertEquals(1, repo.registerCallCount)
    }

    @Test fun `server error remains content with message`() = runTest(dispatcher) {
        repo.registerResult = Result.Error(Exception("duplicado"))
        val vm = vm(); fillValid(vm)
        vm.onAction(RegisterUiAction.OnRegisterClick); advanceUntilIdle()
        val state = vm.uiState.value as RegisterUiState.Content
        Assert.assertNotNull(state.errorMessage)
    }

    private fun fillValid(vm: RegisterViewModel) {
        vm.onAction(RegisterUiAction.OnFullNameChange("Agustin Falcon"))
        vm.onAction(RegisterUiAction.OnUserNameChange("agustin"))
        vm.onAction(RegisterUiAction.OnEmailChange("agustin@test.com"))
        vm.onAction(RegisterUiAction.OnPasswordChange("Password1!"))
        vm.onAction(RegisterUiAction.OnConfirmPasswordChange("Password1!"))
        vm.onAction(RegisterUiAction.OnTermsToggle)
    }
}
