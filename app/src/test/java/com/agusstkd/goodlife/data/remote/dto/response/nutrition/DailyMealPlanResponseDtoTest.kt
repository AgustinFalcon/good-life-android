package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.MealType
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DailyMealPlanResponseDtoTest {

    private val monday = LocalDate(2026, 11, 16)
    private val tuesday = LocalDate(2026, 11, 10)

    @Test
    fun `backend mealName maps on a matching weekday`() {
        val summary = backendPlan().toDailySummary(monday)!!

        assertEquals(9L, summary.id)
        assertEquals("Pollo a la plancha con verduras v2", summary.name)
        assertEquals(MealType.LUNCH, summary.mealType)
        assertEquals("https://img", summary.imageUrl)
        assertEquals(274.0, summary.totalCalories, 0.01)
    }

    @Test
    fun `plan is omitted on a weekday that is not scheduled`() {
        assertNull(backendPlan().toDailySummary(tuesday))
    }

    @Test
    fun `missing mealName is omitted instead of crashing`() {
        assertNull(backendPlan().copy(mealName = null).toDailySummary(monday))
    }

    @Test
    fun `unknown mealType is omitted instead of becoming snack`() {
        assertNull(backendPlan().copy(mealType = "PRE_WORKOUT").toDailySummary(monday))
    }

    private fun backendPlan() = DailyMealPlanResponseDto(
        id = 9L,
        mealId = 1L,
        mealName = "Pollo a la plancha con verduras v2",
        mealImageUrl = "https://img",
        mealType = "LUNCH",
        daysOfWeek = listOf(1),
        totalCalories = 274.0,
        totalProtein = 43.0,
        totalCarbs = 9.0,
        totalFat = 8.0,
        isCustomMeal = true,
    )
}
