package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.nutrition.MealPlanApiService
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.DailyMealPlanResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.core.result.Result

/**
 * DataSource remoto de planes de comida.
 */
class MealPlanRemoteDataSource(
    private val apiService: MealPlanApiService
) {

    /**
     * Obtiene los planes de comida para una fecha específica.
     *
     * @param date Fecha en formato ISO-8601 (ej: "2026-05-15").
     */
    suspend fun getMealPlansByDate(
        date: String
    ): Result<List<DailyMealPlanResponseDto>> {
        return executeApiCall {
            apiService.getMealPlansByDate(date)
        }
    }

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