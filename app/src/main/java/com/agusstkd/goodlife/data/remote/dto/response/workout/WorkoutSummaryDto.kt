package com.agusstkd.goodlife.data.remote.dto.response.workout

import kotlinx.serialization.Serializable

/**
 * Resumen de un workout incluido en el daily log.
 *
 * NOTA: Estructura provisional — todos los campos son nullable con default
 * para evitar crashes por desalineación con el backend.
 * Alinear con la respuesta real cuando se implemente el módulo Workouts.
 */
@Serializable
data class WorkoutSummaryDto(
    val id: Long? = null,
    val name: String? = null,
    val routineName: String? = null,
    val exerciseCount: Int? = null,
)
