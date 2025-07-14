package com.arthurslife.app.testutils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Comprehensive test utilities for Arthur's Life app testing.
 *
 * This file provides common testing utilities that can be used across all test files
 * to ensure consistent testing patterns and reduce boilerplate code.
 */

/**
 * JUnit 5 extension for setting up coroutine testing environment.
 * Automatically sets up TestDispatcher and cleans up after tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineTestExtension : BeforeEachCallback, AfterEachCallback {
    private val testDispatcher = UnconfinedTestDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}

/**
 * Test scope provider for consistent coroutine testing.
 * Use this for all coroutine-related tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
object TestScopeProvider {
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = UnconfinedTestDispatcher(testScheduler)

    fun createTestScope(): TestScope = TestScope(testDispatcher)

    fun runTest(
        timeout: Duration = 10.seconds,
        testBody: suspend TestScope.() -> Unit,
    ) = kotlinx.coroutines.test.runTest(
        context = testDispatcher,
        timeout = timeout,
        testBody = testBody,
    )
}

/**
 * Flow testing utilities for testing reactive streams.
 */
object FlowTestUtils {
    /**
     * Collects all values emitted by a Flow within a given time frame.
     *
     * @param flow The flow to collect from
     * @param timeout Maximum time to wait for values
     * @return List of collected values
     */
    suspend fun <T> Flow<T>.collectValues(
        timeout: Duration = 1.seconds,
    ): List<T> = kotlinx.coroutines.withTimeout(timeout.inWholeMilliseconds) {
        this@collectValues.toList()
    }

    /**
     * Collects the first value emitted by a Flow.
     *
     * @param flow The flow to collect from
     * @param timeout Maximum time to wait for the first value
     * @return The first emitted value
     */
    suspend fun <T> Flow<T>.firstValue(
        timeout: Duration = 1.seconds,
    ): T = kotlinx.coroutines.withTimeout(timeout.inWholeMilliseconds) {
        this@firstValue.first()
    }

    /**
     * Tests that a flow emits expected values in order.
     *
     * @param flow The flow to test
     * @param expectedValues The expected values in order
     * @param timeout Maximum time to wait for all values
     */
    suspend fun <T> Flow<T>.shouldEmitValues(
        vararg expectedValues: T,
        timeout: Duration = 1.seconds,
    ) {
        val actualValues = collectValues(timeout)
        if (actualValues != expectedValues.toList()) {
            throw AssertionError(
                "Expected flow to emit ${expectedValues.toList()}, but got $actualValues",
            )
        }
    }

    /**
     * Tests that a flow emits at least the expected number of values.
     *
     * @param flow The flow to test
     * @param expectedCount Minimum number of values expected
     * @param timeout Maximum time to wait for values
     */
    suspend fun <T> Flow<T>.shouldEmitAtLeast(
        expectedCount: Int,
        timeout: Duration = 1.seconds,
    ) {
        val actualValues = collectValues(timeout)
        if (actualValues.size < expectedCount) {
            throw AssertionError(
                "Expected flow to emit at least $expectedCount values, but got ${actualValues.size}",
            )
        }
    }
}

/**
 * Exception testing utilities for testing error conditions.
 */
object ExceptionTestUtils {
    /**
     * Tests that a suspend function throws a specific exception.
     *
     * @param expectedExceptionClass The expected exception class
     * @param block The suspend function to test
     * @return The thrown exception for further assertions
     */
    suspend inline fun <reified T : Throwable> shouldThrow(
        expectedExceptionClass: Class<T> = T::class.java,
        noinline block: suspend () -> Unit,
    ): T {
        return try {
            block()
            throw AssertionError("Expected ${expectedExceptionClass.simpleName} to be thrown")
        } catch (e: Throwable) {
            if (expectedExceptionClass.isInstance(e)) {
                @Suppress("UNCHECKED_CAST")
                e as T
            } else {
                throw AssertionError(
                    "Expected ${expectedExceptionClass.simpleName} but got ${e::class.simpleName}: ${e.message}",
                )
            }
        }
    }

    /**
     * Tests that a suspend function does not throw any exception.
     *
     * @param block The suspend function to test
     */
    suspend fun shouldNotThrow(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            throw AssertionError(
                "Expected no exception to be thrown, but got ${e::class.simpleName}: ${e.message}",
            )
        }
    }
}

/**
 * State verification utilities for testing stateful components.
 */
