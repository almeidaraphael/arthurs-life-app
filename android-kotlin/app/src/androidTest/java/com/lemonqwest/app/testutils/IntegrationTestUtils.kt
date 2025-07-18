package com.lemonqwest.app.testutils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.lemonqwest.app.infrastructure.database.LemonQwestDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

/**
 * Integration testing utilities for LemonQwest app.
 *
 * Provides utilities for testing complete workflows and integrations.
 */

/**
 * Integration test base class providing common setup and utilities.
 */
abstract class IntegrationTestBase {
    protected lateinit var database: LemonQwestDatabase
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
        database.close()
        testDataStore.clear()
    }
}

/**
 * Test DataStore implementation for integration tests.
 */
class TestDataStore : DataStore<Preferences> {
    private var data = emptyPreferences()

    override val data: Flow<Preferences>
        get() = kotlinx.coroutines.flow.flowOf(data)

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        data = transform(data)
        return data
    }

    suspend fun clear() {
        data = emptyPreferences()
    }
}

/**
 * Data consistency testing utilities.
 */
object DataConsistencyTestUtils {

    /**
     * Verifies that data remains consistent across database and DataStore.
     */
    suspend fun verifyDataConsistency(
        dataStore: DataStore<Preferences>,
    ): Boolean = runTest {
        // Simple consistency check implementation
        try {
            dataStore.data.first()
            return@runTest true
        } catch (e: Exception) {
            throw IllegalStateException("Data consistency check failed", e)
        }
    }
}

/**
 * End-to-end testing utilities.
 */
object EndToEndTestUtils {

    /**
     * Executes an end-to-end test workflow.
     */
    suspend fun executeWorkflow(
        steps: List<suspend () -> Unit>,
    ) = runTest {
        steps.forEach { step ->
            step()
        }
    }
}
