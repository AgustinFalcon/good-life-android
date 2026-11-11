package com.agusstkd.goodlife.presentation.screen.tabs.settings.model

/**
 * Efectos de navegación one-shot del tab Settings.
 *
 * El Owner los colecta y los reenvía al grafo. El Owner no hace `when` sobre [SettingsUiAction].
 */
sealed interface SettingsNavigationEffect {
    /** Navegar a la pantalla de perfil. */
    data object NavigateToProfile : SettingsNavigationEffect
}
