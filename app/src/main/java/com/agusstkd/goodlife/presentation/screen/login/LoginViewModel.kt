package com.agusstkd.goodlife.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.navigation.route.navigateToMain
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login.
 *
 * ## Responsabilidades:
 * - Mantener el estado de UI (LoginUiState)
 * - Procesar las acciones del usuario (LoginUiAction)
 * - Validar credenciales
 * - Navegar a otras pantallas via ComposeNavigationController
 *
 * @property navigationController Controlador de navegación reactivo
 */
class LoginViewModel(
    private val navigationController: ComposeNavigationController
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Simular carga inicial
        viewModelScope.launch {
            delay(1000)
            _uiState.value = LoginUiState.Content()
        }
    }

    /**
     * Procesa las acciones del usuario.
     */
    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.OnEmailChange -> updateEmail(action.value)
            is LoginUiAction.OnPasswordChange -> updatePassword(action.value)
            is LoginUiAction.OnRememberUserToggle -> toggleRememberUser()
            is LoginUiAction.OnLoginClick -> performLogin()
            is LoginUiAction.OnRegisterClick -> navigateToRegister()
            is LoginUiAction.OnForgotPasswordClick -> navigateToForgotPassword()
            is LoginUiAction.OnGoogleLoginClick -> loginWithGoogle()
            is LoginUiAction.OnAppleLoginClick -> loginWithApple()
            is LoginUiAction.OnTermsClick -> navigateToTerms()
            is LoginUiAction.OnPrivacyClick -> navigateToPrivacy()
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // UPDATE STATE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun updateEmail(value: String) {
        val currentState = _uiState.value
        if (currentState is LoginUiState.Content) {
            _uiState.value = currentState.copy(
                email = value,
                isEmailError = false // Limpiar error al escribir
            )
        }
    }

    private fun updatePassword(value: String) {
        val currentState = _uiState.value
        if (currentState is LoginUiState.Content) {
            _uiState.value = currentState.copy(
                password = value,
                isPasswordError = false // Limpiar error al escribir
            )
        }
    }

    private fun toggleRememberUser() {
        val currentState = _uiState.value
        if (currentState is LoginUiState.Content) {
            _uiState.value = currentState.copy(
                rememberUser = !currentState.rememberUser
            )
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // ACTIONS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    private fun performLogin() {
        val currentState = _uiState.value
        if (currentState !is LoginUiState.Content) return

        val email = currentState.email
        val password = currentState.password

        // Validar campos vacíos
        val isEmailEmpty = email.isBlank()
        val isPasswordEmpty = password.isBlank()

        if (isEmailEmpty || isPasswordEmpty) {
            _uiState.value = currentState.copy(
                isEmailError = isEmailEmpty,
                isPasswordError = isPasswordEmpty
            )
            return
        }

        // TODO: Implementar autenticación real con el backend
        // Por ahora, simular login con credenciales hardcodeadas
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            delay(1500) // Simular llamada al servidor

            val validEmail = "agustin"
            val validPassword = "1234"

            if (email == validEmail && password == validPassword) {
                _uiState.value = LoginUiState.Success
                delay(500)
                navigationController.navigateToMain()
            } else {
                _uiState.value = currentState.copy(
                    isEmailError = true,
                    isPasswordError = true
                )
            }
        }
    }

    private fun navigateToRegister() {
        navigationController.navigateTo(AppRoute.Register)
    }

    private fun navigateToForgotPassword() {
        // TODO: Implementar navegación a pantalla de recuperar contraseña
        // navigationController.navigateTo(AppRoute.ForgotPassword)
    }

    private fun loginWithGoogle() {
        // TODO: Implementar login con Google
    }

    private fun loginWithApple() {
        // TODO: Implementar login con Apple
    }

    private fun navigateToTerms() {
        // TODO: Navegar a términos y condiciones
    }

    private fun navigateToPrivacy() {
        // TODO: Navegar a política de privacidad
    }
}
