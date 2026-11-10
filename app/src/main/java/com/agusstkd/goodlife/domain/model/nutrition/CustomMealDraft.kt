package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Borrador de una comida custom antes de persistirla.
 *
 * Lo arma la presentación a partir del wizard; el repositorio de catálogo
 * lo traduce al DTO de red. El backend calcula los macros a partir de
 * [ingredients].
 *
 * @property name Nombre de la comida (requerido).
 * @property description Descripción opcional.
 * @property tags Etiquetas asociadas (ej: tipo de comida).
 * @property ingredients Ingredientes con cantidad y unidad.
 */
data class CustomMealDraft(
    val name: String,
    val description: String?,
    val tags: List<String>,
    val ingredients: List<CustomMealIngredientDraft>,
)
