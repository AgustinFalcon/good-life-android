package com.agusstkd.goodlife.domain.model.nutrition

import kotlinx.datetime.LocalTime

/**
 * Modelo draft de un plan de comidas en creación.
 *
 * Representa el plan completo que está armando el usuario antes de persistirlo.
 * Contiene múltiples [ScheduledMealDraft] distribuidas a lo largo del día.
 * Los macros totales se calculan automáticamente sumando todas las comidas programadas.
 *
 * @property name Nombre del plan de comidas (ej: "Plan Volumen Marzo").
 * @property description Descripción opcional del plan.
 * @property scheduledMeals Lista de comidas programadas para el día.
 */
data class MealPlanDraft(
    val name: String,
    val description: String?,
    val scheduledMeals: List<ScheduledMealDraft>,
) {
    /**
     * Total de kilocalorías del plan completo.
     * Suma las calorías de todas las [scheduledMeals].
     */
    val totalCalories: Double get() = scheduledMeals.sumOf { it.totalCalories }

    /**
     * Total de gramos de proteína del plan completo.
     * Suma la proteína de todas las [scheduledMeals].
     */
    val totalProtein: Double get() = scheduledMeals.sumOf { it.totalProtein }

    /**
     * Total de gramos de carbohidratos del plan completo.
     * Suma los carbohidratos de todas las [scheduledMeals].
     */
    val totalCarbs: Double get() = scheduledMeals.sumOf { it.totalCarbs }

    /**
     * Total de gramos de grasa del plan completo.
     * Suma las grasas de todas las [scheduledMeals].
     */
    val totalFat: Double get() = scheduledMeals.sumOf { it.totalFat }
}

/**
 * Modelo draft de una comida programada dentro de un plan de comidas.
 *
 * Puede contener una comida del catálogo ([selectedMeal]) y/o ingredientes custom
 * ([customIngredients]). Los macros totales se calculan automáticamente sumando ambas fuentes.
 * Esto permite máxima flexibilidad: usar comidas predefinidas, crear custom, o combinar ambas.
 *
 * @property mealType Tipo de comida según momento del día.
 * @property scheduledTime Hora programada opcional para la comida.
 * @property selectedMeal Comida del catálogo seleccionada (opcional).
 * @property customIngredients Lista de ingredientes custom agregados manualmente.
 */
data class ScheduledMealDraft(
    val mealType: MealType,
    val scheduledTime: LocalTime?,
    val selectedMeal: MealSummary?,
    val customIngredients: List<MealIngredientDraft>,
) {
    /**
     * Total de kilocalorías de esta comida programada.
     * Suma calorías de [selectedMeal] (si existe) + [customIngredients].
     */
    val totalCalories: Double get() = (selectedMeal?.calories ?: 0.0) + customIngredients.sumOf { it.scaledCalories }

    /**
     * Total de gramos de proteína de esta comida programada.
     * Suma proteína de [selectedMeal] (si existe) + [customIngredients].
     */
    val totalProtein: Double get() = (selectedMeal?.protein ?: 0.0) + customIngredients.sumOf { it.scaledProtein }

    /**
     * Total de gramos de carbohidratos de esta comida programada.
     * Suma carbohidratos de [selectedMeal] (si existe) + [customIngredients].
     */
    val totalCarbs: Double get() = (selectedMeal?.carbs ?: 0.0) + customIngredients.sumOf { it.scaledCarbs }

    /**
     * Total de gramos de grasa de esta comida programada.
     * Suma grasas de [selectedMeal] (si existe) + [customIngredients].
     */
    val totalFat: Double get() = (selectedMeal?.fat ?: 0.0) + customIngredients.sumOf { it.scaledFat }
}