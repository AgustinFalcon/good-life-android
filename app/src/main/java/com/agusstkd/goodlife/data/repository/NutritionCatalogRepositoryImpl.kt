package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.data.remote.datasource.remote.NutritionCatalogRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.toIngredient
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.toMealSummary
import com.agusstkd.goodlife.data.remote.dto.response.toDomain
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto
import com.agusstkd.goodlife.core.result.Result

class NutritionCatalogRepositoryImpl(
    private val remoteDataSource: NutritionCatalogRemoteDataSource
) : NutritionCatalogRepository {

    override suspend fun searchIngredients(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<PageResult<Ingredient>> {
        return when (val result = remoteDataSource.searchIngredients(query, page, pageSize)) {
            is Result.Success -> Result.Success(result.data.toDomain { it.toIngredient() })
            is Result.Error -> result
        }
    }

    override suspend fun searchMeals(
        query: String,
        page: Int,
        pageSize: Int
    ): Result<PageResult<MealSummary>> {
        return when (val result = remoteDataSource.searchMeals(query, page, pageSize)) {
            is Result.Success -> Result.Success(result.data.toDomain { it.toMealSummary() })
            is Result.Error -> result
        }
    }

    override suspend fun createCustomIngredient(
        request: CreateCustomIngredientRequestDto
    ): Result<Ingredient> {
        return when (val result = remoteDataSource.createCustomIngredient(request)) {
            is Result.Success -> Result.Success(result.data.toIngredient())
            is Result.Error -> result
        }
    }

    override suspend fun createCustomMeal(
        request: CreateCustomMealRequestDto
    ): Result<MealSummary> {
        return when (val result = remoteDataSource.createCustomMeal(request)) {
            is Result.Success -> Result.Success(result.data.toMealSummary())
            is Result.Error -> result
        }
    }
}
