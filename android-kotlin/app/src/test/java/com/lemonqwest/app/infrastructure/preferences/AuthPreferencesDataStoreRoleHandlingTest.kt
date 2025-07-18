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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@DisplayName("AuthPreferencesDataStore Role Handling Tests")
@Execution(ExecutionMode.SAME_THREAD)
class AuthPreferencesDataStoreRoleHandlingTest {
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
    @DisplayName("Should handle CAREGIVER role persistence correctly")
    fun shouldHandleCaregiverRolePersistenceCorrectly() = runTest {
        val userId = "caregiver-user"
        val role = UserRole.CAREGIVER
        authPreferencesDataStore.setCurrentUser(userId, role)
        val storedRole = authPreferencesDataStore.currentRole.first()
        assertEquals(UserRole.CAREGIVER, storedRole)
    }

    @Test
    @DisplayName("Should handle CHILD role persistence correctly")
    fun shouldHandleChildRolePersistenceCorrectly() = runTest {
        val userId = "child-user"
        val role = UserRole.CHILD
        authPreferencesDataStore.setCurrentUser(userId, role)
        val storedRole = authPreferencesDataStore.currentRole.first()
        assertEquals(UserRole.CHILD, storedRole)
    }

    @Test
    @DisplayName("Should handle role switching between CAREGIVER and CHILD")
    fun shouldHandleRoleSwitchingBetweenCaregiverAndChild() = runTest {
        val userId = "switch-user"
        authPreferencesDataStore.setCurrentUser(userId, UserRole.CAREGIVER)
        val caregiverRole = authPreferencesDataStore.currentRole.first()
        authPreferencesDataStore.setCurrentUser(userId, UserRole.CHILD)
        val childRole = authPreferencesDataStore.currentRole.first()
        assertEquals(UserRole.CAREGIVER, caregiverRole)
        assertEquals(UserRole.CHILD, childRole)
    }
}
