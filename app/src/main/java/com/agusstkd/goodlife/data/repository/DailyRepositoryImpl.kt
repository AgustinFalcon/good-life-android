package com.agusstkd.goodlife.data.repository

import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.local.dao.DailyDao
import com.agusstkd.goodlife.data.local.entity.toDomain
import com.agusstkd.goodlife.data.local.entity.toEntity
import com.agusstkd.goodlife.data.local.entity.toItemEntity
import com.agusstkd.goodlife.data.remote.datasource.remote.DailyRemoteDataSource
import com.agusstkd.goodlife.data.remote.dto.response.daily.toDomain
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.repository.DailyRepository
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.LocalDate

/**
 * Implementación del repositorio de daily logs con patrón SWR (Stale-While-Revalidate).
 *
 * ## Estrategia SWR:
 * 1. Intenta obtener data fresca del backend (fuente de verdad).
 * 2. Si el backend responde OK → guarda en cache y devuelve data fresca.
 * 3. Si el backend falla → usa cache como fallback (offline-first).
 * 4. Si no hay cache ni backend → devuelve el error original.
 *
 * ## Flujo:
 * ```
 * getDailyLog(date)
 *     ├─ Backend OK → save to Room → return fresh
 *     ├─ Backend FAIL + Cache exists → return stale (offline fallback)
 *     └─ Backend FAIL + No cache → return error
 * ```
 *
 * @param remoteDataSource DataSource para llamadas HTTP
 * @param dailyDao DAO Room para cache local
 */
class DailyRepositoryImpl(
    private val remoteDataSource: DailyRemoteDataSource,
    private val dailyDao: DailyDao,
) : DailyRepository {

    override suspend fun getDailyLog(date: LocalDate): Result<DailyLog> {
        val dateString = date.toString()

        return when (val remoteResult = remoteDataSource.getDailyLogByDate(dateString)) {
            is Result.Success -> {
                val freshLog = remoteResult.data.toDomain()
                saveToCacheQuietly(freshLog)
                Result.Success(freshLog)
            }
            is Result.Error -> {
                val cached = dailyDao.getDailyLogByDate(dateString)
                if (cached != null) {
                    Result.Success(cached.toDomain())
                } else {
                    remoteResult
                }
            }
        }
    }

    override suspend fun updateItemStatus(itemId: Long, status: DailyItemStatus): Result<DailyLog> {
        return when (val remoteResult = remoteDataSource.updateItemStatus(itemId, status)) {
            is Result.Success -> {
                val freshLog = remoteResult.data.toDomain()
                saveToCacheQuietly(freshLog)
                Result.Success(freshLog)
            }
            is Result.Error -> remoteResult
        }
    }

    /**
     * Guarda el daily log en cache sin propagar errores de Room.
     * Si Room falla, la app sigue funcionando con data del backend.
     */
    private suspend fun saveToCacheQuietly(dailyLog: DailyLog) {
        try {
            val logEntity = dailyLog.toEntity()
            val itemEntities = dailyLog.items.map { it.toItemEntity(dailyLog.id) }
            dailyDao.saveDailyLog(logEntity, itemEntities)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Cache failure is non-critical; backend data already returned
        }
    }
}
