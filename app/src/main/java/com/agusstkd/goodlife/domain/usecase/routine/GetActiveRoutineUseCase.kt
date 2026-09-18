package com.agusstkd.goodlife.domain.usecase.routine

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.RoutineRepository
import com.agusstkd.goodlife.domain.usecase.routine.result.GetActiveRoutineResult
import kotlinx.coroutines.withContext

/**
 * Caso de uso: obtener la rutina activa del usuario autenticado.
 *
 * ## Responsabilidades:
 * - Delegar al [RoutineRepository] la llamada al backend
 * - Mapear [Result] → [GetActiveRoutineResult] semántico
 * - Ejecutar en [DispatcherProvider.io] (responsabilidad del UseCase)
 *
 * ## Flujo:
 * ```
 * WorkoutsTabViewModel
 *     ↓
 * GetActiveRoutineUseCase()
 *     ↓
 * RoutineRepository.getActiveRoutine()
 *     ↓
 * RoutineRemoteDataSource → RoutineApiService → GET /api/v1/routines/active
 *     ↓
 * GetActiveRoutineResult.Success | NotFound | ServerError | NetworkError
 * ```
 */
class GetActiveRoutineUseCase(
    private val repository: RoutineRepository,
    private val dispatcher: DispatcherProvider,
) {

    suspend operator fun invoke(): GetActiveRoutineResult {
        return withContext(dispatcher.io) {
            when (val result = repository.getActiveRoutine()) {
                is Result.Success -> result.data?.let(GetActiveRoutineResult::Success) ?: GetActiveRoutineResult.NotFound
                is Result.Error -> when (val ex = result.exception) {
                    is ApiException.NotFoundException -> GetActiveRoutineResult.NotFound
                    is ApiException.ServerException -> GetActiveRoutineResult.ServerError(ex.message)
                    else -> GetActiveRoutineResult.NetworkError
                }
            }
        }
    }
}
