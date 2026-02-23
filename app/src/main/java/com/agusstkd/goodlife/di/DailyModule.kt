package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.repository.DailyRepositoryImpl
import com.agusstkd.goodlife.domain.repository.DailyRepository
import com.agusstkd.goodlife.domain.usecase.daily.GetDailyItemsUseCase
import com.agusstkd.goodlife.domain.usecase.daily.UpdateItemStatusUseCase
import com.agusstkd.goodlife.presentation.screen.tabs.daily.DailyTabViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

import com.agusstkd.goodlife.data.remote.datasource.DailyRemoteDataSource

/**
 * Módulo de Koin para Daily feature.
 *
 * Define dependencias de:
 * - DataSources: DailyRemoteDataSource
 * - Repositories: DailyRepository
 * - UseCases: GetDailyItems, UpdateItemStatus
 * - ViewModels: DailyTabViewModel
 *
 * FASE 1: Solo backend (sin Room/cache).
 * FASE 2 (futuro): Agregar DailyDao y SWR cache strategy.
 */
val dailyModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DATA SOURCES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * DailyRemoteDataSource - Llamadas HTTP de daily logs.
     */
    factory {
        DailyRemoteDataSource(apiService = get())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * DailyRepository - Repositorio de daily logs.
     * Singleton porque puede cachear estado (FASE 2).
     */
    single<DailyRepository> {
        DailyRepositoryImpl(remoteDataSource = get())
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
            dispatcher = get(),
            getDailyItemsUseCase = get(),
            updateItemStatusUseCase = get()
        )
    }
}