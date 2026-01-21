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
     * @property fullName Nombre completo del usuario (guardado localmente)
     * @property userName Nombre de usuario único
     * @property email Correo electrónico
     * @property password Contraseña
     * @property confirmPassword Confirmación de contraseña
     * @property acceptedTerms Si aceptó términos y condiciones
     * @property isFullNameError Si hay error en el nombre completo
     * @property isUserNameError Si hay error en el username
     * @property isEmailError Si hay error en el email
     * @property isPasswordError Si hay error en la contraseña
     * @property isConfirmPasswordError Si hay error en la confirmación
     * @property isLoading Si el botón está en estado loading
     * @property errorMessage Error general (ej: del servidor)
     *
     * Nota: Los mensajes de error individuales se guardan pero el componente
     * TextFieldComponent solo muestra el borde rojo (no el mensaje).
     * El errorMessage general se muestra en un Snackbar.
     */
    data class Content(
        val fullName: String = "",
        val userName: String = "",
        val email: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val acceptedTerms: Boolean = false,
        val isFullNameError: Boolean = false,
        val fullNameErrorMessage: String? = null,
        val isUserNameError: Boolean = false,
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
