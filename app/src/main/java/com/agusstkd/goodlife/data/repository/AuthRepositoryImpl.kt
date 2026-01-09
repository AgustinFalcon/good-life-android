package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import kotlinx.coroutines.delay

/**
 * Implementación del repositorio de autenticación.
 *
 * ## Implementación actual: FAKE/MOCK
 * Esta es una implementación temporal para desarrollo.
 * Simula llamadas al servidor con delays.
 *
 * ## Implementación futura:
 * - Conectar con Retrofit para llamadas al backend
 * - Usar Room para caché local del usuario
 * - Implementar refresh tokens
 *
 * ## Credenciales de prueba:
 * - Email: agustin / agustin@gmail.com
 * - Password: 1234
 */
class AuthRepositoryImpl : AuthRepository {

    // Usuario mock para desarrollo
    private var currentUser: User? = null

    override suspend fun login(email: String, password: String): Result<User> {
        // Simular delay de red
        delay(NETWORK_DELAY)

        // Credenciales de prueba
        val validCredentials = listOf(
            Pair("agustin", "1234"),
            Pair("agustin@gmail.com", "1234")
        )

        val isValid = validCredentials.any { (validEmail, validPassword) ->
            email.equals(validEmail, ignoreCase = true) && password == validPassword
        }

        return if (isValid) {
            val user = User(
                id = "user_001",
                email = email,
                name = "Agustín",
                profileImageUrl = null
            )
            currentUser = user
            Result.Success(user)
        } else {
            Result.Error(
                exception = IllegalArgumentException("Credenciales inválidas"),
                message = "El usuario o la contraseña son incorrectos"
            )
        }
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        // Simular delay de red
        delay(NETWORK_DELAY)

        // Simular registro exitoso
        val user = User(
            id = "user_${System.currentTimeMillis()}",
            email = email,
            name = name,
            profileImageUrl = null
        )
        currentUser = user
        return Result.Success(user)
    }

    override suspend fun logout(): Result<Unit> {
        delay(NETWORK_DELAY / 2)
        currentUser = null
        return Result.Success(Unit)
    }

    override suspend fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    override suspend fun getCurrentUser(): Result<User> {
        return currentUser?.let {
            Result.Success(it)
        } ?: Result.Error(
            exception = IllegalStateException("No hay sesión activa"),
            message = "No hay usuario autenticado"
        )
    }

    companion object {
        private const val NETWORK_DELAY = 1500L // 1.5 segundos para simular red
    }
}
