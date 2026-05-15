package com.agusstkd.goodlife.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para items individuales del daily log.
 *
 * Tiene FK a [DailyLogEntity] con CASCADE delete:
 * cuando se borra un daily log, se borran sus items automáticamente.
 */
@Entity(
    tableName = "daily_item",
    foreignKeys = [
        ForeignKey(
            entity = DailyLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dailyLogId")]
)
data class DailyItemEntity(
    @PrimaryKey
    val id: Long,
    val dailyLogId: Long,
    val type: String,
    val referenceId: Long,
    val scheduledTime: String?,
    val status: String,
    val title: String,
    val description: String?
)
