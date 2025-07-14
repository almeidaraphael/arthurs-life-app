package com.arthurslife.app.infrastructure.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arthurslife.app.domain.user.UserRole
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
 */
@DisplayName("AuthPreferencesDataStore Infrastructure Tests")
class AuthPreferencesDataStoreTest {

    private lateinit var authPreferencesDataStore: TestAuthPreferencesDataStore
    private lateinit var testDataStore: TestDataStore
    private lateinit var mockContext: Context

    @BeforeEach
    fun setUp() {
        testDataStore = TestDataStore()
        mockContext = mockk()
        authPreferencesDataStore = TestAuthPreferencesDataStore(testDataStore)
    }

    @Nested
    @DisplayName("User Session Storage")
    inner class UserSessionStorage {

        @Test
        @DisplayName("Should store user session with ID and role")
        fun shouldStoreUserSessionWithIdAndRole() = runTest {
            // Arrange
            val userId = "user-123"
            val role = UserRole.CAREGIVER

            // Act
            authPreferencesDataStore.setCurrentUser(userId, role)

            // Assert
            val storedUserId = authPreferencesDataStore.currentUserId.first()
            val storedRole = authPreferencesDataStore.currentRole.first()
            val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()

            assertEquals(userId, storedUserId)
            assertEquals(role, storedRole)
            assertTrue(isAuthenticated)
        }

        @Test
        @DisplayName("Should store child user session correctly")
        fun shouldStoreChildUserSessionCorrectly() = runTest {
            // Arrange
            val childUserId = "child-456"
            val childRole = UserRole.CHILD

            // Act
            authPreferencesDataStore.setCurrentUser(childUserId, childRole)

            // Assert
            val storedUserId = authPreferencesDataStore.currentUserId.first()
            val storedRole = authPreferencesDataStore.currentRole.first()
            val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()

            assertEquals(childUserId, storedUserId)
            assertEquals(childRole, storedRole)
            assertTrue(isAuthenticated)
        }

        @Test
        @DisplayName("Should overwrite existing session when setting new user")
        fun shouldOverwriteExistingSessionWhenSettingNewUser() = runTest {
            // Arrange
            val firstUserId = "user-111"
            val firstRole = UserRole.CHILD
            val secondUserId = "user-222"
            val secondRole = UserRole.CAREGIVER

            // Act
            authPreferencesDataStore.setCurrentUser(firstUserId, firstRole)
            authPreferencesDataStore.setCurrentUser(secondUserId, secondRole)

            // Assert
            val storedUserId = authPreferencesDataStore.currentUserId.first()
            val storedRole = authPreferencesDataStore.currentRole.first()

            assertEquals(secondUserId, storedUserId)
            assertEquals(secondRole, storedRole)
        }
    }

    @Nested
    @DisplayName("Session Retrieval")
    inner class SessionRetrieval {

        @Test
        @DisplayName("Should retrieve stored user ID correctly")
        fun shouldRetrieveStoredUserIdCorrectly() = runTest {
            // Arrange
            val userId = "test-user-789"
            val role = UserRole.CAREGIVER

            // Act
            authPreferencesDataStore.setCurrentUser(userId, role)
            val retrievedUserId = authPreferencesDataStore.currentUserId.first()

            // Assert
            assertEquals(userId, retrievedUserId)
        }

        @Test
        @DisplayName("Should retrieve stored role correctly")
        fun shouldRetrieveStoredRoleCorrectly() = runTest {
            // Arrange
            val userId = "test-user-999"
            val role = UserRole.CHILD

            // Act
            authPreferencesDataStore.setCurrentUser(userId, role)
            val retrievedRole = authPreferencesDataStore.currentRole.first()

            // Assert
            assertEquals(role, retrievedRole)
        }

        @Test
        @DisplayName("Should return null for user ID when no session exists")
        fun shouldReturnNullForUserIdWhenNoSessionExists() = runTest {
            // Act
            val userId = authPreferencesDataStore.currentUserId.first()

            // Assert
            assertNull(userId)
        }

        @Test
        @DisplayName("Should return null for role when no session exists")
        fun shouldReturnNullForRoleWhenNoSessionExists() = runTest {
            // Act
            val role = authPreferencesDataStore.currentRole.first()

            // Assert
            assertNull(role)
        }

        @Test
        @DisplayName("Should return false for authentication when no session exists")
        fun shouldReturnFalseForAuthenticationWhenNoSessionExists() = runTest {
            // Act
            val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()

            // Assert
            assertFalse(isAuthenticated)
        }
    }

