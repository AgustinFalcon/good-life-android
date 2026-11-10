package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Tipo de comida según el momento del día.
 *
 * Los valores de wire coinciden con el backend. Un valor desconocido
 * entra por [fromWire] como [UNKNOWN]; la UI no lo trata como snack válido.
 */
enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    AFTERNOON_SNACK,
    SNACK,
    UNKNOWN;

    companion object {
        val selectable: List<MealType> = entries.filter { it != UNKNOWN }

        fun fromWire(raw: String?): MealType =
            selectable.firstOrNull { it.name == raw } ?: UNKNOWN

        /**
         * Sugiere un tipo de comida basado en la hora del día.
         *
         * @param hour Hora en formato 24h (0-23)
         * @return Tipo de comida sugerido para ese horario
         */
        fun suggestFromHour(hour: Int): MealType = when (hour) {
            in 6..10 -> BREAKFAST
            in 11..14 -> LUNCH
            in 15..17 -> AFTERNOON_SNACK
            in 18..22 -> DINNER
            else -> SNACK
        }
    }
}
