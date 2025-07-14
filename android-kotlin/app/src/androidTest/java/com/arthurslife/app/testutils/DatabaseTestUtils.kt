package com.arthurslife.app.testutils

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.arthurslife.app.infrastructure.database.ArthursLifeDatabase
import com.arthurslife.app.infrastructure.database.dao.TaskDao
import com.arthurslife.app.infrastructure.database.dao.UserDao
import com.arthurslife.app.infrastructure.database.entities.TaskEntity
import com.arthurslife.app.infrastructure.database.entities.UserEntity
import kotlinx.coroutines.runBlocking

/**
 * Database testing utilities for Arthur's Life app.
 *
 * This file provides utilities for testing database operations, including:
 * - In-memory database setup
 * - Test data creation and cleanup
 * - Database state verification
 * - Migration testing utilities
 */

/**
 * In-memory database factory for testing.
 * Creates clean database instances for each test.
 */
object DatabaseTestFactory {
    /**
     * Creates an in-memory Room database for testing.
     *
     * @return Test database instance
     */
    fun createInMemoryDatabase(): ArthursLifeDatabase {
        return Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ArthursLifeDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    /**
     * Creates a test database with pre-populated data.
     *
     * @param prepopulateData Whether to add test data
     * @return Test database instance with optional test data
     */
    fun createTestDatabase(prepopulateData: Boolean = false): ArthursLifeDatabase {
        val database = createInMemoryDatabase()

        if (prepopulateData) {
            runBlocking {
                populateTestData(database)
            }
        }

        return database
    }

    /**
     * Populates the database with realistic test data.
     *
     * @param database The database to populate
     */
    private suspend fun populateTestData(database: ArthursLifeDatabase) {
        val userDao = database.userDao()
        val taskDao = database.taskDao()
        insertTestUsers(userDao)
        insertTestTasks(taskDao)
    }

    private suspend fun insertTestUsers(userDao: UserDao) {
        val testUsers = listOf(
            UserEntity(
                id = "user-child-1",
                name = "Arthur",
                role = "CHILD",
                tokenBalance = 25,
                pinHash = null,
                createdAt = System.currentTimeMillis(),
                displayName = "Arthur Child",
                avatarType = "default",
                avatarData = "",
                favoriteColor = "#FFD700",
            ),
            UserEntity(
                id = "user-caregiver-1",
                name = "Parent",
                role = "CAREGIVER",
                tokenBalance = 0,
                pinHash = "hashed-pin-1234",
                createdAt = System.currentTimeMillis(),
                displayName = "Parent Caregiver",
                avatarType = "default",
                avatarData = "",
                favoriteColor = "#00FF00",
            ),
        )
        testUsers.forEach { userDao.insertUser(it) }
    }

    private suspend fun insertTestTasks(taskDao: TaskDao) {
        val testTasks = listOf(
            TaskEntity(
                id = "task-1",
                title = "Brush teeth",
                category = "PERSONAL_CARE",
                tokenReward = 5,
                isCompleted = false,
                assignedToUserId = "user-child-1",
                createdAt = System.currentTimeMillis(),
                completedAt = null,
                customIconName = "icon_brush",
                customColorHex = "#FFD700",
            ),
            TaskEntity(
                id = "task-2",
                title = "Make bed",
                category = "HOUSEHOLD",
                tokenReward = 10,
                isCompleted = true,
                assignedToUserId = "user-child-1",
                createdAt = System.currentTimeMillis(),
                completedAt = System.currentTimeMillis(),
                customIconName = "icon_bed",
                customColorHex = "#00FF00",
            ),
            TaskEntity(
                id = "task-3",
                title = "Math homework",
                category = "HOMEWORK",
                tokenReward = 15,
                isCompleted = false,
                assignedToUserId = "user-child-1",
                createdAt = System.currentTimeMillis(),
                completedAt = null,
                customIconName = "icon_math",
                customColorHex = "#0000FF",
            ),
        )
        testTasks.forEach { taskDao.insert(it) }
    }
}

/**
 * Database assertion utilities for verifying database state.
 */
object DatabaseAssertions {
    /**
     * Verifies that a user exists in the database.
     *
     * @param userDao The UserDao instance
     * @param userId The user ID to check
     * @return The found user entity
     */
    suspend fun verifyUserExists(userDao: UserDao, userId: String): UserEntity {
        val user = userDao.getUserById(userId)
        if (user == null) {
            throw AssertionError("User with ID $userId not found in database")
        }
        return user
    }