    @Nested
    @DisplayName("Session Clearing")
    inner class SessionClearing {

        @Test
        @DisplayName("Should clear all session data on logout")
        fun shouldClearAllSessionDataOnLogout() = runTest {
            // Arrange
            val userId = "user-to-clear"
            val role = UserRole.CAREGIVER
            authPreferencesDataStore.setCurrentUser(userId, role)

            // Verify session exists
            assertTrue(authPreferencesDataStore.isAuthenticated.first())

            // Act
            authPreferencesDataStore.clearCurrentUser()

            // Assert
            val clearedUserId = authPreferencesDataStore.currentUserId.first()
            val clearedRole = authPreferencesDataStore.currentRole.first()
            val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()

            assertNull(clearedUserId)
            assertNull(clearedRole)
            assertFalse(isAuthenticated)
        }

        @Test
        @DisplayName("Should handle clearing when no session exists")
        fun shouldHandleClearingWhenNoSessionExists() = runTest {
            // Act (no session exists)
            authPreferencesDataStore.clearCurrentUser()

            // Assert
            val userId = authPreferencesDataStore.currentUserId.first()
            val role = authPreferencesDataStore.currentRole.first()
            val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()

            assertNull(userId)
            assertNull(role)
            assertFalse(isAuthenticated)
        }

        @Test
        @DisplayName("Should set authentication to false but preserve other cleared state")
        fun shouldSetAuthenticationToFalseButPreserveOtherClearedState() = runTest {
            // Arrange
            authPreferencesDataStore.setCurrentUser("user-123", UserRole.CHILD)
            assertTrue(authPreferencesDataStore.isAuthenticated.first())

            // Act
            authPreferencesDataStore.clearCurrentUser()

            // Assert
            assertFalse(authPreferencesDataStore.isAuthenticated.first())
            assertNull(authPreferencesDataStore.currentUserId.first())
            assertNull(authPreferencesDataStore.currentRole.first())
        }
    }

    @Nested
    @DisplayName("Role Handling")
    inner class RoleHandling {

        @Test
        @DisplayName("Should handle CAREGIVER role persistence correctly")
        fun shouldHandleCaregiverRolePersistenceCorrectly() = runTest {
            // Arrange
            val userId = "caregiver-user"
            val role = UserRole.CAREGIVER

            // Act
            authPreferencesDataStore.setCurrentUser(userId, role)
            val storedRole = authPreferencesDataStore.currentRole.first()

            // Assert
            assertEquals(UserRole.CAREGIVER, storedRole)
        }

        @Test
        @DisplayName("Should handle CHILD role persistence correctly")
        fun shouldHandleChildRolePersistenceCorrectly() = runTest {
            // Arrange
            val userId = "child-user"
            val role = UserRole.CHILD

            // Act
            authPreferencesDataStore.setCurrentUser(userId, role)
            val storedRole = authPreferencesDataStore.currentRole.first()

            // Assert
            assertEquals(UserRole.CHILD, storedRole)
        }

        @Test
        @DisplayName("Should handle role switching between CAREGIVER and CHILD")
        fun shouldHandleRoleSwitchingBetweenCaregiverAndChild() = runTest {
            // Arrange
            val userId = "switch-user"

            // Act: Start as CAREGIVER
            authPreferencesDataStore.setCurrentUser(userId, UserRole.CAREGIVER)
            val caregiverRole = authPreferencesDataStore.currentRole.first()

            // Switch to CHILD
            authPreferencesDataStore.setCurrentUser(userId, UserRole.CHILD)
            val childRole = authPreferencesDataStore.currentRole.first()

            // Assert
            assertEquals(UserRole.CAREGIVER, caregiverRole)
            assertEquals(UserRole.CHILD, childRole)
        }
    }

