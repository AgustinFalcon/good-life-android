package com.agusstkd.goodlife.domain.usecase.login

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase

/**
 * Caso de uso para ejecutar el login.
 *
 * ## Flujo:
 * 1. Validar email/usuario
 * 2. Validar contraseña
 * 3. Autenticar con el repositorio
 */
class LoginUseCase(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val authRepository: AuthRepository,
    private val language: AppLanguage
) {

    suspend operator fun invoke(email: String, password: String): LoginResult {
        val emailValidation = validateEmail(email)
        if (!emailValidation.isValid) {
            return LoginResult.ValidationError(
                emailError = emailValidation.errorMessage,
                passwordError = null
            )
        }

        val passwordValidation = validatePassword(password)
        if (!passwordValidation.isValid) {
            return LoginResult.ValidationError(
                emailError = null,
                passwordError = passwordValidation.errorMessage
            )
        }

        return when (val result = authRepository.login(email, password)) {
            is Result.Success -> LoginResult.Success(result.data)
            is Result.Error -> LoginResult.Error(
                result.message ?: language.errorTexts.loginError
            )
            is Result.Loading -> LoginResult.Error(language.errorTexts.unexpectedState)
        }
    }
}

sealed interface LoginResult {
    data class Success(val user: User) : LoginResult
    data class ValidationError(
        val emailError: String?,
        val passwordError: String?
    ) : LoginResult
    data class Error(val message: String) : LoginResult
}
