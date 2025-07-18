package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.auth.AuthResult
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.user.UserRole
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for AuthViewModel error handling.
 *
 * Tests cover:
 * - Service exception handling during authentication
 * - Network error scenarios
 * - Service unavailable error handling
 * - State consistency during error conditions
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - Error Handling Tests")
class AuthViewModelErrorHandlingTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUpAuthViewModelErrorHandling() {
        MockKAnnotations.init(this, relaxUnitFun = false)
        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
    }

    @AfterEach
    fun tearDownAuthViewModelErrorHandling() {
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
    @DisplayName("Should handle authentication service exceptions gracefully")
    fun shouldHandleAuthenticationServiceExceptionsGracefully() {
        val pin = "1234"
        val serviceException = RuntimeException("Authentication service error")
        coEvery { mockAuthenticationService.authenticateWithPin(pin) } throws serviceException
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        try {
            authViewModel.authenticateWithPin(pin, onResult)
        } catch (e: RuntimeException) {
            println("Expected service exception caught: ${e.message}")
        }
        advanceUntilIdle()
        coVerify { mockAuthenticationService.authenticateWithPin(pin) }
        val state = authViewModel.authState.first()
        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
    }

    @Test
    @DisplayName("Should handle network errors during role switching")
    fun shouldHandleNetworkErrorsDuringRoleSwitching() {
        val targetRole = UserRole.CHILD
        val networkException = RuntimeException("Network unavailable")
        coEvery { mockAuthenticationService.switchRole(targetRole) } throws networkException
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        try {
            authViewModel.switchRole(targetRole, onResult)
        } catch (e: RuntimeException) {
            println("Expected service exception caught: ${e.message}")
        }
        advanceUntilIdle()
        coVerify { mockAuthenticationService.switchRole(targetRole) }
        val state = authViewModel.authState.first()
        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
    }

    @Test
    @DisplayName("Should handle child authentication service failures")
    fun shouldHandleChildAuthenticationServiceFailures() {
        val authException = RuntimeException("Child auth service failure")
        coEvery { mockAuthenticationService.authenticateAsChild() } throws authException
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        try {
            authViewModel.authenticateAsChild(onResult)
        } catch (e: RuntimeException) {
            println("Expected service exception caught: ${e.message}")
        }
        advanceUntilIdle()
        coVerify { mockAuthenticationService.authenticateAsChild() }
        val state = authViewModel.authState.first()
        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
    }

    @Test
    @DisplayName("Should maintain state consistency during multiple error scenarios")
    fun shouldMaintainStateConsistencyDuringMultipleErrorScenarios() {
        val pin = "1234"
        val role = UserRole.CAREGIVER
        val authException = RuntimeException("Service error")
        coEvery { mockAuthenticationService.authenticateWithPin(pin) } throws authException
        coEvery { mockAuthenticationService.authenticateAsChild() } throws authException
        coEvery { mockAuthenticationService.switchRole(role) } throws authException
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        try {
            authViewModel.authenticateWithPin(pin, onResult)
        } catch (e: RuntimeException) {
            println("Expected multi-error scenario exception: ${e.message}")
        }
        try {
            authViewModel.authenticateAsChild(onResult)
        } catch (e: RuntimeException) {
            println("Expected multi-error scenario exception: ${e.message}")
        }
        try {
            authViewModel.switchRole(role, onResult)
        } catch (e: RuntimeException) {
            println("Expected multi-error scenario exception: ${e.message}")
        }
        advanceUntilIdle()
        val finalState = authViewModel.authState.first()
        assertNull(finalState.currentUser)
        assertFalse(finalState.isAuthenticated)
        assertNull(finalState.currentRole)
    }

    @Test
    @DisplayName("Should handle service unavailable scenarios gracefully")
    fun shouldHandleServiceUnavailableScenarios() {
        val serviceUnavailableException = RuntimeException("Service temporarily unavailable")
        coEvery { mockAuthenticationService.authenticateWithPin(any()) } throws serviceUnavailableException
        coEvery { mockAuthenticationService.authenticateAsChild() } throws serviceUnavailableException
        coEvery { mockAuthenticationService.switchRole(any()) } throws serviceUnavailableException
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        listOf(
            { authViewModel.authenticateWithPin("1234", onResult) },
            { authViewModel.authenticateAsChild(onResult) },
            { authViewModel.switchRole(UserRole.CHILD, onResult) },
        ).forEach { operation ->
            try {
                operation()
            } catch (e: RuntimeException) {
                println("Expected service unavailability exception: ${e.message}")
            }
        }
        advanceUntilIdle()
        val state = authViewModel.authState.first()
        assertNull(state.currentUser)
        assertFalse(state.isAuthenticated)
    }
}
