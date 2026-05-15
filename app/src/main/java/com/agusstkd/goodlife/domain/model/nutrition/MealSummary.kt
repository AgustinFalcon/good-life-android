package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Modelo de dominio resumido de una comida del catálogo de nutrición.
 *
 * Versión simplificada de `Meal` optimizada para listados y selección.
 * No incluye ingredientes detallados ni pasos de preparación para mejor performance.
 * Puede ser global (del catálogo del sistema) o custom del usuario.
 *
 * Los macros están expresados por porción de referencia ([servingSize] [servingUnit]).
 * Para escalar a una cantidad distinta:
 * ```
 * macroEscalado = macroPorPorcion * (cantidadDeseada / servingSize)
 * ```
 *
 * @property id Identificador único asignado por el backend.
 * @property name Nombre de la comida (ej: "Ensalada César").
 * @property description Descripción breve opcional.
 * @property imageUrl URL de la imagen de la comida.
 * @property prepTimeMinutes Tiempo de preparación en minutos.
 * @property servingSize Tamaño de la porción de referencia (ej: 1.0).
 * @property servingUnit Unidad de la porción (ej: "porción", "taza").
 * @property calories Kilocalorías por porción de referencia.
 * @property protein Gramos de proteína por porción de referencia.
 * @property carbs Gramos de carbohidratos por porción de referencia.
 * @property fat Gramos de grasa por porción de referencia.
 * @property isGlobal Si es del catálogo global (true) o custom del usuario (false).
 *
 * @see MealIngredientDraft
 */
data class MealSummary(
    val id: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val prepTimeMinutes: Int?,
    val servingSize: Double,
    val servingUnit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val isGlobal: Boolean,
)