package com.agusstkd.goodlife.domain.model.auth

/**
 * Modelo de dominio para tokens de autenticación.
 *
 * Representa los tokens JWT necesarios para mantener la sesión del usuario.
 *
 * @property accessToken Token de acceso para requests autenticados (30 min)
 * @property refreshToken Token para renovar el accessToken sin credenciales (7 días)
 * @property accessTokenExpiresAt Timestamp de expiración del accessToken
 * @property refreshTokenExpiresAt Timestamp de expiración del refreshToken
 */
data class AuthToken(
    val accessToken: String,
    val refreshToken: String?,
    val accessTokenExpiresAt: Long,
    val refreshTokenExpiresAt: Long?
) {
    /**
     * Verifica si el accessToken ha expirado.
     * Se considera expirado 1 minuto antes para dar margen.
     */
    fun isAccessTokenExpired(): Boolean {
        val buffer = 60 * 1000L // 1 minuto de buffer
        return System.currentTimeMillis() >= (accessTokenExpiresAt - buffer)
    }

    /**
     * Verifica si el refreshToken ha expirado.
     */
    fun isRefreshTokenExpired(): Boolean {
        return refreshTokenExpiresAt?.let {
            System.currentTimeMillis() >= it
        } ?: true
    }

    /**
     * Verifica si hay tokens válidos para autenticación.
     */
    fun hasValidTokens(): Boolean {
        return !isAccessTokenExpired() || (refreshToken != null && !isRefreshTokenExpired())
    }
}
