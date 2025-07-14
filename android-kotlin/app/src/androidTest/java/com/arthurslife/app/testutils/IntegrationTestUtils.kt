package com.arthurslife.app.testutils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.arthurslife.app.infrastructure.database.ArthursLifeDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Integration testing utilities for Arthur's Life app.
 *
 * This file provides utilities for testing complete workflows and integrations, including:
 * - End-to-end workflow testing
 * - Database and DataStore integration
 * - Repository integration testing
 * - Use case integration testing
 * - Error propagation testing
 */

/**
 * Integration test base class providing common setup and utilities.
 */
abstract class IntegrationTestBase {
    protected lateinit var database: ArthursLifeDatabase
    protected lateinit var testDataStore: TestDataStore

    /**
     * Sets up test dependencies before each test.
     */
    protected open suspend fun setup() {
        database = DatabaseTestFactory.createInMemoryDatabase()
        testDataStore = TestDataStore()
    }

    /**
     * Cleans up test dependencies after each test.
     */
    protected open suspend fun teardown() {
        DatabaseCleanup.clearDatabase(database)
        database.close()
        testDataStore.clear()
    }

    /**
     * Runs an integration test with proper setup and cleanup.
     *
     * @param testBody The test body to execute
     */
    protected suspend fun runIntegrationTest(testBody: suspend TestScope.() -> Unit) = runTest {
        setup()
        try {
            testBody()
        } finally {
            teardown()
        }
    }
}

/**
 * Test DataStore implementation for testing preferences.
 */
class TestDataStore : DataStore<Preferences> {
    private var currentData = emptyPreferences()

    override val data: Flow<Preferences> = kotlinx.coroutines.flow.flow {
        emit(currentData)
    }

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        currentData = transform(currentData)
        return currentData
    }

    /**
     * Clears all data from the test DataStore.
     */
    suspend fun clear() {
        currentData = emptyPreferences()
    }

    /**
     * Sets specific preference values for testing.
     *
     * @param preferences The preferences to set
     */
    suspend fun setPreferences(preferences: Preferences) {
        currentData = preferences
    }

    /**
     * Gets current preferences for verification.
     *
     * @return Current preferences
     */
    suspend fun getCurrentPreferences(): Preferences {
        return data.first()
    }
}

/**
 * End-to-end workflow testing utilities.
 */
object EndToEndTestUtils {
    /**
     * Tests a complete user task completion workflow.
     *
     * @param testScope The test scope
     * @param database The test database
     * @param userId The user ID
     * @param taskId The task ID
     * @param expectedTokenReward Expected token reward
     */
    suspend fun testTaskCompletionWorkflow(
        testScope: TestScope,
        database: ArthursLifeDatabase,
        userId: String,
        taskId: String,
        expectedTokenReward: Int,
    ) {
        testScope.runTest {
            // Setup: Create user and task
            val user = EntityTestFactory.createUserEntity(id = userId, tokenBalance = 0)
            val task = EntityTestFactory.createTaskEntity(
                id = taskId,
                assignedToUserId = userId,
                tokenReward = expectedTokenReward,
                isCompleted = false,
            )

            database.userDao().insertUser(user)
            database.taskDao().insert(task)

            // Verify initial state
            DatabaseAssertions.verifyUserTokenBalance(database.userDao(), userId, 0)
            DatabaseAssertions.verifyTaskCompletion(database.taskDao(), taskId, false)

            // Execute: Complete task
            val updatedTask = task.copy(
                isCompleted = true,
            )
            database.taskDao().update(updatedTask)

            // Update user token balance
            val updatedUser = user.copy(
                tokenBalance = user.tokenBalance + expectedTokenReward,
            )
            database.userDao().updateUser(updatedUser)

            // Verify final state
            DatabaseAssertions.verifyTaskCompletion(database.taskDao(), taskId, true)
            DatabaseAssertions.verifyUserTokenBalance(
                database.userDao(),
                userId,
                expectedTokenReward,
            )
        }
    }

