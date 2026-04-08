package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.remote.api.habit.HabitApiService
import com.agusstkd.goodlife.data.remote.datasource.remote.HabitRemoteDataSource
import com.agusstkd.goodlife.data.repository.HabitRepositoryImpl
import com.agusstkd.goodlife.domain.repository.HabitRepository
import com.agusstkd.goodlife.domain.usecase.habit.CreateHabitUseCase
import com.agusstkd.goodlife.presentation.screen.add.habit.CreateHabitViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo de Koin para la feature de Habit (crear hábito).
 *
 * Define dependencias siguiendo la cadena:
 * DataSource → Repository → UseCase → ViewModel.
 *
 * ## Dependencias externas (resueltas por otros módulos)
 * - [networkModule]: [HabitApiService]
 * - [coreModule]: [AppLanguage], [DispatcherProvider], [ComposeNavigationController]
 *
 * FASE 1: Solo creación de hábito.
 * FASE 2 (futuro): Edición, desactivación, listado.
 */
val habitModule = module {

    // ═══════════════════════════════════════════════════════════════════
    // DATA SOURCES
    // ═══════════════════════════════════════════════════════════════════

    factory {
        HabitRemoteDataSource(habitApiService = get<HabitApiService>())
    }

    // ═══════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════

    single<HabitRepository> {
        HabitRepositoryImpl(habitRemoteDataSource = get())
    }

    // ═══════════════════════════════════════════════════════════════════
    // USE CASES
    // ═══════════════════════════════════════════════════════════════════

    factory {
        CreateHabitUseCase(
            repository = get(),
            dispatcher = get(),
        )
    }

    // ═══════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════

    viewModel {
        CreateHabitViewModel(
            navigationController = get(),
            language = get(),
            createHabitUseCase = get(),
        )
    }
}
