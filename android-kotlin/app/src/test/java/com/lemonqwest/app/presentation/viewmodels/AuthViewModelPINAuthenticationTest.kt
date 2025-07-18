package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.AuthResult
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
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
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.OptIn

/**
 * Focused test suite for AuthViewModel PIN authentication.
 *
 * Tests cover:
 * - Successful PIN authentication with valid credentials
 * - Invalid PIN authentication failure handling
 * - User not found authentication failure handling
 * - Edge cases with empty and very long PIN inputs
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - PIN Authentication Tests")
@Execution(ExecutionMode.SAME_THREAD)
class AuthViewModelPINAuthenticationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUpAuthViewModelPINAuthentication() {
        MockKAnnotations.init(this, relaxUnitFun = false)
        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()
    }

    @AfterEach
    fun tearDownAuthViewModelPINAuthentication() {
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
    @DisplayName("Should authenticate successfully with valid PIN")
    fun shouldAuthenticateSuccessfullyWithValidPin() = mainDispatcherRule.runTest {
        val validPin = "1234"
        val authenticatedUser = TestDataFactory.createCaregiverUser(
            id = "user-789",
            name = "Authenticated User",
        )
        val successResult = AuthResult.Success(authenticatedUser)

        coEvery { mockAuthenticationService.authenticateWithPin(validPin) } returns successResult

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit

        authViewModel.authenticateWithPin(validPin, onResult)

        coVerify { mockAuthenticationService.authenticateWithPin(validPin) }
        coVerify { onResult(capture(resultSlot)) }

        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.Success)
        assertEquals(authenticatedUser, (capturedResult as AuthResult.Success).user)

        val updatedState = authViewModel.authState.first()
        assertEquals(authenticatedUser, updatedState.currentUser)
        assertTrue(updatedState.isAuthenticated)
        assertEquals(UserRole.CAREGIVER, updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle invalid PIN authentication failure")
    fun shouldHandleInvalidPinAuthenticationFailure() = mainDispatcherRule.runTest {
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        val invalidPin = "wrong"
        val failureResult = AuthResult.InvalidPin

        coEvery { mockAuthenticationService.authenticateWithPin(invalidPin) } returns failureResult

        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit

        authViewModel.authenticateWithPin(invalidPin, onResult)

        coVerify { mockAuthenticationService.authenticateWithPin(invalidPin) }
        coVerify { onResult(capture(resultSlot)) }

        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.InvalidPin)

        val updatedState = authViewModel.authState.first()
        assertNull(updatedState.currentUser)
        assertFalse(updatedState.isAuthenticated)
        assertNull(updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle user not found authentication failure")
    fun shouldHandleUserNotFoundAuthenticationFailure() = mainDispatcherRule.runTest {
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        val pin = "1234"
        val userNotFoundResult = AuthResult.UserNotFound

        coEvery { mockAuthenticationService.authenticateWithPin(pin) } returns userNotFoundResult

        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit

        authViewModel.authenticateWithPin(pin, onResult)

        coVerify { mockAuthenticationService.authenticateWithPin(pin) }
        coVerify { onResult(capture(resultSlot)) }

        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.UserNotFound)

        val updatedState = authViewModel.authState.first()
        assertNull(updatedState.currentUser)
        assertFalse(updatedState.isAuthenticated)
        assertNull(updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle empty PIN input gracefully")
    fun shouldHandleEmptyPinInputGracefully() = mainDispatcherRule.runTest {
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        val emptyPin = ""
        val failureResult = AuthResult.InvalidPin

        coEvery { mockAuthenticationService.authenticateWithPin(emptyPin) } returns failureResult

        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit

        authViewModel.authenticateWithPin(emptyPin, onResult)

        coVerify { mockAuthenticationService.authenticateWithPin(emptyPin) }
        coVerify { onResult(failureResult) }
    }

    @Test
    @DisplayName("Should handle very long PIN input")
    fun shouldHandleVeryLongPinInput() = mainDispatcherRule.runTest {
        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        val longPin = "1".repeat(1000)
        val failureResult = AuthResult.InvalidPin

        coEvery { mockAuthenticationService.authenticateWithPin(longPin) } returns failureResult

        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit

        authViewModel.authenticateWithPin(longPin, onResult)

        coVerify { mockAuthenticationService.authenticateWithPin(longPin) }
        coVerify { onResult(failureResult) }
    }
}
