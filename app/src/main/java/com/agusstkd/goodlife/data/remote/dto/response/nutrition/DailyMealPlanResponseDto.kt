package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import kotlinx.serialization.Serializable

/**
 * DTO de respuesta del plan de comidas para el listado diario.
 *
 * Respuesta del backend para GET /api/v1/meal-plans?date=YYYY-MM-DD.
 * Todos los campos son nullable para evitar crashes por desalineación con el backend.
 */
@Serializable
data class DailyMealPlanResponseDto(
    val id: Long? = null,
    val name: String? = null,
    val date: String? = null,
    val mealType: String? = null,
    val scheduledTime: String? = null,
    val mealName: String? = null,
    val imageUrl: String? = null,
    val totalCalories: Double? = null,
    val totalProtein: Double? = null,
    val totalCarbs: Double? = null,
    val totalFat: Double? = null,
    val isActive: Boolean? = null,
)
