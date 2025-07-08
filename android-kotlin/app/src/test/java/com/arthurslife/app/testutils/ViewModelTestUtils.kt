package com.arthurslife.app.testutils

import androidx.lifecycle.ViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * ViewModel testing utilities for Arthur's Life app.
 *
 * This file provides utilities for testing ViewModels, including:
 * - StateFlow testing
 * - ViewModel lifecycle management
 * - UI state verification
 * - Action testing
 * - Error handling verification
 */

/**
 * ViewModel test base class providing common testing functionality.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {
    protected val testDispatcher = UnconfinedTestDispatcher()
    protected val testScope = TestScope(testDispatcher)

    /**
     * Executes a ViewModel test with proper coroutine handling.
     *
     * @param testBody The test body to execute
     */
    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        testScope.runTest {
            testBody()
        }
    }

    /**
     * Advances the test dispatcher until all coroutines are idle.
     */
    protected suspend fun advanceUntilIdle() {
        testScope.advanceUntilIdle()
    }
}

/**
 * StateFlow testing utilities for ViewModel state management.
 */
object StateFlowTestUtils {
    /**
     * Verifies that a StateFlow emits the expected value.
     *
     * @param stateFlow The StateFlow to test
     * @param expectedValue The expected value
     * @param timeout Maximum time to wait for the value
     */
    suspend fun <T> StateFlow<T>.shouldEmit(
        expectedValue: T,
        timeout: Duration = 1.seconds,
    ) {
        val actualValue = kotlinx.coroutines.withTimeout(timeout.inWholeMilliseconds) {
            this@shouldEmit.first { it == expectedValue }
        }
        if (actualValue != expectedValue) {
            throw AssertionError("Expected StateFlow to emit $expectedValue, but got $actualValue")
        }
    }

    /**
     * Verifies that a StateFlow's current value matches the expected value.
     *
     * @param stateFlow The StateFlow to test
     * @param expectedValue The expected value
     */
    fun <T> StateFlow<T>.shouldHaveValue(expectedValue: T) {
        val actualValue = this.value
        if (actualValue != expectedValue) {
            throw AssertionError(
                "Expected StateFlow to have value $expectedValue, but got $actualValue",
            )
        }
    }

    /**
     * Collects and verifies a sequence of values from a StateFlow.
     *
     * @param stateFlow The StateFlow to test
     * @param expectedValues The expected sequence of values
     * @param timeout Maximum time to wait for all values
     */
    suspend fun <T> StateFlow<T>.shouldEmitSequence(
        expectedValues: List<T>,
        timeout: Duration = 2.seconds,
        scope: CoroutineScope = @OptIn(
            kotlinx.coroutines.DelicateCoroutinesApi::class,
        ) kotlinx.coroutines.GlobalScope,
    ) {
        val collectedValues = mutableListOf<T>()
        val job = scope.launch {
            this@shouldEmitSequence.collect { value ->
                collectedValues.add(value)
                if (collectedValues.size >= expectedValues.size) {
                    return@collect
                }
            }
        }

        kotlinx.coroutines.withTimeout(timeout.inWholeMilliseconds) {
            job.join()
        }

        if (collectedValues != expectedValues) {
            throw AssertionError(
                "Expected StateFlow to emit sequence $expectedValues, but got $collectedValues",
            )
        }
    }

    /**
     * Verifies that a StateFlow never emits a specific value.
     *
     * @param stateFlow The StateFlow to test
     * @param forbiddenValue The value that should not be emitted
     * @param waitTime How long to wait and verify the value is not emitted
     */
    suspend fun <T> StateFlow<T>.shouldNotEmit(
        forbiddenValue: T,
        waitTime: Duration = 500.milliseconds,
        scope: CoroutineScope = @OptIn(
            kotlinx.coroutines.DelicateCoroutinesApi::class,
        ) kotlinx.coroutines.GlobalScope,
    ) {
        val job = scope.launch {
            this@shouldNotEmit.collect { value ->
                if (value == forbiddenValue) {
                    throw AssertionError("StateFlow should not emit $forbiddenValue, but it did")
                }
            }
        }

        kotlinx.coroutines.delay(waitTime.inWholeMilliseconds)
        job.cancel()
    }
}

