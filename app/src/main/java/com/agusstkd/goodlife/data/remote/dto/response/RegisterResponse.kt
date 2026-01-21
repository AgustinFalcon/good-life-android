package com.agusstkd.goodlife.data.remote.dto.response

import com.agusstkd.goodlife.domain.model.User
import kotlinx.serialization.Serializable

/**
 * Respuesta del endpoint POST /api/v1/register
 *
 * Contiene los datos del usuario recién registrado (sin password).
 *
 * @property username Nombre de usuario
 * @property email Email del usuario
 * @property message Mensaje de confirmación del servidor
 */
@Serializable
data class RegisterResponse(
    val username: String,
    val email: String,
    val message: String? = null
)

/**
 * Convierte RegisterResponse a modelo de dominio User.
 */
fun RegisterResponse.toDomain(): User = User(
    id = "0",  // El backend no devuelve ID en registro
    email = email,
    name = username,
    profileImageUrl = null
)
