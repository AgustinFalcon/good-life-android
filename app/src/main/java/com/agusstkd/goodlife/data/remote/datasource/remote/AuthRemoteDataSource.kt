package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.auth.AuthApiService
import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.dto.request.auth.RegisterRequest
import com.agusstkd.goodlife.data.remote.dto.response.AuthResponse
import com.agusstkd.goodlife.data.remote.dto.response.RegisterResponse

/**
 * DataSource remoto para operaciones de autenticación.
 *
 * Responsabilidades:
 * - Ejecutar llamadas HTTP al backend
 * - Interpretar códigos de respuesta (code en BaseResponse)
 * - Convertir respuestas a Result<T>
 *
 * @param apiService Servicio Retrofit para las llamadas HTTP de autenticación
 */
class AuthRemoteDataSource(
    private val apiService: AuthApiService
) {

    /**
     * Realiza login con credenciales.
     *
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Result con AuthResponse en caso de éxito
     */
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return executeApiCall {
            apiService.login(
                grantType = "password",
                username = username,
                password = password
            )
        }
    }

    /**
     * Renueva el access token usando el refresh token.
     *
     * @param refreshToken Token de refresco válido
     * @return Result con AuthResponse (nuevo accessToken)
     */
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return executeApiCall {
            apiService.refreshToken(
                grantType = "refreshToken",
                refreshToken = refreshToken
            )
        }
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param username Nombre de usuario
     * @param email Correo electrónico
     * @param password Contraseña
     * @return Result con RegisterResponse en caso de éxito
     */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<RegisterResponse> {
        return executeApiCall {
            apiService.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )
            )
        }
    }

    /** Notifica al backend que revoque los refresh tokens del usuario. */
    suspend fun logout(): Result<Unit> = executeApiCall { apiService.logout() }
}
