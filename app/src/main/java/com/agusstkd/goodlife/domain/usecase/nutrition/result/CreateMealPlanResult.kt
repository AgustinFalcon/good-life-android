package com.agusstkd.goodlife.domain.usecase.nutrition.result

sealed interface CreateMealPlanResult {
    data class Success(val mealPlanId: Long) : CreateMealPlanResult
    data object ValidationError : CreateMealPlanResult
    data object NetworkError : CreateMealPlanResult
    data object ServerError : CreateMealPlanResult
}