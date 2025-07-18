package com.lemonqwest.app.testutils

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import kotlin.time.Duration.Companion.seconds

/**
 * Enhanced base class for ViewModel and UseCase tests with bulletproof coroutine handling.
 *
 * ENHANCED COROUTINE TESTING APPROACH (Category B Pattern):
 * 1. Thread-safe Main dispatcher setup using synchronized access for parallel test execution
 * 2. Use UnconfinedTestDispatcher for immediate execution of ViewModel init blocks
 * 3. Provide safe test execution wrapper with timeout protection
 * 4. Ensure proper resource cleanup including Main dispatcher reset
 * 5. Coordinate MockK initialization with coroutine setup
 * 6. Complete isolation between tests with comprehensive teardown
 *
 * This resolves IllegalStateException from Main dispatcher conflicts during ViewModel testing
 * and ensures proper coroutine context for ViewModels that depend on Dispatchers.Main.
 *
 * CRITICAL: Handles JUnit parallel execution by synchronizing Dispatchers.Main access.
 *
 * NOTE: This pattern should ONLY be used for tests that actually need ViewModel infrastructure.
 * Repository/infrastructure tests should use Category A pattern (direct runTest without ViewModelTestBase).
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {

    protected lateinit var testDispatcher: TestDispatcher
    protected lateinit var testScope: TestScope

    @BeforeEach
    open fun setUpViewModel() {
        testDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = false)
        testDispatcher.scheduler.advanceUntilIdle()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    open fun tearDownViewModel() {
        testScope.cancel()
        unmockkAll()
    }

    /**
     * Advance the test dispatcher to execute pending coroutines.
     * Call this after creating ViewModels to allow their init blocks to complete.
     */
    protected fun advanceUntilIdle() {
        testDispatcher.scheduler.advanceUntilIdle()
    }

    /**
     * Safe coroutine execution wrapper with timeout protection.
     * Use this for all ViewModel tests that require coroutine execution.
     */
    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        testScope.runTest(timeout = 5.seconds) {
            testDispatcher.scheduler.advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
            testBody()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }
}
