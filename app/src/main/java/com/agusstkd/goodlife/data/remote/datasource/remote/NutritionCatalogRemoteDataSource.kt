package com.agusstkd.goodlife.data.remote.datasource.remote

import com.agusstkd.goodlife.data.remote.api.executeApiCall
import com.agusstkd.goodlife.data.remote.api.nutrition.NutritionCatalogApiService
import com.agusstkd.goodlife.data.remote.dto.response.PageResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.IngredientResponseDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealSummaryResponseDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto
import com.agusstkd.goodlife.core.result.Result

/**
 * DataSource remoto del catálogo de nutrición.
 *
 * Abstrae las llamadas HTTP al backend de nutrición usando [executeApiCall] 
 * para manejo consistente de errores y transformación de excepciones HTTP 
 * en [Result] semánticos.
 */
class NutritionCatalogRemoteDataSource(
    private val apiService: NutritionCatalogApiService
) {
    
    /**
     * Busca ingredientes en el catálogo remoto paginado.
     * 
     * @param query Término de búsqueda.
     * @param page Número de página (base 0).  
     * @param pageSize Elementos por página.
     * @return [Result] con página de ingredientes o error HTTP.
     */
    suspend fun searchIngredients(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<PageResponseDto<IngredientResponseDto>> {
        return executeApiCall {
            apiService.searchIngredients(query, page, pageSize)
        }
    }
    
    /**
     * Busca comidas en el catálogo remoto paginado.
     */
    suspend fun searchMeals(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<PageResponseDto<MealSummaryResponseDto>> {
        return executeApiCall {
            apiService.searchMeals(query, page, pageSize)
        }
    }
    
    /**
     * Crea un ingrediente custom del usuario.
     */
    suspend fun createCustomIngredient(
        request: CreateCustomIngredientRequestDto
    ): Result<IngredientResponseDto> {
        return executeApiCall {
            apiService.createCustomIngredient(request)
        }
    }
    
    /**
     * Crea una comida custom del usuario.
     */
    suspend fun createCustomMeal(
        request: CreateCustomMealRequestDto
    ): Result<MealSummaryResponseDto> {
        return executeApiCall {
            apiService.createCustomMeal(request)
        }
    }
}
