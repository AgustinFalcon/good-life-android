package com.agusstkd.goodlife.presentation.screen.login.model

import androidx.compose.runtime.Stable

/**
 * Acciones de UI para la pantalla de Login.
 *
 * Define todas las interacciones posibles del usuario:
 * - Cambios en campos de texto
 * - Clicks en botones
 * - Navegación
 * - Biometría
 */
@Stable
sealed interface LoginUiAction {

    data class OnEmailChange(val value: String) : LoginUiAction

    data class OnPasswordChange(val value: String) : LoginUiAction

    data object OnRememberUserToggle : LoginUiAction

    data object OnLoginClick : LoginUiAction

    data object OnRegisterClick : LoginUiAction

    data object OnForgotPasswordClick : LoginUiAction

    data object OnGoogleLoginClick : LoginUiAction

    data object OnAppleLoginClick : LoginUiAction

    data object OnTermsClick : LoginUiAction

    data object OnPrivacyClick : LoginUiAction

    // ═══════ BIOMETRÍA ═══════

    /** Toggle del checkbox de activar biometría */
    data object OnBiometricToggle : LoginUiAction

    /** Click en el ícono de huella (abre prompt) */
    data object OnBiometricIconClick : LoginUiAction

    /**
     * Dispara la autenticación biométrica con el contexto de plataforma.
     * En Android, [platformContext] es la FragmentActivity necesaria para BiometricPrompt.
     * El ViewModel lo pasa opacamente al BiometricLoginHandler sin conocer el tipo concreto.
     */
    data class OnBiometricAuthenticate(val platformContext: Any?) : LoginUiAction

    /** Resetear flag de mostrar prompt (Owner lo envía tras disparar la autenticación) */
    data object OnBiometricPromptShown : LoginUiAction
}
