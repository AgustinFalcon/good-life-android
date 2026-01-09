package com.agusstkd.goodlife.presentation.screen.splash.model


/**
 * Estados posibles de la pantalla Splash.
 *
 * El Splash es simple: solo muestra el logo y navega automáticamente.
 */
sealed interface SplashUiState {
    /** Estado inicial mientras carga */
    data object Loading : SplashUiState

    /** Listo para navegar (después del delay) */
    data object Ready : SplashUiState
}