/**
 * UI State testing utilities for common UI state patterns.
 */
object UiStateTestUtils {
    /**
     * Verifies that a UI state transitions through loading, success, and error states correctly.
     *
     * @param initialState The initial UI state
     * @param loadingState The loading UI state
     * @param successState The success UI state
     * @param errorState The error UI state
     * @param stateFlow The StateFlow containing the UI state
     * @param action The action that triggers the state changes
     */
    suspend fun <T> verifyLoadingStateTransition(
        initialState: T,
        loadingState: T,
        successState: T,
        errorState: T? = null,
        stateFlow: StateFlow<T>,
        action: suspend () -> Unit,
    ) {
        // Verify initial state
        StateFlowTestUtils.run { stateFlow.shouldHaveValue(initialState) }

        // Trigger action and verify loading state
        action()
        StateFlowTestUtils.run { stateFlow.shouldEmit(loadingState) }

        // Verify final state (success or error)
        val finalState = stateFlow.first { it != loadingState }
        val expectedFinalStates = listOfNotNull(successState, errorState)
        if (finalState !in expectedFinalStates) {
            throw AssertionError(
                "Expected final state to be one of $expectedFinalStates, but got $finalState",
            )
        }
    }

    /**
     * Verifies that a UI state remains in loading state for a minimum duration.
     *
     * @param stateFlow The StateFlow containing the UI state
     * @param loadingState The loading UI state
     * @param minDuration Minimum duration the loading state should be maintained
     * @param action The action that triggers loading
     */
    suspend fun <T> verifyMinimumLoadingDuration(
        stateFlow: StateFlow<T>,
        loadingState: T,
        minDuration: Duration,
        action: suspend () -> Unit,
    ) {
        val startTime = System.currentTimeMillis()
        action()

        StateFlowTestUtils.run { stateFlow.shouldEmit(loadingState) }

        // Wait for state to change from loading
        stateFlow.first { it != loadingState }

        val endTime = System.currentTimeMillis()
        val actualDuration = endTime - startTime

        if (actualDuration < minDuration.inWholeMilliseconds) {
            throw AssertionError(
                "Loading state was maintained for only ${actualDuration}ms, expected at least ${minDuration.inWholeMilliseconds}ms",
            )
        }
    }
}

/**
 * ViewModel action testing utilities.
 */
object ViewModelActionTestUtils {
    /**
     * Verifies that a ViewModel action produces the expected state change.
     *
     * @param viewModel The ViewModel instance
     * @param stateFlow The StateFlow to observe
     * @param initialState The expected initial state
     * @param expectedFinalState The expected final state
     * @param action The action to execute
     */
    suspend fun <T, VM : ViewModel> verifyActionProducesState(
        viewModel: VM,
        stateFlow: StateFlow<T>,
        initialState: T,
        expectedFinalState: T,
        action: suspend VM.() -> Unit,
    ) {
        StateFlowTestUtils.run { stateFlow.shouldHaveValue(initialState) }
        viewModel.action()
        StateFlowTestUtils.run { stateFlow.shouldEmit(expectedFinalState) }
    }

    /**
     * Verifies that a ViewModel action is idempotent (produces same result when called multiple times).
     *
     * @param viewModel The ViewModel instance
     * @param stateFlow The StateFlow to observe
     * @param expectedState The expected state after action
     * @param action The action to execute
     * @param iterations Number of times to execute the action
     */
    suspend fun <T, VM : ViewModel> verifyActionIdempotent(
        viewModel: VM,
        stateFlow: StateFlow<T>,
        expectedState: T,
        action: suspend VM.() -> Unit,
        iterations: Int = 3,
    ) {
        repeat(iterations) {
            viewModel.action()
            StateFlowTestUtils.run { stateFlow.shouldEmit(expectedState) }
        }
    }