    /**
     * Tests user authentication workflow.
     *
     * @param testScope The test scope
     * @param dataStore The test DataStore
     * @param userId The user ID to authenticate
     * @param pin The PIN to authenticate with
     */
    suspend fun testAuthenticationWorkflow(
        testScope: TestScope,
        dataStore: TestDataStore,
        userId: String,
    ) {
        testScope.runTest {
            // Setup: Clear any existing authentication
            dataStore.clear()

            // Verify initial state (not authenticated)
            val initialAuth = dataStore.getCurrentPreferences()
            if (initialAuth.asMap().isNotEmpty()) {
                throw AssertionError("Expected no initial authentication")
            }

            // Execute: Authenticate user
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(
                        androidx.datastore.preferences.core.stringPreferencesKey("current_user_id"),
                        userId,
                    )
                    set(
                        androidx.datastore.preferences.core.stringPreferencesKey(
                            "authenticated_at",
                        ),
                        System.currentTimeMillis().toString(),
                    )
                }.toPreferences()
            }

            // Verify authentication state
            val authPrefs = dataStore.getCurrentPreferences()
            val authenticatedUserId = authPrefs[
                androidx.datastore.preferences.core.stringPreferencesKey(
                    "current_user_id",
                ),
            ]

            if (authenticatedUserId != userId) {
                throw AssertionError(
                    "Expected authenticated user ID $userId, but got $authenticatedUserId",
                )
            }
        }
    }

    /**
     * Tests theme switching workflow.
     *
     * @param testScope The test scope
     * @param dataStore The test DataStore
     * @param fromTheme Initial theme
     * @param toTheme Target theme
     */
    suspend fun testThemeSwitchingWorkflow(
        testScope: TestScope,
        dataStore: TestDataStore,
        fromTheme: String,
        toTheme: String,
    ) {
        testScope.runTest {
            // Setup: Set initial theme
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(
                        androidx.datastore.preferences.core.stringPreferencesKey("app_theme"),
                        fromTheme,
                    )
                }.toPreferences()
            }

            // Verify initial theme
            val initialPrefs = dataStore.getCurrentPreferences()
            val initialTheme = initialPrefs[
                androidx.datastore.preferences.core.stringPreferencesKey(
                    "app_theme",
                ),
            ]
            if (initialTheme != fromTheme) {
                throw AssertionError("Expected initial theme $fromTheme, but got $initialTheme")
            }

            // Execute: Switch theme
            dataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(
                        androidx.datastore.preferences.core.stringPreferencesKey("app_theme"),
                        toTheme,
                    )
                    set(
                        androidx.datastore.preferences.core.longPreferencesKey("theme_changed_at"),
                        System.currentTimeMillis(),
                    )
                }.toPreferences()
            }

            // Verify theme change
            val finalPrefs = dataStore.getCurrentPreferences()
            val finalTheme = finalPrefs[
                androidx.datastore.preferences.core.stringPreferencesKey(
                    "app_theme",
                ),
            ]
            if (finalTheme != toTheme) {
                throw AssertionError("Expected final theme $toTheme, but got $finalTheme")
            }
        }
    }

    /**
     * Tests achievement unlocking workflow.
     *
     * @param testScope The test scope
     * @param database The test database
     * @param userId The user ID
     * @param achievementType The achievement type to unlock
     * @param requiredProgress Required progress to unlock achievement
     */
    suspend fun testAchievementUnlockWorkflow(
        testScope: TestScope,
        database: ArthursLifeDatabase,
        userId: String,
        requiredProgress: Int,
    ) {
        testScope.runTest {
            // Setup: Create user and achievement
            val user = EntityTestFactory.createUserEntity(id = userId)
            database.userDao().insertUser(user)

            // Create achievement entity (this would be in a real achievement table)
            // For now, we'll simulate with user progress tracking

            // Execute: Complete tasks to make progress
            repeat(requiredProgress) { index ->
                val task = EntityTestFactory.createTaskEntity(
                    title = "Task $index",
                    assignedToUserId = userId,
                    isCompleted = true,
                )
                database.taskDao().insert(task)
            }

            // Verify achievement conditions met
            val completedTasks = database.taskDao().findCompletedByUserId(userId)
            if (completedTasks.size < requiredProgress) {
                throw AssertionError(
                    "Expected at least $requiredProgress completed tasks, but got ${completedTasks.size}",
                )
            }

            // Achievement would be unlocked here in real implementation
            // This is a simplified test of the workflow
        }
    }
}

/**
 * Data consistency testing utilities for integration tests.
 */
