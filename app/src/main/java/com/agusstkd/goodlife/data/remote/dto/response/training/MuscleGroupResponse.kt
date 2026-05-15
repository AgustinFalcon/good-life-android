package com.agusstkd.goodlife.data.remote.dto.response.training

import com.agusstkd.goodlife.domain.model.training.MuscleGroup
import kotlinx.serialization.Serializable

@Serializable
data class MuscleGroupResponse(
    val id: Long,
    val code: String? = null,
    val name: String? = null,
    val iconUrl: String? = null,
)

fun MuscleGroupResponse.toDomain(): MuscleGroup = MuscleGroup(
    id = id,
    code = code.orEmpty(),
    name = name.orEmpty(),
    iconUrl = iconUrl,
)
