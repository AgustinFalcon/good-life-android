package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import kotlinx.serialization.Serializable

/**
 * DTO de respuesta al crear un plan de comidas.
 * 
 * Respuesta del backend después de POST /meal-plans.
 */
@Serializable
data class MealPlanResponseDto(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val totalCalories: Double? = null,
    val totalProtein: Double? = null,
    val totalCarbs: Double? = null,
    val totalFat: Double? = null,
    val isActive: Boolean? = null,
)