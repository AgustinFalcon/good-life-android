package com.agusstkd.goodlife.domain.usecase.login

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase

/**
 * Caso de uso para ejecutar el login.
 *
 * ## Responsabilidades:
 * 1. Validar los campos de entrada usando los UseCases de validación
 * 2. Llamar al repositorio para autenticar
 * 3. Retornar el resultado (User o Error)
 *
 * ## Flujo:
 * ```
 * LoginUseCase
 *     ├── ValidateEmailUseCase
 *     ├── ValidatePasswordUseCase
 *     └── AuthRepository.login()
 * ```
 *
 * @property validateEmail UseCase para validar email/usuario
 * @property validatePassword UseCase para validar contraseña
 * @property authRepository Repositorio de autenticación
 */
class LoginUseCase(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val authRepository: AuthRepository
) {

    /**
     * Ejecuta el proceso de login.
     *
     * @param email Correo electrónico o nombre de usuario
     * @param password Contraseña
     * @return Result<User> con el usuario autenticado o error
     */
    suspend operator fun invoke(email: String, password: String): LoginResult {
        // Paso 1: Validar email
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            return LoginResult.ValidationError(
                emailError = emailValidation.errorMessage,
                passwordError = null
            )
        }

        // Paso 2: Validar password
        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            return LoginResult.ValidationError(
                emailError = null,
                passwordError = passwordValidation.errorMessage
            )
        }

        // Paso 3: Ejecutar login en el repositorio
        return when (val result = authRepository.login(email, password)) {
            is Result.Success -> LoginResult.Success(result.data)
            is Result.Error -> LoginResult.Error(
                result.message ?: "Error al iniciar sesión"
            )
            is Result.Loading -> LoginResult.Error("Estado inesperado")
        }
    }
}

/**
 * Resultado del proceso de login.
 *
 * Encapsula todos los posibles resultados:
 * - Success: Login exitoso con el usuario
 * - ValidationError: Errores de validación en los campos
 * - Error: Error de autenticación o de red
 */
sealed interface LoginResult {

    /**
     * Login exitoso.
     *
     * @property user Usuario autenticado
     */
    data class Success(val user: User) : LoginResult

    /**
     * Error de validación en los campos.
     *
     * @property emailError Mensaje de error del email (null si es válido)
     * @property passwordError Mensaje de error del password (null si es válido)
     */
    data class ValidationError(
        val emailError: String?,
        val passwordError: String?
    ) : LoginResult

    /**
     * Error de autenticación o de red.
     *
     * @property message Mensaje de error
     */
    data class Error(val message: String) : LoginResult
}
