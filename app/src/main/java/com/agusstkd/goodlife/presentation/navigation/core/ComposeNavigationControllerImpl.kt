package com.agusstkd.goodlife.presentation.navigation.core

import androidx.navigation.NavOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * One-shot [ComposeNavigationController] backed by a bounded channel.
 *
 * The buffer preserves the Splash handoff when it happens before the root host
 * starts collecting. Once delivered, an action is consumed and cannot replay to
 * a recreated host. Dispatch is synchronous and has no unmanaged coroutine
 * lifetime.
 */
class ComposeNavigationControllerImpl : ComposeNavigationController {

    private val navigationActions = Channel<NavigationAction>(Channel.BUFFERED)

    override val navigationAction: Flow<NavigationAction> = navigationActions.receiveAsFlow()

    override fun <T : Any> navigateTo(route: T, navOptions: NavOptions) {
        dispatch(NavigationAction.NavigateTo(route, navOptions))
    }

    override fun navigateUp() {
        dispatch(NavigationAction.NavigateUp)
    }

    override fun <T : Any> popBackTo(route: T, inclusive: Boolean) {
        dispatch(NavigationAction.PopBackTo(route, inclusive))
    }

    private fun dispatch(action: NavigationAction) {
        check(navigationActions.trySend(action).isSuccess) {
            "Navigation action could not be dispatched."
        }
    }
}
