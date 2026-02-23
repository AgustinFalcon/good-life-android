package com.agusstkd.goodlife.presentation.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.AuthScreenTexts
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.domain.usecase.register.RegisterUseCase
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
 * ## Responsabilidades:
 * - Gestionar el estado del formulario de registro
 * - Procesar acciones del usuario
 * - Ejecutar validaciones y registro via [RegisterUseCase]
 * - Coordinar la navegación
 *
 * ## Estado:
 * - [RegisterUiState.Content]: Formulario activo
 * - [RegisterUiState.Loading]: Cargando
 * - [RegisterUiState.Success]: Registro exitoso
 *
 * @property registerUseCase Caso de uso para registro
 * @property navigationController Controlador de navegación
 * @property dispatcherProvider Proveedor de dispatchers
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val navigationController: ComposeNavigationController,
    private val dispatcherProvider: DispatcherProvider,
    private val language: AppLanguage
) : ViewModel() {

    val screenTexts: AuthScreenTexts
        get() = AuthScreenTexts(language.authTexts, language.accessibilityTexts)

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Content())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ACCIONES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Procesa las acciones del usuario.
     *
     * @param action Acción a procesar
     */
    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.OnFullNameChange -> updateFullName(action.value)
            is RegisterUiAction.OnUserNameChange -> updateUserName(action.value)
            is RegisterUiAction.OnEmailChange -> updateEmail(action.value)
            is RegisterUiAction.OnPasswordChange -> updatePassword(action.value)
            is RegisterUiAction.OnConfirmPasswordChange -> updateConfirmPassword(action.value)
            is RegisterUiAction.OnTermsToggle -> toggleTerms()
            is RegisterUiAction.OnRegisterClick -> performRegister()
            is RegisterUiAction.OnLoginClick -> navigateToLogin()
            is RegisterUiAction.OnDismissError -> dismissError()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ACTUALIZACIÓN DE CAMPOS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Actualiza el nombre completo y limpia errores.
     */
    private fun updateFullName(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(
            fullName = value,
            isFullNameError = false,
            fullNameErrorMessage = null
        )
    }

    /**
     * Actualiza el nombre de usuario y limpia errores.
     */
    private fun updateUserName(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(
            userName = value,
            isUserNameError = false,
            userNameErrorMessage = null
        )
    }

    /**
     * Actualiza el email y limpia errores.
     */
    private fun updateEmail(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(
            email = value,
            isEmailError = false,
            emailErrorMessage = null
        )
    }

    /**
     * Actualiza la contraseña y limpia errores.
     * También re-valida la confirmación si ya tiene valor.
     */
    private fun updatePassword(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return

        // Si confirmPassword ya tiene valor, verificar si coinciden
        val passwordsMatch = current.confirmPassword.isEmpty() ||
                value == current.confirmPassword

        _uiState.value = current.copy(
            password = value,
            isPasswordError = false,
            passwordErrorMessage = null,
            // Actualizar error de confirmación si ya escribió algo
            isConfirmPasswordError = !passwordsMatch && current.confirmPassword.isNotEmpty(),
            confirmPasswordErrorMessage = if (!passwordsMatch && current.confirmPassword.isNotEmpty()) {
                language.validationTexts.passwordsDontMatch
            } else null
        )
    }

    /**
     * Actualiza la confirmación de contraseña.
     * Valida en tiempo real si coincide con la contraseña.
     */
    private fun updateConfirmPassword(value: String) {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return

        // Validar si las contraseñas coinciden
        val passwordsMatch = value.isEmpty() || value == current.password

        _uiState.value = current.copy(
            confirmPassword = value,
            isConfirmPasswordError = !passwordsMatch,
            confirmPasswordErrorMessage = if (!passwordsMatch) {
                language.validationTexts.passwordsDontMatch
            } else null
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // TOGGLES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Alterna la aceptación de términos y condiciones.
     */
    private fun toggleTerms() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(acceptedTerms = !current.acceptedTerms)
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REGISTRO
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Ejecuta el proceso de registro.
     *
     * Primero verifica que los términos estén aceptados,
     * luego delega la validación y registro al UseCase.
     */
    private fun performRegister() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return

        // Verificar términos aceptados
        if (!current.acceptedTerms) {
            _uiState.value = current.copy(
                errorMessage = language.errorTexts.mustAcceptTerms
            )
            return
        }

        viewModelScope.launch(dispatcherProvider.io) {
            _uiState.value = current.copy(isLoading = true, errorMessage = null)

            val result = registerUseCase(
                userName = current.userName,
                fullName = current.fullName,
                email = current.email,
                password = current.password,
                confirmPassword = current.confirmPassword
            )

            when (result) {
                is RegisterUseCase.RegisterResult.Success -> {
                    _uiState.value = RegisterUiState.Success
                    navigationController.navigateToLoginFromRegister()
                }

                is RegisterUseCase.RegisterResult.ValidationError -> {
                    _uiState.value = handleValidationError(current, result)
                }

                is RegisterUseCase.RegisterResult.ServerError -> {
                    _uiState.value = current.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    /**
     * Maneja los errores de validación según el campo.
     */
    private fun handleValidationError(
        current: RegisterUiState.Content,
        error: RegisterUseCase.RegisterResult.ValidationError
    ): RegisterUiState.Content {
        return when (error.field) {
            RegisterUseCase.RegisterResult.Field.FULL_NAME -> current.copy(
                isLoading = false,
                isFullNameError = true,
                fullNameErrorMessage = error.message
            )

            RegisterUseCase.RegisterResult.Field.USERNAME -> current.copy(
                isLoading = false,
                isUserNameError = true,
                userNameErrorMessage = error.message
            )

            RegisterUseCase.RegisterResult.Field.EMAIL -> current.copy(
                isLoading = false,
                isEmailError = true,
                emailErrorMessage = error.message
            )

            RegisterUseCase.RegisterResult.Field.PASSWORD -> current.copy(
                isLoading = false,
                isPasswordError = true,
                passwordErrorMessage = error.message
            )

            RegisterUseCase.RegisterResult.Field.CONFIRM_PASSWORD -> current.copy(
                isLoading = false,
                isConfirmPasswordError = true,
                confirmPasswordErrorMessage = error.message
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // NAVEGACIÓN
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Navega a la pantalla de login.
     */
    private fun navigateToLogin() {
        navigationController.navigateToLoginFromRegister()
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ERRORES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * Descarta el mensaje de error.
     */
    private fun dismissError() {
        val current = (_uiState.value as? RegisterUiState.Content) ?: return
        _uiState.value = current.copy(errorMessage = null)
    }
}
