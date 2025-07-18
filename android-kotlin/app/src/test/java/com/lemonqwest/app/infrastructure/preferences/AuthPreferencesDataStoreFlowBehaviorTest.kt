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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore Flow Behavior Tests")
@Execution(ExecutionMode.SAME_THREAD)
class AuthPreferencesDataStoreFlowBehaviorTest {
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
    @DisplayName("Should emit current user ID through Flow")
    fun shouldEmitCurrentUserIdThroughFlow() = runTest {
        val userId = "flow-user-123"
        val role = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(userId, role)
        authPreferencesDataStore.currentUserId.first().let { emittedUserId ->
            assertEquals(userId, emittedUserId)
        }
    }

    @Test
    @DisplayName("Should emit role changes through Flow")
    fun shouldEmitRoleChangesThroughFlow() = runTest {
        val userId = "flow-role-user"
        authPreferencesDataStore.setCurrentUser(userId, UserRole.CAREGIVER)
        assertEquals(UserRole.CAREGIVER, authPreferencesDataStore.currentRole.first())
        authPreferencesDataStore.setCurrentUser(userId, UserRole.CHILD)
        assertEquals(UserRole.CHILD, authPreferencesDataStore.currentRole.first())
    }

    @Test
    @DisplayName("Should emit authentication state changes through Flow")
    fun shouldEmitAuthenticationStateChangesThroughFlow() = runTest {
        assertFalse(authPreferencesDataStore.isAuthenticated.first())
        authPreferencesDataStore.setCurrentUser("user-123", UserRole.CHILD)
        assertTrue(authPreferencesDataStore.isAuthenticated.first())
        authPreferencesDataStore.clearCurrentUser()
        assertFalse(authPreferencesDataStore.isAuthenticated.first())
    }
}
