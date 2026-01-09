package com.agusstkd.goodlife.presentation.screen.home.model

import androidx.compose.runtime.Stable

/**
 * Estados de UI para la pantalla Home.
 *
 * @Stable indica a Compose que este estado es estable y no requiere
 * recomposición innecesaria cuando sus propiedades no cambian.
 */
@Stable
sealed interface HomeUiState {

    /**
     * Estado de carga inicial.
     * Se muestra mientras se obtienen los datos del usuario.
     */
    data object Loading : HomeUiState

    /**
     * Estado principal con los datos del usuario.
     *
     * @property userName Nombre del usuario logueado
     * @property userEmail Email del usuario logueado
     */
    data class Content(
        val userName: String,
        val userEmail: String
    ) : HomeUiState

    /**
     * Estado de error.
     * Se muestra cuando falla la carga de datos.
     *
     * @property message Mensaje de error a mostrar
     */
    data class Error(val message: String) : HomeUiState
}
