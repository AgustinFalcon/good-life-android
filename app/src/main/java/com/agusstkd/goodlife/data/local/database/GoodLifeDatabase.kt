package com.agusstkd.goodlife.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
 * - [DailyLogEntity] + [DailyItemEntity]: Cache SWR de daily logs (v2, workout v3, meal v4)
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
    version = 4,
    exportSchema = false
)
abstract class GoodLifeDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun dailyDao(): DailyDao

    companion object {
        private const val DATABASE_NAME = "goodlife_db"

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE daily_item ADD COLUMN mealCalories INTEGER")
                db.execSQL("ALTER TABLE daily_item ADD COLUMN mealProtein INTEGER")
            }
        }

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
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
