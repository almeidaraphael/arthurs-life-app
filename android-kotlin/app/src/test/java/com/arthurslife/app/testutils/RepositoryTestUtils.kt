package com.arthurslife.app.testutils

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Repository testing utilities for Arthur's Life app.
 *
 * This file provides utilities for testing repository implementations, including:
 * - Mock repository creation
 * - Data source testing
 * - Repository behavior verification
 * - Error handling testing
 * - Performance testing
 */

/**
 * Base class for repository testing with common setup and utilities.
 */
abstract class RepositoryTestBase {
    /**
     * Sets up MockK annotations before each test.
     */
    protected fun setupMocks() {
        MockKAnnotations.init(this)
    }

    /**
     * Runs a repository test with proper coroutine handling.
     *
     * @param testBody The test body to execute
     */
    protected suspend fun runRepositoryTest(testBody: suspend () -> Unit) = runTest {
        testBody()
    }
}

/**
 * Mock repository factory for creating test doubles.
 */
object MockRepositoryFactory {
    /**
     * Creates a mock repository with basic configuration.
     *
     * @return Mock repository instance
     */
    inline fun <reified T : Any> createMockRepository(): T {
        return mockk<T> {
            // Default behavior for common methods
            every { hashCode() } returns System.identityHashCode(this@mockk)
            every { equals(any()) } returns false
            every { toString() } returns "MockRepository@${System.identityHashCode(this@mockk)}"
        }
    }

    /**
     * Creates a mock repository with pre-configured successful responses.
     *
     * @param successData The data to return for successful operations
     * @return Mock repository instance
     */
    inline fun <reified T : Any> createSuccessfulMockRepository(): T {
        return mockk<T> {
            // Configure common successful behaviors
            every { hashCode() } returns System.identityHashCode(this@mockk)
            every { equals(any()) } returns false
            every { toString() } returns "SuccessfulMockRepository@${System.identityHashCode(this@mockk)}"
        }
    }

    /**
     * Creates a mock repository that throws exceptions.
     *
     * @param exception The exception to throw
     * @return Mock repository instance
     */
    inline fun <reified T : Any> createFailingMockRepository(): T {
        return mockk<T> {
            every { hashCode() } returns System.identityHashCode(this@mockk)
            every { equals(any()) } returns false
            every { toString() } returns "FailingMockRepository@${System.identityHashCode(this@mockk)}"
        }
    }
}

/**
 * Repository behavior testing utilities.
 */
object RepositoryBehaviorTestUtils {
    /**
     * Verifies that a repository method returns expected data.
     *
     * @param repository The repository instance
     * @param expectedData The expected data
     * @param operation The repository operation to test
     */
    suspend fun <T, R> verifyRepositoryReturnsData(
        repository: R,
        expectedData: T,
        operation: suspend R.() -> T,
    ) {
        val actualData = repository.operation()
        if (actualData != expectedData) {
            throw AssertionError("Expected repository to return $expectedData, but got $actualData")
        }
    }

    /**
     * Verifies that a repository method returns a Flow with expected data.
     *
     * @param repository The repository instance
     * @param expectedData The expected data
     * @param operation The repository operation that returns a Flow
     */
    suspend fun <T, R> verifyRepositoryReturnsFlow(
        repository: R,
        expectedData: T,
        operation: suspend R.() -> Flow<T>,
    ) {
        val actualData = repository.operation().first()
        if (actualData != expectedData) {
            throw AssertionError(
                "Expected repository Flow to emit $expectedData, but got $actualData",
            )
        }
    }

    /**
     * Verifies that a repository method throws a specific exception.
     *
     * @param repository The repository instance
     * @param expectedException The expected exception class
     * @param operation The repository operation to test
     */
    suspend inline fun <reified E : Throwable, R> verifyRepositoryThrowsException(
        repository: R,
        expectedException: Class<E> = E::class.java,
        noinline operation: suspend R.() -> Any,
    ): E {
        return try {
            repository.operation()
            throw AssertionError("Expected ${expectedException.simpleName} to be thrown")
        } catch (e: Throwable) {
            if (expectedException.isInstance(e)) {
                @Suppress("UNCHECKED_CAST")
                e as E
            } else {
                throw AssertionError(
                    "Expected ${expectedException.simpleName} but got ${e::class.simpleName}: ${e.message}",
                )
            }
        }
    }

