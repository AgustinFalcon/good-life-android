package com.agusstkd.goodlife.domain.usecase.workoutlog

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.training.FinishWorkoutDraft
import com.agusstkd.goodlife.domain.repository.WorkoutLogRepository
import com.agusstkd.goodlife.domain.usecase.workoutlog.result.FinishWorkoutResult
import kotlinx.coroutines.withContext

/**
 * Registra un entrenamiento terminado.
 *
 * Valida que haya ejercicios, delega al [WorkoutLogRepository] y mapea [Result]
 * a [FinishWorkoutResult]. No conoce AppLanguage ni HTTP.
 */
class FinishWorkoutUseCase(
    private val repository: WorkoutLogRepository,
    private val dispatcher: DispatcherProvider,
) {

    /**
     * @param draft Sesión realizada. Si [FinishWorkoutDraft.exercises] está vacío
     * no se llama al repositorio.
     */
    suspend operator fun invoke(draft: FinishWorkoutDraft): FinishWorkoutResult {
        if (draft.exercises.isEmpty()) return FinishWorkoutResult.EmptyExercises

        return withContext(dispatcher.io) {
            when (val result = repository.finishWorkout(draft)) {
                is Result.Success -> FinishWorkoutResult.Success(logId = result.data)
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.NotFoundException -> FinishWorkoutResult.NotFound
                    is ApiException.ServerException -> FinishWorkoutResult.ServerError(ex.message)
                    else -> FinishWorkoutResult.NetworkError
                }
            }
        }
    }
}
