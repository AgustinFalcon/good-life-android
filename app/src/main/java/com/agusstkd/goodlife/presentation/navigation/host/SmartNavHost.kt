package com.agusstkd.goodlife.presentation.navigation.host

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.core.NavigationAction

private const val ANIMATION_DURATION = 300

/**
 * NavHost inteligente que observa eventos de navegación reactivos.
 *
 * Escucha el [ComposeNavigationController.navigationAction] SharedFlow
 * y ejecuta las navegaciones cuando la Activity está en estado STARTED.
 *
 * Incluye animaciones de transición preconfiguradas (slide + fade).
 *
 * @param navController Controlador de navegación de Compose.
 * @param navigationController Controlador reactivo que emite eventos.
 * @param startDestination Ruta inicial de la aplicación.
 * @param graphBuilder Constructor del grafo de navegación.
 */
@Composable
fun SmartNavHost(
    navController: NavHostController,
    navigationController: ComposeNavigationController,
    startDestination: Any,
    graphBuilder: NavGraphBuilder.() -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observa eventos de navegación solo cuando Activity está visible
    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            navigationController.navigationAction.collect { action ->
                handleNavigationAction(navController, action)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        // Pantalla que entra al navegar hacia adelante (ej: A -> B, entra B desde la derecha)
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
        },
        // Pantalla que sale al navegar hacia adelante (ej: A -> B, sale A hacia la izquierda)
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
        },
        // Pantalla que reaparece al volver atrás (ej: B -> A, reaparece A desde la izquierda)
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
        },
        // Pantalla que desaparece al volver atrás (ej: B -> A, desaparece B hacia la derecha)
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(ANIMATION_DURATION)
            ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
        },
        builder = graphBuilder
    )
}

/**
 * Procesa una acción de navegación y la ejecuta en el NavController.
 *
 * @param navController Controlador donde ejecutar la navegación.
 * @param action Acción a procesar.
 */
private fun handleNavigationAction(
    navController: NavHostController,
    action: NavigationAction
) {
    try {
        when (action) {
            is NavigationAction.NavigateTo<*> -> {
                navController.navigate(action.route, action.navOptions)
            }
            is NavigationAction.NavigateUp -> {
                navController.navigateUp()
            }
            is NavigationAction.PopBackTo<*> -> {
                navController.popBackStack(action.route, action.inclusive)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
