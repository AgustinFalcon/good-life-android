package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.result.map
import com.agusstkd.goodlife.data.remote.datasource.remote.MealPlanRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.toDailySummary
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import kotlinx.datetime.LocalDate

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

    override suspend fun createMealPlan(
        request: CreateMealPlanRequestDto
    ): Result<MealPlanResponseDto> {
        return remoteDataSource.createMealPlan(request)
    }
}