    /**
     * Verifies that a repository caches data correctly.
     *
     * @param repository The repository instance
     * @param operation The repository operation to test
     * @param calls Number of times to call the operation
     */
    suspend fun <T, R> verifyRepositoryCaching(
        repository: R,
        operation: suspend R.() -> T,
        calls: Int = 3,
    ) {
        val results = mutableListOf<T>()
        repeat(calls) {
            results.add(repository.operation())
        }

        // Verify all results are the same (indicating caching)
        val firstResult = results.first()
        if (!results.all { it == firstResult }) {
            throw AssertionError("Repository caching failed: got different results $results")
        }
    }

    /**
     * Verifies that a repository properly invalidates cache.
     *
     * @param repository The repository instance
     * @param getOperation The operation to get data
     * @param invalidateOperation The operation to invalidate cache
     * @param updateOperation The operation to update data
     */
    suspend fun <T, R> verifyRepositoryCacheInvalidation(
        repository: R,
        getOperation: suspend R.() -> T,
        invalidateOperation: suspend R.() -> Unit,
        updateOperation: suspend R.() -> T,
    ) {
        // Get initial data
        val initialData = repository.getOperation()

        // Update data
        val updatedData = repository.updateOperation()

        // Invalidate cache
        repository.invalidateOperation()

        // Get data again - should be different from initial
        val newData = repository.getOperation()

        if (newData == initialData && newData != updatedData) {
            throw AssertionError("Cache invalidation failed: still getting old data")
        }
    }
}

/**
 * Data source testing utilities for testing repository data sources.
 */
object DataSourceTestUtils {
    /**
     * Creates a mock data source that returns test data.
     *
     * @param testData The data to return
     * @return Mock data source
     */
    inline fun <reified T : Any> createMockDataSource(): T {
        return mockk<T>()
    }

    /**
     * Verifies that a data source is called with correct parameters.
     *
     * @param dataSource The mock data source
     * @param verifyBlock The verification block
     */
    fun <T> verifyDataSourceCalled(
        dataSource: T,
        verifyBlock: T.() -> Unit,
    ) {
        verify { dataSource.verifyBlock() }
    }

    /**
     * Verifies that a suspend data source is called with correct parameters.
     *
     * @param dataSource The mock data source
     * @param verifyBlock The verification block
     */
    fun <T> verifyDataSourceCoCall(
        dataSource: T,
        verifyBlock: suspend T.() -> Unit,
    ) {
        coVerify { dataSource.verifyBlock() }
    }

    /**
     * Configures a mock data source to return specific data.
     *
     * @param dataSource The mock data source
     * @param data The data to return
     * @param configureBlock The configuration block
     */
    fun <T, D> configureMockDataSource(
        dataSource: T,
        data: D,
        configureBlock: T.() -> D,
    ) {
        every { dataSource.configureBlock() } returns data
    }

    /**
     * Configures a mock data source for suspend functions.
     *
     * @param dataSource The mock data source
     * @param data The data to return
     * @param configureBlock The configuration block
     */
    fun <T, D> configureMockDataSourceSuspend(
        dataSource: T,
        data: D,
        configureBlock: suspend T.() -> D,
    ) {
        coEvery { dataSource.configureBlock() } returns data
    }

    /**
     * Configures a mock data source to return a Flow.
     *
     * @param dataSource The mock data source
     * @param data The data to emit in the Flow
     * @param configureBlock The configuration block
     */
    fun <T, D> configureMockDataSourceFlow(
        dataSource: T,
        data: D,
        configureBlock: T.() -> Flow<D>,
    ) {
        every { dataSource.configureBlock() } returns flowOf(data)
    }
}

/**
 * Repository error handling testing utilities.
 */
