package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.result.map
import com.agusstkd.goodlife.data.remote.datasource.remote.MealPlanRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.toDailySummary
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber

/**
 * Implementación del repositorio de planes de comida.
 */
class MealPlanRepositoryImpl(
    private val remoteDataSource: MealPlanRemoteDataSource
) : MealPlanRepository {

    override suspend fun getMealPlansByDate(date: LocalDate): Result<List<DailyMealPlanSummary>> {
        return remoteDataSource.getMealPlansByDate(date.toString()).map { dtoList ->
            dtoList.mapNotNull { it.toDailySummary(date) }
        }
    }

    override suspend fun createMealPlan(draft: MealPlanDraft): Result<Long> {
        val request = draft.toRequestDto() ?: return Result.Error(IllegalArgumentException("invalid meal plan draft"))
        return remoteDataSource.createMealPlan(request).map { dto -> dto.id ?: 0L }
    }
}

private fun MealPlanDraft.toRequestDto(): CreateMealPlanRequestDto? {
    val scheduled = scheduledMeals.singleOrNull() ?: return null
    val mealId = scheduled.selectedMeal?.id ?: return null
    if (scheduled.mealType == MealType.UNKNOWN) return null
    if (daysOfWeek.isEmpty()) return null

    return CreateMealPlanRequestDto(
        mealId = mealId,
        mealType = scheduled.mealType.name,
        scheduledTime = scheduled.scheduledTime?.toApiTime(),
        daysOfWeek = daysOfWeek.map { it.isoDayNumber }.sorted(),
        startDate = startDate?.toString(),
        endDate = endDate?.toString(),
    )
}

private fun LocalTime.toApiTime(): String =
    "%02d:%02d:%02d".format(hour, minute, second)
