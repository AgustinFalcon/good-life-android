package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto

/**
 * Repositorio del catálogo de nutrición.
 *
 * Gestiona el acceso a ingredientes y comidas tanto del catálogo global 
 * como los elementos custom creados por el usuario.
 * Abstrae el acceso a datos del backend de nutrición.
 */
interface NutritionCatalogRepository {

    /**
     * Busca ingredientes en el catálogo paginado.
     *
     * @param query Término de búsqueda para filtrar ingredientes por nombre.
     * @param page Número de página (base 0).
     * @param pageSize Cantidad de elementos por página.
     * @return Lista paginada de ingredientes que coinciden con la búsqueda.
     */
    suspend fun searchIngredients(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<PageResult<Ingredient>>

    /**
     * Busca comidas en el catálogo paginado.
     *
     * @param query Término de búsqueda para filtrar comidas por nombre.
     * @param page Número de página (base 0).
     * @param pageSize Cantidad de elementos por página.
     * @return Lista paginada de comidas que coinciden con la búsqueda.
     */
    suspend fun searchMeals(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<PageResult<MealSummary>>

    /**
     * Crea un ingrediente custom del usuario.
     *
     * @param request Datos del ingrediente a crear.
     * @return El ingrediente creado con ID asignado por el backend.
     */
    suspend fun createCustomIngredient(
        request: CreateCustomIngredientRequestDto
    ): Result<Ingredient>

    /**
     * Crea una comida custom del usuario.
     *
     * @param request Datos de la comida a crear.
     * @return La comida creada con ID asignado por el backend.
     */
    suspend fun createCustomMeal(
        request: CreateCustomMealRequestDto
    ): Result<MealSummary>
}
