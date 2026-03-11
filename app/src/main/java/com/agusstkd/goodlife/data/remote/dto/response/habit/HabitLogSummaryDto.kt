package com.agusstkd.goodlife.data.remote.dto.response.habit

import kotlinx.serialization.Serializable

@Serializable
data class HabitLogSummaryDto(
    val id: Long,
    val habitId: Long? = null,
    val userId: Long? = null,
    val date: String? = null,
    val targetValue: Double? = null,
    val currentValue: Double? = null,
    val isCompleted: Boolean = false,
    val completedAt: String? = null,
    val notes: String? = null,
    val habit: HabitSummaryDto? = null,
)

@Serializable
data class HabitSummaryDto(
    val id: Long,
    val name: String,
    val unit: String,
    val targetValue: Int,
    val category: String,
)
