package com.agusstkd.goodlife.domain.model.training

/**
 * Objetivo principal de una rutina.
 *
 * El JSON entra solo por [fromWire]. Un valor desconocido es [UNKNOWN];
 * la UI no lo trata como [STRENGTH] ni imprime el texto crudo.
 */
enum class GoalType {
    MUSCLE_GAIN,
    WEIGHT_LOSS,
    STRENGTH,
    ENDURANCE,
    FLEXIBILITY,
    UNKNOWN;

    companion object {
        val selectable: List<GoalType> = entries.filter { it != UNKNOWN }

        /**
         * Traduce el valor de wire a un caso del tipo cerrado.
         *
         * @param raw Nombre exacto del enum, o nulo/vacío/desconocido.
         * @return El caso conocido o [UNKNOWN].
         */
        fun fromWire(raw: String?): GoalType =
            selectable.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}
