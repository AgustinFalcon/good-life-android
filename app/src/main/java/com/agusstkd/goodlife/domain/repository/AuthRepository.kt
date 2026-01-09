package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.User

/**
 * Repositorio de autenticación.
 *
 * Define el contrato para operaciones de autenticación.
 * La implementación está en la capa de datos.
 *
 * ## Principio de Inversión de Dependencias:
 * - Esta interfaz pertenece al DOMINIO
 * - La implementación (AuthRepositoryImpl) pertenece a DATA
 * - El ViewModel solo conoce esta interfaz, no la implementación
 */
interface AuthRepository {

    /**
     * Inicia sesión con email y contraseña.
     *
     * @param email Correo electrónico o nombre de usuario
     * @param password Contraseña
     * @return Result<User> con el usuario autenticado o error
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Registra un nuevo usuario.
     *
     * @param email Correo electrónico
     * @param password Contraseña
     * @param name Nombre del usuario
     * @return Result<User> con el usuario registrado o error
     */
    suspend fun register(email: String, password: String, name: String): Result<User>

    /**
     * Cierra la sesión del usuario actual.
     *
     * @return Result<Unit> indicando éxito o error
     */
    suspend fun logout(): Result<Unit>

    /**
     * Verifica si hay un usuario autenticado.
     *
     * @return true si hay sesión activa, false si no
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * Obtiene el usuario actual autenticado.
     *
     * @return Result<User> con el usuario o error si no hay sesión
     */
    suspend fun getCurrentUser(): Result<User>
}
