package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import kotlinx.serialization.Serializable

@Serializable
data class IngredientResponseDto(
    val id: Long? = null,
    val name: String? = null,
    val brand: String? = null,
    val isGlobal: Boolean? = null,
    val isCustom: Boolean? = null,
    val baseQuantity: Double? = null,
    val servingSize: Double? = null,
    val baseUnit: String? = null,
    val servingUnit: String? = null,
    val calories: Double? = null,
    val protein: Double? = null,
    val carbs: Double? = null,
    val fat: Double? = null,
    val imageUrl: String? = null,
    val availableUnits: List<String>? = null,
)

fun IngredientResponseDto.toIngredient(): Ingredient {
    val unit = PortionUnit.fromWire(baseUnit ?: servingUnit)
    return Ingredient(
        id = id ?: 0L,
        name = name.orEmpty(),
        brand = brand,
        isGlobal = isGlobal ?: isCustom?.not() ?: false,
        servingSize = baseQuantity ?: servingSize ?: 0.0,
        servingUnit = unit,
        calories = calories ?: 0.0,
        protein = protein ?: 0.0,
        carbs = carbs ?: 0.0,
        fat = fat ?: 0.0,
        imageUrl = imageUrl,
        availableUnits = availableUnits
            ?.map(PortionUnit::fromWire)
            ?.filter { it != PortionUnit.UNKNOWN }
            ?.ifEmpty { PortionUnit.compatibleWith(unit) }
            ?: PortionUnit.compatibleWith(unit),
    )
}
