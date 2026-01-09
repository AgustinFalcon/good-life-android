package com.agusstkd.goodlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agusstkd.goodlife.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de base de datos del usuario.
 */
@Dao
interface UserDao {

    /**
     * Inserta o reemplaza el usuario actual.
     * Como solo guardamos un usuario, usamos REPLACE.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    /**
     * Obtiene el usuario actual (el primero de la tabla).
     * Solo debería haber uno.
     */
    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): UserEntity?

    /**
     * Observa cambios en el usuario actual.
     * Útil para actualizar la UI automáticamente.
     */
    @Query("SELECT * FROM user LIMIT 1")
    fun observeUser(): Flow<UserEntity?>

    /**
     * Elimina todos los usuarios (logout).
     */
    @Query("DELETE FROM user")
    suspend fun deleteUser()

    /**
     * Verifica si hay un usuario guardado.
     */
    @Query("SELECT COUNT(*) FROM user")
    suspend fun hasUser(): Int
}
