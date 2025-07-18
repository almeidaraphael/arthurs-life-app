package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for AuthViewModel state management.
 *
 * Tests cover:
 * - Authentication state structure and properties
 * - State flow behavior and updates
 * - Current role derivation from user
 * - State consistency across operations
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - State Management Tests")
class AuthViewModelStateManagementTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUpViewModel() {
        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()

        io.mockk.coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        io.mockk.coEvery { mockAuthenticationService.logout() } returns Unit
        io.mockk.coEvery {
            mockAuthenticationDomainService.authenticateWithPin(any())
        } returns com.lemonqwest.app.domain.auth.AuthResult.InvalidPin
        io.mockk.coEvery {
            mockAuthenticationDomainService.switchRole(any())
        } returns com.lemonqwest.app.domain.auth.AuthResult.UserNotFound
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
    @DisplayName("Should maintain correct authentication state structure")
    fun shouldMaintainCorrectAuthenticationStateStructure() = mainDispatcherRule.runTest {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val state = authViewModel.authState.first()

        assertNotNull(state)
        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
        assertNull(state.currentRole)
    }

    @Test
    @DisplayName("Should derive current role correctly from user")
    fun shouldDeriveCurrentRoleCorrectlyFromUser() = mainDispatcherRule.runTest {
        val testCases = listOf(
            TestDataFactory.createChildUser() to UserRole.CHILD,
            TestDataFactory.createCaregiverUser() to UserRole.CAREGIVER,
        )

        testCases.forEach { (user, expectedRole) ->
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns user

            authViewModel = createViewModel()
            authViewModel.initialize()
            advanceUntilIdle()
            val state = authViewModel.authState.first()

            assertEquals(user, state.currentUser)
            assertTrue(state.isAuthenticated)
            assertEquals(expectedRole, state.currentRole)
        }
    }

    @Test
    @DisplayName("Should maintain state consistency across multiple state queries")
    fun shouldMaintainStateConsistencyAcrossMultipleStateQueries() = mainDispatcherRule.runTest {
        val testUser = TestDataFactory.createCaregiverUser(
            id = "consistency-test",
            name = "Consistency User",
        )
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testUser

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        val state1 = authViewModel.authState.first()
        val state2 = authViewModel.authState.first()
        val state3 = authViewModel.authState.first()

        assertEquals(state1.currentUser, state2.currentUser)
        assertEquals(state1.isAuthenticated, state2.isAuthenticated)
        assertEquals(state1.currentRole, state2.currentRole)

        assertEquals(state2.currentUser, state3.currentUser)
        assertEquals(state2.isAuthenticated, state3.isAuthenticated)
        assertEquals(state2.currentRole, state3.currentRole)
    }

    @Test
    @DisplayName("Should handle null user gracefully in state management")
    fun shouldHandleNullUserGracefullyInStateManagement() = mainDispatcherRule.runTest {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val state = authViewModel.authState.first()

        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
        assertNull(state.currentRole)
    }

    @Test
    @DisplayName("Should update state correctly after user authentication")
    fun shouldUpdateStateCorrectlyAfterUserAuthentication() = mainDispatcherRule.runTest {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val initialState = authViewModel.authState.first()

        assertNull(initialState.currentUser)
        assertFalse(initialState.isAuthenticated)

        val authenticatedUser = TestDataFactory.createChildUser(
            id = "newly-authenticated",
            name = "New User",
        )
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns authenticatedUser

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val updatedState = authViewModel.authState.first()

        assertEquals(authenticatedUser, updatedState.currentUser)
        assertTrue(updatedState.isAuthenticated)
        assertEquals(UserRole.CHILD, updatedState.currentRole)
    }
}
