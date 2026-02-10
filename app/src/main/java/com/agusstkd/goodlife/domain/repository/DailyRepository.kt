package com.agusstkd.goodlife.domain.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import kotlinx.datetime.LocalDate

/**
 * Repositorio para acceso a daily logs.
 *
 * FASE 1: Solo backend (fuente de verdad 100%).
 * FASE 2 (futuro): Agregar Room para cache offline con SWR.
 */
interface DailyRepository {

    /**
     * Obtiene el daily log de una fecha específica.
     *
     * @param date Fecha del daily log
     * @return Result con DailyLog (Domain Model)
     */
    suspend fun getDailyLog(date: LocalDate): Result<DailyLog>

    /**
     * Actualiza el status de un item del daily log.
     *
     * @param itemId ID del item a actualizar
     * @param status Nuevo status (COMPLETED, SKIPPED, PENDING)
     * @return Result con DailyLog actualizado
     */
    suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLog>
}
