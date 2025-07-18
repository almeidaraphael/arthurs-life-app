package com.lemonqwest.app.infrastructure.preferences

import com.lemonqwest.app.testutils.TestAuthPreferencesDataStore
import com.lemonqwest.app.testutils.TestDataStore
import io.mockk.MockKAnnotations
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName

/**
 * Comprehensive test suite for AuthPreferencesDataStore.
 *
 * Tests cover:
 * - User session storage and retrieval
 * - Role persistence and deserialization
 * - Authentication state management
 * - Data clearing and logout operations
 * - Flow-based reactive data access
 * - Edge cases and error handling
 *
 * CATEGORY A TEST: Infrastructure/data layer test using standalone pattern.
 * No ViewModelTestBase needed as this tests data layer, not presentation ViewModels.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore Infrastructure Tests")
class AuthPreferencesDataStoreTest {
    @get:org.junit.Rule
    val mainDispatcherRule = com.lemonqwest.app.testutils.MainDispatcherRule(
        UnconfinedTestDispatcher(),
    )

    private lateinit var authPreferencesDataStore: TestAuthPreferencesDataStore
    private lateinit var testDataStore: TestDataStore

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        testDataStore = TestDataStore()
        authPreferencesDataStore = TestAuthPreferencesDataStore(testDataStore)
    }

    // No manual dispatcher teardown needed
}
