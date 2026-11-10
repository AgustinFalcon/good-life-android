package com.agusstkd.goodlife.data.remote.dto.response.habit

import com.agusstkd.goodlife.domain.model.habit.HabitCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class HabitResponseTest {

    @Test
    fun `known category maps through fromWire`() {
        val habit = sampleHabit(category = "HYDRATION").toDomain()
        assertEquals(HabitCategory.HYDRATION, habit.category)
        assertEquals("Agua", habit.name)
    }

    @Test
    fun `unknown category is kept as Unknown not Custom`() {
        val habit = sampleHabit(category = "CUSTOM_V2").toDomain()
        assertEquals(HabitCategory.UNKNOWN, habit.category)
        assertEquals("Agua", habit.name)
    }

    private fun sampleHabit(category: String) = HabitResponse(
        id = 1L,
        userId = 2L,
        name = "Agua",
        description = null,
        category = category,
        targetValue = 8,
        unit = "vasos",
        isActive = true,
        daysOfWeek = listOf(1),
        startDate = "2026-01-01",
        endDate = null,
        scheduledTime = null,
        createdAt = "2026-01-01T00:00:00",
    )
}
