package com.agusstkd.goodlife.presentation.screen.tabs.meals.model

/**
 * One-shot navigation requests owned by the Meals tab.
 *
 * The owner forwards these effects to the tab graph. They must never use the
 * app-level navigation controller because detail destinations belong to the
 * nested tab graph.
 */
sealed interface MealsNavigationEffect {
    data class NavigateToMealDetail(
        val planId: Long,
        val dateIso: String,
    ) : MealsNavigationEffect
}