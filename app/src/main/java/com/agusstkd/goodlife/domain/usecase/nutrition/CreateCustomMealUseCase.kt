package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomMealResult
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext

class CreateCustomMealUseCase(
    private val repository: NutritionCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        request: CreateCustomMealRequestDto
    ): CreateCustomMealResult = withContext(dispatcher.io) {
        if (request.name.isBlank()) {
            return@withContext CreateCustomMealResult.ValidationError
        }

        return@withContext when (val result = repository.createCustomMeal(request)) {
            is Result.Success -> CreateCustomMealResult.Success(result.data)
            is Result.Error -> when (result.exception) {
                is ApiException.ServerException -> CreateCustomMealResult.ServerError
                else -> CreateCustomMealResult.NetworkError
            }
        }
    }
}