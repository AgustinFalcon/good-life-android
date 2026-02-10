package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.core.result.map
import com.agusstkd.goodlife.data.remote.datasource.DailyRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.response.daily.toDomain
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.datetime.LocalDate


/**
 * Implementación del repositorio de daily logs.
 *
 * FASE 1: Solo backend (fuente de verdad 100%).
 * Backend maneja toda la lógica de generación y cálculo de daily logs.
 *
 * FASE 2 (futuro): Agregar Room para cache offline y SWR.
 *
 * ## Responsabilidades:
 * - Llamar al DataSource (capa de red)
 * - Mapear Response → Domain Model usando `.toDomain()`
 * - Devolver Result<DailyLog> (NO Result<DailyLogResponse>)
 *
 * @param remoteDataSource DataSource para llamadas HTTP
 */
class DailyRepositoryImpl(
    private val remoteDataSource: DailyRemoteDataSource
) : DailyRepository {

    /**
     * Obtiene el daily log de una fecha específica.
     *
     * LocalDate.toString() devuelve ISO-8601 ("2026-02-05"), compatible con el backend.
     * Response del backend se mapea a Domain Model antes de devolver.
     *
     * @param date Fecha del daily log
     * @return Result con DailyLog (Domain Model)
     */
    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        return remoteDataSource.getDailyLogByDate(date.toString())
            .map { response -> response.toDomain() }
    }

    /**
     * Actualiza el status de un item del daily log.
     *
     * El backend recalcula automáticamente el completionRate del daily log.
     * Response del backend se mapea a Domain Model antes de devolver.
     *
     * @param itemId ID del item a actualizar
     * @param status Nuevo status (COMPLETED, SKIPPED, PENDING)
     * @return Result con DailyLog actualizado
     */
    override suspend fun updateItemStatus(itemId: Long, status: ItemStatus): Result<DailyLog> {
        return remoteDataSource.updateItemStatus(itemId, status)
            .map { response -> response.toDomain() }
    }
}
