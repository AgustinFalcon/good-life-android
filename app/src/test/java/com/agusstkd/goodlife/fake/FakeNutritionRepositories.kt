package com.agusstkd.goodlife.fake

import com.agusstkd.goodlife.core.pagination.PageResult
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.nutrition.CustomIngredientDraft
import com.agusstkd.goodlife.domain.model.nutrition.CustomMealDraft
import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealPlanDraft
import com.agusstkd.goodlife.domain.model.nutrition.Ingredient
import com.agusstkd.goodlife.domain.model.nutrition.MealSummary
import com.agusstkd.goodlife.domain.model.nutrition.PortionUnit
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import kotlinx.datetime.LocalDate

class FakeMealPlanRepository : MealPlanRepository {
    var getResult: Result<List<DailyMealPlanSummary>> = Result.Success(emptyList())
    var createResult: Result<Long> = Result.Success(1L)
    var getHandler: (suspend (LocalDate) -> Result<List<DailyMealPlanSummary>>)? = null
    val requestedDates = mutableListOf<LocalDate>()
    var createCallCount = 0
    var lastCreateDraft: MealPlanDraft? = null

    override suspend fun getMealPlansByDate(date: LocalDate): Result<List<DailyMealPlanSummary>> {
        requestedDates.add(date)
        return getHandler?.invoke(date) ?: getResult
    }

    override suspend fun createMealPlan(draft: MealPlanDraft): Result<Long> {
        createCallCount++
        lastCreateDraft = draft
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
    var lastCreateIngredientDraft: CustomIngredientDraft? = null
    var lastCreateMealDraft: CustomMealDraft? = null

    override suspend fun searchIngredients(query: String, page: Int, pageSize: Int): Result<PageResult<Ingredient>> {
        searchIngredientsCallCount++
        return ingredientResult
    }

    override suspend fun searchMeals(query: String, page: Int, pageSize: Int): Result<PageResult<MealSummary>> {
        searchMealsCallCount++
        return mealResult
    }

    override suspend fun createCustomIngredient(draft: CustomIngredientDraft): Result<Ingredient> {
        lastCreateIngredientDraft = draft
        return createIngredientResult
    }

    override suspend fun createCustomMeal(draft: CustomMealDraft): Result<MealSummary> {
        lastCreateMealDraft = draft
        return createMealResult
    }

    companion object {
        fun defaultIngredient() = Ingredient(1L, "Rice", null, 100.0, PortionUnit.GRAM, 130.0, 2.7, 28.0, 0.3, true, null)
        fun defaultMeal() = MealSummary(1L, "Bowl", null, null, null, 1.0, PortionUnit.PIECE, 500.0, 30.0, 60.0, 10.0, true)
    }
}
