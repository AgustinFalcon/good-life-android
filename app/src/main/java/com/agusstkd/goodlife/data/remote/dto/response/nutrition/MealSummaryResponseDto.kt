package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import kotlinx.serialization.Serializable

/**
 * DTO de respuesta para comidas del catálogo (versión summary).
 * 
 * Basado en el endpoint GET /api/v1/meals del backend.
 * Todos los campos son defensivos (nullable) para evitar crashes.
 */
@Serializable
data class MealSummaryResponseDto(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val prepTimeMinutes: Int? = null,
    val servingSize: Double? = null,
    val servingUnit: String? = null,
    val isGlobal: Boolean? = null,
    val totalCalories: Double? = null,
    val totalProtein: Double? = null,
    val totalCarbs: Double? = null,
    val totalFat: Double? = null,
)

/**
 * Mapea MealSummaryResponseDto defensivo a MealSummary de dominio limpio.
 */
fun MealSummaryResponseDto.toMealSummary() = MealSummary(
    id = id ?: 0L,
    name = name ?: "Sin nombre",
    description = description,
    imageUrl = imageUrl,
    prepTimeMinutes = prepTimeMinutes,
    servingSize = servingSize ?: 1.0,
    servingUnit = servingUnit ?: "porción",
    calories = totalCalories ?: 0.0,
    protein = totalProtein ?: 0.0,
    carbs = totalCarbs ?: 0.0,
    fat = totalFat ?: 0.0,
    isGlobal = isGlobal ?: false,
)