package com.agusstkd.goodlife.domain.model.habit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class HabitCategoryTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(HabitCategory.HYDRATION, HabitCategory.fromWire("HYDRATION"))
        assertEquals(HabitCategory.CUSTOM, HabitCategory.fromWire("CUSTOM"))
        assertEquals(HabitCategory.MINDFULNESS, HabitCategory.fromWire("MINDFULNESS"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(HabitCategory.UNKNOWN, HabitCategory.fromWire(null))
        assertEquals(HabitCategory.UNKNOWN, HabitCategory.fromWire(""))
        assertEquals(HabitCategory.UNKNOWN, HabitCategory.fromWire("CUSTOM_V2"))
        assertEquals(HabitCategory.UNKNOWN, HabitCategory.fromWire("UNKNOWN"))
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(HabitCategory.UNKNOWN in HabitCategory.selectable)
        assertEquals(13, HabitCategory.selectable.size)
    }
}
