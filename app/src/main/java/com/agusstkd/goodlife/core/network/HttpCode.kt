package com.agusstkd.goodlife.core.network

/**
 * Códigos HTTP estándar usados en la API de GoodLife.
 *
 * Corresponden a los códigos del backend (HttpCode.kt).
 *
 * @property code Código numérico HTTP
 */
enum class HttpCode(val code: Int) {
    // 2xx - Éxito
    SUCCESS(200),
    CREATED(201),
    NO_CONTENT(204),

    // 4xx - Errores del cliente
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),

    // 5xx - Errores del servidor
    INTERNAL_SERVER_ERROR(500);

    companion object {
        /**
         * Obtiene el HttpCode correspondiente a un código numérico.
         *
         * @param code Código HTTP numérico
         * @return HttpCode correspondiente o null si no existe
         */
        fun fromCode(code: Int): HttpCode? = entries.find { it.code == code }

        /**
         * Verifica si un código indica éxito (2xx).
         */
        fun isSuccess(code: Int): Boolean = code in 200..299

        /**
         * Verifica si un código indica error del cliente (4xx).
         */
        fun isClientError(code: Int): Boolean = code in 400..499

        /**
         * Verifica si un código indica error del servidor (5xx).
         */
        fun isServerError(code: Int): Boolean = code in 500..599
    }
}
