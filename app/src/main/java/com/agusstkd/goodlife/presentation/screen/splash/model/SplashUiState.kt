package com.agusstkd.goodlife.presentation.screen.splash.model

import androidx.compose.runtime.Stable

/**
 * Estados posibles de la pantalla Splash.
 *
 * El Splash es simple: solo muestra el logo y navega automáticamente.
 *
 * @Stable indica a Compose que este estado es estable y no necesita
 * recomposición innecesaria.
 */
@Stable
sealed interface SplashUiState {
    /** Estado inicial mientras carga */
    data object Loading : SplashUiState

    /** Listo para navegar (después del delay) */
    data object Ready : SplashUiState
}
