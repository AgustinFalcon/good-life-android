package com.agusstkd.goodlife.domain.usecase.nutrition

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.result.CreateMealPlanResult
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

class CreateMealPlanUseCase(
    private val repository: MealPlanRepository,
    private val dispatcher: DispatcherProvider,
) {
    suspend fun execute(
        mealPlanDraft: MealPlanDraft
    ): CreateMealPlanResult = withContext(dispatcher.io) {
        val request = mapDraftToRequestDto(mealPlanDraft)
            ?: return@withContext CreateMealPlanResult.ValidationError

        return@withContext when (val result = repository.createMealPlan(request)) {
            is Result.Success -> CreateMealPlanResult.Success(result.data.id ?: 0L)
            is Result.Error -> when (val ex = result.exception) {
                is ApiException.BadRequestException,
                is ApiException.ServerException,
                is ApiException.NotFoundException,
                is ApiException.UnknownApiException -> CreateMealPlanResult.ServerError(ex.message)
                else -> CreateMealPlanResult.NetworkError
            }
        }
    }

    private fun mapDraftToRequestDto(draft: MealPlanDraft): CreateMealPlanRequestDto? {
        val scheduled = draft.scheduledMeals.singleOrNull() ?: return null
        val mealId = scheduled.selectedMeal?.id ?: return null
        if (draft.daysOfWeek.isEmpty()) return null

        return CreateMealPlanRequestDto(
            mealId = mealId,
            mealType = scheduled.mealType.name,
            scheduledTime = scheduled.scheduledTime?.toApiTime(),
            daysOfWeek = draft.daysOfWeek.map { it.isoDayNumber }.sorted(),
            startDate = draft.startDate?.toString(),
            endDate = draft.endDate?.toString(),
        )
    }
}

private fun LocalTime.toApiTime(): String =
    "%02d:%02d:%02d".format(hour, minute, second)
