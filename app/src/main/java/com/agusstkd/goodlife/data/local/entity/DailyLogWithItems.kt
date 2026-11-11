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

/** Convierte la relación Room a [DailyLog], descartando items con type o status desconocidos. */
fun DailyLogWithItems.toDomain(): DailyLog = DailyLog(
    id = log.id,
    date = LocalDate.parse(log.date),
    completionRate = log.completionRate,
    items = items.mapNotNull { it.toDomain() }
)

/**
 * Convierte la fila Room a [DailyItem].
 *
 * Si el tipo o el status persistidos no pertenecen al conjunto cerrado, el item se descarta.
 *
 * @return El item de dominio, o null si type/status son [DailyItemType.UNKNOWN] o [DailyItemStatus.UNKNOWN].
 */
fun DailyItemEntity.toDomain(): DailyItem? {
    val itemType = DailyItemType.fromWire(type)
    val itemStatus = DailyItemStatus.fromWire(status)
    if (itemType == DailyItemType.UNKNOWN || itemStatus == DailyItemStatus.UNKNOWN) {
        return null
    }
    return DailyItem(
        id = id,
        type = itemType,
        referenceId = referenceId,
        scheduledTime = scheduledTime?.let { LocalTime.parse(it) },
        status = itemStatus,
        title = title,
        description = description,
        workoutRoutineName = workoutRoutineName,
        workoutExerciseCount = workoutExerciseCount,
        workoutDayOrder = workoutDayOrder,
        mealCalories = mealCalories,
        mealProtein = mealProtein,
    )
}

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
    description = description,
    workoutRoutineName = workoutRoutineName,
    workoutExerciseCount = workoutExerciseCount,
    workoutDayOrder = workoutDayOrder,
    mealCalories = mealCalories,
    mealProtein = mealProtein,
)
