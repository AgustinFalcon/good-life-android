package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Modelo de dominio de un ingrediente del catálogo de nutrición.
 *
 * Representa un ingrediente disponible para armar [MealIngredientDraft]s.
 * Puede ser global (del catálogo del sistema) o custom del usuario.
 *
 * Todos los macros están expresados por porción de referencia ([servingSize] [servingUnit]).
 * Para escalar a una cantidad distinta:
 * ```
 * macroEscalado = macroPorPorcion * (cantidadDeseada / servingSize)
 * ```
 *
 * @property id Identificador único asignado por el backend.
 * @property name Nombre del ingrediente (ej: "Pechuga de pollo").
 * @property brand Marca comercial opcional (ej: "Acme Foods").
 * @property servingSize Tamaño de la porción de referencia (ej: 100.0).
 * @property servingUnit Unidad de la porción (ej: "g", "ml", "unidad").
 * @property calories Kilocalorías por porción de referencia.
 * @property protein Gramos de proteína por porción de referencia.
 * @property carbs Gramos de carbohidratos por porción de referencia.
 * @property fat Gramos de grasa por porción de referencia.
 * @property isGlobal Si es del catálogo global (true) o custom del usuario (false).
 * @property imageUrl URL de imagen opcional del ingrediente.
 *
 * @see MealIngredientDraft
 */
data class Ingredient(
    val id: Long,
    val name: String,
    val brand: String?,
    val servingSize: Double,
    val servingUnit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val isGlobal: Boolean,
    val imageUrl: String?,
)
