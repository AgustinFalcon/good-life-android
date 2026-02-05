package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable

/**
 * Acciones del usuario en la pantalla Daily.
 *
 * ## Responsabilidades:
 * - Representar todas las interacciones posibles del usuario
 * - Ser inmutables y type-safe (sealed interface)
 * - Facilitar testing (easy to mock)
 *
 * ## Filosofía:
 * ```
 * User interaction
 *     ↓
 * DailyUiAction
 *     ↓
 * ViewModel.onAction(action)
 *     ↓
 * State update
 *     ↓
 * UI recomposition
 * ```
 */
@Stable
sealed interface DailyUiAction {

    /**
     * Usuario navega al día anterior.
     *
     * Ejemplo: De "Hoy" (4 de febrero) → "Ayer" (3 de febrero)
     */
    data object OnPreviousDay : DailyUiAction

    /**
     * Usuario navega al día siguiente.
     *
     * Ejemplo: De "Hoy" (4 de febrero) → "Mañana" (5 de febrero)
     */
    data object OnNextDay : DailyUiAction
}
