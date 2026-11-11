package com.agusstkd.goodlife.core.network

import okhttp3.logging.HttpLoggingInterceptor

/**
 * Impide que el logger HTTP deje JWT o refresh tokens en logcat.
 *
 * `BASIC` de OkHttp no imprime headers; este redactor cubre `HEADERS`/`BODY`
 * si alguien sube el nivel, y query/form `refreshToken`.
 */
internal object HttpLogRedactor {

    private val authorization = Regex("""(?i)(authorization\s*:\s*bearer\s+)\S+""")
    private val refreshToken = Regex("""(?i)(refreshToken=)[^&\s]+""")

    fun redact(message: String): String =
        authorization.replace(message, "$1***")
            .let { refreshToken.replace(it, "$1***") }

    fun clamp(level: HttpLoggingInterceptor.Level): HttpLoggingInterceptor.Level =
        when (level) {
            HttpLoggingInterceptor.Level.HEADERS,
            HttpLoggingInterceptor.Level.BODY -> HttpLoggingInterceptor.Level.BASIC
            else -> level
        }
}
