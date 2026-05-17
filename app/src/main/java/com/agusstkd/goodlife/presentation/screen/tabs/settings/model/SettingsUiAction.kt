package com.agusstkd.goodlife.presentation.screen.tabs.settings.model

/**
 * Acciones que el usuario puede disparar desde la pantalla de Settings.
 */
sealed class SettingsUiAction {
    /** El usuario quiere cerrar sesión. */
    data object Logout : SettingsUiAction()

    /** El usuario quiere ver su perfil. Navegación delegada al Owner. */
    data object NavigateToProfile : SettingsUiAction()
}
