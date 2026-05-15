package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.core.network.ApiException
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.repository.DailyRepository
import com.agusstkd.goodlife.domain.usecase.daily.result.UpdateItemStatusResult
import kotlinx.coroutines.withContext

/**
 * Caso de uso para actualizar el status de un item del daily log.
 *
 * ## Responsabilidades
 * - Cambiar el dispatcher a IO.
 * - Llamar al repositorio.
 * - Convertir el [Result] genérico del repositorio a [UpdateItemStatusResult],
 *   traduciendo cada tipo de [ApiException] a un subtipo semántico concreto.
 *
 * ## Flujo
 * 1. Backend actualiza el status del item.
 * 2. Backend recalcula el completionRate del daily log.
 * 3. Backend devuelve el daily log completo actualizado.
 * 4. Este UseCase lo envuelve en [UpdateItemStatusResult.Success].
 *
 * ## Mapping de errores
 * | Result.Error causa                  | UpdateItemStatusResult              |
 * |-------------------------------------|-------------------------------------|
 * | [ApiException.NotFoundException]    | [UpdateItemStatusResult.NotFound]   |
 * | [ApiException.ServerException]      | [UpdateItemStatusResult.ServerError]|
 * | IOException / cualquier otro        | [UpdateItemStatusResult.NetworkError]|
 */
class UpdateItemStatusUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {

    /**
     * Actualiza el status de un item.
     *
     * @param itemId ID del item a actualizar.
     * @param status Nuevo status (COMPLETED, SKIPPED, PENDING).
     * @return [UpdateItemStatusResult] con el subtipo correspondiente al resultado.
     */
    suspend operator fun invoke(itemId: Long, status: DailyItemStatus): UpdateItemStatusResult {
        return withContext(dispatcher.io) {
            when (val result = repository.updateItemStatus(itemId, status)) {
                is Result.Success -> UpdateItemStatusResult.Success(dailyLog = result.data)
                is Result.Error   -> when (val ex = result.exception) {
                    is ApiException.NotFoundException -> UpdateItemStatusResult.NotFound
                    is ApiException.ServerException   -> UpdateItemStatusResult.ServerError(
                        message = ex.message ?: "Error de servidor"
                    )
                    else -> UpdateItemStatusResult.NetworkError
                }
            }
        }
    }
}
