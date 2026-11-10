package com.agusstkd.goodlife.data.remote.dto.response.daily

import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitLogSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.workout.WorkoutSummaryDto
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyItemResponseTest {

    @Test
    fun `workout maps routine facts and leaves description unformatted`() {
        val item = DailyItemResponse(
            id = 1L,
            type = DailyItemType.WORKOUT,
            referenceId = 40L,
            scheduledTime = null,
            status = DailyItemStatus.PENDING,
            workout = WorkoutSummaryDto(
                id = 1L,
                name = "PushDay",
                routineName = "SmokeRutina",
                dayOrder = 1,
            ),
        ).toDomain()

        assertEquals("PushDay", item.title)
        assertEquals(null, item.description)
        assertEquals("SmokeRutina", item.workoutRoutineName)
        assertEquals(null, item.workoutExerciseCount)
        assertEquals(1, item.workoutDayOrder)
    }

    @Test
    fun `completed habit with zero log shows the target as current`() {
        val item = DailyItemResponse(
            id = 1L,
            type = DailyItemType.HABIT,
            referenceId = 30L,
            scheduledTime = null,
            status = DailyItemStatus.COMPLETED,
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

        assertEquals("8 / 8 vasos", item.toDomain().description)
    }
}
