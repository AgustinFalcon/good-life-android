package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomIngredientResult
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext

class CreateCustomIngredientUseCase(
    private val repository: NutritionCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        request: CreateCustomIngredientRequestDto
    ): CreateCustomIngredientResult = withContext(dispatcher.io) {
        if (request.name.isBlank()) {
            return@withContext CreateCustomIngredientResult.ValidationError
        }

        return@withContext when (val result = repository.createCustomIngredient(request)) {
            is Result.Success -> CreateCustomIngredientResult.Success(result.data)
            is Result.Error -> when (result.exception) {
                is ApiException.ServerException -> CreateCustomIngredientResult.ServerError
                else -> CreateCustomIngredientResult.NetworkError
            }
        }
    }
}