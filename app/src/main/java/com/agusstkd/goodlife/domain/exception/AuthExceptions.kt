package com.agusstkd.goodlife.domain.exception

/**
 * Excepciones relacionadas con autenticación.
 *
 * Definidas en la capa domain para ser independientes de la implementación.
 * Pueden ser usadas por UseCases y Repository interfaces.
 */

/**
 * Excepción lanzada cuando no hay sesión activa.
 *
 * Se usa cuando:
 * - Se intenta obtener el usuario actual sin estar logueado
 * - Se intenta acceder a recursos protegidos sin sesión
 */
class NoSessionException(
    message: String = "No hay sesión activa"
) : Exception(message)

/**
 * Excepción lanzada cuando las credenciales son inválidas.
 */
class InvalidCredentialsException(
    message: String = "Credenciales inválidas"
) : Exception(message)

/**
 * Excepción lanzada cuando el token ha expirado.
 */
class TokenExpiredException(
    message: String = "La sesión ha expirado"
) : Exception(message)