    /**
     * Verifies that a user does not exist in the database.
     *
     * @param userDao The UserDao instance
     * @param userId The user ID to check
     */
    suspend fun verifyUserNotExists(userDao: UserDao, userId: String) {
        val user = userDao.getUserById(userId)
        if (user != null) {
            throw AssertionError("User with ID $userId should not exist in database")
        }
    }

    /**
     * Verifies that a task exists in the database.
     *
     * @param taskDao The TaskDao instance
     * @param taskId The task ID to check
     * @return The found task entity
     */
    suspend fun verifyTaskExists(taskDao: TaskDao, taskId: String): TaskEntity {
        val task = taskDao.findById(taskId)
        if (task == null) {
            throw AssertionError("Task with ID $taskId not found in database")
        }
        return task
    }

    /**
     * Verifies that a task does not exist in the database.
     *
     * @param taskDao The TaskDao instance
     * @param taskId The task ID to check
     */
    suspend fun verifyTaskNotExists(taskDao: TaskDao, taskId: String) {
        val task = taskDao.findById(taskId)
        if (task != null) {
            throw AssertionError("Task with ID $taskId should not exist in database")
        }
    }

    /**
     * Verifies the total number of users in the database.
     *
     * @param userDao The UserDao instance
     * @param expectedCount Expected number of users
     */
    suspend fun verifyUserCount(userDao: UserDao, expectedCount: Int) {
        val actualCount = userDao.getAllUsers().size
        if (actualCount != expectedCount) {
            throw AssertionError("Expected $expectedCount users, but found $actualCount")
        }
    }

    /**
     * Verifies the total number of tasks in the database.
     *
     * @param taskDao The TaskDao instance
     * @param expectedCount Expected number of tasks
     */
    suspend fun verifyTaskCount(taskDao: TaskDao, expectedCount: Int) {
        val actualCount = taskDao.getAllTasks().size
        if (actualCount != expectedCount) {
            throw AssertionError("Expected $expectedCount tasks, but found $actualCount")
        }
    }

    /**
     * Verifies the number of tasks assigned to a specific user.
     *
     * @param taskDao The TaskDao instance
     * @param userId The user ID
     * @param expectedCount Expected number of tasks
     */
    suspend fun verifyTaskCountForUser(taskDao: TaskDao, userId: String, expectedCount: Int) {
        val actualCount = taskDao.findByUserId(userId).size
        if (actualCount != expectedCount) {
            throw AssertionError(
                "Expected $expectedCount tasks for user $userId, but found $actualCount",
            )
        }
    }

    /**
     * Verifies the number of completed tasks for a user.
     *
     * @param taskDao The TaskDao instance
     * @param userId The user ID
     * @param expectedCount Expected number of completed tasks
     */
    suspend fun verifyCompletedTaskCountForUser(
        taskDao: TaskDao,
        userId: String,
        expectedCount: Int,
    ) {
        val actualCount = taskDao.findCompletedByUserId(userId).size
        if (actualCount != expectedCount) {
            throw AssertionError(
                "Expected $expectedCount completed tasks for user $userId, but found $actualCount",
            )
        }
    }

    /**
     * Verifies that a user has the expected token balance.
     *
     * @param userDao The UserDao instance
     * @param userId The user ID
     * @param expectedBalance Expected token balance
     */
    suspend fun verifyUserTokenBalance(userDao: UserDao, userId: String, expectedBalance: Int) {
        val user = verifyUserExists(userDao, userId)
        if (user.tokenBalance != expectedBalance) {
            throw AssertionError(
                "Expected token balance $expectedBalance for user $userId, but found ${user.tokenBalance}",
            )
        }
    }

    /**
     * Verifies that a task has the expected completion status.
     *
     * @param taskDao The TaskDao instance
     * @param taskId The task ID
     * @param expectedCompleted Expected completion status
     */
    suspend fun verifyTaskCompletion(taskDao: TaskDao, taskId: String, expectedCompleted: Boolean) {
        val task = verifyTaskExists(taskDao, taskId)
        if (task.isCompleted != expectedCompleted) {
            throw AssertionError(
                "Expected task $taskId to be ${if (expectedCompleted) "completed" else "not completed"}, but it was ${if (task.isCompleted) "completed" else "not completed"}",
            )
        }
    }
}

/**
 * Database cleanup utilities for test isolation.
 */
object DatabaseCleanup {
    /**
     * Clears all data from the database.
     *
     * @param database The database to clear
     */
    suspend fun clearDatabase(database: ArthursLifeDatabase) {
        database.clearAllTables()
    }

