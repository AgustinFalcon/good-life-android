package com.agusstkd.goodlife.data.remote.dto.response

import kotlinx.serialization.Serializable

/**
 * Wrapper genérico para las respuestas del backend.
 *
 * Todas las respuestas del API siguen esta estructura:
 * ```json
 * {
 *   "code": 200,
 *   "data": { ... },
 *   "message": null
 * }
 * ```
 *
 * @param T El tipo de dato contenido en [data]
 */
@Serializable
data class BaseResponse<T>(
    val code: Int,
    val data: T? = null,
    val message: String? = null
) {
    val isSuccess: Boolean get() = code in 200..299
    val isError: Boolean get() = !isSuccess
}
