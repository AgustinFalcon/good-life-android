package com.agusstkd.goodlife.domain.model.nutrition

/**
 * Tipo de comida según el momento del día.
 *
 * Define las categorías de comidas soportadas por el sistema de nutrición.
 * Los valores coinciden con el backend para serialización directa.
 */
enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK,
    PRE_WORKOUT,
    POST_WORKOUT;

    companion object {
        /**
         * Sugiere un tipo de comida basado en la hora del día.
         *
         * @param hour Hora en formato 24h (0-23)
         * @return Tipo de comida sugerido para ese horario
         */
        fun suggestFromHour(hour: Int): MealType = when (hour) {
            in 6..10 -> BREAKFAST
            in 11..14 -> LUNCH
            in 15..17 -> SNACK
            in 18..22 -> DINNER
            else -> SNACK
        }
    }
}
