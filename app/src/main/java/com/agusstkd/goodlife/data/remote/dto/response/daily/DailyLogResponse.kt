package com.agusstkd.goodlife.data.remote.dto.response.daily

import com.agusstkd.goodlife.domain.model.daily.DailyLog
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
 * @see DailyLog Domain model equivalente
 */
@Serializable
data class DailyLogResponse(
    val id: Long,
    val date: LocalDate,
    val completionRate: Double,
    val items: List<DailyItemResponse>
)

/**
 * Convierte DailyLogResponse a modelo de dominio DailyLog.
 *
 * Mapea también todos los items a modelos de dominio.
 *
 * @return DailyLog listo para usar en ViewModel/UseCases
 */
fun DailyLogResponse.toDomain(): DailyLog {
    return DailyLog(
        id = id,
        date = date,
        completionRate = completionRate,
        items = items.map { it.toDomain() }
    )
}