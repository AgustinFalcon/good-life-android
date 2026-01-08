package com.agusstkd.goodlife.presentation.navigation.core

import androidx.navigation.NavOptions
import kotlinx.coroutines.flow.SharedFlow

/**
 * Abstracción del sistema de navegación de Compose.
 *
 * Desacopla los ViewModels del NavController, permitiendo:
 * - Testing sin dependencias de Android
 * - Navegación reactiva mediante SharedFlow
 * - Preparación para Kotlin Multiplatform
 *
 * Los ViewModels inyectan esta interface y llaman sus métodos.
 * [SmartNavHost] observa [navigationAction] y ejecuta la navegación real.
 *
 * @see ComposeNavigationControllerImpl
 * @see NavigationAction
 */
interface ComposeNavigationController {

    /**
     * Flow de acciones de navegación.
     *
     * SmartNavHost observa este flow y ejecuta las navegaciones.
     * Es SharedFlow (no StateFlow) para evitar reemitir eventos al rotar.
     */
    val navigationAction: SharedFlow<NavigationAction>

    /**
     * Navega a una ruta type-safe.
     *
     * @param T Tipo de la ruta (@Serializable).
     * @param route Ruta destino.
     * @param navOptions Opciones de navegación.
     */
    fun <T : Any> navigateTo(
        route: T,
        navOptions: NavOptions = NavOptions.Builder().build()
    )

    /**
     * Navega hacia atrás en el stack.
     */
    fun navigateUp()

    /**
     * Hace pop del stack hasta una ruta específica.
     *
     * @param T Tipo de la ruta (@Serializable).
     * @param route Ruta hasta donde hacer pop.
     * @param inclusive Si es true, también remueve la ruta destino.
     */
    fun <T : Any> popBackTo(route: T, inclusive: Boolean = false)
}
