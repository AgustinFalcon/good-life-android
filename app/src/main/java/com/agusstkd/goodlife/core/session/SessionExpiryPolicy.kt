package com.agusstkd.goodlife.core.session

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Decide si un 401 en `/api/v1/token` es expiración de sesión o un login fallido.
 *
 * Login y refresh comparten el endpoint. Un password incorrecto no debe borrar
 * tokens ni emitir [SessionEventBus.SessionEvent.SessionExpired], aunque quede
 * un refresh residual de una sesión anterior.
 */
object SessionExpiryPolicy {

    const val GRANT_PASSWORD = "password"
    const val GRANT_REFRESH = "refreshToken"

    /**
     * @param grantType Valor de `grantType` del body form, o `null` si no se pudo leer.
     * @param hasStoredRefreshToken `true` si hay refresh token persistido.
     * @return `true` si hay que limpiar sesión.
     */
    fun shouldWipeSessionOnTokenEndpointFailure(
        grantType: String?,
        hasStoredRefreshToken: Boolean,
    ): Boolean = when (grantType) {
        GRANT_PASSWORD -> false
        GRANT_REFRESH -> true
        else -> hasStoredRefreshToken
    }

    /**
     * Extrae `grantType` de un body `application/x-www-form-urlencoded`.
     */
    fun grantTypeFromFormBody(form: String): String? =
        form.split('&').firstNotNullOfOrNull { pair ->
            val parts = pair.split('=', limit = 2)
            if (parts.size != 2) return@firstNotNullOfOrNull null
            val key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8.name())
            if (key != "grantType") return@firstNotNullOfOrNull null
            URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name()).ifBlank { null }
        }
}
