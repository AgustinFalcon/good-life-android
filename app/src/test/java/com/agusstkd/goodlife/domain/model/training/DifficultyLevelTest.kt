package com.agusstkd.goodlife.domain.model.training

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DifficultyLevelTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(DifficultyLevel.BEGINNER, DifficultyLevel.fromWire("BEGINNER"))
        assertEquals(DifficultyLevel.INTERMEDIATE, DifficultyLevel.fromWire("INTERMEDIATE"))
        assertEquals(DifficultyLevel.ADVANCED, DifficultyLevel.fromWire("ADVANCED"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(DifficultyLevel.UNKNOWN, DifficultyLevel.fromWire(null))
        assertEquals(DifficultyLevel.UNKNOWN, DifficultyLevel.fromWire(""))
        assertEquals(DifficultyLevel.UNKNOWN, DifficultyLevel.fromWire("EXPERT"))
        assertEquals(DifficultyLevel.UNKNOWN, DifficultyLevel.fromWire("UNKNOWN"))
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(DifficultyLevel.UNKNOWN in DifficultyLevel.selectable)
        assertEquals(3, DifficultyLevel.selectable.size)
    }
}
