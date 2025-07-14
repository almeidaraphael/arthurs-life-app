package com.arthurslife.app.infrastructure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arthurslife.app.infrastructure.database.dao.AchievementDao
import com.arthurslife.app.infrastructure.database.dao.RewardDao
import com.arthurslife.app.infrastructure.database.dao.TaskDao
import com.arthurslife.app.infrastructure.database.dao.UserDao
import com.arthurslife.app.infrastructure.database.entities.AchievementEntity
import com.arthurslife.app.infrastructure.database.entities.RewardEntity
import com.arthurslife.app.infrastructure.database.entities.TaskEntity
import com.arthurslife.app.infrastructure.database.entities.UserEntity

/**
 * Main Room database for Arthur's Life app.
 *
 * This database contains all the entity tables and provides access to DAOs
 * for performing database operations. It follows Room's recommended patterns
 * for database configuration and migration handling.
 */
@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        RewardEntity::class,
        AchievementEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class ArthursLifeDatabase : RoomDatabase() {

    /**
     * Provides access to user-related database operations.
     */
    abstract fun userDao(): UserDao

    /**
     * Provides access to task-related database operations.
     */
    abstract fun taskDao(): TaskDao

    /**
     * Provides access to reward-related database operations.
     */
    abstract fun rewardDao(): RewardDao

    /**
     * Provides access to achievement-related database operations.
     */
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: ArthursLifeDatabase? = null

        /**
         * Gets the singleton database instance.
         *
         * @param context Application context
         * @return Database instance
         */
        fun getDatabase(context: Context): ArthursLifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArthursLifeDatabase::class.java,
                    "arthurs_life_database",
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Creates a database instance for testing purposes.
         * This allows tests to use in-memory databases.
         *
         * @param context Application context
         * @param inMemory Whether to create an in-memory database
         * @return Database instance
         */
        fun createDatabase(context: Context, inMemory: Boolean = false): ArthursLifeDatabase {
            return if (inMemory) {
                Room.inMemoryDatabaseBuilder(
                    context,
                    ArthursLifeDatabase::class.java,
                ).allowMainThreadQueries().build()
            } else {
                Room.databaseBuilder(
                    context,
                    ArthursLifeDatabase::class.java,
                    "arthurs_life_database",
                ).build()
            }
        }
    }
}
