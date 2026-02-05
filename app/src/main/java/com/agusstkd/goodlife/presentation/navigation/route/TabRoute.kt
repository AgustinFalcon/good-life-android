package com.agusstkd.goodlife.presentation.navigation.route

import kotlinx.serialization.Serializable

/**
 * Rutas de navegación dentro del sistema de tabs.
 *
 * Define las pantallas accesibles desde el Bottom Navigation
 * y sus sub-navegaciones (detalles, formularios, etc.).
 */
@Serializable
sealed interface TabRoute {

    // ===== HOME TAB =====

    /** Pantalla principal del tab Home. */
    @Serializable
    data object Daily : TabRoute

    /**
     * Detalle de una tarea.
     *
     * @param taskId Identificador de la tarea.
     */
    @Serializable
    data class DailyDetail(val taskId: Long) : TabRoute

    // ===== WORKOUTS TAB =====

    /** Lista de entrenamientos. */
    @Serializable
    data object Workouts : TabRoute

    /**
     * Detalle de un entrenamiento.
     *
     * @param workoutId Identificador del entrenamiento.
     */
    @Serializable
    data class WorkoutDetail(val workoutId: Long) : TabRoute

    // ===== MEALS TAB =====

    /** Lista de comidas. */
    @Serializable
    data object Meals : TabRoute

    /**
     * Detalle de una comida.
     *
     * @param mealId Identificador de la comida.
     */
    @Serializable
    data class MealDetail(val mealId: Long) : TabRoute

    // ===== SETTINGS TAB =====

    /** Pantalla de configuración. */
    @Serializable
    data object Settings : TabRoute

    /** Pantalla de perfil de usuario. */
    @Serializable
    data object Profile : TabRoute
}

/**
 * Rutas de los grafos de navegación de cada tab.
 *
 * Se usan como startDestination de cada navigation() anidado.
 */
@Serializable
sealed interface TabGraphRoute {

    @Serializable
    data object DailyGraph : TabGraphRoute

    @Serializable
    data object WorkoutsGraph : TabGraphRoute

    @Serializable
    data object MealsGraph : TabGraphRoute

    @Serializable
    data object SettingsGraph : TabGraphRoute
}