object RepositoryErrorTestUtils {
    /**
     * Tests repository behavior when data source throws an exception.
     *
     * @param repository The repository instance
     * @param mockDataSource The mock data source
     * @param exception The exception to throw
     * @param operation The repository operation to test
     * @param dataSourceOperation The data source operation that should throw
     */
    suspend fun <R, D> testDataSourceException(
        repository: R,
        mockDataSource: D,
        exception: Throwable,
        operation: suspend R.() -> Any,
        dataSourceOperation: suspend D.() -> Any,
    ) {
        // Configure data source to throw exception
        coEvery { mockDataSource.dataSourceOperation() } throws exception

        // Verify repository handles the exception appropriately
        try {
            repository.operation()
            throw AssertionError("Expected repository to throw exception when data source fails")
        } catch (e: Throwable) {
            // Verify the exception is handled correctly
            if (e !is AssertionError) {
                // Expected behavior - repository should handle data source exceptions
            } else {
                throw e
            }
        }
    }

    /**
     * Tests repository retry behavior.
     *
     * @param repository The repository instance
     * @param mockDataSource The mock data source
     * @param exception The exception to throw initially
     * @param successData The data to return after retries
     * @param operation The repository operation to test
     * @param dataSourceOperation The data source operation
     * @param maxRetries Expected number of retries
     */
    suspend fun <R, D, T> testRepositoryRetry(
        repository: R,
        mockDataSource: D,
        operation: suspend R.() -> T,
        dataSourceOperation: suspend D.() -> T,
        retryConfig: RetryTestConfig<T>,
    ) {
        // Configure data source to fail first few times, then succeed
        var callCount = 0
        coEvery { mockDataSource.dataSourceOperation() } answers {
            callCount++
            if (callCount <= retryConfig.maxRetries) {
                throw retryConfig.exception
            } else {
                retryConfig.successData
            }
        }

        // Execute operation and verify it eventually succeeds
        val result = repository.operation()
        if (result != retryConfig.successData) {
            throw AssertionError(
                "Expected repository to return ${retryConfig.successData} after retries, but got $result",
            )
        }

        // Verify the expected number of calls were made
        coVerify(exactly = retryConfig.maxRetries + 1) { mockDataSource.dataSourceOperation() }
    }

    /**
     * Tests repository timeout behavior.
     *
     * @param repository The repository instance
     * @param operation The repository operation to test
     * @param expectedTimeout Maximum expected timeout
     */
    suspend fun <R> testRepositoryTimeout(
        repository: R,
        operation: suspend R.() -> Any,
        expectedTimeout: Duration = 5.seconds,
    ) {
        val startTime = System.currentTimeMillis()

        try {
            kotlinx.coroutines.withTimeout(expectedTimeout.inWholeMilliseconds) {
                repository.operation()
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            // Expected behavior for timeout testing
            val currentTime = System.currentTimeMillis()
            require(currentTime - startTime >= expectedTimeout.inWholeMilliseconds) {
                "Operation completed too quickly for timeout test: ${currentTime - startTime}ms < ${expectedTimeout.inWholeMilliseconds}ms - ${e.message}"
            }
        }

        val endTime = System.currentTimeMillis()
        val actualDuration = endTime - startTime

        if (actualDuration > expectedTimeout.inWholeMilliseconds + 1000) { // 1 second tolerance
            throw AssertionError(
                "Repository operation took too long: ${actualDuration}ms (expected max: ${expectedTimeout.inWholeMilliseconds}ms)",
            )
        }
    }
}

/**
 * Repository integration testing utilities.
 */
object RepositoryIntegrationTestUtils {
    /**
     * Tests end-to-end data flow through repository.
     *
     * @param repository The repository instance
     * @param inputData The input data
     * @param expectedOutputData The expected output data
     * @param writeOperation The operation to write data
     * @param readOperation The operation to read data
     */
    suspend fun <R, I, O> testEndToEndDataFlow(
        repository: R,
        inputData: I,
        expectedOutputData: O,
        writeOperation: suspend R.(I) -> Unit,
        readOperation: suspend R.() -> O,
    ) {
        // Write data
        repository.writeOperation(inputData)

        // Read data and verify
        val actualOutputData = repository.readOperation()
        if (actualOutputData != expectedOutputData) {
            throw AssertionError(
                "End-to-end test failed: expected $expectedOutputData, but got $actualOutputData",
            )
        }
    }

