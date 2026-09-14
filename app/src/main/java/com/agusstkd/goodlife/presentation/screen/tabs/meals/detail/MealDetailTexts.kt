package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail

import com.agusstkd.goodlife.core.datetime.language.AppLanguage

data class MealDetailTexts(
    val title: String, val back: String, val retry: String, val loading: String,
    val notFound: String, val invalidRoute: String, val mealLabel: String, val scheduleLabel: String,
    val proteinLabel: String, val carbsLabel: String, val fatLabel: String, val statusLabel: String,
    val active: String, val inactive: String, val imageUnavailable: String, val calorieUnit: String, val gramUnit: String,
)

internal fun AppLanguage.mealDetailTexts() = tabDetailTexts.let {
    MealDetailTexts(it.mealTitle, it.back, it.retry, it.loading, it.mealNotFound, it.invalidRoute,
        it.mealLabel, it.scheduleLabel, it.proteinLabel, it.carbsLabel, it.fatLabel, it.statusLabel, it.active, it.inactive, it.mealImageUnavailable, it.calorieUnit, it.gramUnit)
}
