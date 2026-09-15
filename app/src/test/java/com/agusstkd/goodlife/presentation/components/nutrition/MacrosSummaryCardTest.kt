package com.agusstkd.goodlife.presentation.components.nutrition

import org.junit.Assert.assertEquals
import org.junit.Test

class MacrosSummaryCardTest {
    @Test
    fun formatNutritionValue_removesOnlyInsignificantTrailingZeros() {
        assertEquals("12", formatNutritionValue(12.0))
        assertEquals("12.5", formatNutritionValue(12.5))
        assertEquals("0.125", formatNutritionValue(0.125))
        assertEquals("12,5", formatNutritionValue(12.5, ','))
        assertEquals("0,125", formatNutritionValue(0.125, ','))
    }
}