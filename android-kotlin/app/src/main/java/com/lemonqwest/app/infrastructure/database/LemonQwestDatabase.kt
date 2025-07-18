package com.lemonqwest.app.infrastructure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lemonqwest.app.infrastructure.database.dao.AchievementDao
import com.lemonqwest.app.infrastructure.database.dao.RewardDao
import com.lemonqwest.app.infrastructure.database.dao.TaskDao
import com.lemonqwest.app.infrastructure.database.dao.UserDao
import com.lemonqwest.app.infrastructure.database.entities.AchievementEntity
import com.lemonqwest.app.infrastructure.database.entities.RewardEntity
import com.lemonqwest.app.infrastructure.database.entities.TaskEntity
import com.lemonqwest.app.infrastructure.database.entities.UserEntity

/**
 * Main Room database for LemonQwest app.
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
abstract class LemonQwestDatabase : RoomDatabase() {

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
        private var INSTANCE: LemonQwestDatabase? = null

        /**
         * Gets the singleton database instance.
         *
         * @param context Application context
         * @return Database instance
         */
        fun getDatabase(context: Context): LemonQwestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LemonQwestDatabase::class.java,
                    "lemonqwest_database",
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
        fun createDatabase(context: Context, inMemory: Boolean = false): LemonQwestDatabase {
            return if (inMemory) {
                Room.inMemoryDatabaseBuilder(
                    context,
                    LemonQwestDatabase::class.java,
                ).allowMainThreadQueries().build()
            } else {
                Room.databaseBuilder(
                    context,
                    LemonQwestDatabase::class.java,
                    "lemonqwest_database",
                ).build()
            }
        }
    }
}
