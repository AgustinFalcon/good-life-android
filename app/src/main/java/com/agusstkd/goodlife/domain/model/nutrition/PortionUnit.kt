package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Unidades de medida disponibles para los ingredientes.
 *
 * Espeja el enum `PortionUnit` del backend. El JSON entra solo por [fromWire].
 * Un valor desconocido es [UNKNOWN]; la UI no lo trata como [GRAM].
 *
 * @property label Abreviación legible para mostrar en la UI (ej. "g", "ml", "ud").
 */
enum class PortionUnit(val label: String) {
    GRAM("g"),
    MILLILITER("ml"),
    PIECE("ud"),
    UNKNOWN("?");

    companion object {
        val selectable: List<PortionUnit> = entries.filter { it != UNKNOWN }

        /**
         * Traduce el valor de wire a un caso del tipo cerrado.
         *
         * Acepta el nombre del enum y alias conocidos (`g`, `ml`, `ud` y variantes).
         * Cualquier otro valor, nulo o en blanco es [UNKNOWN].
         *
         * @param raw Unidad enviada por backend o ingresada por el usuario.
         * @return El caso conocido o [UNKNOWN].
         */
        fun fromWire(raw: String?): PortionUnit {
            if (raw.isNullOrBlank()) return UNKNOWN
            selectable.firstOrNull { it.name == raw }?.let { return it }
            return when (raw.lowercase()) {
                "g", "gram", "gramo", "gramos" -> GRAM
                "ml", "milliliter", "mililitro" -> MILLILITER
                "piece", "ud", "unidad", "unit" -> PIECE
                else -> UNKNOWN
            }
        }

        /**
         * Convierte el String que devuelve el backend/Ingredient al enum.
         *
         * @param value Unidad en texto.
         * @return El caso conocido o [UNKNOWN].
         */
        @Deprecated(
            message = "Use fromWire so unknown values map to UNKNOWN",
            replaceWith = ReplaceWith("fromWire(value)"),
        )
        fun fromString(value: String): PortionUnit = fromWire(value)

        /**
         * Devuelve las unidades compatibles con una unidad base dada.
         * [UNKNOWN] no es compatible con ninguna unidad.
         * Cuando el backend implemente MEJORA-013, esto vendrá del servidor.
         *
         * @param base Unidad de referencia.
         * @return Unidades seleccionables compatibles, o lista vacía si [base] es [UNKNOWN].
         */
        fun compatibleWith(base: PortionUnit): List<PortionUnit> = when (base) {
            GRAM -> listOf(GRAM)
            MILLILITER -> listOf(MILLILITER)
            PIECE -> listOf(PIECE)
            UNKNOWN -> emptyList()
        }
    }
}
