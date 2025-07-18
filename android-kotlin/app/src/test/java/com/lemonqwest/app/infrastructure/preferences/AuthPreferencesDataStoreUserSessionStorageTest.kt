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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore User Session Storage Tests")
@Execution(ExecutionMode.SAME_THREAD)
class AuthPreferencesDataStoreUserSessionStorageTest {
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
    @DisplayName("Should store user session with ID and role")
    fun shouldStoreUserSessionWithIdAndRole() = runTest {
        val userId = "user-123"
        val role = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(userId, role)
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
        val childUserId = "child-456"
        val childRole = UserRole.CHILD
        authPreferencesDataStore.setCurrentUser(childUserId, childRole)
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
        val firstUserId = "user-111"
        val firstRole = UserRole.CHILD
        val secondUserId = "user-222"
        val secondRole = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(firstUserId, firstRole)
        authPreferencesDataStore.setCurrentUser(secondUserId, secondRole)
        val storedUserId = authPreferencesDataStore.currentUserId.first()
        val storedRole = authPreferencesDataStore.currentRole.first()
        assertEquals(secondUserId, storedUserId)
        assertEquals(secondRole, storedRole)
    }
}
