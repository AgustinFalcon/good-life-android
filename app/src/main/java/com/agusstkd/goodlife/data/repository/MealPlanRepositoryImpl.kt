package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.data.remote.datasource.remote.MealPlanRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.core.result.Result

/**
 * Implementación del repositorio de planes de comida.
 */
class MealPlanRepositoryImpl(
    private val remoteDataSource: MealPlanRemoteDataSource
) : MealPlanRepository {
    
    override suspend fun createMealPlan(
        request: CreateMealPlanRequestDto
    ): Result<MealPlanResponseDto> {
        return remoteDataSource.createMealPlan(request)
    }
}