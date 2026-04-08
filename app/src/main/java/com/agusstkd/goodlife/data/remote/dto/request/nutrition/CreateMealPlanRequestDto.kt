package com.agusstkd.goodlife.data.remote.dto.request.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.MealType
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

/**
 * DTO de request para crear plan de comidas completo.
 * 
 * Representa el plan completo del día con múltiples comidas programadas.
 */
@Serializable
data class CreateMealPlanRequestDto(
    val name: String,
    val description: String?,
    val scheduledMeals: List<ScheduledMealRequestDto>,
)

/**
 * DTO de request para una comida programada dentro del plan.
 */
@Serializable 
data class ScheduledMealRequestDto(
    val mealType: MealType,
    val scheduledTime: LocalTime?,
    val mealId: Long?,  // ID de comida del catálogo (opcional)
    val customIngredients: List<MealIngredientRequestDto>,  // Ingredientes custom adicionales
)

/**
 * DTO de request para un ingrediente dentro de una comida programada.
 */
@Serializable
data class MealIngredientRequestDto(
    val ingredientId: Long,
    val quantity: Double,
    val unit: String,
)