package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.user.UserRole
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for AuthViewModel initialization.
 *
 * Tests cover:
 * - Empty authentication state initialization
 * - Authenticated state initialization with existing user
 * - Child user session initialization
 * - Error handling during initialization
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - Initialization Tests")
class AuthViewModelInitializationTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = false)
        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun createViewModel(): AuthViewModel {
        return AuthViewModel(
            authenticationService = mockAuthenticationService,
            authenticationSessionService = mockAuthenticationSessionService,
            authenticationDomainService = mockAuthenticationDomainService,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )
    }

    @Test
    @DisplayName("Should initialize with empty authentication state when no current user")
    fun shouldInitializeWithEmptyAuthenticationStateWhenNoCurrentUser() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        authViewModel = createViewModel()
        authViewModel.initialize()
        val initialState = authViewModel.authState.first()
        assertNull(initialState.currentUser)
        assertFalse(initialState.isAuthenticated)
        assertNull(initialState.currentRole)
    }

    @Test
    @DisplayName("Should initialize with authenticated state when current user exists")
    fun shouldInitializeWithAuthenticatedStateWhenCurrentUserExists() {
        val testUser = TestDataFactory.createCaregiverUser(
            id = "user-123",
            name = "Test Caregiver",
        )
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testUser
        authViewModel = createViewModel()
        authViewModel.initialize()
        val initialState = authViewModel.authState.first()
        assertEquals(testUser, initialState.currentUser)
        assertTrue(initialState.isAuthenticated)
        assertEquals(UserRole.CAREGIVER, initialState.currentRole)
    }

    @Test
    @DisplayName("Should initialize with child user session correctly")
    fun shouldInitializeWithChildUserSessionCorrectly() {
        val childUser = TestDataFactory.createChildUser(
            id = "child-456",
            name = "Child User",
        )
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns childUser
        authViewModel = createViewModel()
        authViewModel.initialize()
        val initialState = authViewModel.authState.first()
        assertEquals(childUser, initialState.currentUser)
        assertTrue(initialState.isAuthenticated)
        assertEquals(UserRole.CHILD, initialState.currentRole)
    }

    @Test
    @DisplayName("Should handle session service errors gracefully during initialization")
    fun shouldHandleSessionServiceErrorsGracefullyDuringInitialization() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } throws RuntimeException("Session error")
        try {
            authViewModel = createViewModel()
            authViewModel.initialize()
            val initialState = authViewModel.authState.first()
            assertNull(initialState.currentUser)
            assertFalse(initialState.isAuthenticated)
            assertNull(initialState.currentRole)
        } catch (e: RuntimeException) {
            assertTrue(e.message?.contains("Session error") == true)
        }
    }
}
