package com.agusstkd.goodlife.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyLog
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * Relación Room 1:N entre [DailyLogEntity] y [DailyItemEntity].
 */
data class DailyLogWithItems(
    @Embedded val log: DailyLogEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "dailyLogId"
    )
    val items: List<DailyItemEntity>
)

fun DailyLogWithItems.toDomain(): DailyLog = DailyLog(
    id = log.id,
    date = LocalDate.parse(log.date),
    completionRate = log.completionRate,
    items = items.map { it.toDomain() }
)

fun DailyItemEntity.toDomain(): DailyItem = DailyItem(
    id = id,
    type = DailyItemType.valueOf(type),
    referenceId = referenceId,
    scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
    status = DailyItemStatus.valueOf(status),
    title = title,
    description = description
)

fun DailyLog.toEntity(): DailyLogEntity = DailyLogEntity(
    id = id,
    date = date.toString(),
    completionRate = completionRate
)

fun DailyItem.toItemEntity(dailyLogId: Long): DailyItemEntity = DailyItemEntity(
    id = id,
    dailyLogId = dailyLogId,
    type = type.name,
    referenceId = referenceId,
    scheduledTime = scheduledTime?.toString(),
    status = status.name,
    title = title,
    description = description
)
