package com.agusstkd.goodlife.domain.model.daily

/**
 * Estado de un item en el Daily Log.
 *
 * Representa el ciclo de vida de una actividad:
 * PENDING -> IN_PROGRESS -> COMPLETED | SKIPPED
 *
 * SKIPPED cuenta como procesado para el cálculo de completion rate,
 * permitiendo que decisiones conscientes no penalicen al usuario.
 */
enum class ItemStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED
}
