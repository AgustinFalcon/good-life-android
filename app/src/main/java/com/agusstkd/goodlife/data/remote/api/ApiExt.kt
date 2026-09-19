package com.agusstkd.goodlife.data.remote.api

import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.network.HttpCode
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import kotlinx.coroutines.CancellationException

/**
 * Extensiones para manejo uniforme de llamadas a la API.
 *
 * ## Arquitectura:
 * Este archivo sigue el patrón de "Extension Functions" en lugar de un BaseDataSource.
 *
 * ## ¿Por qué Extension Functions y no BaseDataSource?
 *
 * ### ✅ CORRECTO (actual):
 * ```kotlin
 * class AuthRemoteDataSource(private val apiService: AuthApiService) {
 *     suspend fun login(...): Result<AuthResponse> {
 *         return executeApiCall { apiService.login(...) }
 *     }
 * }
 * ```
 * **Ventajas:**
 * - No requiere herencia (composición > herencia)
 * - Fácil de testear (mock del apiService)
 * - Flexible (cada DataSource puede customizar executeApiCall si es necesario)
 * - Funcional y conciso
 * - Sigue principio DRY (Don't Repeat Yourself)
 *
 * ### ❌ INCORRECTO (BaseDataSource):
 * ```kotlin
 * abstract class BaseDataSource(private val apiService: AuthApiService) {
 *     suspend fun <T> executeApiCall(...): Result<T> { ... }
 * }
 *
 * class AuthRemoteDataSource(apiService: AuthApiService) : BaseDataSource(apiService) {
 *     suspend fun login(...): Result<AuthResponse> {
 *         return executeApiCall { apiService.login(...) }
 *     }
 * }
 * ```
 * **Desventajas:**
 * - Requiere herencia (acoplamiento)
 * - Menos flexible
 * - Más verboso
 * - Dificulta testing (mock de clase abstracta)
 *
 * ## Otras alternativas profesionales:
 * 1. **Result builders** (tipo Arrow-kt Either):
 *    ```kotlin
 *    suspend fun <T> safeApiCall(block: suspend () -> T): Result<T>
 *    ```
 * 2. **Delegación con inline functions**:
 *    ```kotlin
 *    inline fun <T, R> T.runCatchingApi(block: T.() -> R): Result<R>
 *    ```
 * 3. **Adapter Pattern con Interceptor**:
 *    - Retrofit CallAdapter que convierte `Call<BaseResponse<T>>` → `Result<T>`
 *    - Más complejo, útil solo para apps muy grandes
 *
 * ## Conclusión:
 * El patrón actual (Extension Functions) es el más recomendado para proyectos KMP.
 * Es usado por Google, JetBrains y apps profesionales de Android.
 *
 * @see executeApiCall
 * @see processResponse
 */


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
public suspend fun <T> executeApiCall(
    call: suspend () -> BaseResponse<T>
): Result<T> {
    return try {
        val response = call()
        processResponse(response)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // Error de red, timeout, etc.
        Result.Error(
            exception = Exception(e.message ?: "Error de conexión", e)
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
public fun <T> processResponse(response: BaseResponse<T>): Result<T> {
    val code = response.code

    // Verificar si es código de éxito (2xx)
    return if (HttpCode.isSuccess(code)) {
        // Éxito: extraer data
        response.data?.let {
            Result.Success(it)
        } ?: run {
            val errorMessage = response.message ?: "Error del servidor (código: $code)"
            Result.Error(
                exception = ApiException.fromCode(
                    code = code,
                    message = errorMessage
                )
            )
        }
    } else {
        // Error: usar SIEMPRE el mensaje del backend
        // Solo usar fallback genérico si el backend no envió mensaje
        val errorMessage = response.message ?: "Error del servidor (código: $code)"
        Result.Error(
            exception = ApiException.fromCode(code, errorMessage)
        )
    }
}

/**
 * Executes an endpoint whose resource may be absent despite a successful HTTP response.
 * This nullable contract is opt-in; standard [executeApiCall] keeps rejecting absent data.
 */
public suspend fun <T> executeOptionalApiCall(
    call: suspend () -> BaseResponse<T?>,
): Result<T?> = try {
    processOptionalResponse(call())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.Error(exception = Exception(e.message ?: "Error de conexión", e))
}

/** Preserves a successful absent payload for an explicitly optional endpoint. */
public fun <T> processOptionalResponse(response: BaseResponse<T?>): Result<T?> =
    if (HttpCode.isSuccess(response.code)) {
        Result.Success(response.data)
    } else {
        Result.Error(
            ApiException.fromCode(
                response.code,
                response.message ?: "Error del servidor (código: ${response.code})",
            ),
        )
    }
