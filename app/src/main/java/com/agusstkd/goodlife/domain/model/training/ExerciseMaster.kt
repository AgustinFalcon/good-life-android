package com.agusstkd.goodlife.domain.model.training



data class ExerciseMaster(
    val id: Long,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val muscleGroupId: Long,
    val muscleGroupName: String,
    val score: Int,
)
