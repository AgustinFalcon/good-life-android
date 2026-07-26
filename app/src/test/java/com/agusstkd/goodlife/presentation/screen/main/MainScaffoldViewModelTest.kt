package com.agusstkd.goodlife.presentation.screen.main

import com.agusstkd.goodlife.core.datetime.FakeDateProvider
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.fake.FakeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.screen.main.model.MainScaffoldUiAction
import com.agusstkd.goodlife.presentation.components.modal.model.QuickActionType
import com.agusstkd.goodlife.presentation.components.bottom.model.BottomMenuOption
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainScaffoldViewModelTest {
    private fun viewModel(nav: FakeNavigationController = FakeNavigationController()) =
        MainScaffoldViewModel(nav, FakeDateProvider(LocalDate(2026, 7, 26)), Spanish)

    @Test fun `initial state builds nav items actions meals and formatted date`() {
        val state = viewModel().uiState.value
        assertEquals(4, state.bottomNavItems.size)
        assertEquals(5, state.quickActions.size)
        assertEquals(7, state.mealOptions.size)
        assertTrue(state.currentDateFormatted.startsWith("Hoy"))
    }

    @Test fun `fab tab and dismiss update modal state`() {
        val vm = viewModel()
        vm.onAction(MainScaffoldUiAction.OnFabClick)
        assertTrue(vm.uiState.value.isModalOpen)
        vm.onAction(MainScaffoldUiAction.OnModalDismiss)
        assertFalse(vm.uiState.value.isModalOpen)
        vm.onAction(MainScaffoldUiAction.OnFabClick)
        vm.onAction(MainScaffoldUiAction.OnTabSelected(BottomMenuOption.FOOD))
        assertEquals(BottomMenuOption.FOOD, vm.uiState.value.selectedTab)
        assertFalse(vm.uiState.value.isModalOpen)
    }

    @Test fun `quick actions close modal and navigate to creation routes`() {
        val nav = FakeNavigationController()
        val vm = viewModel(nav)
        vm.onAction(MainScaffoldUiAction.OnFabClick)
        vm.onAction(MainScaffoldUiAction.OnQuickActionClick(QuickActionType.TASK))
        vm.onAction(MainScaffoldUiAction.OnQuickActionClick(QuickActionType.HABIT))
        vm.onAction(MainScaffoldUiAction.OnQuickActionClick(QuickActionType.WORKOUT))
        vm.onAction(MainScaffoldUiAction.OnQuickActionClick(QuickActionType.MEAL))
        assertEquals(listOf(AppRoute.CreateTask, AppRoute.CreateHabit, AppRoute.CreateRoutine, AppRoute.CreateMealPlan), nav.navigatedActions.map { (it as NavigationAction.NavigateTo<*>).route })
        assertFalse(vm.uiState.value.isModalOpen)
    }
}

