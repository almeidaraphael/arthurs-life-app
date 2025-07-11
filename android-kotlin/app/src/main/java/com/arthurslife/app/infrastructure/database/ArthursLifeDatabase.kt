package com.arthurslife.app.infrastructure.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.infrastructure.database.dao.AchievementDao
import com.arthurslife.app.infrastructure.database.dao.RewardDao
import com.arthurslife.app.infrastructure.database.dao.TaskDao
import com.arthurslife.app.infrastructure.database.dao.UserDao
import com.arthurslife.app.infrastructure.database.entities.AchievementEntity
import com.arthurslife.app.infrastructure.database.entities.RewardEntity
import com.arthurslife.app.infrastructure.database.entities.TaskEntity
import com.arthurslife.app.infrastructure.database.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

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
                ).addCallback(DatabaseCallback(CoroutineScope(Dispatchers.IO)))
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

        /**
         * Database callback for seeding initial data.
         */
        private class DatabaseCallback(
            private val scope: CoroutineScope,
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database)
                    }
                }
            }

            /**
             * Populates the database with initial sample data.
             */
            private suspend fun populateDatabase(database: ArthursLifeDatabase) {
                val userDao = database.userDao()
                val taskDao = database.taskDao()

                // Insert child user
                userDao.insertUser(
                    UserEntity(
                        id = "child-1",
                        name = "Child User",
                        role = "CHILD",
                        pinHash = "child-pin-hash",
                        tokenBalance = 0,
                        createdAt = System.currentTimeMillis(),
                    ),
                )

                // Insert sample tasks
                val sampleTasks = createSampleTasks()
                sampleTasks.forEach { task ->
                    taskDao.insert(TaskEntity.fromDomain(task))
                }
            }

            /**
             * Creates sample tasks for demonstration.
             */
            private fun createSampleTasks(): List<com.arthurslife.app.domain.task.Task> {
                val childUserId = "child-1"
                val currentTime = System.currentTimeMillis()

                return createPersonalCareTasks(childUserId, currentTime) +
                    createHouseholdTasks(childUserId, currentTime) +
                    createHomeworkTasks(childUserId, currentTime)
            }

            /**
             * Creates personal care tasks.
             */
            private fun createPersonalCareTasks(
                childUserId: String,
                currentTime: Long,
            ): List<com.arthurslife.app.domain.task.Task> {
                return listOf(
                    "Brush your teeth",
                    "Wash your hands",
                    "Get dressed",
                    "Take a shower",
                ).map { title ->
                    com.arthurslife.app.domain.task.Task(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        category = TaskCategory.PERSONAL_CARE,
                        tokenReward = 5,
                        assignedToUserId = childUserId,
                        createdAt = currentTime,
                    )
                }
            }

            /**
             * Creates household tasks.
             */
            private fun createHouseholdTasks(
                childUserId: String,
                currentTime: Long,
            ): List<com.arthurslife.app.domain.task.Task> {
                return listOf(
                    "Make your bed",
                    "Clean your room",
                    "Put away toys",
                ).map { title ->
                    com.arthurslife.app.domain.task.Task(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        category = TaskCategory.HOUSEHOLD,
                        tokenReward = 10,
                        assignedToUserId = childUserId,
                        createdAt = currentTime,
                    )
                }
            }

            /**
             * Creates homework tasks.
             */
            private fun createHomeworkTasks(
                childUserId: String,
                currentTime: Long,
            ): List<com.arthurslife.app.domain.task.Task> {
                return listOf(
                    "Complete math homework",
                    "Read for 20 minutes",
                    "Practice spelling words",
                ).map { title ->
                    com.arthurslife.app.domain.task.Task(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        category = TaskCategory.HOMEWORK,
                        tokenReward = 15,
                        assignedToUserId = childUserId,
                        createdAt = currentTime,
                    )
                }
            }
        }
    }
}
