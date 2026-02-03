package com.agusstkd.goodlife.presentation.components.bottom.model

/**
 * Opciones del Bottom Navigation.
 *
 * Define las tabs disponibles en la navegación principal.
 * Cada opción corresponde a un grafo de navegación anidado.
 *
 * ## Mapeo a Grafos:
 * - HOME → TabGraphRoute.HomeGraph
 * - EXERCISES → TabGraphRoute.WorkoutsGraph
 * - FOOD → TabGraphRoute.MealsGraph
 * - SETTINGS → TabGraphRoute.SettingsGraph
 */
enum class BottomMenuOption {
    /**
     * Tab de Diario/Tareas.
     * Pantalla principal con tareas del día.
     */
    HOME,

    /**
     * Tab de Ejercicios/Workouts.
     * Rutinas, ejercicios y estadísticas de entrenamiento.
     */
    EXERCISES,

    /**
     * Tab de Comidas/Alimentación.
     * Registro de comidas, calorías y nutrientes.
     */
    FOOD,

    /**
     * Tab de Configuración/Más.
     * Perfil, ajustes, y opciones adicionales.
     */
    SETTINGS
}
