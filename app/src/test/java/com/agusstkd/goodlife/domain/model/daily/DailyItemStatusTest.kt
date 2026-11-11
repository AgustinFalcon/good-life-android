package com.agusstkd.goodlife.domain.model.daily

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class DailyItemStatusTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(DailyItemStatus.PENDING, DailyItemStatus.fromWire("PENDING"))
        assertEquals(DailyItemStatus.IN_PROGRESS, DailyItemStatus.fromWire("IN_PROGRESS"))
        assertEquals(DailyItemStatus.COMPLETED, DailyItemStatus.fromWire("COMPLETED"))
        assertEquals(DailyItemStatus.SKIPPED, DailyItemStatus.fromWire("SKIPPED"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(DailyItemStatus.UNKNOWN, DailyItemStatus.fromWire(null))
        assertEquals(DailyItemStatus.UNKNOWN, DailyItemStatus.fromWire(""))
        assertEquals(DailyItemStatus.UNKNOWN, DailyItemStatus.fromWire("CANCELLED"))
        assertEquals(DailyItemStatus.UNKNOWN, DailyItemStatus.fromWire("UNKNOWN"))
    }

    @Test
    fun `skipped and completed count toward completion`() {
        assertEquals(true, DailyItemStatus.COMPLETED.countsTowardCompletion)
        assertEquals(true, DailyItemStatus.SKIPPED.countsTowardCompletion)
        assertEquals(false, DailyItemStatus.PENDING.countsTowardCompletion)
        assertEquals(false, DailyItemStatus.IN_PROGRESS.countsTowardCompletion)
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(DailyItemStatus.UNKNOWN in DailyItemStatus.selectable)
        assertEquals(4, DailyItemStatus.selectable.size)
    }
}
