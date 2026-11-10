package com.agusstkd.goodlife.domain.model.daily

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DailyItemTypeTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(DailyItemType.TASK, DailyItemType.fromWire("TASK"))
        assertEquals(DailyItemType.HABIT, DailyItemType.fromWire("HABIT"))
        assertEquals(DailyItemType.WORKOUT, DailyItemType.fromWire("WORKOUT"))
        assertEquals(DailyItemType.MEAL, DailyItemType.fromWire("MEAL"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(DailyItemType.UNKNOWN, DailyItemType.fromWire(null))
        assertEquals(DailyItemType.UNKNOWN, DailyItemType.fromWire(""))
        assertEquals(DailyItemType.UNKNOWN, DailyItemType.fromWire("NOTE"))
        assertEquals(DailyItemType.UNKNOWN, DailyItemType.fromWire("UNKNOWN"))
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(DailyItemType.UNKNOWN in DailyItemType.selectable)
        assertEquals(4, DailyItemType.selectable.size)
    }
}
