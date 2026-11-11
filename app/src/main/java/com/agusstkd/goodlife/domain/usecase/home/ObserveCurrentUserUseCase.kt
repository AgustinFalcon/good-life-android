package com.agusstkd.goodlife.domain.usecase.home

import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observa el usuario autenticado persistido. Emite `null` si no hay sesión.
 */
class ObserveCurrentUserUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Flow<User?> = authRepository.observeCurrentUser()
}
