package com.agusstkd.goodlife.data.remote.dto.response.meal

import kotlinx.serialization.Serializable

/**
 * Resumen de un meal plan incluido en el daily log.
 *
 * @param id ID del meal plan
 * @param mealName Nombre de la comida ("Arroz con Pollo")
 * @param mealType Tipo de comida ("BREAKFAST", "LUNCH", "DINNER", "SNACK")
 * @param calories Calorías totales
 * @param protein Proteína en gramos
 */
@Serializable
data class MealSummaryDto(
    val id: Long,
    val mealName: String,
    val mealType: String,
    val calories: Int,
    val protein: Int
)