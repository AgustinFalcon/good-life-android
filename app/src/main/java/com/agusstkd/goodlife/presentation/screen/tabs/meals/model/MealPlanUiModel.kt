package com.agusstkd.goodlife.presentation.screen.tabs.meals.model

import androidx.compose.runtime.Immutable
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary

/**
 * Modelo UI de un plan de comidas para el tab Meals.
 *
 * Contiene los datos ya formateados para renderizar directamente.
 * El mapping Domain → UiModel se hace en el ViewModel.
 *
 * @property id ID del plan de comida.
 * @property name Nombre del plan (ej: "Plan Volumen Marzo").
 * @property mealTypeLabel Label localizado del tipo de comida (ej: "Desayuno").
 * @property scheduledTimeLabel Hora formateada (ej: "08:30") o null.
 * @property mealName Nombre de la comida principal, si existe.
 * @property imageUrl URL de la imagen de la comida, si existe.
 * @property totalCalories Calorías totales en kcal.
 * @property totalProtein Proteínas totales en gramos.
 * @property totalCarbs Carbohidratos totales en gramos.
 * @property totalFat Grasas totales en gramos.
 */
@Immutable
data class MealPlanUiModel(
    val id: Long,
    val name: String,
    val mealTypeLabel: String,
    val scheduledTimeLabel: String?,
    val mealName: String?,
    val imageUrl: String?,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
)

/**
 * Mapea [DailyMealPlanSummary] (Domain) → [MealPlanUiModel] (UI).
 *
 * @param mealTypeLabel Label localizado del tipo de comida.
 */
fun DailyMealPlanSummary.toUiModel(mealTypeLabel: String): MealPlanUiModel {
    return MealPlanUiModel(
        id = id,
        name = name,
        mealTypeLabel = mealTypeLabel,
        scheduledTimeLabel = scheduledTime?.let { "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}" },
        mealName = mealName,
        imageUrl = imageUrl,
        totalCalories = totalCalories,
        totalProtein = totalProtein,
        totalCarbs = totalCarbs,
        totalFat = totalFat,
    )
}
