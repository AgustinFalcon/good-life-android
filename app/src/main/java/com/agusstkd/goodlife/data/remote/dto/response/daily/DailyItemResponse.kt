package com.agusstkd.goodlife.data.remote.dto.response.daily

import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitLogSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.meal.MealSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.task.TaskSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.workout.WorkoutSummaryDto
import com.agusstkd.goodlife.domain.model.daily.DailyItem
import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.DailyItemStatus
import kotlinx.datetime.LocalTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Item del daily log (response del backend).
 *
 * Representa un item individual del daily log: task, habit, workout o meal.
 *
 * ## Campos opcionales:
 * Dependiendo del tipo ([itemType]), solo uno de los siguientes será no-null:
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
    val type: DailyItemType,

    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: DailyItemStatus,

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
 * - WORKOUT → workout.name, rutina formateada
 * - MEAL → mealPlan.mealName, calorías formateadas
 *
 * @return DailyItem listo para usar en ViewModel/UseCases
 */
fun DailyItemResponse.toDomain(): DailyItem {
    val (title, description) = extractTitleAndDescription()

    return DailyItem(
        id = id,
        type = type,
        referenceId = referenceId,
        scheduledTime = scheduledTime,
        status = status,
        title = title,
        description = description
    )
}

/**
 * Extrae título y descripción según el tipo de item.
 */
private fun DailyItemResponse.extractTitleAndDescription(): Pair<String, String?> {
    return when (type) {
        DailyItemType.TASK -> {
            (task?.title ?: "Tarea sin título") to task?.description
        }
        DailyItemType.HABIT -> {
            val habitName = habitLog?.habitName ?: "Hábito sin nombre"
            val description = habitLog?.let {
                "${it.currentValue} / ${it.targetValue} ${it.unit}"
            }
            habitName to description
        }
        DailyItemType.WORKOUT -> {
            val name = workout?.name ?: "Entrenamiento"
            val description = workout?.let {
                "Rutina: ${it.routineName} • ${it.exerciseCount} ejercicios"
            }
            name to description
        }
        DailyItemType.MEAL -> {
            val name = mealPlan?.mealName ?: "Comida"
            val description = mealPlan?.let {
                "${it.calories} kcal • ${it.protein}g proteína"
            }
            name to description
        }
    }
}