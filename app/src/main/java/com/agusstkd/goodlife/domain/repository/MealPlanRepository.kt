package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto

/**
 * Repositorio de planes de comida.
 */
interface MealPlanRepository {
    
    /**
     * Crea un plan de comidas completo.
     */
    suspend fun createMealPlan(
        request: CreateMealPlanRequestDto
    ): Result<MealPlanResponseDto>
}