package com.agusstkd.goodlife.data.remote.dto.response.daily

import com.agusstkd.goodlife.domain.model.daily.DailyItemType
import com.agusstkd.goodlife.domain.model.daily.ItemStatus
import kotlinx.datetime.LocalTime

import com.agusstkd.goodlife.data.remote.dto.response.habit.HabitLogSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.meal.MealSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.task.TaskSummaryDto
import com.agusstkd.goodlife.data.remote.dto.response.workout.WorkoutSummaryDto
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
 */
@Serializable
data class DailyItemResponse(
    val id: Long,

    @SerialName("itemType")
    val type: DailyItemType,

    val referenceId: Long,
    val scheduledTime: LocalTime?,
    val status: ItemStatus,

    // Solo uno de estos será no-null según el tipo
    val task: TaskSummaryDto? = null,
    val habitLog: HabitLogSummaryDto? = null,
    val workout: WorkoutSummaryDto? = null,
    val mealPlan: MealSummaryDto? = null
)