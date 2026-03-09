package com.agusstkd.goodlife.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.agusstkd.goodlife.data.local.dao.DailyDao
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.data.local.entity.DailyItemEntity
import com.agusstkd.goodlife.data.local.entity.DailyLogEntity
import com.agusstkd.goodlife.data.local.entity.UserEntity

/**
 * Base de datos Room de GoodLife.
 *
 * ## Entidades:
 * - [UserEntity]: Usuario autenticado (v1)
 * - [DailyLogEntity] + [DailyItemEntity]: Cache SWR de daily logs (v2)
 *
 * Usa `fallbackToDestructiveMigration` porque estamos en desarrollo.
 * En producción, cada versión necesitará una `Migration` explícita.
 */
@Database(
    entities = [
        UserEntity::class,
        DailyLogEntity::class,
        DailyItemEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class GoodLifeDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun dailyDao(): DailyDao

    companion object {
        private const val DATABASE_NAME = "goodlife_db"

        @Volatile
        private var INSTANCE: GoodLifeDatabase? = null

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
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
