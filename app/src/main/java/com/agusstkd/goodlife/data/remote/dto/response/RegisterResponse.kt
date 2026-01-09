package com.agusstkd.goodlife.data.remote.dto.response

import com.agusstkd.goodlife.domain.model.User
import kotlinx.serialization.Serializable

/**
 * Respuesta del endpoint POST /api/v1/register
 *
 * Contiene los datos del usuario recién registrado.
 */
@Serializable
data class RegisterResponse(
    val id: Long,
    val username: String,
    val email: String,
    val createdAt: String? = null
)

/**
 * Convierte RegisterResponse a modelo de dominio User.
 */
fun RegisterResponse.toDomain(): User = User(
    id = id.toString(),
    email = email,
    name = username,
    profileImageUrl = null
)
