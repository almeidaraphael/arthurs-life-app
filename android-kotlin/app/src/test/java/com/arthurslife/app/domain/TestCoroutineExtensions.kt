package com.arthurslife.app.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Test utilities for coroutine-based testing in Arthur's Life domain layer.
 *
 * This file provides extension functions and utilities to simplify testing
 * of suspend functions and coroutine-based code in the domain layer.
 */

/**
 * JUnit extension for setting up test coroutines with proper main dispatcher.
 *
 * This extension automatically sets up the test dispatcher before each test
 * and cleans up after each test, ensuring proper isolation between tests.
 *
 * Usage:
 * ```kotlin
 * @ExtendWith(TestCoroutineExtension::class)
 * class MyTest {
 *     @Test
 *     fun testSuspendFunction() = runTest {
 *         // Test code here
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineExtension : BeforeEachCallback, AfterEachCallback {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}

/**
 * Creates a test coroutine scope with unconfined dispatcher.
 *
 * This is useful for testing code that requires a specific scope
 * but should execute immediately in tests.
 *
 * @return CoroutineScope suitable for testing
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createTestScope(): CoroutineScope {
    return CoroutineScope(UnconfinedTestDispatcher())
}

/**
 * Executes a suspend function in a test context with timing control.
 *
 * This function provides a convenient way to test suspend functions
 * with controlled timing and scheduler advancement.
 *
 * @param block The suspend function to execute
 * @return The result of the suspend function
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> runTestWithScheduler(
    scheduler: TestCoroutineScheduler = TestCoroutineScheduler(),
    block: suspend CoroutineScope.() -> T,
): T {
    var result: T? = null
    runTest(context = UnconfinedTestDispatcher(scheduler)) {
        result = block()
    }
    return result!!
}

/**
 * Test context for coroutine-based repository and use case testing.
 *
 * This class provides a consistent testing environment for testing
 * domain layer components that use coroutines.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineContext {
    val scheduler = TestCoroutineScheduler()
    val dispatcher = UnconfinedTestDispatcher(scheduler)
    val scope = CoroutineScope(dispatcher)

    /**
     * Advances the test scheduler by the specified time.
     *
     * @param timeMillis Time to advance in milliseconds
     */
    fun advanceTimeBy(timeMillis: Long) {
        scheduler.advanceTimeBy(timeMillis)
    }

    /**
     * Runs all pending coroutines to completion.
     */
    fun runCurrent() {
        scheduler.runCurrent()
    }

    /**
     * Advances the scheduler until there are no more tasks.
     */
    fun advanceUntilIdle() {
        scheduler.advanceUntilIdle()
    }
}

/**
 * Creates a test context for coroutine testing.
 *
 * @return TestCoroutineContext ready for use
 */
fun createTestCoroutineContext(): TestCoroutineContext = TestCoroutineContext()