object StateTestUtils {
    /**
     * Verifies that a state change occurs within a given time frame.
     *
     * @param initialState The initial state
     * @param expectedState The expected final state
     * @param stateProvider Function that returns the current state
     * @param action The action that should cause the state change
     * @param timeout Maximum time to wait for state change
     */
    suspend fun <T> verifyStateChange(
        initialState: T,
        expectedState: T,
        stateProvider: suspend () -> T,
        action: suspend () -> Unit,
        timeout: Duration = 1.seconds,
    ) {
        val actualInitialState = stateProvider()
        if (actualInitialState != initialState) {
            throw AssertionError(
                "Initial state mismatch: expected $initialState, got $actualInitialState",
            )
        }

        action()

        val actualFinalState = kotlinx.coroutines.withTimeout(timeout.inWholeMilliseconds) {
            var currentState: T
            do {
                currentState = stateProvider()
                if (currentState == expectedState) break
                kotlinx.coroutines.delay(10)
            } while (true)
            currentState
        }

        if (actualFinalState != expectedState) {
            throw AssertionError(
                "Final state mismatch: expected $expectedState, got $actualFinalState",
            )
        }
    }

    /**
     * Verifies that a state remains unchanged after an action.
     *
     * @param expectedState The expected unchanged state
     * @param stateProvider Function that returns the current state
     * @param action The action that should not change the state
     * @param waitTime Time to wait after action to verify state remains unchanged
     */
    suspend fun <T> verifyStateUnchanged(
        expectedState: T,
        stateProvider: suspend () -> T,
        action: suspend () -> Unit,
        waitTime: Duration = 100.milliseconds,
    ) {
        val initialState = stateProvider()
        if (initialState != expectedState) {
            throw AssertionError(
                "Initial state mismatch: expected $expectedState, got $initialState",
            )
        }

        action()
        kotlinx.coroutines.delay(waitTime.inWholeMilliseconds)

        val finalState = stateProvider()
        if (finalState != expectedState) {
            throw AssertionError(
                "State changed unexpectedly: expected $expectedState, got $finalState",
            )
        }
    }
}

/**
 * Memory and performance testing utilities.
 */
object PerformanceTestUtils {
    /**
     * Measures the execution time of a suspend function.
     *
     * @param block The function to measure
     * @return Pair of (result, execution time in milliseconds)
     */
    suspend fun <T> measureExecutionTime(block: suspend () -> T): Pair<T, Long> {
        val startTime = System.currentTimeMillis()
        val result = block()
        val endTime = System.currentTimeMillis()
        return Pair(result, endTime - startTime)
    }

    /**
     * Verifies that a function executes within a specified time limit.
     *
     * @param expectedMaxTime Maximum allowed execution time
     * @param block The function to test
     * @return The result of the function
     */
    suspend fun <T> verifyExecutionTime(
        expectedMaxTime: Duration,
        block: suspend () -> T,
    ): T {
        val (result, executionTime) = measureExecutionTime(block)
        if (executionTime > expectedMaxTime.inWholeMilliseconds) {
            throw AssertionError(
                "Function took too long: ${executionTime}ms (expected max: ${expectedMaxTime.inWholeMilliseconds}ms)",
            )
        }
        return result
    }
}

/**
 * Cleanup utilities for test resource management.
 */
object CleanupUtils {
    /**
     * Ensures proper cleanup of coroutine scopes in tests.
     *
     * @param scope The scope to clean up
     */
    fun cleanupScope(scope: CoroutineScope) {
        scope.cancel()
    }

    /**
     * Runs a block with automatic cleanup of provided resources.
     *
     * @param resources Resources to clean up
     * @param block The test block to execute
     */
    suspend fun <T> withCleanup(
        vararg resources: AutoCloseable,
        block: suspend () -> T,
    ): T {
        try {
            return block()
        } finally {
            resources.forEach { resource ->
                try {
                    resource.close()
                } catch (e: Exception) {
                    // Log cleanup errors but don't fail the test
                    println("Warning: Failed to cleanup resource: ${e.message}")
                }
            }
        }
    }
}

/**
 * Data validation utilities for testing data integrity.
 */
object DataTestUtils {
    /**
     * Verifies that two objects are deeply equal, providing detailed error messages.
     *
     * @param expected The expected object
     * @param actual The actual object
     * @param message Custom error message prefix
     */
    fun <T> verifyDeepEquals(expected: T, actual: T, message: String = "Objects not equal") {
        if (expected != actual) {
            throw AssertionError("$message:\nExpected: $expected\nActual: $actual")
        }
    }

    /**
     * Verifies that a collection contains all expected elements.
     *
     * @param collection The collection to check
     * @param expectedElements The elements that should be present
     */
    fun <T> verifyContainsAll(collection: Collection<T>, vararg expectedElements: T) {
        val missing = expectedElements.filterNot { collection.contains(it) }
        if (missing.isNotEmpty()) {
            throw AssertionError("Collection missing elements: $missing")
        }
    }

    /**
     * Verifies that a collection contains no duplicate elements.
     *
     * @param collection The collection to check
     */
    fun <T> verifyNoDuplicates(collection: Collection<T>) {
        val duplicates = collection.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) {
            throw AssertionError("Collection contains duplicates: ${duplicates.keys}")
        }
    }
}
