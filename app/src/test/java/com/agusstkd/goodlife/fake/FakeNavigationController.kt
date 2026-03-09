package com.agusstkd.goodlife.fake

import androidx.navigation.NavOptions
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class FakeNavigationController : ComposeNavigationController {

    val navigatedActions = mutableListOf<NavigationAction>()

    private val _navigationAction = MutableSharedFlow<NavigationAction>(replay = 1)
    override val navigationAction: SharedFlow<NavigationAction> = _navigationAction

    override fun <T : Any> navigateTo(route: T, navOptions: NavOptions) {
        val action = NavigationAction.NavigateTo(route, navOptions)
        navigatedActions.add(action)
        _navigationAction.tryEmit(action)
    }

    override fun navigateUp() {
        val action = NavigationAction.NavigateUp
        navigatedActions.add(action)
        _navigationAction.tryEmit(action)
    }

    override fun <T : Any> popBackTo(route: T, inclusive: Boolean) {
        val action = NavigationAction.PopBackTo(route, inclusive)
        navigatedActions.add(action)
        _navigationAction.tryEmit(action)
    }
}