    /**
     * Clears all users from the database.
     *
     * @param database The database to clear
     */
    suspend fun clearUsers(database: ArthursLifeDatabase) {
        // Since UserDao doesn't have deleteAll, we'll get all users and delete them individually
        val users = database.userDao().getAllUsers()
        users.forEach { user ->
            database.userDao().deleteUser(user.id)
        }
    }

    /**
     * Clears all tasks from the database.
     *
     * @param database The database to clear
     */
    suspend fun clearTasks(database: ArthursLifeDatabase) {
        database.taskDao().deleteAll()
    }

    /**
     * Clears tasks for a specific user.
     *
     * @param database The database to clear
     * @param userId The user ID
     */
    suspend fun clearTasksForUser(database: ArthursLifeDatabase, userId: String) {
        val tasks = database.taskDao().findByUserId(userId)
        tasks.forEach { task ->
            database.taskDao().deleteById(task.id)
        }
    }
}

/**
 * Database migration testing utilities.
 */
object MigrationTestUtils {
    /**
     * Tests database migration from one version to another.
     * This is a placeholder for future migration testing.
     */
    fun testMigration() {
        // Placeholder for migration testing
        // Would use Room's MigrationTestHelper in actual implementation
        TODO("Migration testing to be implemented when migrations are needed")
    }
}

/**
 * Database performance testing utilities.
 */
object DatabasePerformanceUtils {
    /**
     * Measures the time taken to execute a database operation.
     *
     * @param operation The database operation to measure
     * @return Pair of (result, execution time in milliseconds)
     */
    suspend fun <T> measureDatabaseOperation(operation: suspend () -> T): Pair<T, Long> {
        val startTime = System.currentTimeMillis()
        val result = operation()
        val endTime = System.currentTimeMillis()
        return Pair(result, endTime - startTime)
    }

    /**
     * Verifies that a database operation completes within a specified time limit.
     *
     * @param maxTimeMs Maximum allowed time in milliseconds
     * @param operation The database operation to test
     * @return The result of the operation
     */
    suspend fun <T> verifyDatabasePerformance(
        maxTimeMs: Long,
        operation: suspend () -> T,
    ): T {
        val (result, executionTime) = measureDatabaseOperation(operation)
        if (executionTime > maxTimeMs) {
            throw AssertionError(
                "Database operation took too long: ${executionTime}ms (max: ${maxTimeMs}ms)",
            )
        }
        return result
    }

    /**
     * Benchmark database operations with multiple iterations.
     *
     * @param iterations Number of iterations to run
     * @param operation The database operation to benchmark
     * @return DatabaseBenchmarkResult with performance metrics
     */
    suspend fun <T> benchmarkDatabaseOperation(
        iterations: Int,
        operation: suspend () -> T,
    ): DatabaseBenchmarkResult {
        val times = mutableListOf<Long>()
        var lastResult: T? = null

        repeat(iterations) {
            val (result, time) = measureDatabaseOperation(operation)
            times.add(time)
            lastResult = result
        }

        return DatabaseBenchmarkResult(
            iterations = iterations,
            times = times,
            averageTime = times.average(),
            minTime = times.minOrNull() ?: 0,
            maxTime = times.maxOrNull() ?: 0,
            lastResult = lastResult,
        )
    }
}

/**
 * Result class for database benchmarking.
 */
data class DatabaseBenchmarkResult(
    val iterations: Int,
    val times: List<Long>,
    val averageTime: Double,
    val minTime: Long,
    val maxTime: Long,
    val lastResult: Any?,
) {
    fun printResults() {
        println("Database Benchmark Results:")
        println("  Iterations: $iterations")
        println("  Average Time: ${averageTime}ms")
        println("  Min Time: ${minTime}ms")
        println("  Max Time: ${maxTime}ms")
        println("  Time Range: ${maxTime - minTime}ms")
    }
}
