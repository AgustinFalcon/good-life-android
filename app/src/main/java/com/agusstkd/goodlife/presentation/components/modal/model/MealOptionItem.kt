package com.agusstkd.goodlife.presentation.components.modal.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.agusstkd.goodlife.domain.model.nutrition.MealType

/**
 * Modelo para una opción de comida en el modal.
 *
 * @property mealType Tipo de comida del dominio (null para "Resumen diario")
 * @property label Texto visible de la opción
 * @property icon Icono de la opción
 */
data class MealOptionItem(
    val mealType: MealType?,
    val label: String,
    val icon: ImageVector
)
