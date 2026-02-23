package com.agusstkd.goodlife.domain.usecase.register

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateUserNameUseCase

/**
 * Orquesta el proceso de registro de usuario.
 *
 * ## Flujo:
 * 1. Valida nombre completo
 * 2. Valida nombre de usuario
 * 3. Valida email
 * 4. Valida contraseña
 * 5. Valida coincidencia de contraseñas
 * 6. Registra en backend
 */
class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val validateUserName: ValidateUserNameUseCase,
    private val validateFullName: ValidateFullNameUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val validatePasswordMatch: ValidatePasswordMatchUseCase,
    private val language: AppLanguage
) {

    sealed interface RegisterResult {
        data class Success(val user: User) : RegisterResult
        data class ValidationError(val field: Field, val message: String) : RegisterResult
        data class ServerError(val message: String) : RegisterResult

        enum class Field {
            USERNAME, FULL_NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD
        }
    }

    suspend operator fun invoke(
        userName: String,
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterResult {
        val v = language.validationTexts
        val e = language.errorTexts

        val fullNameResult = validateFullName(fullName)
        if (!fullNameResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.FULL_NAME,
                fullNameResult.errorMessage ?: v.invalidFullName
            )
        }

        val userNameResult = validateUserName(userName)
        if (!userNameResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.USERNAME,
                userNameResult.errorMessage ?: v.invalidUsername
            )
        }

        val emailResult = validateEmail(email)
        if (!emailResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.EMAIL,
                emailResult.errorMessage ?: v.invalidEmail
            )
        }

        val passwordResult = validatePassword(password)
        if (!passwordResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.PASSWORD,
                passwordResult.errorMessage ?: v.invalidPassword
            )
        }

        val matchResult = validatePasswordMatch(password, confirmPassword)
        if (!matchResult.isValid) {
            return RegisterResult.ValidationError(
                RegisterResult.Field.CONFIRM_PASSWORD,
                matchResult.errorMessage ?: v.passwordsDontMatch
            )
        }

        return when (val result = authRepository.register(
            username = userName,
            email = email,
            password = password
        )) {
            is Result.Success -> RegisterResult.Success(result.data)
            is Result.Error -> RegisterResult.ServerError(
                result.message ?: e.registerError
            )
            is Result.Loading -> RegisterResult.ServerError(e.operationInProgress)
        }
    }
}
