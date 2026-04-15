package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateMealPlanResult
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.ScheduledMealRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.MealIngredientRequestDto
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext

class CreateMealPlanUseCase(
    private val repository: MealPlanRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        mealPlanDraft: MealPlanDraft
    ): CreateMealPlanResult = withContext(dispatcher.io) {
        if (mealPlanDraft.name.isBlank() || mealPlanDraft.scheduledMeals.isEmpty()) {
            return@withContext CreateMealPlanResult.ValidationError
        }

        val request = mapDraftToRequestDto(mealPlanDraft)

        return@withContext when (val result = repository.createMealPlan(request)) {
            is Result.Success -> CreateMealPlanResult.Success(result.data.id ?: 0L)
            is Result.Error -> when (val ex = result.exception) {
                is ApiException.ServerException -> CreateMealPlanResult.ServerError(ex.message)
                else -> CreateMealPlanResult.NetworkError
            }
        }
    }

    private fun mapDraftToRequestDto(draft: MealPlanDraft): CreateMealPlanRequestDto {
        return CreateMealPlanRequestDto(
            name = draft.name,
            description = draft.description,
            scheduledMeals = draft.scheduledMeals.map { scheduledMeal ->
                ScheduledMealRequestDto(
                    mealType = scheduledMeal.mealType,
                    scheduledTime = scheduledMeal.scheduledTime,
                    mealId = scheduledMeal.selectedMeal?.id,
                    customIngredients = scheduledMeal.customIngredients.map { customIngredient ->
                        MealIngredientRequestDto(
                            ingredientId = customIngredient.ingredient.id,
                            quantity = customIngredient.quantity,
                            unit = customIngredient.unit
                        )
                    }
                )
            }
        )
    }
}
