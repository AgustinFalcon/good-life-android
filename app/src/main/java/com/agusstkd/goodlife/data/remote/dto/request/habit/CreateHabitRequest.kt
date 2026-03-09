package com.agusstkd.goodlife.data.remote.dto.request.habit

import kotlinx.serialization.Serializable

/**
 * DTO para el body de `POST /api/v1/habits`.
 *
 * Los campos de fecha y hora se serializan como strings ISO
 * porque el backend los parsea así (YYYY-MM-DD, HH:mm:ss).
 *
 * @see com.agusstkd.goodlife.data.repository.HabitRepositoryImpl
 */
@Serializable
data class CreateHabitRequest(
    val name: String,
    val description: String?,
    val category: String,
    val targetValue: Int,
    val unit: String,
    val daysOfWeek: List<Int>,
    val startDate: String,
    val endDate: String?,
    val scheduledTime: String?,
)
