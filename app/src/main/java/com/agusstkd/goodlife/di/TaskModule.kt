package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.remote.api.task.TaskApiService
import com.agusstkd.goodlife.data.remote.datasource.remote.TaskRemoteDataSource
import com.agusstkd.goodlife.data.repository.TaskRepositoryImpl
import com.agusstkd.goodlife.domain.repository.TaskRepository
import com.agusstkd.goodlife.domain.usecase.task.CreateTaskUseCase
import com.agusstkd.goodlife.presentation.screen.add.task.CreateTaskViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo de Koin para la feature de Task (crear tarea).
 *
 * Define dependencias siguiendo el patrón de [dailyModule]:
 * DataSource → Repository → UseCase → ViewModel.
 *
 * ## Dependencias externas (resueltas por otros módulos)
 * - [networkModule]: [TaskApiService]
 * - [coreModule]: [AppLanguage], [DispatcherProvider]
 *
 * FASE 1: Solo creación de tarea.
 * FASE 2 (futuro): Edición, eliminación, detalle.
 */
val taskModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DATA SOURCES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * TaskRemoteDataSource — llamadas HTTP del módulo Task.
     */
    factory {
        TaskRemoteDataSource(taskApiService = get<TaskApiService>())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // REPOSITORIES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * TaskRepository — repositorio de tareas.
     * Singleton para permitir cache en FASE 2.
     */
    single<TaskRepository> {
        TaskRepositoryImpl(taskRemoteDataSource = get())
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // USE CASES
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * CreateTaskUseCase — crear nueva tarea con validación.
     */
    factory {
        CreateTaskUseCase(
            repository = get(),
            dispatcher = get(),
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * CreateTaskViewModel — ViewModel de la pantalla de creación de tarea.
     */
    viewModel {
        CreateTaskViewModel(
            navigationController = get(),
            language = get(),
            createTaskUseCase = get(),
        )
    }
}