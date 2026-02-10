package com.agusstkd.goodlife.data.remote.dto.response.daily

import kotlinx.datetime.LocalDate

import kotlinx.serialization.Serializable

/**
 * Daily log completo (response del backend).
 *
 * Representa todo lo que el usuario tiene que hacer en un día específico.
 * El backend genera automáticamente el daily log si no existe.
 *
 * ## Campos:
 * - [completionRate]: Porcentaje de items completados (0.0 - 1.0)
 * - [items]: Lista de items (tasks, habits, workouts, meals)
 *
 * @see DailyItemResponse
 */
@Serializable
data class DailyLogResponse(
    val id: Long,
    val userId: Long,
    val date: LocalDate,
    val completionRate: Double,
    val items: List<DailyItemResponse>
)