package com.agusstkd.goodlife.domain.usecase.session

import com.agusstkd.goodlife.core.storage.TokenManager

/**
 * Verifica si existe una sesión válida (access o refresh token vigentes).
 *
 * Se usa en el Splash para decidir la ruta inicial:
 * - true  → navegar directo a Main (auto-login)
 * - false → navegar a Login
 *
 * El constructor de tests recibe un lambda para no depender de [TokenManager]
 * (Android) en JVM unit tests.
 *
 * @param hasValidSession Comprueba si hay tokens vigentes.
 */
class CheckSessionUseCase(
    private val hasValidSession: () -> Boolean,
) {
    constructor(tokenManager: TokenManager) : this({ tokenManager.hasValidToken() })

    /**
     * @return `true` si hay sesión activa, `false` si no.
     */
    operator fun invoke(): Boolean = hasValidSession()
}
