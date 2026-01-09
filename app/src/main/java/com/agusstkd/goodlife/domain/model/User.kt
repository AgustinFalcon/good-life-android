package com.agusstkd.goodlife.domain.model

/**
 * Modelo de dominio para el usuario.
 *
 * Representa la información básica del usuario autenticado.
 * Este modelo es independiente de la capa de datos (DTO, Entity).
 *
 * @property id Identificador único del usuario
 * @property email Correo electrónico
 * @property name Nombre del usuario
 * @property profileImageUrl URL de la imagen de perfil (opcional)
 */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val profileImageUrl: String? = null
)
