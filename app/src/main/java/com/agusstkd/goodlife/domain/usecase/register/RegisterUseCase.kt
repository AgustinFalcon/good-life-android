package com.agusstkd.goodlife.domain.usecase.register

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateUserNameUseCase

/**
 * Orquesta el proceso de registro de usuario.
 *
 * ## Flujo de validación:
 * 1. Valida nombre completo (fullName)
 * 2. Valida nombre de usuario (userName)
 * 3. Valida email
 * 4. Valida contraseña
 * 5. Valida que las contraseñas coincidan
 * 6. Llama al repositorio para registrar
 *
 * ## Uso:
 * ```kotlin
 * val result = registerUseCase(userName, fullName, email, password, confirmPassword)
 * when (result) {
 *     is RegisterResult.Success -> // Usuario registrado
 *     is RegisterResult.ValidationError -> // Error de validación
 *     is RegisterResult.ServerError -> // Error del servidor
 * }
 * ```
 *
 * @property authRepository Repositorio de autenticación
 * @property validateUserName Validador de nombre de usuario
 * @property validateFullName Validador de nombre completo
 * @property validateEmail Validador de email
 * @property validatePassword Validador de contraseña
 * @property validatePasswordMatch Validador de coincidencia de contraseñas
 */
class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val validateUserName: ValidateUserNameUseCase,
    private val validateFullName: ValidateFullNameUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val validatePasswordMatch: ValidatePasswordMatchUseCase
) {

    /**
     * Resultado del registro.
     */
    sealed interface RegisterResult {
        /**
         * Registro exitoso.
         * @property user Usuario creado
         */
        data class Success(val user: User) : RegisterResult

        /**
         * Error de validación en un campo específico.
         * @property field Campo con error
         * @property message Mensaje de error
         */
        data class ValidationError(val field: Field, val message: String) : RegisterResult

        /**
         * Error del servidor.
         * @property message Mensaje de error
         */
        data class ServerError(val message: String) : RegisterResult

        /**
         * Campos que pueden tener errores de validación.
         */
        enum class Field {
            USERNAME,
            FULL_NAME,
            EMAIL,
            PASSWORD,
            CONFIRM_PASSWORD
        }
    }

    /**
     * Ejecuta el proceso de registro.
     *
     * @param userName Nombre de usuario único
     * @param fullName Nombre completo del usuario
     * @param email Correo electrónico
     * @param password Contraseña
     * @param confirmPassword Confirmación de contraseña
     * @return [RegisterResult] con el resultado del registro
     */
    suspend operator fun invoke(
        userName: String,
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterResult {

        // 1. Validar nombre completo (no se envía al backend aún, pero se valida)
        val fullNameResult = validateFullName(fullName)
        if (!fullNameResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.FULL_NAME,
                fullNameResult.errorMessage ?: "Nombre inválido"
            )
        }

        // 2. Validar nombre de usuario
        val userNameResult = validateUserName(userName)
        if (!userNameResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.USERNAME,
                userNameResult.errorMessage ?: "Nombre de usuario inválido"
            )
        }

        // 3. Validar email
        val emailResult = validateEmail(email)
        if (!emailResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.EMAIL,
                emailResult.errorMessage ?: "Email inválido"
            )
        }

        // 4. Validar contraseña
        val passwordResult = validatePassword(password)
        if (!passwordResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.PASSWORD,
                passwordResult.errorMessage ?: "Contraseña inválida"
            )
        }

        // 5. Validar que las contraseñas coincidan
        val matchResult = validatePasswordMatch(password, confirmPassword)
        if (!matchResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.CONFIRM_PASSWORD,
                matchResult.errorMessage ?: "Las contraseñas no coinciden"
            )
        }

        // 6. Llamar al repositorio (username, email, password)
        return when (val result = authRepository.register(
            username = userName,
            email = email,
            password = password
        )) {
            is Result.Success -> RegisterResult.Success(result.data)
            is Result.Error -> RegisterResult.ServerError(
                result.exception.message ?: "Error al registrar"
            )
            is Result.Loading -> RegisterResult.ServerError("Operación en curso")
        }
    }
}