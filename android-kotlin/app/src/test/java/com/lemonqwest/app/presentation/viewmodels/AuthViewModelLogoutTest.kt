package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for AuthViewModel logout operations.
 *
 * Tests cover:
 * - Successful logout with state clearing
 * - Logout service interaction verification
 * - State management during logout process
 * - Error handling during logout operations
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - Logout Tests")
class AuthViewModelLogoutTest {
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
    @DisplayName("Should logout successfully and clear authentication state")
    fun shouldLogoutSuccessfullyAndClearAuthenticationState() {
        val authenticatedUser = TestDataFactory.createCaregiverUser(
            id = "user-logout",
            name = "User To Logout",
        )
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns authenticatedUser
        coEvery { mockAuthenticationService.logout() } returns Unit
        authViewModel = createViewModel()
        authViewModel.initialize()
        val initialState = authViewModel.authState.first()
        assert(initialState.isAuthenticated)
        authViewModel.logout()
        coVerify { mockAuthenticationService.logout() }
        val loggedOutState = authViewModel.authState.first()
        assertNull(loggedOutState.currentUser)
        assertFalse(loggedOutState.isAuthenticated)
        assertNull(loggedOutState.currentRole)
    }

    @Test
    @DisplayName("Should handle logout when no user is authenticated")
    fun shouldHandleLogoutWhenNoUserIsAuthenticated() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        coEvery { mockAuthenticationService.logout() } returns Unit
        authViewModel = createViewModel()
        authViewModel.initialize()
        authViewModel.logout()
        coVerify { mockAuthenticationService.logout() }
        val state = authViewModel.authState.first()
        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
        assertNull(state.currentRole)
    }

    @Test
    @DisplayName("Should handle logout service errors gracefully")
    fun shouldHandleLogoutServiceErrorsGracefully() {
        val authenticatedUser = TestDataFactory.createChildUser()
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns authenticatedUser
        coEvery { mockAuthenticationService.logout() } throws RuntimeException("Logout error")
        authViewModel = createViewModel()
        authViewModel.initialize()
        try {
            authViewModel.logout()
        } catch (e: RuntimeException) {
            println("Expected logout exception caught: ${e.message}")
        }
        coVerify { mockAuthenticationService.logout() }
    }

    @Test
    @DisplayName("Should maintain consistent state after multiple logout calls")
    fun shouldMaintainConsistentStateAfterMultipleLogoutCalls() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        coEvery { mockAuthenticationService.logout() } returns Unit
        authViewModel = createViewModel()
        authViewModel.initialize()
        repeat(3) {
            authViewModel.logout()
        }
        coVerify(exactly = 3) { mockAuthenticationService.logout() }
        val finalState = authViewModel.authState.first()
        assertNull(finalState.currentUser)
        assertFalse(finalState.isAuthenticated)
        assertNull(finalState.currentRole)
    }
}
