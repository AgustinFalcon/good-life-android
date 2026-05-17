package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.result.map
import com.agusstkd.goodlife.data.remote.datasource.remote.MealPlanRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.DailyMealPlanResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Implementación del repositorio de planes de comida.
 */
class MealPlanRepositoryImpl(
    private val remoteDataSource: MealPlanRemoteDataSource
) : MealPlanRepository {

    override suspend fun getMealPlansByDate(date: LocalDate): Result<List<DailyMealPlanSummary>> {
        return remoteDataSource.getMealPlansByDate(date.toString()).map { dtoList ->
            dtoList.mapNotNull { it.toDomain(date) }
        }
    }

    override suspend fun createMealPlan(
        request: CreateMealPlanRequestDto
    ): Result<MealPlanResponseDto> {
        return remoteDataSource.createMealPlan(request)
    }

    private fun DailyMealPlanResponseDto.toDomain(date: LocalDate): DailyMealPlanSummary? {
        val resolvedId = id ?: return null
        val resolvedName = name ?: return null
        val mealTypeEnum = runCatching { MealType.valueOf(mealType ?: "") }.getOrNull() ?: MealType.SNACK
        val scheduledTimeLocal = scheduledTime?.let { timeStr ->
            runCatching { LocalTime.parse(timeStr) }.getOrNull()
        }
        return DailyMealPlanSummary(
            id = resolvedId,
            name = resolvedName,
            date = date,
            mealType = mealTypeEnum,
            scheduledTime = scheduledTimeLocal,
            mealName = mealName,
            imageUrl = imageUrl,
            totalCalories = totalCalories ?: 0.0,
            totalProtein = totalProtein ?: 0.0,
            totalCarbs = totalCarbs ?: 0.0,
            totalFat = totalFat ?: 0.0,
            isActive = isActive ?: true,
        )
    }
}