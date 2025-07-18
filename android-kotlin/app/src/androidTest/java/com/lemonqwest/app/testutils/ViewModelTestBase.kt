package com.lemonqwest.app.testutils

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.time.Duration.Companion.seconds

/**
 * Base class for Android instrumentation tests that require coroutine and ViewModel testing support.
 *
 * Provides:
 * - Thread-safe TestDispatcher setup for parallel test execution
 * - MockK initialization and cleanup
 * - ViewModel-specific testing utilities
 * - Proper resource cleanup to prevent test isolation issues
 *
 * This class addresses the TestMainDispatcher.kt:66 errors by providing synchronized
 * dispatcher management and proper setup/teardown patterns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {

    companion object {
        /**
         * Synchronization lock to prevent race conditions during parallel test execution.
         * This addresses the TestMainDispatcher.kt:66 error pattern.
         */
        private val mainDispatcherLock = Any()
    }

    protected lateinit var testDispatcher: TestDispatcher
    protected lateinit var testScope: TestScope

    @BeforeEach
    open fun setUpViewModel() {
        synchronized(mainDispatcherLock) {
            // Create test dispatcher with deterministic execution
            testDispatcher = UnconfinedTestDispatcher()
            testScope = TestScope(testDispatcher)

            // Set main dispatcher for coroutine testing
            Dispatchers.setMain(testDispatcher)

            // Initialize MockK with strict behavior
            MockKAnnotations.init(this, relaxUnitFun = false)

            // Multiple stabilization passes to handle edge case timing issues
            // This prevents the TestMainDispatcher.kt:66 errors
            testDispatcher.scheduler.advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    @AfterEach
    open fun tearDownViewModel() {
        synchronized(mainDispatcherLock) {
            // Cancel test scope to clean up coroutines
            testScope.cancel()

            // Reset main dispatcher
            Dispatchers.resetMain()

            // Clean up all mocks to prevent test interference
            unmockkAll()
        }
    }

    /**
     * Runs a test with proper ViewModel test scope and dispatcher management.
     *
     * This method ensures:
     * - Proper coroutine execution context
     * - Timeout handling for hanging tests
     * - Dispatcher stabilization to prevent timing issues
     */
    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        testScope.runTest(timeout = 5.seconds) {
            // Ensure dispatcher is stable before test execution
            testDispatcher.scheduler.advanceUntilIdle()

            // Execute test body
            testBody()

            // Final stabilization to ensure all coroutines complete
            advanceUntilIdle()
        }
    }
}
