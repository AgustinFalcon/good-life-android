package com.agusstkd.goodlife.data.remote.api.nutrition

import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.DailyMealPlanResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import retrofit2.http.*

/**
 * API service de planes de comida.
 *
 * Define endpoints para gestión de meal plans.
 * Todos los endpoints devuelven respuestas envueltas en [BaseResponse].
 */
interface MealPlanApiService {

    /**
     * Obtiene los planes de comida del usuario para una fecha específica.
     *
     * GET /api/v1/meal-plans?date=YYYY-MM-DD
     *
     * @param date Fecha en formato ISO-8601 (ej: "2026-05-15").
     */
    @GET("api/v1/meal-plans")
    suspend fun getMealPlansByDate(
        @Query("date") date: String
    ): BaseResponse<List<DailyMealPlanResponseDto>>

    /**
     * Crear plan de comidas completo.
     *
     * POST /api/v1/meal-plans
     */
    @POST("api/v1/meal-plans")
    suspend fun createMealPlan(
        @Body request: CreateMealPlanRequestDto
    ): BaseResponse<MealPlanResponseDto>
}