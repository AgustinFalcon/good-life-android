package com.agusstkd.goodlife.core.network

import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class HttpLogRedactorTest {

    @Test
    fun `bearer token is stripped`() {
        val redacted = HttpLogRedactor.redact("Authorization: Bearer eyJhbGciOi.secret")
        assertFalse(redacted.contains("eyJhbGciOi.secret"))
        assertEquals("Authorization: Bearer ***", redacted)
    }

    @Test
    fun `refreshToken query value is stripped`() {
        val redacted = HttpLogRedactor.redact("POST /token?grantType=refreshToken&refreshToken=abc.def")
        assertFalse(redacted.contains("abc.def"))
        assertEquals("POST /token?grantType=refreshToken&refreshToken=***", redacted)
    }

    @Test
    fun `headers and body clamp to basic`() {
        assertEquals(
            HttpLoggingInterceptor.Level.BASIC,
            HttpLogRedactor.clamp(HttpLoggingInterceptor.Level.HEADERS),
        )
        assertEquals(
            HttpLoggingInterceptor.Level.BASIC,
            HttpLogRedactor.clamp(HttpLoggingInterceptor.Level.BODY),
        )
        assertEquals(
            HttpLoggingInterceptor.Level.NONE,
            HttpLogRedactor.clamp(HttpLoggingInterceptor.Level.NONE),
        )
    }
}
