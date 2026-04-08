package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.SearchIngredientsResult
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext

class SearchIngredientsUseCase(
    private val repository: NutritionCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        query: String,
        page: Int = 0,
        pageSize: Int = 20
    ): SearchIngredientsResult = withContext(dispatcher.io) {
        return@withContext when (val result = repository.searchIngredients(query, page, pageSize)) {
            is Result.Success -> SearchIngredientsResult.Success(result.data)
            is Result.Error -> when (result.exception) {
                is ApiException.ServerException -> SearchIngredientsResult.ServerError
                else -> SearchIngredientsResult.NetworkError
            }
        }
    }
}