package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IngredientResponseDtoTest {

    @Test
    fun `baseQuantity and baseUnit map without defaulting to grams`() {
        val ingredient = IngredientResponseDto(
            id = 9L,
            name = "Oats",
            baseQuantity = 40.0,
            baseUnit = "GRAM",
            calories = 150.0,
            availableUnits = listOf("g"),
        ).toIngredient()

        assertEquals(40.0, ingredient.servingSize, 0.0)
        assertEquals(PortionUnit.GRAM, ingredient.servingUnit)
        assertEquals(listOf(PortionUnit.GRAM), ingredient.availableUnits)
    }

    @Test
    fun `unknown unit is UNKNOWN not GRAM`() {
        val ingredient = IngredientResponseDto(
            id = 1L,
            name = "Mystery",
            baseQuantity = 1.0,
            baseUnit = "CUP",
        ).toIngredient()

        assertEquals(PortionUnit.UNKNOWN, ingredient.servingUnit)
        assertTrue(ingredient.availableUnits.isEmpty())
    }
}
