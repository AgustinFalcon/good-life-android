package com.agusstkd.goodlife.domain.usecase.session

import com.agusstkd.goodlife.core.storage.TokenManager

/**
 * Verifica si existe una sesión válida (access o refresh token vigentes).
 *
 * Se usa en el Splash para decidir la ruta inicial:
 * - true  → navegar directo a Main (auto-login)
 * - false → navegar a Login
 *
 * @param tokenManager Gestión de tokens JWT
 */
class CheckSessionUseCase(
    private val tokenManager: TokenManager
) {
    operator fun invoke(): Boolean = tokenManager.hasValidToken()
}
