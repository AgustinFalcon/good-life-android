package com.agusstkd.goodlife.domain.model.daily

import kotlinx.datetime.LocalDate

/**
 * Daily log unificado con todas las actividades del día (Domain Model).
 *
 * Representa todo lo que el usuario tiene que hacer en un día específico.
 * El backend genera automáticamente el daily log si no existe.
 *
 * ## Uso:
 * Este es el modelo de dominio, libre de detalles de serialización.
 * El ViewModel y UseCases usan este modelo, NO el Response del backend.
 *
 * @property id ID del daily log
 * @property userId ID del usuario propietario
 * @property date Fecha del daily log
 * @property completionRate Porcentaje de items completados (0.0 - 1.0)
 * @property items Lista de items del día (tasks, habits, workouts, meals)
 *
 * @see DailyItem
 * @see com.agusstkd.goodlife.data.remote.dto.response.daily.DailyLogResponse
 */
data class DailyLog(
    val id: Long,
    val userId: Long,
    val date: LocalDate,
    val completionRate: Double,
    val items: List<DailyItem>
)
