package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Modelo draft de un ingrediente seleccionado durante la creación de una comida.
 *
 * Representa un [Ingredient] del catálogo con una cantidad personalizada específica.
 * Los macros se calculan automáticamente para la cantidad seleccionada usando las 
 * propiedades `scaled*` que aplican regla de tres desde la porción de referencia.
 *
 * @property ingredient Ingrediente base del catálogo de nutrición.
 * @property quantity Cantidad personalizada seleccionada (ej: 150.0).
 * @property unit Unidad de la cantidad (ej: "g", "ml"). Puede diferir del [Ingredient.servingUnit].
 *
 * @see Ingredient
 */
data class MealIngredientDraft(
    val ingredient: Ingredient,
    val quantity: Double,
    val unit: String,
) {

    /**
     * Kilocalorías calculadas para la [quantity] seleccionada.
     * Aplica regla de tres desde la porción de referencia del ingrediente.
     */
    val scaledCalories: Double get() = ingredient.calories * (quantity / ingredient.servingSize)

    /**
     * Gramos de proteína calculados para la [quantity] seleccionada.
     * Aplica regla de tres desde la porción de referencia del ingrediente.
     */
    val scaledProtein: Double get() = ingredient.protein * (quantity / ingredient.servingSize)

    /**
     * Gramos de carbohidratos calculados para la [quantity] seleccionada.
     * Aplica regla de tres desde la porción de referencia del ingrediente.
     */
    val scaledCarbs: Double get() = ingredient.carbs * (quantity / ingredient.servingSize)

    /**
     * Gramos de grasa calculados para la [quantity] seleccionada.
     * Aplica regla de tres desde la porción de referencia del ingrediente.
     */
    val scaledFat: Double get() = ingredient.fat * (quantity / ingredient.servingSize)

}
