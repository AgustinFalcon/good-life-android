package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomIngredientRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateCustomMealRequestDto
import com.agusstkd.goodlife.data.remote.dto.request.nutrition.CreateMealPlanRequestDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealPlanResponseDto
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import kotlinx.datetime.LocalDate

class FakeMealPlanRepository : MealPlanRepository {
    var getResult: Result<List<DailyMealPlanSummary>> = Result.Success(emptyList())
    var createResult: Result<MealPlanResponseDto> = Result.Success(MealPlanResponseDto(id = 1L, name = "Meal", totalCalories = 0.0, totalProtein = 0.0, totalCarbs = 0.0, totalFat = 0.0, isActive = true))
    val requestedDates = mutableListOf<LocalDate>()
    var createCallCount = 0

    override suspend fun getMealPlansByDate(date: LocalDate): Result<List<DailyMealPlanSummary>> {
        requestedDates.add(date)
        return getResult
    }

    override suspend fun createMealPlan(request: CreateMealPlanRequestDto): Result<MealPlanResponseDto> {
        createCallCount++
        return createResult
    }
}

class FakeNutritionCatalogRepository : NutritionCatalogRepository {
    var ingredientResult: Result<PageResult<Ingredient>> = Result.Success(PageResult(emptyList(), 0, 20, 0, true))
    var mealResult: Result<PageResult<MealSummary>> = Result.Success(PageResult(emptyList(), 0, 20, 0, true))
    var createIngredientResult: Result<Ingredient> = Result.Success(defaultIngredient())
    var createMealResult: Result<MealSummary> = Result.Success(defaultMeal())
    var searchMealsCallCount = 0
    var searchIngredientsCallCount = 0

    override suspend fun searchIngredients(query: String, page: Int, pageSize: Int): Result<PageResult<Ingredient>> {
        searchIngredientsCallCount++
        return ingredientResult
    }

    override suspend fun searchMeals(query: String, page: Int, pageSize: Int): Result<PageResult<MealSummary>> {
        searchMealsCallCount++
        return mealResult
    }

    override suspend fun createCustomIngredient(request: CreateCustomIngredientRequestDto): Result<Ingredient> = createIngredientResult
    override suspend fun createCustomMeal(request: CreateCustomMealRequestDto): Result<MealSummary> = createMealResult

    companion object {
        fun defaultIngredient() = Ingredient(1L, "Rice", null, 100.0, "g", 130.0, 2.7, 28.0, 0.3, true, null)
        fun defaultMeal() = MealSummary(1L, "Bowl", null, null, null, 1.0, "unit", 500.0, 30.0, 60.0, 10.0, true)
    }
}