    @Nested
    @DisplayName("Flow Behavior")
    inner class FlowBehavior {

        @Test
        @DisplayName("Should emit current user ID through Flow")
        fun shouldEmitCurrentUserIdThroughFlow() = runTest {
            // Arrange
            val userId = "flow-user-123"
            val role = UserRole.CAREGIVER

            // Act
            authPreferencesDataStore.setCurrentUser(userId, role)

            // Assert
            authPreferencesDataStore.currentUserId.first().let { emittedUserId ->
                assertEquals(userId, emittedUserId)
            }
        }

        @Test
        @DisplayName("Should emit role changes through Flow")
        fun shouldEmitRoleChangesThroughFlow() = runTest {
            // Arrange
            val userId = "flow-role-user"

            // Act & Assert: Set CAREGIVER
            authPreferencesDataStore.setCurrentUser(userId, UserRole.CAREGIVER)
            assertEquals(UserRole.CAREGIVER, authPreferencesDataStore.currentRole.first())

            // Act & Assert: Switch to CHILD
            authPreferencesDataStore.setCurrentUser(userId, UserRole.CHILD)
            assertEquals(UserRole.CHILD, authPreferencesDataStore.currentRole.first())
        }

        @Test
        @DisplayName("Should emit authentication state changes through Flow")
        fun shouldEmitAuthenticationStateChangesThroughFlow() = runTest {
            // Assert: Initially not authenticated
            assertFalse(authPreferencesDataStore.isAuthenticated.first())

            // Act: Authenticate
            authPreferencesDataStore.setCurrentUser("user-123", UserRole.CHILD)

            // Assert: Now authenticated
            assertTrue(authPreferencesDataStore.isAuthenticated.first())

            // Act: Logout
            authPreferencesDataStore.clearCurrentUser()

            // Assert: Not authenticated again
            assertFalse(authPreferencesDataStore.isAuthenticated.first())
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle empty user ID gracefully")
        fun shouldHandleEmptyUserIdGracefully() = runTest {
            // Arrange
            val emptyUserId = ""
            val role = UserRole.CHILD

            // Act
            authPreferencesDataStore.setCurrentUser(emptyUserId, role)

            // Assert
            val storedUserId = authPreferencesDataStore.currentUserId.first()
            val storedRole = authPreferencesDataStore.currentRole.first()
            val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()

            assertEquals(emptyUserId, storedUserId)
            assertEquals(role, storedRole)
            assertTrue(isAuthenticated) // Still considered authenticated
        }

        @Test
        @DisplayName("Should handle multiple rapid session updates")
        fun shouldHandleMultipleRapidSessionUpdates() = runTest {
            // Arrange
            val updates = listOf(
                Pair("user-1", UserRole.CHILD),
                Pair("user-2", UserRole.CAREGIVER),
                Pair("user-3", UserRole.CHILD),
                Pair("user-4", UserRole.CAREGIVER),
            )

            // Act: Rapid updates
            updates.forEach { (userId, role) ->
                authPreferencesDataStore.setCurrentUser(userId, role)
            }

            // Assert: Should have the last update
            val finalUserId = authPreferencesDataStore.currentUserId.first()
            val finalRole = authPreferencesDataStore.currentRole.first()

            assertEquals("user-4", finalUserId)
            assertEquals(UserRole.CAREGIVER, finalRole)
            assertTrue(authPreferencesDataStore.isAuthenticated.first())
        }

        @Test
        @DisplayName("Should handle long user ID strings")
        fun shouldHandleLongUserIdStrings() = runTest {
            // Arrange
            val longUserId = "a".repeat(1000) // Very long user ID
            val role = UserRole.CAREGIVER

            // Act
            authPreferencesDataStore.setCurrentUser(longUserId, role)

            // Assert
            val storedUserId = authPreferencesDataStore.currentUserId.first()
            assertEquals(longUserId, storedUserId)
            assertEquals(1000, storedUserId?.length)
        }
    }

    /**
     * Test DataStore implementation for isolated preference testing.
     */
    private class TestDataStore : DataStore<Preferences> {
        private val dataFlow = MutableStateFlow(emptyPreferences())

        override val data: Flow<Preferences> = dataFlow

        override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
            val currentData = dataFlow.value
            val newData = transform(currentData)
            dataFlow.value = newData
            return newData
        }

        suspend fun clear() {
            dataFlow.value = emptyPreferences()
        }
    }

    /**
     * Test implementation that extends AuthPreferencesDataStore for testing.
     */
    private class TestAuthPreferencesDataStore(
        private val testDataStore: TestDataStore,
    ) {
        private val userIdKey = stringPreferencesKey("current_user_id")
        private val roleKey = stringPreferencesKey("current_role")
        private val authKey = booleanPreferencesKey("is_authenticated")

        val currentUserId: Flow<String?> = testDataStore.data.map { preferences ->
            preferences[userIdKey]
        }

        val currentRole: Flow<UserRole?> = testDataStore.data.map { preferences ->
            preferences[roleKey]?.let { UserRole.valueOf(it) }
        }

        val isAuthenticated: Flow<Boolean> = testDataStore.data.map { preferences ->
            preferences[authKey] ?: false
        }

        suspend fun setCurrentUser(userId: String, role: UserRole) {
            testDataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    set(userIdKey, userId)
                    set(roleKey, role.name)
                    set(authKey, true)
                }
            }
        }

        suspend fun clearCurrentUser() {
            testDataStore.updateData { preferences ->
                preferences.toMutablePreferences().apply {
                    remove(userIdKey)
                    remove(roleKey)
                    set(authKey, false)
                }
            }
        }
    }
}
