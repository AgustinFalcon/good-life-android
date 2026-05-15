package com.agusstkd.goodlife.domain.model.daily

import kotlinx.serialization.Serializable

/**
 * Estado de un item en el Daily Log.
 *
 * Representa el ciclo de vida de una actividad:
 * PENDING -> IN_PROGRESS -> COMPLETED | SKIPPED
 *
 * SKIPPED cuenta como procesado para el cálculo de completion rate,
 * permitiendo que decisiones conscientes no penalicen al usuario.
 *
 * @Serializable: kotlinx.serialization necesita esta anotación para deserializar
 * el campo "status" del JSON. Trade-off aceptado — el enum es simple y estable.
 */
@Serializable
enum class DailyItemStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    SKIPPED
}
