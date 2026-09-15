package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.nutrition.GetMealPlansUseCase
import com.agusstkd.goodlife.fake.FakeDailyRepository
import com.agusstkd.goodlife.fake.FakeMealPlanRepository
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.DailyDetailViewModel
import com.agusstkd.goodlife.presentation.screen.tabs.meals.detail.MealDetailViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class DetailKoinModuleTest {
    @Test
    fun parameterizedDetailViewModels_resolveWithIdAndDateArguments() {
        val application = startKoin {
            modules(
                module {
                    single<AppLanguage> { Spanish }
                    factory { GetDailyItemsUseCase(FakeDailyRepository(), TestDispatcherProvider()) }
                    factory { GetMealPlansUseCase(FakeMealPlanRepository(), TestDispatcherProvider()) }
                },
                dailyDetailModule,
                mealDetailModule,
            )
        }
        try {
            assertNotNull(application.koin.get<DailyDetailViewModel> { parametersOf(0L, "2026-09-14") })
            assertNotNull(application.koin.get<MealDetailViewModel> { parametersOf(0L, "2026-09-14") })
        } finally {
            stopKoin()
        }
    }
}