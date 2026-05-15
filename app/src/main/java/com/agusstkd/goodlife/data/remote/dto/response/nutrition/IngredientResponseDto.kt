package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import kotlinx.serialization.Serializable


@Serializable
data class IngredientResponseDto(
    val id: Long? = null,
    val name: String? = null,
    val brand: String? = null,
    val isGlobal: Boolean? = null,
    val servingSize: Double? = null,
    val servingUnit: String? = null,
    val calories: Double? = null,
    val protein: Double? = null,
    val carbs: Double? = null,
    val fat: Double? = null,
    val imageUrl: String? = null,
)

fun IngredientResponseDto.toIngredient() = Ingredient(
    id = id ?: 0L,
    name = name ?: "Sin nombre",
    brand = brand,  // ← Este puede ser null en domain
    isGlobal = isGlobal ?: false,
    servingSize = servingSize ?: 100.0,
    servingUnit = servingUnit ?: "g",
    calories = calories ?: 0.0,
    protein = protein ?: 0.0,
    carbs = carbs ?: 0.0,
    fat = fat ?: 0.0,
    imageUrl = imageUrl,
)
