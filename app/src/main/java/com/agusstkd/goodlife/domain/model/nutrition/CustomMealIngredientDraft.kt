package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Ingrediente incluido en un [CustomMealDraft].
 *
 * Referencia un ingrediente ya persistido por id, con la cantidad y unidad
 * que el usuario eligió para la comida custom.
 *
 * @property ingredientId Identificador del ingrediente en el catálogo.
 * @property quantity Cantidad seleccionada.
 * @property unit Unidad de la cantidad (ej: "g", "ml").
 */
data class CustomMealIngredientDraft(
    val ingredientId: Long,
    val quantity: Double,
    val unit: String,
)
