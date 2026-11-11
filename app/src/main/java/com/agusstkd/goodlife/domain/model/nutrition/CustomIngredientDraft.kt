package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Borrador de un ingrediente custom antes de persistirlo.
 *
 * Lo arma la presentación a partir del formulario; el repositorio de catálogo
 * lo traduce al DTO de red. El dominio no conoce el contrato HTTP.
 *
 * @property name Nombre del ingrediente (requerido).
 * @property brand Marca comercial opcional.
 * @property servingSize Tamaño de la porción de referencia.
 * @property servingUnit Unidad de la porción; se serializa como `baseUnit` en el borde.
 * @property calories Kilocalorías por porción de referencia.
 * @property protein Gramos de proteína por porción de referencia.
 * @property carbs Gramos de carbohidratos por porción de referencia.
 * @property fat Gramos de grasa por porción de referencia.
 */
data class CustomIngredientDraft(
    val name: String,
    val brand: String?,
    val servingSize: Double,
    val servingUnit: PortionUnit,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
)
