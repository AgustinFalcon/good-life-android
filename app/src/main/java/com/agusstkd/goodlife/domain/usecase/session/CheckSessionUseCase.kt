package com.agusstkd.goodlife.domain.usecase.session

import com.agusstkd.goodlife.domain.repository.AuthRepository

/**
 * Verifica si hay sesión usable: tokens vigentes **y** usuario local.
 *
 * Se usa en el Splash para decidir la ruta inicial:
 * - true  → navegar directo a Main (auto-login)
 * - false → navegar a Login
 *
 * El constructor de tests recibe un lambda para no depender de Android.
 */
class CheckSessionUseCase(
    private val hasActiveSession: suspend () -> Boolean,
) {
    constructor(authRepository: AuthRepository) : this(authRepository::isLoggedIn)

    suspend operator fun invoke(): Boolean = hasActiveSession()
}
