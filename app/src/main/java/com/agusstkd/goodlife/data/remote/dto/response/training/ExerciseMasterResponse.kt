package com.agusstkd.goodlife.data.remote.dto.response.training

import com.agusstkd.goodlife.domain.model.training.ExerciseMaster
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseMasterResponse(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val muscleGroupId: Long? = null,
    val muscleGroupName: String? = null,
    val score: Int? = null,
)

fun ExerciseMasterResponse.toDomain(): ExerciseMaster = ExerciseMaster(
    id = id,
    name = name.orEmpty(),
    description = description,
    imageUrl = imageUrl,
    muscleGroupId = muscleGroupId ?: 0L,
    muscleGroupName = muscleGroupName.orEmpty(),
    score = score ?: 0,
)
