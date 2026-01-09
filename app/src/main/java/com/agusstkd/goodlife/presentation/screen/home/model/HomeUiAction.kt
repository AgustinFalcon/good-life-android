package com.agusstkd.goodlife.presentation.screen.home.model

import androidx.compose.runtime.Stable

/**
 * Acciones de UI para la pantalla Home.
 *
 * @Stable indica a Compose que este estado es estable.
 */
@Stable
sealed interface HomeUiAction {

    /**
     * Usuario tocó el botón de cerrar sesión.
     */
    data object OnLogoutClick : HomeUiAction

    /**
     * Usuario tocó el botón de reintentar (en estado de error).
     */
    data object OnRetryClick : HomeUiAction
}
