package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable

import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus

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

    /**
     * Usuario pull-to-refresh para recargar datos.
     */
    data object OnRefresh : DailyUiAction

    /**
     * Usuario hace click en un item del daily log.
     *
     * @property itemId ID del item clickeado
     */
    data class OnItemClick(val itemId: Long) : DailyUiAction

    /**
     * Usuario cambia el status de un item (checkmark, skip, etc).
     *
     * @property itemId ID del item
     * @property newStatus Nuevo status (COMPLETED, SKIPPED, PENDING)
     */
    data class OnItemStatusChange(
        val itemId: Long,
        val newStatus: DailyItemStatus
    ) : DailyUiAction
}
