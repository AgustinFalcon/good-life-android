package com.agusstkd.goodlife.domain.model.training

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PerceivedExertionTest {

    @Test
    fun `fromWire null and blank are Absent`() {
        assertEquals(PerceivedExertion.Absent, PerceivedExertion.fromWire(null))
        assertEquals(PerceivedExertion.Absent, PerceivedExertion.fromWire(""))
        assertEquals(PerceivedExertion.Absent, PerceivedExertion.fromWire("  "))
        assertEquals(null, PerceivedExertion.fromWire(null).toDraftRpe())
    }

    @Test
    fun `fromWire 1 and 10 are Score`() {
        val one = PerceivedExertion.fromWire("1") as PerceivedExertion.Score
        val ten = PerceivedExertion.fromWire("10") as PerceivedExertion.Score
        assertEquals(1, one.value)
        assertEquals(10, ten.value)
        assertEquals(1, one.toDraftRpe())
        assertEquals(10, ten.toDraftRpe())
    }

    @Test
    fun `fromWire 11 and abc are Unknown`() {
        assertTrue(PerceivedExertion.fromWire("11") is PerceivedExertion.Unknown)
        assertTrue(PerceivedExertion.fromWire("abc") is PerceivedExertion.Unknown)
        assertEquals(null, PerceivedExertion.fromWire("11").toDraftRpe())
        assertEquals(null, PerceivedExertion.fromWire("abc").toDraftRpe())
    }
}
