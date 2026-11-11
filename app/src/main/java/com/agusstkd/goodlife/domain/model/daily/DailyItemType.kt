package com.agusstkd.goodlife.domain.model.daily

/**
 * Tipo de item en el Daily Log.
 *
 * Identifica la categoría de cada actividad registrada en el día.
 * El Daily Log unifica tareas, hábitos, entrenamientos y comidas
 * en una vista centralizada.
 *
 * El JSON entra solo por [fromWire]. Un valor desconocido es [UNKNOWN]
 * y el mapper descarta el item; la UI no lo trata como un tipo válido.
 *
 * @see DailyItem
 */
enum class DailyItemType {
    TASK,
    HABIT,
    WORKOUT,
    MEAL,
    UNKNOWN;

    companion object {
        val selectable: List<DailyItemType> = entries.filter { it != UNKNOWN }

        /**
         * Traduce el valor de wire a un caso del tipo cerrado.
         *
         * @param raw Nombre exacto del enum, o nulo/vacío/desconocido.
         * @return El caso conocido o [UNKNOWN].
         */
        fun fromWire(raw: String?): DailyItemType =
            selectable.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}
