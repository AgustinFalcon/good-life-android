package com.agusstkd.goodlife.data.remote.dto.response.daily

import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitLogSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.nutrition.MealSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.task.TaskSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.workout.WorkoutSummaryDto
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Item del daily log (response del backend).
 *
 * Representa un item individual del daily log: task, habit, workout o meal.
 *
 * [type] y [status] llegan como String de wire. El traductor [toDomain]
 * los convierte con [DailyItemType.fromWire] y [DailyItemStatus.fromWire].
 *
 * ## Campos opcionales:
 * Dependiendo del tipo, solo uno de los siguientes será no-null:
 * - [task]: Si itemType == TASK
 * - [habitLog]: Si itemType == HABIT
 * - [workout]: Si itemType == WORKOUT
 * - [mealPlan]: Si itemType == MEAL
 *
 * El backend usa el campo "itemType" (no "type") en el JSON.
 *
 * @see DailyLogResponse
 * @see TaskSummaryDto
 * @see HabitLogSummaryDto
 * @see WorkoutSummaryDto
 * @see MealSummaryDto
 * @see DailyItem Domain model equivalente
 */
@Serializable
data class DailyItemResponse(
    val id: Long,

    @SerialName("itemType")
    val type: String,

    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: String,

    // Solo uno de estos será no-null según el tipo
    val task: TaskSummaryDto? = null,
    val habitLog: HabitLogSummaryDto? = null,
    val workout: WorkoutSummaryDto? = null,
    val mealPlan: MealSummaryDto? = null
)

/**
 * Convierte DailyItemResponse a modelo de dominio DailyItem.
 *
 * Extrae el título y descripción del campo correcto según el tipo:
 * - TASK → task.title, task.description
 * - HABIT → habitLog.habitName, progreso formateado
 * - WORKOUT → workout.name; hechos de rutina en el dominio (copy en la UI)
 * - MEAL → mealPlan.mealName, calorías formateadas
 *
 * Si [type] o [status] no pertenecen al conjunto cerrado, el item se descarta.
 *
 * @return DailyItem listo para usar en ViewModel/UseCases, o null si el wire es desconocido.
 */
fun DailyItemResponse.toDomain(): DailyItem? {
    val itemType = DailyItemType.fromWire(type)
    val itemStatus = DailyItemStatus.fromWire(status)
    if (itemType == DailyItemType.UNKNOWN || itemStatus == DailyItemStatus.UNKNOWN) {
        return null
    }

    val (title, description) = extractTitleAndDescription(itemType, itemStatus)

    return DailyItem(
        id = id,
        type = itemType,
        referenceId = referenceId,
        scheduledTime = scheduledTime,
        status = itemStatus,
        title = title,
        description = description,
        workoutRoutineName = workout?.routineName,
        workoutExerciseCount = workout?.exerciseCount,
        workoutDayOrder = workout?.dayOrder,
    )
}

private fun DailyItemResponse.extractTitleAndDescription(
    itemType: DailyItemType,
    itemStatus: DailyItemStatus,
): Pair<String, String?> {
    return when (itemType) {
        DailyItemType.TASK -> {
            (task?.title ?: "Tarea sin título") to task?.description
        }
        DailyItemType.HABIT -> {
            val habitName = habitLog?.habit?.name ?: "Hábito sin nombre"
            val unit = habitLog?.habit?.unit ?: ""
            val target = habitLog?.targetValue?.toInt() ?: habitLog?.habit?.targetValue ?: 0
            val logged = habitLog?.currentValue?.toInt() ?: 0
            val current = if (itemStatus == DailyItemStatus.COMPLETED && logged < target) target else logged
            val description = "$current / $target $unit".trimEnd()
            habitName to description
        }
        DailyItemType.WORKOUT -> {
            val name = workout?.name ?: "Entrenamiento"
            name to null
        }
        DailyItemType.MEAL -> {
            val name = mealPlan?.mealName ?: "Comida"
            val description = mealPlan?.let {
                val cal = it.calories ?: 0
                val prot = it.protein ?: 0
                "$cal kcal • ${prot}g proteína"
            }
            name to description
        }
        DailyItemType.UNKNOWN -> "" to null
    }
}
