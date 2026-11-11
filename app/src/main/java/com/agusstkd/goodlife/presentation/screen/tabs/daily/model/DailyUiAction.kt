package com.agusstkd.goodlife.presentation.screen.tabs.daily.model

import androidx.compose.runtime.Stable

import kotlinx.datetime.LocalDate

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

    /** Abre el bottomsheet de calendario para saltar a una fecha. */
    data object OnCalendarClick : DailyUiAction

    /** Cierra el bottomsheet de calendario sin cambiar la fecha. */
    data object OnCalendarDismiss : DailyUiAction

    /**
     * Usuario eligió una fecha en el calendario.
     *
     * @property date Día al que debe saltar el Daily.
     */
    data class OnCalendarDateSelected(val date: LocalDate) : DailyUiAction

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
     * Usuario cambia el filtro activo (All / Task / Habit / Workout / Meal).
     *
     * @property filter Nuevo filtro seleccionado
     */
    data class OnFilterChange(val filter: DailyFilter): DailyUiAction

    /**
     * Usuario toca el checkmark de un item.
     *
     * El ViewModel decide el nuevo status (COMPLETED ↔ PENDING; SKIPPED se ignora).
     *
     * @property itemId ID del item
     */
    data class OnItemStatusChange(
        val itemId: Long,
    ) : DailyUiAction
}
