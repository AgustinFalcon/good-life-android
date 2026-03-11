package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.remote.api.training.RoutineApiService
import com.agusstkd.goodlife.data.remote.api.training.TrainingCatalogApiService
import com.agusstkd.goodlife.data.remote.datasource.RoutineRemoteDataSource
import com.agusstkd.goodlife.data.remote.datasource.TrainingCatalogRemoteDataSource
import com.agusstkd.goodlife.data.repository.RoutineRepositoryImpl
import com.agusstkd.goodlife.data.repository.TrainingCatalogRepositoryImpl
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import com.agusstkd.goodlife.domain.repository.TrainingCatalogRepository
import com.agusstkd.goodlife.domain.usecase.routine.ActivateRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.CreateRoutineUseCase
import com.agusstkd.goodlife.domain.usecase.routine.GetMuscleGroupsUseCase
import com.agusstkd.goodlife.domain.usecase.routine.SearchExercisesUseCase
import com.agusstkd.goodlife.presentation.screen.add.routine.CreateRoutineViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val routineModule = module {

    // ── Data Sources ──
    factory { TrainingCatalogRemoteDataSource(trainingCatalogApiService = get()) }
    factory { RoutineRemoteDataSource(routineApiService = get()) }

    // ── Repositories ──
    single<TrainingCatalogRepository> { TrainingCatalogRepositoryImpl(trainingCatalogRemoteDataSource = get()) }
    single<RoutineRepository> { RoutineRepositoryImpl(routineRemoteDataSource = get()) }

    // ── Use Cases ──
    factory { GetMuscleGroupsUseCase(repository = get(), dispatcher = get()) }
    factory { SearchExercisesUseCase(repository = get(), dispatcher = get()) }
    factory { CreateRoutineUseCase(repository = get(), dispatcher = get()) }
    factory { ActivateRoutineUseCase(repository = get(), dispatcher = get()) }

    // ── ViewModel ──
    viewModel {
        CreateRoutineViewModel(
            navigationController = get(),
            language = get(),
            createRoutineUseCase = get(),
            activateRoutineUseCase = get(),
            getMuscleGroupsUseCase = get(),
            searchExercisesUseCase = get(),
        )
    }
}
