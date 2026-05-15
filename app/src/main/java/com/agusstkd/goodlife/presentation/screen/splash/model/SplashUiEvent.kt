package com.agusstkd.goodlife.presentation.screen.splash.model

import androidx.compose.runtime.Stable

/**
 * Acciones que el usuario puede realizar en Splash.
 *
 * @Stable indica a Compose que este estado es estable.
 */
@Stable
sealed interface SplashUiAction {
    /** Usuario hizo click en "Continuar" */
    data object ContinueClicked : SplashUiAction
}