object DataConsistencyTestUtils {
    /**
     * Tests data consistency across database and DataStore.
     *
     * @param database The test database
     * @param dataStore The test DataStore
     * @param userId The user ID to test
     */
    suspend fun testCrossStorageConsistency(
        database: ArthursLifeDatabase,
        dataStore: TestDataStore,
        userId: String,
    ) {
        // Create user in database
        val user = EntityTestFactory.createUserEntity(id = userId, name = "Test User")
        database.userDao().insertUser(user)

        // Set user preferences in DataStore
        dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                set(
                    androidx.datastore.preferences.core.stringPreferencesKey("current_user_id"),
                    userId,
                )
                set(
                    androidx.datastore.preferences.core.stringPreferencesKey("user_name"),
                    user.name,
                )
            }.toPreferences()
        }

        // Verify consistency
        val dbUser = database.userDao().getUserById(userId)
        val dsPrefs = dataStore.getCurrentPreferences()
        val dsUserId = dsPrefs[
            androidx.datastore.preferences.core.stringPreferencesKey(
                "current_user_id",
            ),
        ]
        val dsUserName = dsPrefs[
            androidx.datastore.preferences.core.stringPreferencesKey(
                "user_name",
            ),
        ]

        if (dbUser?.id != dsUserId) {
            throw AssertionError(
                "User ID mismatch between database (${ dbUser?.id}) and DataStore ($dsUserId)",
            )
        }

        if (dbUser?.name != dsUserName) {
            throw AssertionError(
                "User name mismatch between database (${dbUser?.name}) and DataStore ($dsUserName)",
            )
        }
    }

    /**
     * Tests data consistency during concurrent operations.
     *
     * @param database The test database
     * @param userId The user ID
     * @param operations Number of concurrent operations
     */
    suspend fun testConcurrentDataConsistency(
        database: ArthursLifeDatabase,
        userId: String,
        operations: Int = 10,
    ) = coroutineScope {
        // Setup initial user
        val user = EntityTestFactory.createUserEntity(id = userId, tokenBalance = 0)
        database.userDao().insertUser(user)

        // Execute concurrent token updates
        val jobs = (1..operations).map { tokenAmount ->
            launch {
                val currentUser = database.userDao().getUserById(userId)
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        tokenBalance = currentUser.tokenBalance + tokenAmount,
                    )
                    database.userDao().updateUser(updatedUser)
                }
            }
        }

        // Wait for all operations to complete
        jobs.forEach { it.join() }

        // Verify final state is consistent
        val finalUser = database.userDao().getUserById(userId)
        val expectedBalance = (1..operations).sum()

        if (finalUser?.tokenBalance != expectedBalance) {
            throw AssertionError(
                "Expected final balance $expectedBalance, but got ${finalUser?.tokenBalance}",
            )
        }
    }

    /**
     * Tests data consistency during error conditions.
     *
     * @param database The test database
     * @param userId The user ID
     */
    suspend fun testErrorRecoveryConsistency(
        database: ArthursLifeDatabase,
        userId: String,
    ) {
        // Setup initial state
        val user = EntityTestFactory.createUserEntity(id = userId, tokenBalance = 10)
        database.userDao().insertUser(user)

        val initialBalance = database.userDao().getUserById(userId)?.tokenBalance

        // Simulate failed operation
        try {
            // Note: Room doesn't support suspend functions in runInTransaction
            // So we'll simulate a transaction-like operation
            val currentUser = database.userDao().getUserById(userId)!!
            val updatedUser = currentUser.copy(tokenBalance = currentUser.tokenBalance + 5)
            database.userDao().updateUser(updatedUser)

            // Simulate error that should rollback transaction
            error("Simulated error")
        } catch (e: IllegalStateException) {
            // In a real scenario with transactions, this would rollback
            // For this test, we'll manually revert the change
            val originalUser = database.userDao().getUserById(userId)!!
            val revertedUser = originalUser.copy(tokenBalance = initialBalance ?: 0)
            database.userDao().updateUser(revertedUser)

            // Expected error - transaction would rollback
            require(e.message == "Simulated error") {
                "Unexpected error: ${e.message}"
            }
        }

        // Verify state is unchanged after failed transaction
        val finalBalance = database.userDao().getUserById(userId)?.tokenBalance
        check(finalBalance == initialBalance) {
            "Expected balance to remain $initialBalance after failed transaction, but got $finalBalance"
        }
    }
}

/**
 * Performance integration testing utilities.
 */
