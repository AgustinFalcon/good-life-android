package com.agusstkd.goodlife.presentation.screen.tabs.settings.model

/**
 * Estado de la pantalla de Settings.
 *
 * @property username Nombre del usuario autenticado.
 * @property isLoading Indica si hay una operación en curso (ej: logout).
 */
data class SettingsUiState(
    val username: String = "",
    val isLoading: Boolean = false
)
