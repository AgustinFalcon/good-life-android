package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.datasource.remote.HabitRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.habit.CreateHabitRequest
import com.agusstkd.goodlife.data.remote.dto.response.habit.toDomain
import com.agusstkd.goodlife.domain.model.habit.Habit
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.domain.repository.HabitRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

/**
 * Implementación del repositorio de hábitos.
 *
 * Convierte tipos de dominio a DTOs para el request, delega al
 * [HabitRemoteDataSource], y mapea la response de vuelta a [Habit].
 *
 * Singleton en Koin para permitir cache en FASE 2.
 *
 * @property habitRemoteDataSource Data source remoto de hábitos.
 */
class HabitRepositoryImpl(
    private val habitRemoteDataSource: HabitRemoteDataSource,
) : HabitRepository {

    override suspend fun createHabit(
        name: String,
        description: String?,
        category: HabitCategory,
        targetValue: Int,
        unit: String,
        daysOfWeek: Set<DayOfWeek>,
        startDate: LocalDate,
        endDate: LocalDate?,
        scheduledTime: LocalTime?,
    ): Result<Habit> {
        val request = CreateHabitRequest(
            name = name,
            description = description,
            category = category.name,
            targetValue = targetValue,
            unit = unit,
            daysOfWeek = daysOfWeek.map { it.isoDayNumber }.sorted(),
            startDate = startDate.toString(),
            endDate = endDate?.toString(),
            scheduledTime = scheduledTime?.toString(),
        )

        return when (val result = habitRemoteDataSource.createHabit(request)) {
            is Result.Success -> Result.Success(result.data.toDomain())
            is Result.Error -> Result.Error(result.exception)
        }
    }
}
