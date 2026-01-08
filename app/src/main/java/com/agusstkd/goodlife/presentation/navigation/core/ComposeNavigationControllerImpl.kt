package com.agusstkd.goodlife.presentation.navigation.core

import androidx.navigation.NavOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Implementación de [ComposeNavigationController] basada en SharedFlow.
 *
 * Emite eventos de navegación que son observados por [SmartNavHost].
 * Es thread-safe y puede ser llamada desde cualquier contexto.
 *
 * Características:
 * - **Thread-safe**: Usa coroutines para emisión asíncrona.
 * - **NonCancellable**: Las acciones de navegación siempre se completan.
 * - **Sin replay**: No reemite eventos al rotar (evita navegación duplicada).
 *
 * @param coroutineScope Scope para emitir eventos. Inyectable para testing.
 */
class ComposeNavigationControllerImpl(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + NonCancellable)
) : ComposeNavigationController {

    private val _navigationAction = MutableSharedFlow<NavigationAction>()

    override val navigationAction: SharedFlow<NavigationAction>
        get() = _navigationAction.asSharedFlow()

    override fun <T : Any> navigateTo(route: T, navOptions: NavOptions) {
        coroutineScope.launch {
            _navigationAction.emit(NavigationAction.NavigateTo(route, navOptions))
        }
    }

    override fun navigateUp() {
        coroutineScope.launch {
            _navigationAction.emit(NavigationAction.NavigateUp)
        }
    }

    override fun <T : Any> popBackTo(route: T, inclusive: Boolean) {
        coroutineScope.launch {
            _navigationAction.emit(NavigationAction.PopBackTo(route, inclusive))
        }
    }
}