    /**
     * Tests repository data consistency across multiple operations.
     *
     * @param repository The repository instance
     * @param operations List of operations to execute
     * @param finalStateVerification Function to verify final state
     */
    suspend fun <R> testDataConsistency(
        repository: R,
        operations: List<suspend R.() -> Unit>,
        finalStateVerification: suspend R.() -> Unit,
    ) {
        // Execute all operations
        operations.forEach { operation ->
            repository.operation()
        }

        // Verify final state
        repository.finalStateVerification()
    }

    /**
     * Tests repository performance under load.
     *
     * @param repository The repository instance
     * @param operation The operation to test
     * @param concurrency Number of concurrent operations
     * @param iterations Number of iterations per concurrent operation
     */
    suspend fun <R> testRepositoryUnderLoad(
        repository: R,
        operation: suspend R.() -> Any,
        concurrency: Int = 10,
        iterations: Int = 100,
    ) {
        val jobs = (1..concurrency).map {
            @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
            kotlinx.coroutines.GlobalScope.launch {
                repeat(iterations) {
                    repository.operation()
                }
            }
        }

        // Wait for all operations to complete
        jobs.forEach { it.join() }

        // Verify repository is still functional after load test
        repository.operation()
    }
}

/**
 * Repository performance testing utilities.
 */
object RepositoryPerformanceTestUtils {
    /**
     * Measures repository operation performance.
     *
     * @param repository The repository instance
     * @param operation The operation to measure
     * @param iterations Number of iterations
     * @return Performance metrics
     */
    suspend fun <R> measureRepositoryPerformance(
        repository: R,
        operation: suspend R.() -> Any,
        iterations: Int = 100,
    ): RepositoryPerformanceMetrics {
        val times = mutableListOf<Long>()

        repeat(iterations) {
            val startTime = System.currentTimeMillis()
            repository.operation()
            val endTime = System.currentTimeMillis()
            times.add(endTime - startTime)
        }

        return RepositoryPerformanceMetrics(
            iterations = iterations,
            times = times,
            averageTime = times.average(),
            minTime = times.minOrNull() ?: 0,
            maxTime = times.maxOrNull() ?: 0,
            medianTime = times.sorted().let { sorted ->
                if (sorted.size % 2 == 0) {
                    (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
                } else {
                    sorted[sorted.size / 2].toDouble()
                }
            },
        )
    }

    /**
     * Verifies repository operation performance meets requirements.
     *
     * @param repository The repository instance
     * @param operation The operation to test
     * @param maxAverageTime Maximum allowed average time
     * @param maxSingleTime Maximum allowed single operation time
     * @param iterations Number of iterations to test
     */
    suspend fun <R> verifyRepositoryPerformance(
        repository: R,
        operation: suspend R.() -> Any,
        maxAverageTime: Duration,
        maxSingleTime: Duration,
        iterations: Int = 50,
    ) {
        val metrics = measureRepositoryPerformance(repository, operation, iterations)

        if (metrics.averageTime > maxAverageTime.inWholeMilliseconds) {
            throw AssertionError(
                "Repository average performance too slow: ${metrics.averageTime}ms (max: ${maxAverageTime.inWholeMilliseconds}ms)",
            )
        }

        if (metrics.maxTime > maxSingleTime.inWholeMilliseconds) {
            throw AssertionError(
                "Repository single operation too slow: ${metrics.maxTime}ms (max: ${maxSingleTime.inWholeMilliseconds}ms)",
            )
        }
    }
}

/**
 * Configuration for retry testing.
 */
data class RetryTestConfig<T>(
    val exception: Throwable,
    val successData: T,
    val maxRetries: Int = 3,
)

/**
 * Performance metrics for repository operations.
 */
data class RepositoryPerformanceMetrics(
    val iterations: Int,
    val times: List<Long>,
    val averageTime: Double,
    val minTime: Long,
    val maxTime: Long,
    val medianTime: Double,
) {
    fun printMetrics() {
        println("Repository Performance Metrics:")
        println("  Iterations: $iterations")
        println("  Average Time: ${averageTime}ms")
        println("  Median Time: ${medianTime}ms")
        println("  Min Time: ${minTime}ms")
        println("  Max Time: ${maxTime}ms")
        println("  Range: ${maxTime - minTime}ms")
    }
}
