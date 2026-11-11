package com.agusstkd.goodlife.domain.usecase.home

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.home.result.GetCurrentUserResult
import kotlinx.coroutines.withContext

/**
 * Caso de uso para obtener el usuario actualmente logueado.
 *
 * Recupera el usuario desde el caché local (Room).
 *
 * @param authRepository Repositorio de autenticación
 * @param dispatcher Proveedor de dispatchers; el UseCase es dueño del IO.
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository,
    private val dispatcher: DispatcherProvider,
) {
    /**
     * Ejecuta el caso de uso.
     *
     * @return Usuario persistido, o [GetCurrentUserResult.Unavailable] si no hay sesión.
     */
    suspend operator fun invoke(): GetCurrentUserResult = withContext(dispatcher.io) {
        when (val result = authRepository.getCurrentUser()) {
            is Result.Success -> GetCurrentUserResult.Success(result.data)
            is Result.Error -> GetCurrentUserResult.Unavailable
        }
    }
}
