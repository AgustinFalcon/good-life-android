package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateMealPlanResult
import kotlinx.coroutines.withContext

class CreateMealPlanUseCase(
    private val repository: MealPlanRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        mealPlanDraft: MealPlanDraft
    ): CreateMealPlanResult = withContext(dispatcher.io) {
        if (!mealPlanDraft.isCreatable) {
            return@withContext CreateMealPlanResult.ValidationError
        }

        return@withContext when (val result = repository.createMealPlan(mealPlanDraft)) {
            is Result.Success -> CreateMealPlanResult.Success(result.data)
            is Result.Error -> when (val ex = result.exception) {
                is ApiException.BadRequestException,
                is ApiException.ServerException,
                is ApiException.NotFoundException,
                is ApiException.UnknownApiException -> CreateMealPlanResult.ServerError(ex.message)
                else -> CreateMealPlanResult.NetworkError
            }
        }
    }
}

private val MealPlanDraft.isCreatable: Boolean
    get() {
        val scheduled = scheduledMeals.singleOrNull() ?: return false
        if (scheduled.selectedMeal?.id == null) return false
        if (scheduled.mealType == MealType.UNKNOWN) return false
        if (daysOfWeek.isEmpty()) return false
        return true
    }
