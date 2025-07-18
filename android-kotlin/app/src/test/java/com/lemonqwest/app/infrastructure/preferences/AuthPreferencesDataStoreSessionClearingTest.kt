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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore Session Clearing Tests")
@Execution(ExecutionMode.SAME_THREAD)
class AuthPreferencesDataStoreSessionClearingTest {
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
    @DisplayName("Should clear all session data on logout")
    fun shouldClearAllSessionDataOnLogout() = runTest {
        val userId = "user-to-clear"
        val role = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(userId, role)
        assertTrue(authPreferencesDataStore.isAuthenticated.first())
        authPreferencesDataStore.clearCurrentUser()
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
        authPreferencesDataStore.clearCurrentUser()
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
        authPreferencesDataStore.setCurrentUser("user-123", UserRole.CHILD)
        assertTrue(authPreferencesDataStore.isAuthenticated.first())
        authPreferencesDataStore.clearCurrentUser()
        assertFalse(authPreferencesDataStore.isAuthenticated.first())
        assertNull(authPreferencesDataStore.currentUserId.first())
        assertNull(authPreferencesDataStore.currentRole.first())
    }
}
