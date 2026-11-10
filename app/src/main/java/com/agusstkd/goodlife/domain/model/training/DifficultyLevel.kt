package com.agusstkd.goodlife.domain.model.training

/**
 * Nivel de dificultad de una rutina.
 *
 * El JSON entra solo por [fromWire]. Un valor desconocido es [UNKNOWN];
 * la UI no lo trata como [BEGINNER] ni imprime el texto crudo.
 */
enum class DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    UNKNOWN;

    companion object {
        val selectable: List<DifficultyLevel> = entries.filter { it != UNKNOWN }

        /**
         * Traduce el valor de wire a un caso del tipo cerrado.
         *
         * @param raw Nombre exacto del enum, o nulo/vacío/desconocido.
         * @return El caso conocido o [UNKNOWN].
         */
        fun fromWire(raw: String?): DifficultyLevel =
            selectable.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}
