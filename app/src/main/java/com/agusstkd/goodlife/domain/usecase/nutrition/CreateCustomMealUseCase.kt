package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.CustomMealDraft
import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateCustomMealResult
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext

class CreateCustomMealUseCase(
    private val repository: NutritionCatalogRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        draft: CustomMealDraft
    ): CreateCustomMealResult = withContext(dispatcher.io) {
        if (draft.name.isBlank()) {
            return@withContext CreateCustomMealResult.ValidationError
        }

        return@withContext when (val result = repository.createCustomMeal(draft)) {
            is Result.Success -> CreateCustomMealResult.Success(result.data)
            is Result.Error -> when (val ex = result.exception) {
                is ApiException.BadRequestException,
                is ApiException.ServerException,
                is ApiException.NotFoundException,
                is ApiException.UnknownApiException -> CreateCustomMealResult.ServerError(ex.message)
                else -> CreateCustomMealResult.NetworkError
            }
        }
    }
}