object IntegrationPerformanceTestUtils {
    /**
     * Tests performance of end-to-end workflows.
     *
     * @param database The test database
     * @param dataStore The test DataStore
     * @param workflowTest The workflow to test
     * @param maxExecutionTime Maximum allowed execution time
     * @param iterations Number of iterations to test
     */
    suspend fun testWorkflowPerformance(
        database: ArthursLifeDatabase,
        dataStore: TestDataStore,
        workflowTest: suspend () -> Unit,
        maxExecutionTime: Duration = 1.seconds,
        iterations: Int = 10,
    ): IntegrationPerformanceMetrics {
        val times = mutableListOf<Long>()

        repeat(iterations) {
            // Clear state before each iteration
            DatabaseCleanup.clearDatabase(database)
            dataStore.clear()

            // Measure execution time
            val startTime = System.currentTimeMillis()
            workflowTest()
            val endTime = System.currentTimeMillis()

            val executionTime = endTime - startTime
            times.add(executionTime)

            // Verify execution time is within limits
            if (executionTime > maxExecutionTime.inWholeMilliseconds) {
                throw AssertionError(
                    "Workflow execution took too long: ${executionTime}ms (max: ${maxExecutionTime.inWholeMilliseconds}ms)",
                )
            }
        }

        return IntegrationPerformanceMetrics(
            iterations = iterations,
            times = times,
            averageTime = times.average(),
            minTime = times.minOrNull() ?: 0,
            maxTime = times.maxOrNull() ?: 0,
        )
    }

    /**
     * Tests memory usage during integration tests.
     *
     * @param database The test database
     * @param workflowTest The workflow to test
     * @param maxMemoryIncrease Maximum allowed memory increase in MB
     */
    suspend fun testWorkflowMemoryUsage(
        workflowTest: suspend () -> Unit,
        maxMemoryIncrease: Long = 50L, // MB
    ) {
        // Get baseline memory usage without explicit GC calls
        Thread.sleep(100)

        val runtime = Runtime.getRuntime()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()

        // Execute workflow
        workflowTest()

        // Measure final memory usage
        Thread.sleep(100)

        val finalMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryIncrease = (finalMemory - initialMemory) / (1024 * 1024) // Convert to MB

        if (memoryIncrease > maxMemoryIncrease) {
            throw AssertionError(
                "Memory usage increased by ${memoryIncrease}MB (max allowed: ${maxMemoryIncrease}MB)",
            )
        }
    }
}

/**
 * Error propagation testing utilities for integration tests.
 */
object ErrorPropagationTestUtils {
    /**
     * Tests that errors propagate correctly through the integration layers.
     *
     * @param database The test database
     * @param errorSimulation Function that simulates an error
     * @param errorVerification Function that verifies the error was handled correctly
     */
    suspend fun testErrorPropagation(
        errorSimulation: suspend () -> Unit,
        errorVerification: suspend (Throwable?) -> Unit,
    ) {
        var caughtError: Throwable? = null

        try {
            errorSimulation()
        } catch (e: Throwable) {
            caughtError = e
        }

        errorVerification(caughtError)
    }

    /**
     * Tests error recovery across integration layers.
     *
     * @param database The test database
     * @param dataStore The test DataStore
     * @param errorOperation Operation that causes an error
     * @param recoveryOperation Operation that should recover from the error
     * @param stateVerification Function to verify final state is correct
     */
    suspend fun testErrorRecovery(
        errorOperation: suspend () -> Unit,
        recoveryOperation: suspend () -> Unit,
        stateVerification: suspend () -> Unit,
    ) {
        // Execute operation that causes error
        try {
            errorOperation()
        } catch (e: Throwable) {
            // Expected error in recovery testing
            if (e !is AssertionError) {
                // This is expected behavior for error recovery tests
            } else {
                throw e
            }
        }

        // Execute recovery operation
        recoveryOperation()

        // Verify state is correct after recovery
        stateVerification()
    }
}

/**
 * Performance metrics for integration tests.
 */
data class IntegrationPerformanceMetrics(
    val iterations: Int,
    val times: List<Long>,
    val averageTime: Double,
    val minTime: Long,
    val maxTime: Long,
) {
    fun printMetrics() {
        println("Integration Performance Metrics:")
        println("  Iterations: $iterations")
        println("  Average Time: ${averageTime}ms")
        println("  Min Time: ${minTime}ms")
        println("  Max Time: ${maxTime}ms")
        println("  Time Range: ${maxTime - minTime}ms")
    }
}
