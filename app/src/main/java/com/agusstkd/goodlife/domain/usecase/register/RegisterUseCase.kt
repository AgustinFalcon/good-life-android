package com.agusstkd.goodlife.domain.usecase.register

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.register.result.RegisterResult
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateFullNameUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordMatchUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidateUserNameUseCase
import kotlinx.coroutines.withContext

/**
 * Caso de uso que orquesta el proceso de registro de usuario.
 *
 * ## Responsabilidades
 * - Validar todos los campos del formulario en orden.
 * - Cambiar el dispatcher a IO (el ViewModel no especifica dispatcher).
 * - Convertir el [Result] del repositorio a [RegisterResult].
 *
 * ## Flujo
 * 1. Valida nombre completo
 * 2. Valida nombre de usuario
 * 3. Valida email
 * 4. Valida contraseña
 * 5. Valida coincidencia de contraseñas
 * 6. Registra en backend (solo si todas las validaciones pasan)
 *
 * ## Por qué el dispatcher vive aquí
 * El UseCase sabe que necesita IO para la llamada al repositorio.
 * El ViewModel solo hace `viewModelScope.launch { }` sin especificar dispatcher.
 */
class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val validateUserName: ValidateUserNameUseCase,
    private val validateFullName: ValidateFullNameUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val validatePasswordMatch: ValidatePasswordMatchUseCase,
    private val dispatcher: DispatcherProvider,
    private val language: AppLanguage
) {

    /**
     * Ejecuta el proceso de registro.
     *
     * @param userName Nombre de usuario único.
     * @param fullName Nombre completo del usuario.
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @param confirmPassword Confirmación de contraseña.
     * @return [RegisterResult] con el subtipo correspondiente al resultado.
     */
    suspend operator fun invoke(
        userName: String,
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): RegisterResult {
        return withContext(dispatcher.io) {
            val v = language.validationTexts
            val e = language.errorTexts

            val fullNameResult = validateFullName(fullName)
            if (!fullNameResult.isValid) {
                return@withContext RegisterResult.ValidationError(
                    RegisterResult.Field.FULL_NAME,
                    fullNameResult.errorMessage ?: v.invalidFullName
                )
            }

            val userNameResult = validateUserName(userName)
            if (!userNameResult.isValid) {
                return@withContext RegisterResult.ValidationError(
                    RegisterResult.Field.USERNAME,
                    userNameResult.errorMessage ?: v.invalidUsername
                )
            }

            val emailResult = validateEmail(email)
            if (!emailResult.isValid) {
                return@withContext RegisterResult.ValidationError(
                    RegisterResult.Field.EMAIL,
                    emailResult.errorMessage ?: v.invalidEmail
                )
            }

            val passwordResult = validatePassword(password)
            if (!passwordResult.isValid) {
                return@withContext RegisterResult.ValidationError(
                    RegisterResult.Field.PASSWORD,
                    passwordResult.errorMessage ?: v.invalidPassword
                )
            }

            val matchResult = validatePasswordMatch(password, confirmPassword)
            if (!matchResult.isValid) {
                return@withContext RegisterResult.ValidationError(
                    RegisterResult.Field.CONFIRM_PASSWORD,
                    matchResult.errorMessage ?: v.passwordsDontMatch
                )
            }

            when (val result = authRepository.register(
                username = userName,
                email = email,
                password = password
            )) {
                is Result.Success -> RegisterResult.Success(result.data)
                is Result.Error   -> RegisterResult.ServerError(
                    result.message ?: e.registerError
                )
            }
        }
    }
}
