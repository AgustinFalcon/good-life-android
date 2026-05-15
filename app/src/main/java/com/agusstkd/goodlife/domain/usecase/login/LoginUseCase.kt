package com.agusstkd.goodlife.domain.usecase.login

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.repository.AuthRepository
import com.agusstkd.goodlife.domain.usecase.validation.ValidateEmailUseCase
import com.agusstkd.goodlife.domain.usecase.validation.ValidatePasswordUseCase
import kotlinx.coroutines.withContext

/**
 * Caso de uso para ejecutar el login.
 *
 * ## Responsabilidades
 * - Validar email y contraseña antes de hacer la request.
 * - Cambiar el dispatcher a IO (el ViewModel no especifica dispatcher).
 * - Convertir el [Result] del repositorio a [LoginResult].
 *
 * ## Flujo
 * 1. Validar email/usuario
 * 2. Validar contraseña
 * 3. Autenticar con el repositorio (red, IO)
 *
 * ## Por qué el dispatcher vive aquí
 * El UseCase sabe que necesita IO para la llamada de red.
 * El ViewModel solo hace `viewModelScope.launch { }` sin especificar dispatcher.
 */
class LoginUseCase(
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val authRepository: AuthRepository,
    private val dispatcher: DispatcherProvider,
    private val language: AppLanguage
) {

    /**
     * Ejecuta el proceso de login.
     *
     * @param email Email o nombre de usuario.
     * @param password Contraseña del usuario.
     * @return [LoginResult] con el subtipo correspondiente al resultado.
     */
    suspend operator fun invoke(email: String, password: String): LoginResult {
        return withContext(dispatcher.io) {
            val emailValidation = validateEmail(email)
            if (!emailValidation.isValid) {
                return@withContext LoginResult.ValidationError(
                    emailError = emailValidation.errorMessage,
                    passwordError = null
                )
            }

            val passwordValidation = validatePassword(password)
            if (!passwordValidation.isValid) {
                return@withContext LoginResult.ValidationError(
                    emailError = null,
                    passwordError = passwordValidation.errorMessage
                )
            }

            when (val result = authRepository.login(email, password)) {
                is Result.Success -> LoginResult.Success(result.data)
                is Result.Error   -> LoginResult.Error(
                    result.message ?: language.errorTexts.loginError
                )
            }
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
