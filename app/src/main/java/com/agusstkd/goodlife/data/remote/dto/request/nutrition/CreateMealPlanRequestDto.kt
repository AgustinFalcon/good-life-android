package com.agusstkd.goodlife.data.remote.dto.request.nutrition

import kotlinx.serialization.Serializable

/**
 * Body de `POST /api/v1/meal-plans`.
 *
 * El backend espera un plan por comida (mealId + mealType + días), no un
 * agregado de `scheduledMeals`. Las fechas van en ISO-8601.
 */
@Serializable
data class CreateMealPlanRequestDto(
    val mealId: Long,
    val mealType: String,
    val scheduledTime: String? = null,
    val daysOfWeek: List<Int>,
    val startDate: String? = null,
    val endDate: String? = null,
)

/**
 * Ingrediente dentro de una meal custom (POST /meals).
 */
@Serializable
data class MealIngredientRequestDto(
    val ingredientId: Long,
    val quantity: Double,
    val unit: String,
)
