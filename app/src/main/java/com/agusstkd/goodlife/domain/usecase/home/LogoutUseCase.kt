package com.agusstkd.goodlife.domain.usecase.home

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.AuthRepository

/**
 * Caso de uso para cerrar sesión.
 *
 * Limpia los tokens y datos del usuario del almacenamiento local.
 *
 * @param authRepository Repositorio de autenticación
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Ejecuta el caso de uso de logout.
     *
     * @return Result<Unit> indicando éxito o error
     */
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}