package com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Stable
sealed interface MealDetailUiState {
    @Immutable data object Loading : MealDetailUiState
    @Immutable data class Content(
        val planName: String,
        val mealTypeLabel: String,
        val scheduledTimeLabel: String?,
        val mealName: String?,
        val imageUrl: String?,
        val totalCalories: Double,
        val totalProtein: Double,
        val totalCarbs: Double,
        val totalFat: Double,
        val isActive: Boolean,
    ) : MealDetailUiState
    @Immutable data object NotFound : MealDetailUiState
    @Immutable data object InvalidRoute : MealDetailUiState
    @Immutable data class Error(val message: String) : MealDetailUiState
}
