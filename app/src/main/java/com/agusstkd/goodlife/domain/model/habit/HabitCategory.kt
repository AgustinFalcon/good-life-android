package com.agusstkd.goodlife.domain.model.habit

/**
 * Categorías de hábito definidas por el backend.
 *
 * Cada categoría agrupa hábitos de un mismo ámbito de bienestar.
 * El JSON entra solo por [fromWire]. Un valor desconocido es [UNKNOWN];
 * la UI no lo trata como [CUSTOM] ni imprime el texto crudo.
 *
 * @see Habit
 */
enum class HabitCategory {
    HYDRATION,
    MEDITATION,
    READING,
    EXERCISE,
    SLEEP,
    NUTRITION,
    LEARNING,
    MINDFULNESS,
    SOCIAL,
    CREATIVITY,
    PRODUCTIVITY,
    HEALTH,
    CUSTOM,
    UNKNOWN;

    companion object {
        val selectable: List<HabitCategory> = entries.filter { it != UNKNOWN }

        /**
         * Traduce el valor de wire a un caso del tipo cerrado.
         *
         * @param raw Nombre exacto del enum, o nulo/vacío/desconocido.
         * @return El caso conocido o [UNKNOWN].
         */
        fun fromWire(raw: String?): HabitCategory =
            selectable.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}
