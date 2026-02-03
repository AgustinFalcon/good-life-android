package com.agusstkd.goodlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.agusstkd.goodlife.domain.model.auth.User

/**
 * Entidad Room para el usuario.
 *
 * Almacena los datos del usuario logueado en la base de datos local.
 * Solo se guarda UN usuario a la vez (el usuario actual).
 */
@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val profileImageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Convierte UserEntity a modelo de dominio User.
 */
fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
    profileImageUrl = profileImageUrl
)

/**
 * Convierte User de dominio a UserEntity para persistir.
 */
fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    name = name,
    profileImageUrl = profileImageUrl
)
