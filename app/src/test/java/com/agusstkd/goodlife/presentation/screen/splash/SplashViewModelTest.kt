package com.agusstkd.goodlife.presentation.screen.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.domain.usecase.session.CheckSessionUseCase
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiAction
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun vm(
        hasSession: Boolean,
        navigation: FakeNavigationController = FakeNavigationController(),
    ) = SplashViewModel(
        navigationController = navigation,
        checkSessionUseCase = CheckSessionUseCase(hasActiveSession = { hasSession }),
        language = Spanish,
    )

    @Test
    fun `valid session navigates to main from splash`() = runTest(dispatcher) {
        val navigation = FakeNavigationController()
        val viewModel = vm(hasSession = true, navigation = navigation)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(
            AppRoute.Main,
            (navigation.navigatedActions.single() as NavigationAction.NavigateTo<*>).route
        )
    }

    @Test
    fun `no session exposes Ready without navigating`() = runTest(dispatcher) {
        val navigation = FakeNavigationController()
        val viewModel = vm(hasSession = false, navigation = navigation)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertEquals(SplashUiState.Ready, viewModel.uiState.value)
        assertTrue(navigation.navigatedActions.isEmpty())
    }

    @Test
    fun `continue clicked navigates to login`() = runTest(dispatcher) {
        val navigation = FakeNavigationController()
        val viewModel = vm(hasSession = false, navigation = navigation)
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onAction(SplashUiAction.ContinueClicked)
        advanceUntilIdle()

        assertEquals(
            AppRoute.Login,
            (navigation.navigatedActions.single() as NavigationAction.NavigateTo<*>).route
        )
    }
}
