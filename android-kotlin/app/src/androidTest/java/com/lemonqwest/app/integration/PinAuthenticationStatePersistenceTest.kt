package com.lemonqwest.app.integration

import com.lemonqwest.app.testutils.AndroidTestBase
import com.lemonqwest.app.testutils.EndToEndTestUtils
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Tests end-to-end authentication state persistence with complete test isolation.
 */
class PinAuthenticationStatePersistenceTest : AndroidTestBase() {
    @Test
    fun testAuthenticationStatePersistence() = runTest {
        val userId = "test-user"
        EndToEndTestUtils.testAuthenticationWorkflow(
            testScope = this,
            dataStore = testDataStore,
            userId = userId,
        )
        val preferences = testDataStore.getCurrentPreferences()
        val storedUserId = preferences[
            androidx.datastore.preferences.core.stringPreferencesKey("current_user_id"),
        ]
        assert(storedUserId == userId) {
            "Expected persisted user ID $userId, but got $storedUserId"
        }
    }
}
