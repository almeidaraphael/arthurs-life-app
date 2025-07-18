package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.AuthResult
import com.lemonqwest.app.domain.auth.AuthenticationDomainService
import com.lemonqwest.app.domain.auth.AuthenticationService
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for AuthViewModel role switching functionality.
 *
 * Tests cover:
 * - Successful role switching from CAREGIVER to CHILD
 * - Role switch failure handling
 * - Successful role switching to CAREGIVER
 * - User not found during role switch scenarios
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - Role Switching Tests")
class AuthViewModelRoleSwitchingTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUp() {
        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
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
    @DisplayName("Should switch role successfully from CAREGIVER to CHILD")
    fun shouldSwitchRoleSuccessfullyFromCaregiverToChild() {
        val targetRole = UserRole.CHILD
        val caregiverInChildMode = TestDataFactory.createCaregiverUser(
            id = "caregiver-123",
            name = "Caregiver Demo",
        )
        authViewModel = createViewModel()
        authViewModel.initialize()
        val successResult = AuthResult.Success(caregiverInChildMode)
        coEvery { mockAuthenticationService.switchRole(targetRole) } returns successResult
        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit
        authViewModel.switchRole(targetRole, onResult)
        coVerify { mockAuthenticationService.switchRole(targetRole) }
        coVerify { onResult(capture(resultSlot)) }
        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.Success)
        val updatedState = authViewModel.authState.first()
        assertEquals(caregiverInChildMode, updatedState.currentUser)
        assertTrue(updatedState.isAuthenticated)
        assertEquals(UserRole.CAREGIVER, updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle role switch failure appropriately")
    fun shouldHandleRoleSwitchFailureAppropriately() {
        authViewModel = createViewModel()
        authViewModel.initialize()
        val targetRole = UserRole.CAREGIVER
        val failureResult = AuthResult.InvalidPin
        coEvery { mockAuthenticationService.switchRole(targetRole) } returns failureResult
        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit
        authViewModel.switchRole(targetRole, onResult)
        coVerify { mockAuthenticationService.switchRole(targetRole) }
        coVerify { onResult(capture(resultSlot)) }
        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.InvalidPin)
        val updatedState = authViewModel.authState.first()
        assertNull(updatedState.currentUser)
        assertFalse(updatedState.isAuthenticated)
    }

    @Test
    @DisplayName("Should switch role to CAREGIVER successfully")
    fun shouldSwitchRoleToCaregiverSuccessfully() {
        val targetRole = UserRole.CAREGIVER
        val caregiverUser = TestDataFactory.createCaregiverUser(
            id = "caregiver-456",
            name = "Authorized Caregiver",
        )
        val successResult = AuthResult.Success(caregiverUser)
        coEvery { mockAuthenticationService.switchRole(targetRole) } returns successResult
        authViewModel = createViewModel()
        authViewModel.initialize()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        authViewModel.switchRole(targetRole, onResult)
        coVerify { mockAuthenticationService.switchRole(targetRole) }
        coVerify { onResult(successResult) }
        val updatedState = authViewModel.authState.first()
        assertEquals(caregiverUser, updatedState.currentUser)
        assertTrue(updatedState.isAuthenticated)
        assertEquals(UserRole.CAREGIVER, updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle user not found during role switch")
    fun shouldHandleUserNotFoundDuringRoleSwitch() {
        authViewModel = createViewModel()
        authViewModel.initialize()
        val targetRole = UserRole.CHILD
        val userNotFoundResult = AuthResult.UserNotFound
        coEvery { mockAuthenticationService.switchRole(targetRole) } returns userNotFoundResult
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit
        authViewModel.switchRole(targetRole, onResult)
        coVerify { mockAuthenticationService.switchRole(targetRole) }
        coVerify { onResult(userNotFoundResult) }
        val updatedState = authViewModel.authState.first()
        assertNull(updatedState.currentUser)
        assertFalse(updatedState.isAuthenticated)
    }
}
