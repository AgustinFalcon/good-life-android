package com.agusstkd.goodlife.domain.model.nutrition

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class MealTypeTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(MealType.BREAKFAST, MealType.fromWire("BREAKFAST"))
        assertEquals(MealType.AFTERNOON_SNACK, MealType.fromWire("AFTERNOON_SNACK"))
        assertEquals(MealType.SNACK, MealType.fromWire("SNACK"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(MealType.UNKNOWN, MealType.fromWire(null))
        assertEquals(MealType.UNKNOWN, MealType.fromWire(""))
        assertEquals(MealType.UNKNOWN, MealType.fromWire("PRE_WORKOUT"))
        assertEquals(MealType.UNKNOWN, MealType.fromWire("SNACKS"))
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(MealType.UNKNOWN in MealType.selectable)
        assertEquals(5, MealType.selectable.size)
    }
}
