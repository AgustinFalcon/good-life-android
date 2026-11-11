package com.agusstkd.goodlife.domain.model.daily

/**
 * Estado de un item en el Daily Log.
 *
 * Representa el ciclo de vida de una actividad:
 * PENDING -> IN_PROGRESS -> COMPLETED | SKIPPED
 *
 * SKIPPED cuenta como procesado para el cálculo de completion rate,
 * permitiendo que decisiones conscientes no penalicen al usuario.
 *
 * El JSON entra solo por [fromWire]. Un valor desconocido es [UNKNOWN]
 * y el mapper descarta el item; la UI no lo trata como un estado válido.
 *
 * @see DailyItem
 */
enum class DailyItemStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED,
    UNKNOWN;

    /** SKIPPED y COMPLETED cuentan para el completion rate; el resto no. */
    val countsTowardCompletion: Boolean
        get() = this == COMPLETED || this == SKIPPED

    companion object {
        val selectable: List<DailyItemStatus> = entries.filter { it != UNKNOWN }

        /**
         * Traduce el valor de wire a un caso del tipo cerrado.
         *
         * @param raw Nombre exacto del enum, o nulo/vacío/desconocido.
         * @return El caso conocido o [UNKNOWN].
         */
        fun fromWire(raw: String?): DailyItemStatus =
            selectable.firstOrNull { it.name == raw } ?: UNKNOWN
    }
}
