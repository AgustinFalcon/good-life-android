package com.agusstkd.goodlife.domain.usecase.nutrition.result

import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.core.pagination.PageResult

sealed interface SearchMealsResult {
    data class Success(val data: PageResult<MealSummary>) : SearchMealsResult
    data object NetworkError : SearchMealsResult
    data object ServerError : SearchMealsResult
}