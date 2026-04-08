package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import kotlinx.serialization.Serializable

/**
 * Resumen de un meal plan incluido en el daily log.
 *
 * NOTA: Estructura provisional — todos los campos son nullable con default
 * para evitar crashes por desalineación con el backend.
 * Alinear con la respuesta real cuando se implemente el módulo Meals.
 */
@Serializable
data class MealSummaryDto(
    val id: Long? = null,
    val mealName: String? = null,
    val mealType: String? = null,
    val calories: Int? = null,
    val protein: Int? = null,
)