    /**
     * Verifies that concurrent actions don't cause race conditions.
     *
     * @param viewModel The ViewModel instance
     * @param stateFlow The StateFlow to observe
     * @param action The action to execute concurrently
     * @param concurrency Number of concurrent executions
     */
    suspend fun <T, VM : ViewModel> verifyConcurrentActions(
        viewModel: VM,
        stateFlow: StateFlow<T>,
        action: suspend VM.() -> Unit,
        concurrency: Int = 5,
    ) {
        val jobs = (1..concurrency).map {
            @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
            kotlinx.coroutines.GlobalScope.launch {
                viewModel.action()
            }
        }

        jobs.forEach { it.join() }

        // Verify state is stable after all concurrent actions
        kotlinx.coroutines.delay(100)
        val finalState = stateFlow.value
        kotlinx.coroutines.delay(100)
        StateFlowTestUtils.run { stateFlow.shouldHaveValue(finalState) }
    }
}

/**
 * ViewModel error handling testing utilities.
 */
object ViewModelErrorTestUtils {
    /**
     * Verifies that a ViewModel properly handles exceptions.
     *
     * @param viewModel The ViewModel instance
     * @param stateFlow The StateFlow to observe
     * @param expectedErrorState The expected error state
     * @param action The action that should throw an exception
     */
    suspend fun <T, VM : ViewModel> verifyExceptionHandling(
        viewModel: VM,
        stateFlow: StateFlow<T>,
        expectedErrorState: T,
        action: suspend VM.() -> Unit,
    ) {
        try {
            viewModel.action()
            StateFlowTestUtils.run { stateFlow.shouldEmit(expectedErrorState) }
        } catch (e: Exception) {
            // If the action throws, verify the error state is still set
            println("ViewModel action threw exception: ${e.message}")
            StateFlowTestUtils.run { stateFlow.shouldEmit(expectedErrorState) }
        }
    }

    /**
     * Verifies that a ViewModel can recover from error states.
     *
     * @param viewModel The ViewModel instance
     * @param stateFlow The StateFlow to observe
     * @param errorState The error state to recover from
     * @param successState The success state after recovery
     * @param errorAction The action that causes an error
     * @param recoveryAction The action that recovers from the error
     */
    suspend fun <T, VM : ViewModel> verifyErrorRecovery(
        stateFlow: StateFlow<T>,
        errorRecoveryConfig: ErrorRecoveryConfig<T, VM>,
    ) {
        // Cause error
        errorRecoveryConfig.errorAction(errorRecoveryConfig.viewModel)
        StateFlowTestUtils.run { stateFlow.shouldEmit(errorRecoveryConfig.errorState) }

        // Recover from error
        errorRecoveryConfig.recoveryAction(errorRecoveryConfig.viewModel)
        StateFlowTestUtils.run { stateFlow.shouldEmit(errorRecoveryConfig.successState) }
    }
}

/**
 * ViewModel lifecycle testing utilities.
 */
object ViewModelLifecycleTestUtils {
    /**
     * Verifies that a ViewModel properly cleans up resources when cleared.
     *
     * @param viewModel The ViewModel instance
     * @param verifyCleanup Function to verify cleanup occurred
     */
    fun <VM : ViewModel> verifyViewModelCleanup(
        verifyCleanup: () -> Unit,
    ) {
        // Simulate ViewModel being cleared
        // This would be done by the framework in real scenarios

        // Clear the ViewModel (in real tests, this would be done by the framework)
        // For unit tests, we can verify that cleanup logic is called
        verifyCleanup()
    }

