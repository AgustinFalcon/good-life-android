package com.agusstkd.goodlife.core.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalEncodingApi::class)
class JwtUtilsTest {
    @Test fun `extracts string numeric claims and expiration`() {
        val future = System.currentTimeMillis() / 1000 + 3600
        val token = token("{\"sub\":\"42\",\"scope\":\"USER\",\"exp\":$future}")
        assertEquals("42", JwtUtils.extractSubject(token))
        assertEquals("USER", JwtUtils.extractScope(token))
        assertFalse(JwtUtils.isExpired(token)!!)
    }

    @Test fun `expired malformed and missing claims return safe values`() {
        val past = System.currentTimeMillis() / 1000 - 3600
        val expired = token("{\"sub\":\"1\",\"exp\":$past}")
        assertTrue(JwtUtils.isExpired(expired)!!)
        assertNull(JwtUtils.extractSubject("bad-token"))
        assertNull(JwtUtils.extractScope(token("{\"sub\":\"1\"}")))
        assertNull(JwtUtils.isExpired(token("{\"exp\":\"soon\"}")))
    }

    private fun token(payload: String): String {
        fun enc(value: String) = Base64.UrlSafe.encode(value.encodeToByteArray()).trimEnd('=')
        return "${enc("{}")}.${enc(payload)}.sig"
    }
}
