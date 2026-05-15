package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.SearchMealsResult
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext

class SearchMealsUseCase(
    private val repository: NutritionCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        query: String,
        page: Int = 0,
        pageSize: Int = 20
    ): SearchMealsResult = withContext(dispatcher.io) {
        return@withContext when (val result = repository.searchMeals(query, page, pageSize)) {
            is Result.Success -> SearchMealsResult.Success(result.data)
            is Result.Error -> when (val ex = result.exception) {
                is ApiException.ServerException -> SearchMealsResult.ServerError(ex.message)
                else -> SearchMealsResult.NetworkError
            }
        }
    }
}