package com.agusstkd.goodlife.presentation.navigation.route

import androidx.navigation.navOptions
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController

/**
 * Extension functions para navegación type-safe.
 *
 * Proporcionan una API limpia para los ViewModels,
 * encapsulando las NavOptions comunes.
 */

// ===== NAVEGACIÓN NIVEL APP =====

/**
 * Navega a Login limpiando Splash del stack.
 */
fun ComposeNavigationController.navigateToLogin() {
    navigateTo(
        route = AppRoute.Login,
        navOptions = navOptions {
            popUpTo<AppRoute.Splash> { inclusive = true }
            launchSingleTop = true
        }
    )
}

/**
 * Navega a Register desde Login.
 */
fun ComposeNavigationController.navigateToRegister() {
    navigateTo(AppRoute.Register)
}


/**
 * Navega a Login desde Register.
 */
fun ComposeNavigationController.navigateToLoginFromRegister() {
    navigateTo(
        route = AppRoute.Login,
        navOptions = navOptions {
            popUpTo<AppRoute.Register> { inclusive = true }
            launchSingleTop = true
        }
    )
}

/**
 * Navega a Main limpiando Login del stack.
 */
fun ComposeNavigationController.navigateToMain() {
    navigateTo(
        route = AppRoute.Main,
        navOptions = navOptions {
            popUpTo<AppRoute.Login> { inclusive = true }
            launchSingleTop = true
        }
    )
}

/**
 * Navega a Login desde Main (logout), limpiando todo el stack.
 */
fun ComposeNavigationController.navigateToLoginFromMain() {
    navigateTo(
        route = AppRoute.Login,
        navOptions = navOptions {
            popUpTo<AppRoute.Main> { inclusive = true }
            launchSingleTop = true
        }
    )
}

// ===== NAVEGACIÓN NIVEL TABS =====

/**
 * Navega al detalle de un workout.
 *
 * @param workoutId Identificador del workout.
 */
fun ComposeNavigationController.navigateToWorkoutDetail(workoutId: Long) {
    navigateTo(TabRoute.WorkoutDetail(workoutId = workoutId))
}

/**
 * Navega al detalle de una tarea.
 *
 * @param taskId Identificador de la tarea.
 */
fun ComposeNavigationController.navigateToTaskDetail(taskId: Long) {
    navigateTo(TabRoute.TaskDetail(taskId = taskId))
}

/**
 * Navega al detalle de una comida.
 *
 * @param mealId Identificador de la comida.
 */
fun ComposeNavigationController.navigateToMealDetail(mealId: Long) {
    navigateTo(TabRoute.MealDetail(mealId = mealId))
}

/**
 * Navega al perfil de usuario.
 */
fun ComposeNavigationController.navigateToProfile() {
    navigateTo(TabRoute.Profile)
}
