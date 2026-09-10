package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.data.local.dao.DailyDao
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.data.local.entity.toDomain
import com.agusstkd.goodlife.data.local.entity.toEntity
import com.agusstkd.goodlife.data.remote.datasource.remote.AuthRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.response.toDomain
import com.agusstkd.goodlife.domain.exception.NoSessionException
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage

/**
 * Implementación del repositorio de autenticación.
 *
 * El perfil local se construye únicamente con la fuente de verdad remota
 * (`GET /api/v1/me`) después del login. El logout intenta revocar los refresh
 * tokens y, aun si el backend no responde, siempre elimina los datos locales
 * sensibles para no dejar una sesión utilizable en este dispositivo.
 */
class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val userDao: UserDao,
    private val dailyDao: DailyDao,
    private val secureCredentialsStorage: SecureCredentialsStorage
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return when (val loginResult = remoteDataSource.login(username = email, password = password)) {
            is Result.Success -> {
                tokenManager.saveTokens(loginResult.data.toDomain())
                when (val profileResult = remoteDataSource.getMe()) {
                    is Result.Success -> {
                        val user = profileResult.data.toDomain()
                        userDao.insertUser(user.toEntity())
                        Result.Success(user)
                    }
                    is Result.Error -> {
                        clearLocalSession()
                        profileResult
                    }
                }
            }
            is Result.Error -> loginResult
        }
    }

    override suspend fun register(username: String, email: String, password: String): Result<User> {
        return when (val registerResult = remoteDataSource.register(
            username = username,
            email = email,
            password = password
        )) {
            is Result.Success -> Result.Success(registerResult.data.toDomain())
            is Result.Error -> registerResult
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val remoteResult = remoteDataSource.logout()
            clearLocalSession()
            remoteResult
        } catch (e: Exception) {
            try {
                clearLocalSession()
            } catch (_: Exception) {
                // Preserve the original failure; the next launch will not consider the session valid.
            }
            Result.Error(Exception("Error al cerrar sesión", e))
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.hasValidToken() && userDao.hasUser() > 0
    }

    override suspend fun getCurrentUser(): Result<User> {
        val userEntity = userDao.getUser()
        return if (userEntity != null) {
            Result.Success(userEntity.toDomain())
        } else {
            Result.Error(NoSessionException("No hay usuario autenticado"))
        }
    }

    private suspend fun clearLocalSession() {
        tokenManager.clearTokens()
        secureCredentialsStorage.clearCredentials()
        dailyDao.clearCache()
        userDao.deleteUser()
    }
}