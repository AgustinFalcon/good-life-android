package com.agusstkd.goodlife.data.remote.dto.response.workout

import kotlinx.serialization.Serializable

/**
 * Resumen de un workout incluido en el daily log.
 *
 * @param id ID del workout
 * @param name Nombre del workout ("Push Day")
 * @param routineName Nombre de la rutina a la que pertenece ("PPL")
 * @param exerciseCount Cantidad de ejercicios del workout
 */
@Serializable
data class WorkoutSummaryDto(
    val id: Long,
    val name: String,
    val routineName: String,
    val exerciseCount: Int
)