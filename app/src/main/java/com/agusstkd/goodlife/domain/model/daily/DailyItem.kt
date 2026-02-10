package com.agusstkd.goodlife.domain.model.daily

import kotlinx.datetime.LocalTime

/**
 * Item individual del daily log (Domain Model).
 *
 * Representa una actividad específica: task, habit, workout o meal.
 *
 * ## Campos:
 * - [title] y [description] ya vienen extraídos según el tipo
 * - NO contiene los objetos anidados (task, habitLog, etc.) del Response
 * - Es más simple y enfocado en lo que necesita la UI
 *
 * @property id ID del item
 * @property type Tipo de item (TASK, HABIT, WORKOUT, MEAL)
 * @property referenceId ID de la entidad referenciada (taskId, habitId, etc.)
 * @property scheduledTime Hora programada o null si no tiene
 * @property status Status actual (PENDING, COMPLETED, SKIPPED)
 * @property title Título del item (extraído según tipo)
 * @property description Descripción opcional (extraída según tipo)
 *
 * @see DailyItemType
 * @see ItemStatus
 * @see com.agusstkd.goodlife.data.remote.dto.response.daily.DailyItemResponse
 */
data class DailyItem(
    val id: Long,
    val type: DailyItemType,
    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: ItemStatus,
    val title: String,
    val description: String?
)
