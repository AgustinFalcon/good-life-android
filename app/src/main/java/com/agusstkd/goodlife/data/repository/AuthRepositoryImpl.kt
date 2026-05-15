package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.storage.TokenManager
import com.agusstkd.goodlife.core.util.JwtUtils
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.data.local.entity.toDomain
import com.agusstkd.goodlife.data.local.entity.toEntity
import com.agusstkd.goodlife.data.remote.datasource.remote.AuthRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.response.toDomain
import com.agusstkd.goodlife.domain.exception.NoSessionException
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository

/**
 * Implementación del repositorio de autenticación.
 *
 * Conecta con:
 * - [AuthRemoteDataSource]: Para llamadas HTTP al backend
 * - [TokenManager]: Para persistir tokens JWT
 * - [UserDao]: Para persistir usuario en Room
 *
 * ## Flujo de Login:
 * 1. Llama al DataSource (HTTP request)
 * 2. Si éxito: guarda tokens + extrae userId del JWT + guarda usuario en Room
 * 3. Retorna Result.Success con User
 *
 * ## Flujo de Logout:
 * 1. Limpia tokens de SharedPrefs
 * 2. Elimina usuario de Room
 *
 * ## Nota sobre datos del usuario:
 * Actualmente el backend solo retorna tokens en el login.
 * El userId se extrae del JWT (claim "sub").
 * El username se deriva del email/username ingresado.
 *
 * TODO: Implementar endpoint GET /api/v1/me en backend para obtener datos completos del usuario.
 *
 * @param remoteDataSource DataSource para llamadas HTTP
 * @param tokenManager Gestor de tokens JWT
 * @param userDao DAO para persistir usuario
 */
class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return when (val loginResult = remoteDataSource.login(username = email, password = password)) {
            is Result.Success -> {
                val authToken = loginResult.data.toDomain()
                tokenManager.saveTokens(authToken)

                val user = buildUserFromLogin(authToken.accessToken, email)
                userDao.insertUser(user.toEntity())

                Result.Success(user)
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
            is Result.Success -> {
                val user = registerResult.data.toDomain()
                // No guardamos en Room aquí porque el usuario aún no ha hecho login
                // Después del registro exitoso, el usuario debe hacer login
                Result.Success(user)
            }
            is Result.Error -> registerResult
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            // Notify backend to revoke refresh tokens (best-effort — always clear local state)
            try { authRemoteDataSource.logout() } catch (_: Exception) { /* non-fatal */ }
            tokenManager.clearTokens()
            userDao.deleteUser()
            Result.Success(Unit)
        } catch (e: Exception) {
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
            Result.Error(
                exception = NoSessionException("No hay usuario autenticado")
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Construye un User a partir de los datos disponibles tras el login.
     *
     * Como el backend solo retorna tokens, extraemos:
     * - userId: Del claim "sub" del JWT
     * - name: Del username/email ingresado (capitalizado)
     *
     * @param accessToken Token JWT del login
     * @param usernameOrEmail Username o email usado para login
     * @return User con los datos disponibles
     */
    private fun buildUserFromLogin(accessToken: String, usernameOrEmail: String): User {
        val userId = JwtUtils.extractSubject(accessToken) ?: UNKNOWN_USER_ID
        val displayName = formatDisplayName(usernameOrEmail)

        return User(
            id = userId,
            email = usernameOrEmail,
            name = displayName,
            profileImageUrl = null
        )
    }

    /**
     * Formatea el nombre para mostrar.
     *
     * Si es email: toma la parte antes del @ y capitaliza.
     * Si es username: capitaliza la primera letra.
     *
     * @param input Email o username
     * @return Nombre formateado para mostrar
     */
    private fun formatDisplayName(input: String): String {
        val baseName = if (input.contains("@")) {
            input.substringBefore("@")
        } else {
            input
        }
        return baseName.replaceFirstChar { it.uppercase() }
    }

    companion object {
        private const val UNKNOWN_USER_ID = "unknown"
    }
}
