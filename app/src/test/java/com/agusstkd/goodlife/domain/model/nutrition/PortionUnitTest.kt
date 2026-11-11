package com.agusstkd.goodlife.domain.model.nutrition

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PortionUnitTest {

    @Test
    fun `fromWire known names map to selectable cases`() {
        assertEquals(PortionUnit.GRAM, PortionUnit.fromWire("GRAM"))
        assertEquals(PortionUnit.MILLILITER, PortionUnit.fromWire("MILLILITER"))
        assertEquals(PortionUnit.PIECE, PortionUnit.fromWire("PIECE"))
    }

    @Test
    fun `fromWire known aliases map to selectable cases`() {
        assertEquals(PortionUnit.GRAM, PortionUnit.fromWire("g"))
        assertEquals(PortionUnit.GRAM, PortionUnit.fromWire("gramos"))
        assertEquals(PortionUnit.MILLILITER, PortionUnit.fromWire("ml"))
        assertEquals(PortionUnit.MILLILITER, PortionUnit.fromWire("mililitro"))
        assertEquals(PortionUnit.PIECE, PortionUnit.fromWire("ud"))
        assertEquals(PortionUnit.PIECE, PortionUnit.fromWire("unidad"))
    }

    @Test
    fun `fromWire unknown blank and null are Unknown`() {
        assertEquals(PortionUnit.UNKNOWN, PortionUnit.fromWire(null))
        assertEquals(PortionUnit.UNKNOWN, PortionUnit.fromWire(""))
        assertEquals(PortionUnit.UNKNOWN, PortionUnit.fromWire("cup"))
        assertEquals(PortionUnit.UNKNOWN, PortionUnit.fromWire("UNKNOWN"))
        assertEquals("?", PortionUnit.UNKNOWN.label)
    }

    @Test
    fun `selectable excludes Unknown`() {
        assertFalse(PortionUnit.UNKNOWN in PortionUnit.selectable)
        assertEquals(3, PortionUnit.selectable.size)
    }

    @Test
    fun `compatibleWith unknown is empty`() {
        assertTrue(PortionUnit.compatibleWith(PortionUnit.UNKNOWN).isEmpty())
        assertEquals(listOf(PortionUnit.GRAM), PortionUnit.compatibleWith(PortionUnit.GRAM))
    }
}
