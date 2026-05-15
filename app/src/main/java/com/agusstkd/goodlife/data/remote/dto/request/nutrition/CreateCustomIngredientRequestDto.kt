package com.agusstkd.goodlife.data.remote.dto.request.nutrition

import kotlinx.serialization.Serializable

/**
 * DTO de request para crear ingrediente custom.
 * 
 * Basado en POST /api/v1/ingredients del backend spec.
 */
@Serializable
data class CreateCustomIngredientRequestDto(
    val name: String,
    val brand: String?,
    val servingSize: Double,
    val servingUnit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
)