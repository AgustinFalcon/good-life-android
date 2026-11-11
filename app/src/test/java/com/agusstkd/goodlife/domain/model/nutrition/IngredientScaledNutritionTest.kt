package com.agusstkd.goodlife.domain.model.nutrition

import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientScaledNutritionTest {

    @Test
    fun `scales macros by quantity over serving size`() {
        val ingredient = Ingredient(
            id = 1L,
            name = "Oats",
            brand = null,
            servingSize = 100.0,
            servingUnit = PortionUnit.GRAM,
            calories = 380.0,
            protein = 12.0,
            carbs = 60.0,
            fat = 7.0,
            isGlobal = true,
            imageUrl = null,
        )

        val scaled = ingredient.scaledNutrition(50.0)

        assertEquals(190.0, scaled.calories, 0.001)
        assertEquals(6.0, scaled.protein, 0.001)
        assertEquals(30.0, scaled.carbs, 0.001)
        assertEquals(3.5, scaled.fat, 0.001)
    }

    @Test
    fun `zero serving size yields zeros`() {
        val ingredient = Ingredient(
            id = 1L,
            name = "Oats",
            brand = null,
            servingSize = 0.0,
            servingUnit = PortionUnit.GRAM,
            calories = 380.0,
            protein = 12.0,
            carbs = 60.0,
            fat = 7.0,
            isGlobal = true,
            imageUrl = null,
        )

        val scaled = ingredient.scaledNutrition(50.0)

        assertEquals(0.0, scaled.calories, 0.0)
        assertEquals(0.0, scaled.protein, 0.0)
    }
}
