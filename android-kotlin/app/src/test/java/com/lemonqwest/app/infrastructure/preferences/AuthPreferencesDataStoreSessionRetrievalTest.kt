package com.lemonqwest.app.infrastructure.preferences

import android.content.Context
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.TestAuthPreferencesDataStore
import com.lemonqwest.app.testutils.TestDataStore
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore Session Retrieval Tests")
class AuthPreferencesDataStoreSessionRetrievalTest {
    @get:org.junit.Rule
    val mainDispatcherRule = com.lemonqwest.app.testutils.MainDispatcherRule(
        UnconfinedTestDispatcher(),
    )

    private lateinit var authPreferencesDataStore: TestAuthPreferencesDataStore
    private lateinit var testDataStore: TestDataStore
    private lateinit var mockContext: Context

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        testDataStore = TestDataStore()
        mockContext = mockk()
        authPreferencesDataStore = TestAuthPreferencesDataStore(testDataStore)
    }

    // No manual dispatcher teardown needed

    @Test
    @DisplayName("Should retrieve stored user ID correctly")
    fun shouldRetrieveStoredUserIdCorrectly() = runTest {
        val userId = "test-user-789"
        val role = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(userId, role)
        val retrievedUserId = authPreferencesDataStore.currentUserId.first()
        assertEquals(userId, retrievedUserId)
    }

    @Test
    @DisplayName("Should retrieve stored role correctly")
    fun shouldRetrieveStoredRoleCorrectly() = runTest {
        val userId = "test-user-999"
        val role = UserRole.CHILD
        authPreferencesDataStore.setCurrentUser(userId, role)
        val retrievedRole = authPreferencesDataStore.currentRole.first()
        assertEquals(role, retrievedRole)
    }

    @Test
    @DisplayName("Should return null for user ID when no session exists")
    fun shouldReturnNullForUserIdWhenNoSessionExists() = runTest {
        val userId = authPreferencesDataStore.currentUserId.first()
        assertNull(userId)
    }

    @Test
    @DisplayName("Should return null for role when no session exists")
    fun shouldReturnNullForRoleWhenNoSessionExists() = runTest {
        val role = authPreferencesDataStore.currentRole.first()
        assertNull(role)
    }

    @Test
    @DisplayName("Should return false for authentication when no session exists")
    fun shouldReturnFalseForAuthenticationWhenNoSessionExists() = runTest {
        val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()
        assertFalse(isAuthenticated)
    }
}
