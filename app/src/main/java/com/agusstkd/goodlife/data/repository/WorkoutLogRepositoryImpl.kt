package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.remote.datasource.remote.WorkoutLogRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.request.workout.toRequest
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft
import com.agusstkd.goodlife.domain.repository.WorkoutLogRepository

/**
 * Implementación de [WorkoutLogRepository].
 *
 * Traduce [FinishWorkoutDraft] a DTO con [toRequest] y delega al DataSource.
 * El `data` ya es `Long`: no hay `toDomain`. El UseCase abre el [Result]
 * y lo convierte a [com.agusstkd.goodlife.domain.usecase.workoutlog.result.FinishWorkoutResult].
 *
 * @property workoutLogRemoteDataSource Llamada HTTP de finish.
 * @see WorkoutLogRemoteDataSource
 */
class WorkoutLogRepositoryImpl(
    private val workoutLogRemoteDataSource: WorkoutLogRemoteDataSource

) : WorkoutLogRepository {

    override suspend fun finishWorkout(draft: FinishWorkoutDraft): Result<Long> {
        return workoutLogRemoteDataSource.finishWorkout(draft.toRequest())
    }
}