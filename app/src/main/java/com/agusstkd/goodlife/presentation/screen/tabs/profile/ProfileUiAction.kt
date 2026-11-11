package com.agusstkd.goodlife.presentation.screen.tabs.profile

/**
 * Acciones de la pantalla de perfil.
 */
sealed interface ProfileUiAction {
    data object OnChangePhoto : ProfileUiAction
}
