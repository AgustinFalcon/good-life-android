package com.agusstkd.goodlife.domain.usecase.home

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.User
import com.agusstkd.goodlife.domain.repository.AuthRepository

/**
 * Caso de uso para obtener el usuario actualmente logueado.
 *
 * Recupera el usuario desde el caché local (Room).
 * Se usa en pantallas que necesitan mostrar datos del usuario.
 *
 * @param authRepository Repositorio de autenticación
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    /**
     * Ejecuta el caso de uso.
     *
     * @return Result<User> con el usuario o error si no hay sesión
     */
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }
}