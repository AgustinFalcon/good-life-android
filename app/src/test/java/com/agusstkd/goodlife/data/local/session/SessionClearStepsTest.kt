package com.agusstkd.goodlife.data.local.session

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionClearStepsTest {

    @Test
    fun `later steps still run after an earlier failure`() = runTest {
        val ran = mutableListOf<String>()

        val error = runSessionClearSteps(
            {
                ran += "tokens"
                throw IllegalStateException("tokens failed")
            },
            { ran += "daily" },
            { ran += "user" },
        )

        assertEquals(listOf("tokens", "daily", "user"), ran)
        assertTrue(error is IllegalStateException)
        assertEquals("tokens failed", error?.message)
    }

    @Test
    fun `success returns null`() = runTest {
        val error = runSessionClearSteps({ /* tokens */ }, { /* daily */ })
        assertEquals(null, error)
    }
}
