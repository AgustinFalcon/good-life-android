package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.nutrition.MealPlanApiService
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.core.result.Result

/**
 * DataSource remoto de planes de comida.
 */
class MealPlanRemoteDataSource(
    private val apiService: MealPlanApiService
) {
    
    /**
     * Crea un plan de comidas completo.
     */
    suspend fun createMealPlan(
        request: CreateMealPlanRequestDto
    ): Result<MealPlanResponseDto> {
        return executeApiCall {
            apiService.createMealPlan(request)
        }
    }
}