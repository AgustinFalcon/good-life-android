package com.agusstkd.goodlife.data.remote.dto.request.nutrition

import kotlinx.serialization.Serializable

/**
 * DTO de request para crear comida custom básica.
 * 
 * Versión simplificada para el wizard de meal plan.
 * Para creación completa de meals con ingredientes e instrucciones,
 * se usaría un DTO más complejo en un flujo dedicado.
 */
@Serializable
data class CreateCustomMealRequestDto(
    val name: String,
    val description: String?,
    val servingSize: Double,
    val servingUnit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
)