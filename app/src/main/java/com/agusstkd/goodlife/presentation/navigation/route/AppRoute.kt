package com.agusstkd.goodlife.presentation.navigation.route

import kotlinx.serialization.Serializable

/**
 * Rutas de navegación a nivel de aplicación.
 *
 * Define las pantallas principales fuera del sistema de tabs:
 * Splash → Login/Register → Main (con tabs).
 *
 * Todas las rutas son type-safe gracias a @Serializable.
 */
@Serializable
sealed interface AppRoute {

    /** Pantalla inicial de carga. */
    @Serializable
    data object Splash : AppRoute

    /** Pantalla de inicio de sesión. */
    @Serializable
    data object Login : AppRoute

    /** Pantalla de registro de usuario. */
    @Serializable
    data object Register : AppRoute

    /** Pantalla principal con tabs (Home, Workouts, Meals, Settings). */
    @Serializable
    data object Main : AppRoute

    /** Formulario de creación de tarea (puntual o recurrente). */
    @Serializable
    data object CreateTask : AppRoute

    /** Formulario de creación de hábito recurrente con meta numérica. */
    @Serializable
    data object CreateHabit : AppRoute

    /** Wizard multi-paso para crear una rutina de entrenamiento. */
    @Serializable
    data object CreateRoutine : AppRoute

    /** Wizard multi-paso para planificar comidas con ingredientes y macros. */
    @Serializable
    data object CreateMealPlan : AppRoute
}
