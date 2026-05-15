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
 * Emite eventos de navegación que son observados por [GoodLifeNavHost].
 * Es thread-safe y puede ser llamada desde cualquier contexto.
 *
 * ## replay = 1
 * Necesario para evitar la race condition inicial en el Splash:
 * el SplashViewModel emite la navegación antes de que el GoodLifeNavHost
 * empiece a colectar. Con replay=1, el último evento queda guardado y
 * se entrega en cuanto el NavHost se suscribe.
 *
 * Seguro porque el [LaunchedEffect] del NavHost usa el navigationController
 * (singleton) como key y nunca se reinicia, por lo que el replay no puede
 * causar navegaciones duplicadas.
 */
class ComposeNavigationControllerImpl(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + NonCancellable)
) : ComposeNavigationController {

    private val _navigationAction = MutableSharedFlow<NavigationAction>(replay = 1)

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
