package com.agusstkd.goodlife.core.network

/**
 * Excepciones personalizadas para errores de la API.
 *
 * Permite identificar el tipo de error basándose en el código HTTP.
 */
sealed class ApiException(
    override val message: String,
    val code: Int
) : Exception(message) {

    /**
     * Error 400 - Bad Request
     * Datos enviados inválidos o incompletos.
     */
    class BadRequestException(message: String) : ApiException(message, HttpCode.BAD_REQUEST.code)

    /**
     * Error 401 - Unauthorized
     * Credenciales inválidas o token expirado.
     */
    class UnauthorizedException(message: String) : ApiException(message, HttpCode.UNAUTHORIZED.code)

    /**
     * Error 403 - Forbidden
     * Sin permisos para acceder al recurso.
     */
    class ForbiddenException(message: String) : ApiException(message, HttpCode.FORBIDDEN.code)

    /**
     * Error 404 - Not Found
     * Recurso no encontrado.
     */
    class NotFoundException(message: String) : ApiException(message, HttpCode.NOT_FOUND.code)

    /**
     * Error 500 - Internal Server Error
     * Error interno del servidor.
     */
    class ServerException(message: String) : ApiException(message, HttpCode.INTERNAL_SERVER_ERROR.code)

    /**
     * Error desconocido con código específico.
     */
    class UnknownApiException(message: String, code: Int) : ApiException(message, code)

    companion object {
        /**
         * Crea la excepción apropiada según el código HTTP.
         *
         * @param code Código HTTP
         * @param message Mensaje de error
         * @return ApiException correspondiente al código
         */
        fun fromCode(code: Int, message: String?): ApiException {
            val errorMessage = message ?: "Error desconocido"
            return when (code) {
                HttpCode.BAD_REQUEST.code -> BadRequestException(errorMessage)
                HttpCode.UNAUTHORIZED.code -> UnauthorizedException(errorMessage)
                HttpCode.FORBIDDEN.code -> ForbiddenException(errorMessage)
                HttpCode.NOT_FOUND.code -> NotFoundException(errorMessage)
                HttpCode.INTERNAL_SERVER_ERROR.code -> ServerException(errorMessage)
                else -> UnknownApiException(errorMessage, code)
            }
        }
    }
}
