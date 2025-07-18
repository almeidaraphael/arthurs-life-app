package com.lemonqwest.app.infrastructure.preferences

import android.content.Context
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.TestAuthPreferencesDataStore
import com.lemonqwest.app.testutils.TestDataStore
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore Edge Cases and Error Handling Tests")
@Execution(ExecutionMode.SAME_THREAD)
class AuthPreferencesDataStoreEdgeCasesAndErrorHandlingTest {
    private val mainDispatcherLock = Any()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var authPreferencesDataStore: TestAuthPreferencesDataStore
    private lateinit var testDataStore: TestDataStore
    private lateinit var mockContext: Context

    @BeforeEach
    fun setUp() {
        synchronized(mainDispatcherLock) {
            try { Dispatchers.resetMain() } catch (_: IllegalStateException) {}
            Dispatchers.setMain(testDispatcher)
        }
        MockKAnnotations.init(this)
        testDataStore = TestDataStore()
        mockContext = mockk()
        authPreferencesDataStore = TestAuthPreferencesDataStore(testDataStore)
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        synchronized(mainDispatcherLock) {
            try { Dispatchers.resetMain() } catch (_: IllegalStateException) {}
        }
    }

    @Test
    @DisplayName("Should handle empty user ID gracefully")
    fun shouldHandleEmptyUserIdGracefully() = runTest {
        val emptyUserId = ""
        val role = UserRole.CHILD
        authPreferencesDataStore.setCurrentUser(emptyUserId, role)
        val storedUserId = authPreferencesDataStore.currentUserId.first()
        val storedRole = authPreferencesDataStore.currentRole.first()
        val isAuthenticated = authPreferencesDataStore.isAuthenticated.first()
        assertEquals(emptyUserId, storedUserId)
        assertEquals(role, storedRole)
        assertTrue(isAuthenticated)
    }

    @Test
    @DisplayName("Should handle multiple rapid session updates")
    fun shouldHandleMultipleRapidSessionUpdates() = runTest {
        val updates = listOf(
            Pair("user-1", UserRole.CHILD),
            Pair("user-2", UserRole.CAREGIVER),
            Pair("user-3", UserRole.CHILD),
            Pair("user-4", UserRole.CAREGIVER),
        )
        updates.forEach { (userId, role) ->
            authPreferencesDataStore.setCurrentUser(userId, role)
        }
        val finalUserId = authPreferencesDataStore.currentUserId.first()
        val finalRole = authPreferencesDataStore.currentRole.first()
        assertEquals("user-4", finalUserId)
        assertEquals(UserRole.CAREGIVER, finalRole)
        assertTrue(authPreferencesDataStore.isAuthenticated.first())
    }

    @Test
    @DisplayName("Should handle long user ID strings")
    fun shouldHandleLongUserIdStrings() = runTest {
        val longUserId = "a".repeat(1000)
        val role = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(longUserId, role)
        val storedUserId = authPreferencesDataStore.currentUserId.first()
        assertEquals(longUserId, storedUserId)
        assertEquals(1000, storedUserId?.length)
    }
}
