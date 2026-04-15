package com.agusstkd.goodlife.domain.usecase.nutrition.result

import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.core.pagination.PageResult

sealed interface SearchIngredientsResult {
    data class Success(val data: PageResult<Ingredient>) : SearchIngredientsResult
    data class ServerError(val message: String) : SearchIngredientsResult
    data object NetworkError : SearchIngredientsResult
}