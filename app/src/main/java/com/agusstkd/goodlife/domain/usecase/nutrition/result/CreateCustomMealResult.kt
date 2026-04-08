package com.agusstkd.goodlife.domain.usecase.nutrition.result

import com.agusstkd.goodlife.domain.model.nutrition.MealSummary

sealed interface CreateCustomMealResult {
    data class Success(val meal: MealSummary) : CreateCustomMealResult
    data object ValidationError : CreateCustomMealResult
    data object NetworkError : CreateCustomMealResult
    data object ServerError : CreateCustomMealResult
}