package com.agusstkd.goodlife.data.remote.dto.response.nutrition

import com.agusstkd.goodlife.domain.model.nutrition.DailyMealPlanSummary
import com.agusstkd.goodlife.domain.model.nutrition.MealType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.isoDayNumber
import kotlinx.serialization.Serializable

/**
 * Item de `GET /api/v1/meal-plans`.
 *
 * El backend devuelve el plan persistido ([mealName], [daysOfWeek], fechas), no un
 * recorte diario. El cliente filtra el día pedido en [toDailySummary].
 */
@Serializable
data class DailyMealPlanResponseDto(
    val id: Long? = null,
    val mealId: Long? = null,
    val mealName: String? = null,
    val name: String? = null,
    val mealImageUrl: String? = null,
    val imageUrl: String? = null,
    val mealType: String? = null,
    val scheduledTime: String? = null,
    val daysOfWeek: List<Int>? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val date: String? = null,
    val totalCalories: Double? = null,
    val totalProtein: Double? = null,
    val totalCarbs: Double? = null,
    val totalFat: Double? = null,
    val isActive: Boolean? = null,
    val isCustomMeal: Boolean? = null,
)

internal fun DailyMealPlanResponseDto.toDailySummary(date: LocalDate): DailyMealPlanSummary? {
    val resolvedId = id ?: return null
    val resolvedName = mealName ?: name ?: return null
    if (!appliesTo(date)) return null

    val mealTypeEnum = runCatching { MealType.valueOf(mealType ?: "") }.getOrNull() ?: MealType.SNACK
    val scheduledTimeLocal = scheduledTime?.let { timeStr ->
        runCatching { LocalTime.parse(timeStr) }.getOrNull()
    }

    return DailyMealPlanSummary(
        id = resolvedId,
        name = resolvedName,
        date = date,
        mealType = mealTypeEnum,
        scheduledTime = scheduledTimeLocal,
        mealName = mealName ?: name,
        imageUrl = (mealImageUrl ?: imageUrl)?.takeIf { it.isNotBlank() },
        totalCalories = totalCalories ?: 0.0,
        totalProtein = totalProtein ?: 0.0,
        totalCarbs = totalCarbs ?: 0.0,
        totalFat = totalFat ?: 0.0,
        isActive = isActive ?: true,
    )
}

internal fun DailyMealPlanResponseDto.appliesTo(date: LocalDate): Boolean {
    val days = daysOfWeek.orEmpty()
    if (days.isNotEmpty() && date.dayOfWeek.isoDayNumber !in days) return false
    val start = startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    val end = endDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    if (start != null && date < start) return false
    if (end != null && date > end) return false
    return true
}
