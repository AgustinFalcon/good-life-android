package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.habit.Habit
import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import com.agusstkd.goodlife.domain.repository.HabitRepository
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Fake de [HabitRepository] para tests unitarios.
 *
 * Permite configurar el resultado de [createHabit] y verificar
 * cuántas veces se llamó y con qué parámetros.
 */
class FakeHabitRepository : HabitRepository {

    var createHabitResult: Result<Habit> = Result.Success(DEFAULT_HABIT)

    var createHabitCallCount = 0
        private set
    var lastName: String? = null
        private set

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
        createHabitCallCount++
        lastName = name
        return createHabitResult
    }

    companion object {
        val DEFAULT_HABIT = Habit(
            id = 1L,
            name = "Beber agua",
            description = null,
            category = HabitCategory.HYDRATION,
            targetValue = 8,
            unit = "vasos",
            isActive = true,
            daysOfWeek = setOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
            ),
            startDate = LocalDate(2026, 3, 9),
            endDate = null,
            scheduledTime = null,
        )
    }
}
