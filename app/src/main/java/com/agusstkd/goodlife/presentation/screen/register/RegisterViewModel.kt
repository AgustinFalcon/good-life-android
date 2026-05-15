package com.agusstkd.goodlife.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.AuthScreenTexts
import com.agusstkd.goodlife.domain.usecase.register.RegisterUseCase
import com.agusstkd.goodlife.domain.usecase.register.result.RegisterResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.navigateToLoginFromRegister
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiAction
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de registro.
 *
 * ## Responsabilidades
 * - Gestionar el estado del formulario de registro
 * - Procesar acciones del usuario
 * - Delegar validaciones y registro al [RegisterUseCase]
 * - Coordinar la navegación
 *
 * ## Sobre el dispatcher
 * Este ViewModel NO recibe ni usa [DispatcherProvider].
 * El dispatcher IO es responsabilidad del [RegisterUseCase] (decisión arquitectónica #1).
 * Todos los launches usan `viewModelScope.launch { }` sin dispatcher.
 *
 * @property registerUseCase Caso de uso para registro
 * @property navigationController Controlador de navegación
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage
) : ViewModel() {

    val screenTexts: AuthScreenTexts
        get() = AuthScreenTexts(language.authTexts, language.accessibilityTexts)

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Content())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.OnFullNameChange        -> updateFullName(action.value)
            is RegisterUiAction.OnUserNameChange        -> updateUserName(action.value)
            is RegisterUiAction.OnEmailChange           -> updateEmail(action.value)
            is RegisterUiAction.OnPasswordChange        -> updatePassword(action.value)
            is RegisterUiAction.OnConfirmPasswordChange -> updateConfirmPassword(action.value)
            is RegisterUiAction.OnTermsToggle           -> toggleTerms()
            is RegisterUiAction.OnRegisterClick         -> performRegister()
            is RegisterUiAction.OnLoginClick            -> navigateToLogin()
            is RegisterUiAction.OnDismissError          -> dismissError()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ACTUALIZACIÓN DE CAMPOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun updateFullName(value: String) {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        _uiState.value = current.copy(fullName = value, isFullNameError = false, fullNameErrorMessage = null)
    }

    private fun updateUserName(value: String) {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        _uiState.value = current.copy(userName = value, isUserNameError = false, userNameErrorMessage = null)
    }

    private fun updateEmail(value: String) {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        _uiState.value = current.copy(email = value, isEmailError = false, emailErrorMessage = null)
    }

    private fun updatePassword(value: String) {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        val passwordsMatch = current.confirmPassword.isEmpty() || value == current.confirmPassword
        _uiState.value = current.copy(
            password = value,
            isPasswordError = false,
            passwordErrorMessage = null,
            isConfirmPasswordError = !passwordsMatch && current.confirmPassword.isNotEmpty(),
            confirmPasswordErrorMessage = if (!passwordsMatch && current.confirmPassword.isNotEmpty()) {
                language.validationTexts.passwordsDontMatch
            } else null
        )
    }

    private fun updateConfirmPassword(value: String) {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        val passwordsMatch = value.isEmpty() || value == current.password
        _uiState.value = current.copy(
            confirmPassword = value,
            isConfirmPasswordError = !passwordsMatch,
            confirmPasswordErrorMessage = if (!passwordsMatch) language.validationTexts.passwordsDontMatch else null
        )
    }

    private fun toggleTerms() {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        _uiState.value = current.copy(acceptedTerms = !current.acceptedTerms)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REGISTRO
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun performRegister() {
        val current = _uiState.value as? RegisterUiState.Content ?: return

        if (!current.acceptedTerms) {
            _uiState.value = current.copy(errorMessage = language.errorTexts.mustAcceptTerms)
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(isLoading = true, errorMessage = null)

            when (val result = registerUseCase(
                userName = current.userName,
                fullName = current.fullName,
                email = current.email,
                password = current.password,
                confirmPassword = current.confirmPassword
            )) {
                is RegisterResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                    navigationController.navigateToLoginFromRegister()
                }
                is RegisterResult.ValidationError -> {
                    _uiState.value = handleValidationError(current, result)
                }
                is RegisterResult.ServerError -> {
                    _uiState.value = current.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun handleValidationError(
        current: RegisterUiState.Content,
        error: RegisterResult.ValidationError
    ): RegisterUiState.Content {
        return when (error.field) {
            RegisterResult.Field.FULL_NAME -> current.copy(
                isLoading = false, isFullNameError = true, fullNameErrorMessage = error.message
            )
            RegisterResult.Field.USERNAME -> current.copy(
                isLoading = false, isUserNameError = true, userNameErrorMessage = error.message
            )
            RegisterResult.Field.EMAIL -> current.copy(
                isLoading = false, isEmailError = true, emailErrorMessage = error.message
            )
            RegisterResult.Field.PASSWORD -> current.copy(
                isLoading = false, isPasswordError = true, passwordErrorMessage = error.message
            )
            RegisterResult.Field.CONFIRM_PASSWORD -> current.copy(
                isLoading = false, isConfirmPasswordError = true, confirmPasswordErrorMessage = error.message
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // NAVEGACIÓN / ERRORES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun navigateToLogin() {
        navigationController.navigateToLoginFromRegister()
    }

    private fun dismissError() {
        val current = _uiState.value as? RegisterUiState.Content ?: return
        _uiState.value = current.copy(errorMessage = null)
    }
}
