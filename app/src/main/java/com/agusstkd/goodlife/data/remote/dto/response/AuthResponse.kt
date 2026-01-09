package com.agusstkd.goodlife.data.remote.dto.response

import com.agusstkd.goodlife.domain.model.AuthToken
import kotlinx.serialization.Serializable

/**
 * Respuesta del endpoint POST /api/v1/token
 *
 * Contiene los tokens JWT necesarios para autenticación.
 *
 * @property accessToken Token de acceso (expira en 30 minutos)
 * @property refreshToken Token de refresco (expira en 7 días). Null cuando es refresh flow.
 */
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String? = null
)

/**
 * Convierte AuthResponse a modelo de dominio AuthToken.
 *
 * Calcula la fecha de expiración basándose en:
 * - Access Token: 30 minutos desde ahora
 * - Refresh Token: 7 días desde ahora
 */
fun AuthResponse.toDomain(): AuthToken {
    val now = System.currentTimeMillis()
    return AuthToken(
        accessToken = accessToken,
        refreshToken = refreshToken,
        accessTokenExpiresAt = now + ACCESS_TOKEN_DURATION_MS,
        refreshTokenExpiresAt = refreshToken?.let { now + REFRESH_TOKEN_DURATION_MS }
    )
}

// Constantes de expiración (del backend: AuthController.kt)
private const val ACCESS_TOKEN_DURATION_MS = 30 * 60 * 1000L      // 30 minutos
private const val REFRESH_TOKEN_DURATION_MS = 7 * 24 * 60 * 60 * 1000L  // 7 días
