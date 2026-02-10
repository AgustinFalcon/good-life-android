package com.agusstkd.goodlife.domain.usecase.daily

import com.agusstkd.goodlife.core.dispatcher.DispatcherProvider
import com.agusstkd.goodlife.data.remote.dto.response.daily.DailyLogResponse
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.coroutines.withContext


/**
 * Caso de uso para actualizar el status de un item del daily log.
 *
 * ## Responsabilidades:
 * - Cambiar el dispatcher a IO
 * - Llamar al repositorio
 * - Devolver Result tal cual
 *
 * ## Flujo:
 * 1. Backend actualiza el item
 * 2. Backend recalcula el completionRate del daily log
 * 3. Backend devuelve el daily log completo actualizado
 */
class UpdateItemStatusUseCase(
    private val repository: DailyRepository,
    private val dispatcher: DispatcherProvider
) {

    /**
     * Actualiza el status de un item.
     *
     * @param itemId ID del item a actualizar
     * @param status Nuevo status (COMPLETED, SKIPPED, PENDING)
     * @return Result con DailyLogResponse actualizado
     */
    suspend operator fun invoke(itemId: Long, status: ItemStatus): Result<DailyLogResponse> {
        return withContext(dispatcher.io) {
            repository.updateItemStatus(itemId, status)
        }
    }
}
