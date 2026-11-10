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
     * Obtiene todos los planes de comida activos del usuario.
     *
     * GET /api/v1/meal-plans
     *
     * El recorte al día pedido vive en el mapper de dominio, no en query `date`.
     */
    @GET("api/v1/meal-plans")
    suspend fun getMealPlans(): BaseResponse<List<DailyMealPlanResponseDto>>

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