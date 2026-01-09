package com.agusstkd.goodlife.data.remote.datasource

import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.network.HttpCode
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.api.GoodLifeApiService
import com.agusstkd.goodlife.data.remote.dto.request.RegisterRequest
import com.agusstkd.goodlife.data.remote.dto.response.AuthResponse
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.RegisterResponse

/**
 * DataSource remoto para operaciones de autenticación.
 *
 * Responsabilidades:
 * - Ejecutar llamadas HTTP al backend
 * - Interpretar códigos de respuesta (code en BaseResponse)
 * - Convertir respuestas a Result<T>
 *
 * @param apiService Servicio Retrofit para las llamadas HTTP
 */
class AuthRemoteDataSource(
    private val apiService: GoodLifeApiService
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

    /**
     * Ejecuta una llamada a la API y procesa la respuesta.
     *
     * Este método:
     * 1. Ejecuta la llamada HTTP
     * 2. Verifica el código de respuesta en BaseResponse.code
     * 3. Convierte a Result.Success o Result.Error según corresponda
     *
     * @param T Tipo de dato esperado en data
     * @param call Bloque suspend que ejecuta la llamada Retrofit
     * @return Result<T> con el resultado de la operación
     */
    private suspend fun <T> executeApiCall(
        call: suspend () -> BaseResponse<T>
    ): Result<T> {
        return try {
            val response = call()
            processResponse(response)
        } catch (e: Exception) {
            // Error de red, timeout, etc.
            Result.Error(
                exception = e,
                message = e.message ?: "Error de conexión"
            )
        }
    }

    /**
     * Procesa la respuesta del backend y la convierte a Result.
     *
     * IMPORTANTE: El mensaje de error viene del backend (response.message).
     * No hardcodeamos mensajes porque el mismo código puede significar cosas diferentes:
     * - 401 puede ser "Credenciales incorrectas" O "Token expirado"
     * - 400 puede ser "Username requerido" O "Email inválido"
     *
     * El backend YA envía el mensaje correcto en cada caso.
     *
     * @param response Respuesta del backend
     * @return Result apropiado según el código
     */
    private fun <T> processResponse(response: BaseResponse<T>): Result<T> {
        val code = response.code

        // Verificar si es código de éxito (2xx)
        return if (HttpCode.isSuccess(code)) {
            // Éxito: extraer data
            response.data?.let {
                Result.Success(it)
            } ?: Result.Error(
                exception = ApiException.fromCode(code, "Error"),
                message = response.message ?: "Error del servidor (código: $code)"
            )
        } else {
            // Error: usar SIEMPRE el mensaje del backend
            // Solo usar fallback genérico si el backend no envió mensaje
            val errorMessage = response.message ?: "Error del servidor (código: $code)"
            Result.Error(
                exception = ApiException.fromCode(code, errorMessage),
                message = errorMessage
            )
        }
    }
}
