package com.agusstkd.goodlife.domain.usecase.home

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.home.result.LogoutResult
import kotlinx.coroutines.withContext

/**
 * Caso de uso para cerrar sesión.
 *
 * Limpia los tokens y datos del usuario del almacenamiento local.
 *
 * @param authRepository Repositorio de autenticación
 * @param dispatcher Proveedor de dispatchers; el UseCase es dueño del IO.
 */
class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val dispatcher: DispatcherProvider,
) {
    /**
     * Ejecuta el caso de uso de logout.
     *
     * @return [LogoutResult] indicando si el repositorio cerró la sesión.
     */
    suspend operator fun invoke(): LogoutResult = withContext(dispatcher.io) {
        when (authRepository.logout()) {
            is Result.Success -> LogoutResult.Success
            is Result.Error -> LogoutResult.Failed
        }
    }
}
