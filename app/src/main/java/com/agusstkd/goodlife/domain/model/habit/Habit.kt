package com.agusstkd.goodlife.domain.model.habit

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Modelo de dominio de un hábito recurrente.
 *
 * A diferencia de [Task], un hábito siempre es recurrente ([daysOfWeek] obligatorio)
 * y tiene una meta numérica ([targetValue] + [unit]) que se trackea diariamente.
 *
 * @property id Identificador único asignado por el backend.
 * @property name Nombre del hábito (ej: "Beber agua").
 * @property description Descripción opcional.
 * @property category Categoría de bienestar a la que pertenece.
 * @property targetValue Meta numérica diaria (ej: 8).
 * @property unit Unidad de la meta (ej: "vasos").
 * @property isActive Si el hábito está activo (soft delete).
 * @property daysOfWeek Días de la semana en que se ejecuta.
 * @property startDate Fecha de inicio del hábito.
 * @property endDate Fecha de fin opcional (null = sin fin).
 * @property scheduledTime Hora de ejecución opcional.
 */
data class Habit(
    val id: Long,
    val name: String,
    val description: String?,
    val category: HabitCategory,
    val targetValue: Int,
    val unit: String,
    val isActive: Boolean,
    val daysOfWeek: Set<DayOfWeek>,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val scheduledTime: LocalTime?,
)
