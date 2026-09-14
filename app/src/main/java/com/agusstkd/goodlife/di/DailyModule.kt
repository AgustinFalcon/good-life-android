package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.repository.DailyRepositoryImpl
import com.agusstkd.goodlife.domain.repository.DailyRepository
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyTabViewModel
import com.agusstkd.goodlife.presentation.screen.tabs.daily.detail.DailyDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

import com.agusstkd.goodlife.data.remote.api.daily.DailyApiService
import com.agusstkd.goodlife.data.remote.datasource.remote.DailyRemoteDataSource

/**
 * Módulo de Koin para Daily feature.
 *
 * Define dependencias de:
 * - DataSources: DailyRemoteDataSource
 * - Repositories: DailyRepository
 * - UseCases: GetDailyItems, UpdateItemStatus
 * - ViewModels: DailyTabViewModel
 *
 * Cache: [DailyRepositoryImpl] usa [DailyDao] + patrón SWR (backend primero, fallback Room).
 * Ver también [DAILY-IMPLEMENTATION-PLAN] en docs (texto puede estar desfasado respecto al código).
 */
/** Parameterized registration kept isolated so it can be validated without network bindings. */
internal val dailyDetailModule = module {
    viewModel { (itemId: Long, dateIso: String) ->
        DailyDetailViewModel(
            itemId = itemId,
            dateIso = dateIso,
            language = get(),
            getDailyItemsUseCase = get(),
        )
    }
}

val dailyModule = module {
    includes(dailyDetailModule)

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DATA SOURCES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * DailyRemoteDataSource - Llamadas HTTP de daily logs.
     */
    factory {
        DailyRemoteDataSource(apiService = get<DailyApiService>())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * DailyRepository - Repositorio de daily logs.
     * Singleton porque puede cachear estado (FASE 2).
     */
    single<DailyRepository> {
        DailyRepositoryImpl(
            remoteDataSource = get(),
            dailyDao = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // USE CASES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * GetDailyItemsUseCase - Obtener items del daily log.
     */
    factory {
        GetDailyItemsUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    /**
     * UpdateItemStatusUseCase - Actualizar status de un item.
     */
    factory {
        UpdateItemStatusUseCase(
            repository = get(),
            dispatcher = get()
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * DailyTabViewModel - ViewModel del tab Daily.
     */
    viewModel {
        DailyTabViewModel(
            dateProvider = get(),
            language = get(),
            getDailyItemsUseCase = get(),
            updateItemStatusUseCase = get()
        )
    }}
