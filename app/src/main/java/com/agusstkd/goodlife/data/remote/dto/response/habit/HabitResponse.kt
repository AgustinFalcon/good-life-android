package com.agusstkd.goodlife.data.remote.dto.response.habit

import com.agusstkd.goodlife.domain.model.habit.Habit
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

/**
 * DTO de respuesta para un hábito (`GET/POST /api/v1/habits`).
 *
 * Contiene todos los campos que el backend retorna en el objeto `data`.
 * La extension function [toDomain] convierte esta representación JSON
 * a un modelo de dominio [Habit] con tipos ricos.
 *
 * @see toDomain
 */
@Serializable
data class HabitResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String?,
    val category: String,
    val targetValue: Int,
    val unit: String,
    val isActive: Boolean,
    val daysOfWeek: List<Int>?,
    val startDate: String?,
    val endDate: String?,
    val scheduledTime: String?,
    val createdAt: String,
)

/**
 * Convierte la respuesta del backend a modelo de dominio.
 *
 * Parsea strings ISO a tipos de kotlinx-datetime y mapea
 * los ints de días a [DayOfWeek] via `DayOfWeek(isoDay)`.
 */
fun HabitResponse.toDomain(): Habit {
    return Habit(
        id = id,
        name = name,
        description = description,
        category = runCatching { HabitCategory.valueOf(category) }
            .getOrDefault(HabitCategory.CUSTOM),
        targetValue = targetValue,
        unit = unit,
        isActive = isActive,
        daysOfWeek = daysOfWeek?.map { DayOfWeek(it) }?.toSet() ?: emptySet(),
        startDate = startDate?.let { LocalDate.parse(it) },
        endDate = endDate?.let { LocalDate.parse(it) },
        scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
    )
}
