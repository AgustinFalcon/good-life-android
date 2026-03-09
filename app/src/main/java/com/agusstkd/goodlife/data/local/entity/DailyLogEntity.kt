package com.agusstkd.goodlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para cachear daily logs (SWR pattern).
 *
 * @property id ID del daily log (mismo que el backend).
 * @property date Fecha ISO-8601 ("2026-03-09").
 * @property completionRate Porcentaje de completado [0.0, 1.0].
 * @property cachedAt Timestamp (millis) de cuando se guardó en cache.
 */
@Entity(tableName = "daily_log")
data class DailyLogEntity(
    @PrimaryKey
    val id: Long,
    val date: String,
    val completionRate: Double,
    val cachedAt: Long = System.currentTimeMillis()
)
