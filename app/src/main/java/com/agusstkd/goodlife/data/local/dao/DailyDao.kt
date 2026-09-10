package com.agusstkd.goodlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.agusstkd.goodlife.data.local.entity.DailyItemEntity
import com.agusstkd.goodlife.data.local.entity.DailyLogEntity
import com.agusstkd.goodlife.data.local.entity.DailyLogWithItems

/**
 * DAO para operaciones de cache de daily logs.
 *
 * Usado por el patrón SWR: cache-first → revalidate con backend.
 */
@Dao
interface DailyDao {

    /**
     * Obtiene el daily log cacheado con todos sus items para una fecha dada.
     * @return null si no hay cache para esa fecha.
     */
    @Transaction
    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    suspend fun getDailyLogByDate(date: String): DailyLogWithItems?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyItems(items: List<DailyItemEntity>)

    @Query("DELETE FROM daily_item WHERE dailyLogId = :logId")
    suspend fun deleteItemsByLogId(logId: Long)

    /**
     * Guarda un daily log completo (log + items) en una transacción.
     * Primero borra los items viejos para evitar duplicados por IDs que ya no existen.
     */
    @Transaction
    suspend fun saveDailyLog(log: DailyLogEntity, items: List<DailyItemEntity>) {
        deleteItemsByLogId(log.id)
        insertDailyLog(log)
        insertDailyItems(items)
    }

    /** Elimina todo el cache diario al cerrar sesión para evitar datos entre cuentas. */
    @Transaction
    suspend fun clearCache() {
        deleteAllItems()
        deleteAllLogs()
    }

    @Query("DELETE FROM daily_item")
    suspend fun deleteAllItems()

    @Query("DELETE FROM daily_log")
    suspend fun deleteAllLogs()
    /**
     * Limpia cache viejo (entries con más de [maxAgeMillis] desde [now]).
     */
    @Query("DELETE FROM daily_log WHERE cachedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}
