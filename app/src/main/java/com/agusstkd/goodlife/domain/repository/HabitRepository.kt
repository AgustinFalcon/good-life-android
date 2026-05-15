package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.habit.Habit
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Contrato de acceso a datos para hábitos.
 *
 * Define las operaciones disponibles sin acoplar a una implementación
 * específica (Retrofit, Ktor, Room, etc.).
 *
 * @see com.agusstkd.goodlife.data.repository.HabitRepositoryImpl
 */
interface HabitRepository {

    /**
     * Crea un nuevo hábito recurrente.
     *
     * @param name Nombre del hábito.
     * @param description Descripción opcional.
     * @param category Categoría de bienestar.
     * @param targetValue Meta numérica diaria.
     * @param unit Unidad de la meta (ej: "vasos", "minutos").
     * @param daysOfWeek Días de la semana activos.
     * @param startDate Fecha de inicio.
     * @param endDate Fecha de fin opcional.
     * @param scheduledTime Hora de ejecución opcional.
     * @return [Result] con el hábito creado o el error.
     */
    suspend fun createHabit(
        name: String,
        description: String?,
        category: HabitCategory,
        targetValue: Int,
        unit: String,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate,
        endDate: LocalDate?,
        scheduledTime: LocalTime?,
    ): Result<Habit>
}
