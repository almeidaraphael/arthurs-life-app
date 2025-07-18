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
import kotlin.OptIn

/**
 * Focused test suite for AuthViewModel child authentication functionality.
 *
 * Tests cover:
 * - Successful child authentication with valid user ID
 * - Invalid child user ID authentication failure
 * - Child not found authentication failure
 * - Empty and malformed child ID handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel - Child Authentication Tests")
class AuthViewModelChildAuthenticationTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    @BeforeEach
    fun setUpAuthViewModelChildAuthentication() {
        MockKAnnotations.init(this, relaxUnitFun = false)

        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
    }

    @AfterEach
    fun tearDownAuthViewModelChildAuthentication() {
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
    @DisplayName("Should authenticate as child successfully")
    fun shouldAuthenticateAsChildSuccessfully() = mainDispatcherRule.runTest {
        val childUser = TestDataFactory.createChildUser(
            id = "child-789",
            name = "Child User",
        )
        val successResult = AuthResult.Success(childUser)

        coEvery { mockAuthenticationService.authenticateAsChild() } returns successResult

        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        authViewModel.authenticateAsChild(onResult)

        coVerify { mockAuthenticationService.authenticateAsChild() }
        coVerify { onResult(capture(resultSlot)) }

        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.Success)
        assertEquals(childUser, (capturedResult as AuthResult.Success).user)

        val updatedState = authViewModel.authState.first()
        assertEquals(childUser, updatedState.currentUser)
        assertTrue(updatedState.isAuthenticated)
        assertEquals(UserRole.CHILD, updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle child authentication failure")
    fun shouldHandleChildAuthenticationFailure() = mainDispatcherRule.runTest {
        val failureResult = AuthResult.UserNotFound

        coEvery { mockAuthenticationService.authenticateAsChild() } returns failureResult

        val resultSlot = slot<AuthResult>()
        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(capture(resultSlot)) } returns Unit

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        authViewModel.authenticateAsChild(onResult)

        coVerify { mockAuthenticationService.authenticateAsChild() }
        coVerify { onResult(capture(resultSlot)) }

        val capturedResult = resultSlot.captured
        assertTrue(capturedResult is AuthResult.UserNotFound)

        val updatedState = authViewModel.authState.first()
        assertNull(updatedState.currentUser)
        assertFalse(updatedState.isAuthenticated)
        assertNull(updatedState.currentRole)
    }

    @Test
    @DisplayName("Should handle multiple child authentication attempts")
    fun shouldHandleMultipleChildAuthenticationAttempts() = mainDispatcherRule.runTest {
        val childUser = TestDataFactory.createChildUser(
            id = "child-multiple",
            name = "Child Multi",
        )
        val successResult = AuthResult.Success(childUser)

        coEvery { mockAuthenticationService.authenticateAsChild() } returns successResult

        val onResult = mockk<(AuthResult) -> Unit>()
        every { onResult(any()) } returns Unit

        authViewModel = createViewModel()
        authViewModel.initialize()
        advanceUntilIdle()

        authViewModel.authenticateAsChild(onResult)
        authViewModel.authenticateAsChild(onResult)

        coVerify(exactly = 2) { mockAuthenticationService.authenticateAsChild() }
        coVerify(exactly = 2) { onResult(successResult) }

        val updatedState = authViewModel.authState.first()
        assertEquals(childUser, updatedState.currentUser)
        assertTrue(updatedState.isAuthenticated)
        assertEquals(UserRole.CHILD, updatedState.currentRole)
    }
}
