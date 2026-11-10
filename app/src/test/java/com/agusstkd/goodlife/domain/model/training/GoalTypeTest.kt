package com.agusstkd.goodlife.domain.model.training

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class GoalTypeTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(GoalType.MUSCLE_GAIN, GoalType.fromWire("MUSCLE_GAIN"))
        assertEquals(GoalType.STRENGTH, GoalType.fromWire("STRENGTH"))
        assertEquals(GoalType.FLEXIBILITY, GoalType.fromWire("FLEXIBILITY"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(GoalType.UNKNOWN, GoalType.fromWire(null))
        assertEquals(GoalType.UNKNOWN, GoalType.fromWire(""))
        assertEquals(GoalType.UNKNOWN, GoalType.fromWire("HYPERTROPHY"))
        assertEquals(GoalType.UNKNOWN, GoalType.fromWire("UNKNOWN"))
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(GoalType.UNKNOWN in GoalType.selectable)
        assertEquals(5, GoalType.selectable.size)
    }
}
