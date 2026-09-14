package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.remote.api.nutrition.MealPlanApiService
import com.agusstkd.goodlife.data.remote.api.nutrition.NutritionCatalogApiService
import com.agusstkd.goodlife.data.remote.datasource.remote.MealPlanRemoteDataSource
import com.agusstkd.goodlife.data.remote.datasource.remote.NutritionCatalogRemoteDataSource
import com.agusstkd.goodlife.data.repository.MealPlanRepositoryImpl
import com.agusstkd.goodlife.data.repository.NutritionCatalogRepositoryImpl
import com.agusstkd.goodlife.domain.repository.MealPlanRepository
import com.agusstkd.goodlife.domain.repository.NutritionCatalogRepository
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateCustomIngredientUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateCustomMealUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.CreateMealPlanUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.GetMealPlansUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.SearchIngredientsUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.SearchMealsUseCase
import com.agusstkd.goodlife.presentation.screen.add.mealplan.CreateMealPlanViewModel
import com.agusstkd.goodlife.presentation.screen.tabs.meals.MealsTabViewModel
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

/** Parameterized registration kept isolated so it can be validated without network bindings. */
internal val mealDetailModule = module {
    viewModel { (planId: Long, dateIso: String) ->
        MealDetailViewModel(
            planId = planId,
            dateIso = dateIso,
            language = get(),
            getMealPlansUseCase = get(),
        )
    }
}

val nutritionModule = module {
    includes(mealDetailModule)

    // ── API Services ──────────────────────────────────────────────────────────
    single<NutritionCatalogApiService> {
        get<Retrofit>().create(NutritionCatalogApiService::class.java)
    }
    single<MealPlanApiService> {
        get<Retrofit>().create(MealPlanApiService::class.java)
    }

    // ── DataSources ───────────────────────────────────────────────────────────
    factory { NutritionCatalogRemoteDataSource(apiService = get()) }
    factory { MealPlanRemoteDataSource(apiService = get()) }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<NutritionCatalogRepository> {
        NutritionCatalogRepositoryImpl(remoteDataSource = get())
    }
    single<MealPlanRepository> {
        MealPlanRepositoryImpl(remoteDataSource = get())
    }

    // ── Use Cases ─────────────────────────────────────────────────────────────
    factory { SearchMealsUseCase(repository = get(), dispatcher = get()) }
    factory { SearchIngredientsUseCase(repository = get(), dispatcher = get()) }
    factory { CreateCustomIngredientUseCase(repository = get(), dispatcher = get()) }
    factory { CreateCustomMealUseCase(repository = get(), dispatcher = get()) }
    factory { CreateMealPlanUseCase(repository = get(), dispatcher = get()) }
    factory { GetMealPlansUseCase(repository = get(), dispatcher = get()) }

    // ── ViewModel ─────────────────────────────────────────────────────────────
    viewModel {
        MealsTabViewModel(
            dateProvider = get(),
            language = get(),
            getMealPlansUseCase = get(),
            navigationController = get(),
        )
    }
    viewModel {
        CreateMealPlanViewModel(
            navigationController = get(),
            language = get(),
            createMealPlanUseCase = get(),
            createCustomMealUseCase = get(),
            createCustomIngredientUseCase = get(),
            searchMealUseCase = get(),
            searchIngredientUseCase = get(),
        )
    }
}