    /**
     * Verifies that a ViewModel's initial state is correct.
     *
     * @param stateFlow The StateFlow to observe
     * @param expectedInitialState The expected initial state
     */
    fun <T, VM : ViewModel> verifyInitialState(
        stateFlow: StateFlow<T>,
        expectedInitialState: T,
    ) {
        StateFlowTestUtils.run { stateFlow.shouldHaveValue(expectedInitialState) }
    }
}

/**
 * Configuration for error recovery testing.
 */
data class ErrorRecoveryConfig<T, VM : ViewModel>(
    val viewModel: VM,
    val errorState: T,
    val successState: T,
    val errorAction: suspend VM.() -> Unit,
    val recoveryAction: suspend VM.() -> Unit,
)

/**
 * Mock ViewModel factory for testing.
 */
object MockViewModelFactory {
    /**
     * Creates a mock ViewModel with the specified StateFlow.
     *
     * @param initialState The initial state of the StateFlow
     * @return Pair of (mock ViewModel, StateFlow)
     */
    inline fun <reified VM : ViewModel, T> createMockViewModel(
        initialState: T,
    ): Pair<VM, MutableStateFlow<T>> {
        val stateFlow = MutableStateFlow(initialState)
        val mockViewModel = mockk<VM>()

        // Configure mock behavior as needed
        every { mockViewModel.hashCode() } returns System.identityHashCode(mockViewModel)
        every { mockViewModel.equals(any()) } returns false
        every { mockViewModel.toString() } returns "MockViewModel@${System.identityHashCode(mockViewModel)}"

        return Pair(mockViewModel, stateFlow)
    }
}

/**
 * ViewModel test assertions for common patterns.
 */
object ViewModelAssertions {
    /**
     * Asserts that a ViewModel action was called with specific parameters.
     *
     * @param mockViewModel The mock ViewModel
     * @param verifyBlock The verification block
     */
    fun <VM : ViewModel> verifyViewModelAction(
        mockViewModel: VM,
        verifyBlock: VM.() -> Unit,
    ) {
        verify { mockViewModel.verifyBlock() }
    }

    /**
     * Asserts that a ViewModel action was never called.
     *
     * @param mockViewModel The mock ViewModel
     * @param verifyBlock The verification block
     */
    fun <VM : ViewModel> verifyViewModelActionNeverCalled(
        mockViewModel: VM,
        verifyBlock: VM.() -> Unit,
    ) {
        verify(exactly = 0) { mockViewModel.verifyBlock() }
    }

    /**
     * Asserts that a ViewModel action was called exactly N times.
     *
     * @param mockViewModel The mock ViewModel
     * @param expectedCalls The expected number of calls
     * @param verifyBlock The verification block
     */
    fun <VM : ViewModel> verifyViewModelActionCalledTimes(
        mockViewModel: VM,
        expectedCalls: Int,
        verifyBlock: VM.() -> Unit,
    ) {
        verify(exactly = expectedCalls) { mockViewModel.verifyBlock() }
    }
}

/**
 * Test data class for representing UI states in tests.
 */
sealed class TestUiState {
    object Initial : TestUiState()
    object Loading : TestUiState()
    data class Success<T>(val data: T) : TestUiState()
    data class Error(val message: String, val throwable: Throwable? = null) : TestUiState()
}

/**
 * Extension functions for easier testing of common UI state patterns.
 */
fun TestUiState.isLoading(): Boolean = this is TestUiState.Loading
fun TestUiState.isSuccess(): Boolean = this is TestUiState.Success<*>
fun TestUiState.isError(): Boolean = this is TestUiState.Error
fun TestUiState.isInitial(): Boolean = this is TestUiState.Initial

@Suppress("UNCHECKED_CAST")
fun <T> TestUiState.getSuccessData(): T? = (this as? TestUiState.Success<T>)?.data
fun TestUiState.getErrorMessage(): String? = (this as? TestUiState.Error)?.message
fun TestUiState.getErrorThrowable(): Throwable? = (this as? TestUiState.Error)?.throwable
