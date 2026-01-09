package com.agusstkd.goodlife.data.remote.dto.request

import kotlinx.serialization.Serializable

/**
 * Request para el endpoint POST /api/v1/register
 *
 * Validaciones del backend:
 * - Username: 3-50 caracteres, no vacío
 * - Email: formato válido, no vacío
 * - Password: mínimo 3 caracteres, no vacío
 */
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)
