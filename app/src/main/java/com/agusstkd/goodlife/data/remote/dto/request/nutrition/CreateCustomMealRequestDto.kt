package com.agusstkd.goodlife.data.remote.dto.request.nutrition

import kotlinx.serialization.Serializable

/**
 * DTO de request para crear una comida custom desde el wizard de meal plan.
 *
 * Mapea directamente al backend: POST /api/v1/meals
 * El backend calcula los macros totales a partir de los ingredientes.
 */
@Serializable
data class CreateCustomMealRequestDto(
    val name: String,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val ingredients: List<MealIngredientRequestDto>,
)