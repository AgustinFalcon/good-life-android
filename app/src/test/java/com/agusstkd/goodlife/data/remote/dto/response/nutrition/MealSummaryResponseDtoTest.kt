package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class MealSummaryResponseDtoTest {

    @Test
    fun `null serving unit is UNKNOWN not a Spanish fallback`() {
        val meal = MealSummaryResponseDto(
            id = 1L,
            name = "Bowl",
            servingUnit = null,
        ).toMealSummary()

        assertEquals(PortionUnit.UNKNOWN, meal.servingUnit)
    }

    @Test
    fun `null name is empty not Spanish fallback`() {
        val meal = MealSummaryResponseDto(id = 1L, name = null).toMealSummary()
        assertEquals("", meal.name)
    }

    @Test
    fun `wire GRAM maps to PortionUnit`() {
        val meal = MealSummaryResponseDto(
            id = 1L,
            name = "Bowl",
            servingUnit = "GRAM",
        ).toMealSummary()

        assertEquals(PortionUnit.GRAM, meal.servingUnit)
    }
}
