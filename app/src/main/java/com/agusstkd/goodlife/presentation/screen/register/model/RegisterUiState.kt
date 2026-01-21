package com.agusstkd.goodlife.presentation.screen.register.model

import androidx.compose.runtime.Stable

/**
 * Estados de la pantalla de registro.
 *
 * ## Estados:
 * - **Loading**: Cargando pantalla inicial
 * - **Content**: Formulario activo con todos los campos
 * - **Success**: Registro exitoso, navegar a login
 *
 * @see RegisterUiAction
 */
@Stable
sealed interface RegisterUiState {

    /**
     * Estado de carga inicial.
     */
    data object Loading : RegisterUiState

    /**
     * Estado principal con el formulario de registro.
     *
     * @property fullName Nombre completo del usuario
     * @property userName Nombre de usuario único
     * @property email Correo electrónico
     * @property password Contraseña
     * @property confirmPassword Confirmación de contraseña
     * @property acceptedTerms Si aceptó términos y condiciones
     * @property isPasswordVisible Si la contraseña es visible
     * @property isConfirmPasswordVisible Si la confirmación es visible
     * @property isFullNameError Si hay error en el nombre completo
     * @property isUserNameError Si hay error en el username
     * @property fullNameErrorMessage Mensaje de error del nombre
     * @property userNameErrorMessage Mensaje de error del username
     * @property isEmailError Si hay error en el email
     * @property emailErrorMessage Mensaje de error del email
     * @property isPasswordError Si hay error en la contraseña
     * @property passwordErrorMessage Mensaje de error de contraseña
     * @property isConfirmPasswordError Si hay error en la confirmación
     * @property confirmPasswordErrorMessage Mensaje de error de confirmación
     * @property isLoading Si el botón está en estado loading
     * @property errorMessage Error general (ej: del servidor)
     */
    data class Content(
        val fullName: String = "",
        val userName: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val acceptedTerms: Boolean = false,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isFullNameError: Boolean = false,
        val isUserNameError: Boolean = false,
        val fullNameErrorMessage: String? = null,
        val userNameErrorMessage: String? = null,
        val isEmailError: Boolean = false,
        val emailErrorMessage: String? = null,
        val isPasswordError: Boolean = false,
        val passwordErrorMessage: String? = null,
        val isConfirmPasswordError: Boolean = false,
        val confirmPasswordErrorMessage: String? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    ) : RegisterUiState

    /**
     * Estado de registro exitoso.
     * Trigger para navegar a la pantalla de login.
     */
    data object Success : RegisterUiState
}
