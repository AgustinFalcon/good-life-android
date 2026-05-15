package com.agusstkd.goodlife.data.remote.api.nutrition

import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.BaseResponse
import com.agusstkd.goodlife.data.remote.dto.response.PageResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.IngredientResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealSummaryResponseDto
import retrofit2.http.*

/**
 * API service del catálogo de nutrición.
 * 
 * Define endpoints para ingredientes y comidas del backend de nutrición.
 * Todos los endpoints devuelven respuestas envueltas en [BaseResponse].
 */
interface NutritionCatalogApiService {
    
    /**
     * Buscar ingredientes paginado.
     * 
     * GET /api/v1/ingredients?search=X&page=0&size=20
     */
    @GET("api/v1/ingredients")
    suspend fun searchIngredients(
        @Query("search") query: String,
        @Query("page") page: Int,
        @Query("size") pageSize: Int,
    ): BaseResponse<PageResponseDto<IngredientResponseDto>>
    
    /**
     * Buscar comidas paginado.
     * 
     * GET /api/v1/meals?search=X&page=0&size=20
     */
    @GET("api/v1/meals")
    suspend fun searchMeals(
        @Query("search") query: String,
        @Query("page") page: Int,
        @Query("size") pageSize: Int,
    ): BaseResponse<PageResponseDto<MealSummaryResponseDto>>
    
    /**
     * Crear ingrediente custom.
     * 
     * POST /api/v1/ingredients
     */
    @POST("api/v1/ingredients")
    suspend fun createCustomIngredient(
        @Body request: CreateCustomIngredientRequestDto
    ): BaseResponse<IngredientResponseDto>
    
    /**
     * Crear comida custom básica.
     * 
     * POST /api/v1/meals
     */
    @POST("api/v1/meals")
    suspend fun createCustomMeal(
        @Body request: CreateCustomMealRequestDto
    ): BaseResponse<MealSummaryResponseDto>
}