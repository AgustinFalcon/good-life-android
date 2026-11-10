package com.agusstkd.goodlife.presentation.screen.tabs.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.fake.FakeAuthRepository
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsNavigationEffect
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsTabViewModelTest {
    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: FakeAuthRepository

    @Before fun setUp() { Dispatchers.setMain(dispatcher); repo = FakeAuthRepository() }
    @After fun tearDown() { Dispatchers.resetMain() }

    private fun createViewModel(
        navigation: FakeNavigationController = FakeNavigationController(),
    ) = SettingsTabViewModel(
        logoutUseCase = LogoutUseCase(repo),
        getCurrentUserUseCase = GetCurrentUserUseCase(repo),
        navigationController = navigation,
        language = Spanish,
    )

    @Test fun `loads current user and logs out`() = runTest(dispatcher) {
        val navigation = FakeNavigationController()
        val vm = createViewModel(navigation)
        advanceUntilIdle()
        assertEquals("Test User", vm.uiState.value.username)

        vm.onAction(SettingsUiAction.Logout)
        advanceUntilIdle()

        assertEquals(1, repo.logoutCallCount)
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(
            AppRoute.Login,
            (navigation.navigatedActions.single() as NavigationAction.NavigateTo<*>).route
        )
    }

    @Test fun `exposes SettingsTexts from AppLanguage`() = runTest(dispatcher) {
        val vm = createViewModel()
        assertEquals(Spanish.settingsTexts, vm.texts)
        assertEquals("Mi cuenta", vm.texts.accountSection)
        assertEquals("Mi Perfil", vm.texts.profileTitle)
        assertEquals("Ver perfil", vm.texts.profileSubtitleFallback)
        assertEquals("Cerrar sesión", vm.texts.logoutTitle)
        assertEquals("Salir de tu cuenta", vm.texts.logoutSubtitle)
    }

    @Test fun `NavigateToProfile emits navigation effect`() = runTest(dispatcher) {
        val vm = createViewModel()
        val effect = async { vm.navigationEffects.first() }
        runCurrent()

        vm.onAction(SettingsUiAction.NavigateToProfile)

        assertEquals(SettingsNavigationEffect.NavigateToProfile, effect.await())
    }
}
