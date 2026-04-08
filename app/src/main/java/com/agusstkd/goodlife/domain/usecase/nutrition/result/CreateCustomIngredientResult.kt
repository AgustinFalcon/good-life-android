package com.agusstkd.goodlife.domain.usecase.nutrition.result

import com.agusstkd.goodlife.domain.model.nutrition.Ingredient

sealed interface CreateCustomIngredientResult {
    data class Success(val ingredient: Ingredient) : CreateCustomIngredientResult
    data object ValidationError : CreateCustomIngredientResult
    data object NetworkError : CreateCustomIngredientResult
    data object ServerError : CreateCustomIngredientResult
}