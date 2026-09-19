package com.agusstkd.goodlife.fake

import androidx.navigation.NavOptions
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeNavigationController : ComposeNavigationController {

    val navigatedActions = mutableListOf<NavigationAction>()

    private val navigationActions = Channel<NavigationAction>(Channel.BUFFERED)
    override val navigationAction: Flow<NavigationAction> = navigationActions.receiveAsFlow()

    override fun <T : Any> navigateTo(route: T, navOptions: NavOptions) {
        val action = NavigationAction.NavigateTo(route, navOptions)
        navigatedActions.add(action)
        check(navigationActions.trySend(action).isSuccess)
    }

    override fun navigateUp() {
        val action = NavigationAction.NavigateUp
        navigatedActions.add(action)
        check(navigationActions.trySend(action).isSuccess)
    }

    override fun <T : Any> popBackTo(route: T, inclusive: Boolean) {
        val action = NavigationAction.PopBackTo(route, inclusive)
        navigatedActions.add(action)
        check(navigationActions.trySend(action).isSuccess)
    }
}
