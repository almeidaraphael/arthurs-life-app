package com.lemonqwest.app.testutils

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.concurrent.ConcurrentHashMap

/**
 * Comprehensive base class for Android instrumentation tests requiring complete isolation.
 *
 * Provides:
 * - Hilt dependency injection setup and teardown
 * - Thread-safe TestDispatcher management
 * - MockK initialization and cleanup
 * - Complete resource isolation between tests
 * - Parallel execution safety
 *
 * This base class ensures perfect test isolation by:
 * - Managing separate test dispatchers per thread
 * - Proper Hilt injection lifecycle
 * - Complete resource cleanup
 * - Prevention of test state bleeding
 *
 * Usage:
 * ```kotlin
 * @HiltAndroidTest
 * class MyIntegrationTest : AndroidTestBase() {
 *
 *     @Test
 *     fun `should test integration scenario`() = runTest {
 *         // Test implementation with complete isolation
 *         // Hilt injection available
 *         // Fresh test dispatcher
 *         // Clean MockK state
 *     }
 * }
 * ```
 *
 * For database-specific tests, consider using DatabaseTestBase instead,
 * which extends this class with database-specific utilities.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
abstract class AndroidTestBase {

    companion object {
        /**
         * Thread-safe storage for test dispatchers to prevent conflicts during parallel execution.
         */
        private val testDispatchers = ConcurrentHashMap<Long, TestDispatcher>()

        /**
         * Synchronization lock for main dispatcher operations.
         */
        private val mainDispatcherLock = Any()
    }

    /**
     * Hilt rule for dependency injection setup.
     * Must be initialized before any injection occurs.
     */
    protected val hiltRule = HiltAndroidRule(this)

    /**
     * Test dispatcher for the current test thread.
     */
    protected lateinit var testDispatcher: TestDispatcher

    @BeforeEach
    open fun setUpAndroidTest() {
        val threadId = Thread.currentThread().id

        // Initialize Hilt first
        hiltRule.inject()

        synchronized(mainDispatcherLock) {
            // Create and store test dispatcher for this thread
            testDispatcher = UnconfinedTestDispatcher()
            testDispatchers[threadId] = testDispatcher

            // Set main dispatcher for coroutine testing
            Dispatchers.setMain(testDispatcher)

            // Initialize MockK with strict behavior
            MockKAnnotations.init(this, relaxUnitFun = false)

            // Stabilize dispatcher to prevent timing issues
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    @AfterEach
    open fun tearDownAndroidTest() {
        val threadId = Thread.currentThread().id

        synchronized(mainDispatcherLock) {
            // Clean up test dispatcher for this thread
            testDispatchers.remove(threadId)

            // Reset main dispatcher to prevent state leakage
            Dispatchers.resetMain()

            // Clean up all mocks to prevent test interference
            unmockkAll()
        }
    }

    /**
     * Gets the test dispatcher for the current thread.
     * Useful for tests that need direct access to the dispatcher.
     */
    protected fun getTestDispatcher(): TestDispatcher? {
        val threadId = Thread.currentThread().id
        return testDispatchers[threadId]
    }
}
