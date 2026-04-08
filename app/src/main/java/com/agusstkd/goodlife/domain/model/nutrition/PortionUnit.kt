package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Unidades de medida disponibles para los ingredientes.
 *
 * Espeja el enum `PortionUnit` del backend.
 *
 * @property label Abreviación legible para mostrar en la UI (ej. "g", "ml", "ud").
 */
enum class PortionUnit(val label: String) {
    GRAM("g"),
    MILLILITER("ml"),
    PIECE("ud");

    companion object {
        /**
         * Convierte el String que devuelve el backend/Ingredient al enum.
         * Si el valor no coincide, devuelve [GRAM] como fallback seguro.
         */
        fun fromString(value: String): PortionUnit = when (value.lowercase()) {
            "g", "gram", "gramo", "gramos"          -> GRAM
            "ml", "milliliter", "mililitro"         -> MILLILITER
            "piece", "ud", "unidad", "unit"         -> PIECE
            else                                    -> GRAM
        }

        /**
         * Devuelve las unidades compatibles con una unidad base dada.
         * Por ahora cada unidad solo es compatible consigo misma.
         * Cuando el backend implemente MEJORA-013, esto vendrá del servidor.
         */
        fun compatibleWith(base: PortionUnit): List<PortionUnit> = when (base) {
            GRAM       -> listOf(GRAM)
            MILLILITER -> listOf(MILLILITER)
            PIECE      -> listOf(PIECE)
        }
    }
}
