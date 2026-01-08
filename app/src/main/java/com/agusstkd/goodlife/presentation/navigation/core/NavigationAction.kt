package com.agusstkd.goodlife.presentation.navigation.core

import androidx.navigation.NavOptions

/**
 * Representa las acciones de navegación posibles en la aplicación.
 *
 * El ViewModel emite estas acciones a través de [ComposeNavigationController],
 * y [SmartNavHost] las procesa para ejecutar la navegación real.
 *
 * @see ComposeNavigationController
 */
sealed class NavigationAction {

    /**
     * Navega a una nueva pantalla.
     *
     * @param T Tipo de la ruta (debe ser @Serializable).
     * @param route Ruta destino type-safe.
     * @param navOptions Opciones de navegación (popUpTo, launchSingleTop, etc.).
     */
    data class NavigateTo<T : Any>(
        val route: T,
        val navOptions: NavOptions = NavOptions.Builder().build()
    ) : NavigationAction()

    /**
     * Navega hacia atrás en el stack (equivalente al botón back).
     */
    data object NavigateUp : NavigationAction()

    /**
     * Hace pop del back stack hasta una ruta específica.
     *
     * @param T Tipo de la ruta (debe ser @Serializable).
     * @param route Ruta hasta donde hacer pop.
     * @param inclusive Si es true, también remueve la ruta destino del stack.
     */
    data class PopBackTo<T : Any>(
        val route: T,
        val inclusive: Boolean = false
    ) : NavigationAction()
}

