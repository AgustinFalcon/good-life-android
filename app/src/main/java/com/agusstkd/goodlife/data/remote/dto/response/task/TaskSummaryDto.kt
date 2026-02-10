package com.agusstkd.goodlife.data.remote.dto.response.task

import kotlinx.serialization.Serializable

@Serializable
data class TaskSummaryDto(
    val id: Long,
    val title: String,
    val description: String?
)
