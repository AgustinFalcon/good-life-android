package com.agusstkd.goodlife.data.remote.dto.response.habit

import kotlinx.serialization.Serializable


@Serializable
data class HabitLogSummaryDto(
    val id: Long,
    val habitId: Long,
    val habitName: String,
    val currentValue: Int,
    val targetValue: Int,
    val progress: Double,
    val unit: String
)
