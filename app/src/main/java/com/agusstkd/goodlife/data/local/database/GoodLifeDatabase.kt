package com.agusstkd.goodlife.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.data.local.entity.UserEntity

/**
 * Base de datos Room de GoodLife.
 *
 * Implementa patrón Singleton thread-safe para garantizar
 * una única instancia de la base de datos.
 *
 * Entidades actuales:
 * - UserEntity: Usuario logueado
 *
 * TODO: Agregar entidades para:
 * - Tasks (caché offline)
 * - Habits (caché offline)
 * - Routines (caché offline)
 */
@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class GoodLifeDatabase : RoomDatabase() {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DAOs
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    abstract fun userDao(): UserDao

    // TODO: Agregar más DAOs cuando se implementen las features
    // abstract fun taskDao(): TaskDao
    // abstract fun habitDao(): HabitDao
    // abstract fun routineDao(): RoutineDao
    // abstract fun mealDao(): MealDao

    companion object {
        const val DATABASE_NAME = "goodlife_db"

        @Volatile
        private var INSTANCE: GoodLifeDatabase? = null

        /**
         * Obtiene la instancia singleton de la base de datos.
         * Thread-safe mediante double-checked locking.
         *
         * @param context Context de la aplicación
         * @return Instancia única de GoodLifeDatabase
         */
        fun getInstance(context: Context): GoodLifeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): GoodLifeDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GoodLifeDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration() // En desarrollo: recrear si cambia schema
                // .addMigrations(MIGRATION_1_2) // Para producción: definir migraciones
                .build()
        }
    }
}
