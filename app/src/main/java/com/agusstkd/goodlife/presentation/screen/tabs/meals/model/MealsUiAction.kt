package com.agusstkd.goodlife.presentation.screen.tabs.meals.model

import androidx.compose.runtime.Stable

/**
 * Acciones del usuario en la pantalla Meals.
 *
 * ## Filosofía:
 * ```
 * User interaction
 *     ↓
 * MealsUiAction
 *     ↓
 * MealsTabViewModel.onAction(action)
 *     ↓
 * State update
 *     ↓
 * UI recomposition
 * ```
 */
@Stable
sealed interface MealsUiAction {

    /** Navega al día anterior. */
    data object OnPreviousDay : MealsUiAction

    /** Navega al día siguiente. */
    data object OnNextDay : MealsUiAction

    /** Pull-to-refresh o retry manual. */
    data object OnRefresh : MealsUiAction

    /**
     * Navega a la pantalla de creación de plan de comida.
     * Disponible desde el estado [MealsUiState.Empty] y como FAB en [MealsUiState.Success].
     */
    data object OnCreateMealPlan : MealsUiAction

    /**
     * Navega al detalle de un plan de comida.
     *
     * @property planId ID del plan clickeado.
     */
    data class OnMealPlanClick(val planId: Long) : MealsUiAction
}
