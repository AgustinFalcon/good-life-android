package com.agusstkd.goodlife.presentation.screen.splash.model


/**
 * Acciones que el usuario puede realizar en Splash.
 */
sealed interface SplashUiAction {
    /** Usuario hizo click en "Continuar" */
    data object ContinueClicked : SplashUiAction
}
