package com.agusstkd.goodlife.data.remote.dto.response

import com.agusstkd.goodlife.domain.model.auth.User
import kotlinx.serialization.Serializable

/**
 * Respuesta del endpoint GET /api/v1/me
 * Datos del usuario autenticado: id, username, email, foto de perfil y roles.
 */
@Serializable
data class UserMeResponse(
    val id: Long,
    val username: String,
    val email: String,
    val profileImageUrl: String? = null,
    val roles: List<String> = emptyList(),
    val createdAt: String? = null
)

fun UserMeResponse.toDomain(): User = User(
    id = id.toString(),
    email = email,
    name = username,
    profileImageUrl = profileImageUrl
)
