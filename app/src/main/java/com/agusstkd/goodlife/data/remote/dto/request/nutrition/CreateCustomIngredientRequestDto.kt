package com.agusstkd.goodlife.data.remote.dto.request.nutrition

import kotlinx.serialization.Serializable

/**
 * DTO de request para crear ingrediente custom.
 *
 * Contrato backend [CreateFoodRequest]: `baseQuantity` / `baseUnit`.
 */
@Serializable
data class CreateCustomIngredientRequestDto(
    val name: String,
    val brand: String?,
    val baseQuantity: Double,
    val baseUnit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
)
