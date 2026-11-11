package com.agusstkd.goodlife.data.remote.dto.response.daily

import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitLogSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.workout.WorkoutSummaryDto
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DailyItemResponseTest {

    @Test
    fun `workout maps routine facts and leaves description unformatted`() {
        val item = DailyItemResponse(
            id = 1L,
            type = "WORKOUT",
            referenceId = 40L,
            scheduledTime = null,
            status = "PENDING",
            workout = WorkoutSummaryDto(
                id = 1L,
                name = "PushDay",
                routineName = "SmokeRutina",
                dayOrder = 1,
            ),
        ).toDomain()

        assertNotNull(item)
        assertEquals("PushDay", item!!.title)
        assertEquals(null, item.description)
        assertEquals("SmokeRutina", item.workoutRoutineName)
        assertEquals(null, item.workoutExerciseCount)
        assertEquals(1, item.workoutDayOrder)
        assertEquals(DailyItemType.WORKOUT, item.type)
        assertEquals(DailyItemStatus.PENDING, item.status)
    }

    @Test
    fun `completed habit with zero log shows the target as current`() {
        val item = DailyItemResponse(
            id = 1L,
            type = "HABIT",
            referenceId = 30L,
            scheduledTime = null,
            status = "COMPLETED",
            habitLog = HabitLogSummaryDto(
                id = 30L,
                currentValue = 0.0,
                targetValue = 8.0,
                isCompleted = true,
                habit = HabitSummaryDto(
                    id = 2L,
                    name = "SmokeHabito",
                    unit = "vasos",
                    targetValue = 8,
                    category = "HYDRATION",
                ),
            ),
        )

        assertEquals("8 / 8 vasos", item.toDomain()?.description)
    }

    @Test
    fun `meal stores macros as facts without copy`() {
        val item = DailyItemResponse(
            id = 1L,
            type = "MEAL",
            referenceId = 50L,
            scheduledTime = null,
            status = "PENDING",
            mealPlan = MealSummaryDto(id = 1L, mealName = "Almuerzo", calories = 700, protein = 42),
        ).toDomain()

        assertNotNull(item)
        assertEquals("Almuerzo", item!!.title)
        assertEquals(null, item.description)
        assertEquals(700, item.mealCalories)
        assertEquals(42, item.mealProtein)
    }

    @Test
    fun `unknown type is discarded`() {
        val item = DailyItemResponse(
            id = 1L,
            type = "NOTE",
            referenceId = 1L,
            scheduledTime = null,
            status = "PENDING",
        ).toDomain()

        assertNull(item)
    }

    @Test
    fun `unknown status is discarded`() {
        val item = DailyItemResponse(
            id = 1L,
            type = "TASK",
            referenceId = 1L,
            scheduledTime = null,
            status = "CANCELLED",
        ).toDomain()

        assertNull(item)
    }
}
