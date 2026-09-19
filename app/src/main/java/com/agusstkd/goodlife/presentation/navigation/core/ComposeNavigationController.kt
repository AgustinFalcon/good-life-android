package com.agusstkd.goodlife.presentation.navigation.core

import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.Flow

/**
 * Abstracción del sistema de navegación de Compose.
 *
 * Desacopla los ViewModels del NavController, permitiendo:
 * - Testing sin dependencias de Android.
 * - Navegación reactiva mediante un stream one-shot.
 * - Preparación para Kotlin Multiplatform.
 *
 * Los ViewModels inyectan esta interface y llaman sus métodos.
 * [GoodLifeNavHost] observa [navigationAction] y ejecuta la navegación real.
 *
 * @see ComposeNavigationControllerImpl
 * @see NavigationAction
 */
interface ComposeNavigationController {

    /**
     * Stream de acciones de navegación consumibles una sola vez.
     *
     * GoodLifeNavHost observa este stream y ejecuta las navegaciones. No es
     * StateFlow ni replayed SharedFlow: una acción consumida no se repite ante
     * una recreación del host.
     */
    val navigationAction: Flow<NavigationAction>

    /** Navigates to a type-safe route. */
    fun <T : Any> navigateTo(
        route: T,
        navOptions: NavOptions = NavOptions.Builder().build()
    )

    /** Navigates up in the back stack. */
    fun navigateUp()

    /** Pops the stack back to a typed route. */
    fun <T : Any> popBackTo(route: T, inclusive: Boolean = false)
}
