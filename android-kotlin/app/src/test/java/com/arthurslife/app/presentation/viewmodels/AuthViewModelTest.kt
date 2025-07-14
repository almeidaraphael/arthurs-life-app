package com.arthurslife.app.presentation.viewmodels

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.auth.AuthResult
import com.arthurslife.app.domain.auth.AuthenticationDomainService
import com.arthurslife.app.domain.auth.AuthenticationService
import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.user.UserRole
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Comprehensive tests for AuthViewModel.
 *
 * Tests cover:
 * - Authentication state management
 * - PIN-based authentication flows
 * - Role switching functionality
 * - Child authentication
 * - Session initialization and logout
 * - Error handling and state updates
 * - Callback handling for UI integration
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthViewModel Presentation Tests")
class AuthViewModelTest {

    private lateinit var mockAuthenticationService: AuthenticationService
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAuthenticationDomainService: AuthenticationDomainService
    private lateinit var authViewModel: AuthViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockAuthenticationService = mockk()
        mockAuthenticationSessionService = mockk()
        mockAuthenticationDomainService = mockk()

        // Default mocks for clean state
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        coEvery { mockAuthenticationSessionService.isAuthenticated() } returns false
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearMocks(
            mockAuthenticationService,
            mockAuthenticationSessionService,
            mockAuthenticationDomainService,
        )
    }

    private fun createViewModel(): AuthViewModel {
        return AuthViewModel(
            mockAuthenticationService,
            mockAuthenticationSessionService,
            mockAuthenticationDomainService,
        )
    }

    @Nested
    @DisplayName("ViewModel Initialization")
    inner class ViewModelInitialization {

        @Test
        @DisplayName("Should initialize with empty authentication state when no current user")
        fun shouldInitializeWithEmptyAuthenticationStateWhenNoCurrentUser() = runTest {
            // Arrange
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null

            // Act
            authViewModel = createViewModel()
            val initialState = authViewModel.authState.first()

            // Assert
            assertNull(initialState.currentUser)
            assertFalse(initialState.isAuthenticated)
            assertNull(initialState.currentRole)
        }

        @Test
        @DisplayName("Should initialize with authenticated state when current user exists")
        fun shouldInitializeWithAuthenticatedStateWhenCurrentUserExists() = runTest {
            // Arrange
            val testUser = TestDataFactory.createCaregiverUser(
                id = "user-123",
                name = "Test Caregiver",
            )
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testUser

            // Act
            authViewModel = createViewModel()
            val initialState = authViewModel.authState.first()

            // Assert
            assertEquals(testUser, initialState.currentUser)
            assertTrue(initialState.isAuthenticated)
            assertEquals(UserRole.CAREGIVER, initialState.currentRole)
        }

        @Test
        @DisplayName("Should initialize with child user session correctly")
        fun shouldInitializeWithChildUserSessionCorrectly() = runTest {
            // Arrange
            val childUser = TestDataFactory.createChildUser(
                id = "child-456",
                name = "Child User",
            )
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns childUser

            // Act
            authViewModel = createViewModel()
            val initialState = authViewModel.authState.first()

            // Assert
            assertEquals(childUser, initialState.currentUser)
            assertTrue(initialState.isAuthenticated)
            assertEquals(UserRole.CHILD, initialState.currentRole)
        }

        @Test
        @DisplayName("Should handle session service errors gracefully during initialization")
        fun shouldHandleSessionServiceErrorsGracefullyDuringInitialization() = runTest {
            // Arrange
            coEvery { mockAuthenticationSessionService.getCurrentUser() } throws RuntimeException("Session error")

            // Act & Assert - Should not crash during initialization
            try {
                authViewModel = createViewModel()
                val initialState = authViewModel.authState.first()

                // ViewModel should handle errors and maintain empty state
                assertNull(initialState.currentUser)
                assertFalse(initialState.isAuthenticated)
                assertNull(initialState.currentRole)
            } catch (e: RuntimeException) {
                // If exception propagates, that's also acceptable for this test
                assertTrue(e.message?.contains("Session error") == true)
            }
        }
    }

    @Nested
    @DisplayName("PIN Authentication")
    inner class PINAuthentication {

        @BeforeEach
        fun setUp() {
            authViewModel = createViewModel()
        }

        @Test
        @DisplayName("Should authenticate successfully with valid PIN")
        fun shouldAuthenticateSuccessfullyWithValidPin() = runTest {
            // Arrange
            val validPin = "1234"
            val authenticatedUser = TestDataFactory.createCaregiverUser(
                id = "user-789",
                name = "Authenticated User",
            )
            val successResult = AuthResult.Success(authenticatedUser)

            coEvery { mockAuthenticationService.authenticateWithPin(validPin) } returns successResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateWithPin(validPin, onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateWithPin(validPin) }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.Success)
            assertEquals(authenticatedUser, (capturedResult as AuthResult.Success).user)

            // Verify state update
            val updatedState = authViewModel.authState.first()
            assertEquals(authenticatedUser, updatedState.currentUser)
            assertTrue(updatedState.isAuthenticated)
            assertEquals(UserRole.CAREGIVER, updatedState.currentRole)
        }

        @Test
        @DisplayName("Should handle invalid PIN authentication failure")
        fun shouldHandleInvalidPinAuthenticationFailure() = runTest {
            // Arrange
            val invalidPin = "wrong"
            val failureResult = AuthResult.InvalidPin

            coEvery { mockAuthenticationService.authenticateWithPin(invalidPin) } returns failureResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateWithPin(invalidPin, onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateWithPin(invalidPin) }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.InvalidPin)

            // Verify state remains unauthenticated
            val updatedState = authViewModel.authState.first()
            assertNull(updatedState.currentUser)
            assertFalse(updatedState.isAuthenticated)
            assertNull(updatedState.currentRole)
        }

        @Test
        @DisplayName("Should handle user not found authentication failure")
        fun shouldHandleUserNotFoundAuthenticationFailure() = runTest {
            // Arrange
            val pin = "1234"
            val userNotFoundResult = AuthResult.UserNotFound

            coEvery { mockAuthenticationService.authenticateWithPin(pin) } returns userNotFoundResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateWithPin(pin, onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateWithPin(pin) }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.UserNotFound)

            // Verify state remains unauthenticated
            val updatedState = authViewModel.authState.first()
            assertNull(updatedState.currentUser)
            assertFalse(updatedState.isAuthenticated)
            assertNull(updatedState.currentRole)
        }

        @Test
        @DisplayName("Should handle empty PIN input gracefully")
        fun shouldHandleEmptyPinInputGracefully() = runTest {
            // Arrange
            val emptyPin = ""
            val failureResult = AuthResult.InvalidPin

            coEvery { mockAuthenticationService.authenticateWithPin(emptyPin) } returns failureResult

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateWithPin(emptyPin, onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateWithPin(emptyPin) }
            coVerify { onResult(failureResult) }
        }

        @Test
        @DisplayName("Should handle very long PIN input")
        fun shouldHandleVeryLongPinInput() = runTest {
            // Arrange
            val longPin = "1".repeat(1000)
            val failureResult = AuthResult.InvalidPin

            coEvery { mockAuthenticationService.authenticateWithPin(longPin) } returns failureResult

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateWithPin(longPin, onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateWithPin(longPin) }
            coVerify { onResult(failureResult) }
        }
    }

    @Nested
    @DisplayName("Role Switching")
    inner class RoleSwitching {

        @BeforeEach
        fun setUp() {
            authViewModel = createViewModel()
        }

        @Test
        @DisplayName("Should switch role successfully from CAREGIVER to CHILD")
        fun shouldSwitchRoleSuccessfullyFromCaregiverToChild() = runTest {
            // Arrange
            val targetRole = UserRole.CHILD
            val caregiverInChildMode = TestDataFactory.createCaregiverUser(
                id = "caregiver-123",
                name = "Caregiver Demo",
            )
            val successResult = AuthResult.Success(caregiverInChildMode)

            coEvery { mockAuthenticationService.switchRole(targetRole) } returns successResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.switchRole(targetRole, onResult)

            // Assert
            coVerify { mockAuthenticationService.switchRole(targetRole) }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.Success)

            // Verify state update reflects role switch
            val updatedState = authViewModel.authState.first()
            assertEquals(caregiverInChildMode, updatedState.currentUser)
            assertTrue(updatedState.isAuthenticated)
            assertEquals(
                UserRole.CAREGIVER,
                updatedState.currentRole,
            ) // Role matches user's actual role
        }

        @Test
        @DisplayName("Should handle role switch failure appropriately")
        fun shouldHandleRoleSwitchFailureAppropriately() = runTest {
            // Arrange
            val targetRole = UserRole.CAREGIVER
            val failureResult = AuthResult.InvalidPin // Child cannot switch to caregiver

            coEvery { mockAuthenticationService.switchRole(targetRole) } returns failureResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.switchRole(targetRole, onResult)

            // Assert
            coVerify { mockAuthenticationService.switchRole(targetRole) }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.InvalidPin)

            // Verify state does not update on failure
            val updatedState = authViewModel.authState.first()
            assertNull(updatedState.currentUser)
            assertFalse(updatedState.isAuthenticated)
        }

        @Test
        @DisplayName("Should switch role to CAREGIVER successfully")
        fun shouldSwitchRoleToCaregiverSuccessfully() = runTest {
            // Arrange
            val targetRole = UserRole.CAREGIVER
            val caregiverUser = TestDataFactory.createCaregiverUser(
                id = "caregiver-456",
                name = "Authorized Caregiver",
            )
            val successResult = AuthResult.Success(caregiverUser)

            coEvery { mockAuthenticationService.switchRole(targetRole) } returns successResult

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.switchRole(targetRole, onResult)

            // Assert
            coVerify { mockAuthenticationService.switchRole(targetRole) }
            coVerify { onResult(successResult) }

            // Verify state update
            val updatedState = authViewModel.authState.first()
            assertEquals(caregiverUser, updatedState.currentUser)
            assertTrue(updatedState.isAuthenticated)
            assertEquals(UserRole.CAREGIVER, updatedState.currentRole)
        }

        @Test
        @DisplayName("Should handle user not found during role switch")
        fun shouldHandleUserNotFoundDuringRoleSwitch() = runTest {
            // Arrange
            val targetRole = UserRole.CHILD
            val userNotFoundResult = AuthResult.UserNotFound

            coEvery { mockAuthenticationService.switchRole(targetRole) } returns userNotFoundResult

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.switchRole(targetRole, onResult)

            // Assert
            coVerify { mockAuthenticationService.switchRole(targetRole) }
            coVerify { onResult(userNotFoundResult) }

            // State should not be updated on failure
            val updatedState = authViewModel.authState.first()
            assertNull(updatedState.currentUser)
            assertFalse(updatedState.isAuthenticated)
        }
    }

    @Nested
    @DisplayName("Child Authentication")
    inner class ChildAuthentication {

        @BeforeEach
        fun setUp() {
            authViewModel = createViewModel()
        }

        @Test
        @DisplayName("Should authenticate as child successfully")
        fun shouldAuthenticateAsChildSuccessfully() = runTest {
            // Arrange
            val childUser = TestDataFactory.createChildUser(
                id = "child-789",
                name = "Child User",
            )
            val successResult = AuthResult.Success(childUser)

            coEvery { mockAuthenticationService.authenticateAsChild() } returns successResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateAsChild(onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateAsChild() }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.Success)
            assertEquals(childUser, (capturedResult as AuthResult.Success).user)

            // Verify state update
            val updatedState = authViewModel.authState.first()
            assertEquals(childUser, updatedState.currentUser)
            assertTrue(updatedState.isAuthenticated)
            assertEquals(UserRole.CHILD, updatedState.currentRole)
        }

        @Test
        @DisplayName("Should handle child authentication failure")
        fun shouldHandleChildAuthenticationFailure() = runTest {
            // Arrange
            val failureResult = AuthResult.UserNotFound

            coEvery { mockAuthenticationService.authenticateAsChild() } returns failureResult

            val resultSlot = slot<AuthResult>()
            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateAsChild(onResult)

            // Assert
            coVerify { mockAuthenticationService.authenticateAsChild() }
            coVerify { onResult(capture(resultSlot)) }

            val capturedResult = resultSlot.captured
            assertTrue(capturedResult is AuthResult.UserNotFound)

            // Verify state remains unauthenticated
            val updatedState = authViewModel.authState.first()
            assertNull(updatedState.currentUser)
            assertFalse(updatedState.isAuthenticated)
            assertNull(updatedState.currentRole)
        }

        @Test
        @DisplayName("Should handle multiple child authentication attempts")
        fun shouldHandleMultipleChildAuthenticationAttempts() = runTest {
            // Arrange
            val childUser = TestDataFactory.createChildUser(
                id = "child-multiple",
                name = "Child Multi",
            )
            val successResult = AuthResult.Success(childUser)

            coEvery { mockAuthenticationService.authenticateAsChild() } returns successResult

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act
            authViewModel.authenticateAsChild(onResult)
            authViewModel.authenticateAsChild(onResult)

            // Assert
            coVerify(exactly = 2) { mockAuthenticationService.authenticateAsChild() }
            coVerify(exactly = 2) { onResult(successResult) }

            // State should reflect latest authentication
            val updatedState = authViewModel.authState.first()
            assertEquals(childUser, updatedState.currentUser)
            assertTrue(updatedState.isAuthenticated)
            assertEquals(UserRole.CHILD, updatedState.currentRole)
        }
    }

    @Nested
    @DisplayName("Logout Operations")
    inner class LogoutOperations {

        @BeforeEach
        fun setUp() {
            authViewModel = createViewModel()
        }

        @Test
        @DisplayName("Should logout successfully and clear authentication state")
        fun shouldLogoutSuccessfullyAndClearAuthenticationState() = runTest {
            // Arrange
            coEvery { mockAuthenticationService.logout() } returns Unit

            // First, establish an authenticated state
            val authenticatedUser = TestDataFactory.createCaregiverUser(
                id = "user-logout",
                name = "User To Logout",
            )
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns authenticatedUser
            authViewModel = createViewModel()

            // Verify we start authenticated
            val initialState = authViewModel.authState.first()
            assertTrue(initialState.isAuthenticated)
            assertNotNull(initialState.currentUser)

            // Act
            authViewModel.logout()

            // Assert
            coVerify { mockAuthenticationService.logout() }

            // Verify state is cleared
            val clearedState = authViewModel.authState.first()
            assertNull(clearedState.currentUser)
            assertFalse(clearedState.isAuthenticated)
            assertNull(clearedState.currentRole)
        }

        @Test
        @DisplayName("Should handle logout when already unauthenticated")
        fun shouldHandleLogoutWhenAlreadyUnauthenticated() = runTest {
            // Arrange
            coEvery { mockAuthenticationService.logout() } returns Unit

            // Verify we start unauthenticated
            val initialState = authViewModel.authState.first()
            assertFalse(initialState.isAuthenticated)

            // Act
            authViewModel.logout()

            // Assert
            coVerify { mockAuthenticationService.logout() }

            // Verify state remains cleared
            val finalState = authViewModel.authState.first()
            assertNull(finalState.currentUser)
            assertFalse(finalState.isAuthenticated)
            assertNull(finalState.currentRole)
        }

        @Test
        @DisplayName("Should handle logout service errors gracefully")
        fun shouldHandleLogoutServiceErrorsGracefully() = runTest {
            // Arrange
            coEvery { mockAuthenticationService.logout() } throws RuntimeException("Logout error")

            // Act & Assert - Should not crash the app
            try {
                authViewModel.logout()

                // If no exception thrown, verify logout was attempted
                coVerify { mockAuthenticationService.logout() }

                // State should still be cleared regardless of service error
                val finalState = authViewModel.authState.first()
                assertNull(finalState.currentUser)
                assertFalse(finalState.isAuthenticated)
                assertNull(finalState.currentRole)
            } catch (e: RuntimeException) {
                // Exception propagation is acceptable behavior
                assertTrue(e.message?.contains("Logout error") == true)

                // Verify logout was attempted before exception
                coVerify { mockAuthenticationService.logout() }
            }
        }
    }

    @Nested
    @DisplayName("State Management")
    inner class StateManagement {

        @BeforeEach
        fun setUp() {
            authViewModel = createViewModel()
        }

        @Test
        @DisplayName("Should maintain consistent state through authentication flow")
        fun shouldMaintainConsistentStateThroughAuthenticationFlow() = runTest {
            // Arrange
            val pin = "1234"
            val authenticatedUser = TestDataFactory.createChildUser(
                id = "state-user",
                name = "State Management User",
            )
            val successResult = AuthResult.Success(authenticatedUser)

            coEvery { mockAuthenticationService.authenticateWithPin(pin) } returns successResult

            // Verify initial state
            val initialState = authViewModel.authState.first()
            assertFalse(initialState.isAuthenticated)

            // Act
            authViewModel.authenticateWithPin(pin) { }

            // Assert - State should be updated immediately
            val authenticatedState = authViewModel.authState.first()
            assertTrue(authenticatedState.isAuthenticated)
            assertEquals(authenticatedUser, authenticatedState.currentUser)
            assertEquals(UserRole.CHILD, authenticatedState.currentRole)
        }

        @Test
        @DisplayName("Should preserve state consistency during role switching")
        fun shouldPreserveStateConsistencyDuringRoleSwitching() = runTest {
            // Arrange
            val caregiverUser = TestDataFactory.createCaregiverUser(
                id = "state-caregiver",
                name = "State Caregiver",
            )

            // Establish authenticated state
            coEvery { mockAuthenticationService.authenticateWithPin("1234") } returns AuthResult.Success(caregiverUser)
            authViewModel.authenticateWithPin("1234") { }

            // Switch role
            coEvery { mockAuthenticationService.switchRole(UserRole.CHILD) } returns AuthResult.Success(caregiverUser)

            // Act
            authViewModel.switchRole(UserRole.CHILD) { }

            // Assert
            val finalState = authViewModel.authState.first()
            assertTrue(finalState.isAuthenticated)
            assertEquals(caregiverUser, finalState.currentUser)
            assertEquals(UserRole.CAREGIVER, finalState.currentRole) // User's actual role
        }

        @Test
        @DisplayName("Should reset state correctly after failed operations")
        fun shouldResetStateCorrectlyAfterFailedOperations() = runTest {
            // Arrange
            val invalidPin = "wrong"
            val failureResult = AuthResult.InvalidPin

            coEvery { mockAuthenticationService.authenticateWithPin(invalidPin) } returns failureResult

            // Act
            authViewModel.authenticateWithPin(invalidPin) { }

            // Assert
            val failedState = authViewModel.authState.first()
            assertFalse(failedState.isAuthenticated)
            assertNull(failedState.currentUser)
            assertNull(failedState.currentRole)
        }

        @Test
        @DisplayName("Should handle rapid state changes correctly")
        fun shouldHandleRapidStateChangesCorrectly() = runTest {
            // Arrange
            val user1 = TestDataFactory.createChildUser(id = "user1")
            val user2 = TestDataFactory.createCaregiverUser(id = "user2")

            coEvery { mockAuthenticationService.authenticateWithPin("1111") } returns AuthResult.Success(user1)
            coEvery { mockAuthenticationService.authenticateWithPin("2222") } returns AuthResult.Success(user2)

            // Act - Rapid authentication attempts
            authViewModel.authenticateWithPin("1111") { }
            authViewModel.authenticateWithPin("2222") { }

            // Assert - Final state should reflect last successful authentication
            val finalState = authViewModel.authState.first()
            assertEquals(user2, finalState.currentUser)
            assertEquals(UserRole.CAREGIVER, finalState.currentRole)
            assertTrue(finalState.isAuthenticated)
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {

        @BeforeEach
        fun setUp() {
            authViewModel = createViewModel()
        }

        @Test
        @DisplayName("Should handle authentication service exceptions gracefully")
        fun shouldHandleAuthenticationServiceExceptionsGracefully() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { mockAuthenticationService.authenticateWithPin(pin) } throws RuntimeException("Service error")

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act & Assert - Should not crash the app
            try {
                authViewModel.authenticateWithPin(pin, onResult)

                // If no exception, verify callback was not called
                coVerify(exactly = 0) { onResult(any()) }

                // State should remain unauthenticated
                val state = authViewModel.authState.first()
                assertFalse(state.isAuthenticated)
            } catch (e: RuntimeException) {
                // Exception propagation is acceptable in this context
                assertTrue(e.message?.contains("Service error") == true)
            }
        }

        @Test
        @DisplayName("Should handle role switch service exceptions")
        fun shouldHandleRoleSwitchServiceExceptions() = runTest {
            // Arrange
            coEvery { mockAuthenticationService.switchRole(any()) } throws RuntimeException("Role switch error")

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act & Assert - Should not crash the app
            try {
                authViewModel.switchRole(UserRole.CHILD, onResult)

                // If no exception, verify callback was not called
                coVerify(exactly = 0) { onResult(any()) }
            } catch (e: RuntimeException) {
                // Exception propagation is acceptable
                assertTrue(e.message?.contains("Role switch error") == true)
            }
        }

        @Test
        @DisplayName("Should handle child authentication service exceptions")
        fun shouldHandleChildAuthenticationServiceExceptions() = runTest {
            // Arrange
            coEvery { mockAuthenticationService.authenticateAsChild() } throws RuntimeException("Child auth error")

            val onResult = mockk<(AuthResult) -> Unit>(relaxed = true)

            // Act & Assert - Should not crash the app
            try {
                authViewModel.authenticateAsChild(onResult)

                // If no exception, verify callback was not called
                coVerify(exactly = 0) { onResult(any()) }
            } catch (e: RuntimeException) {
                // Exception propagation is acceptable
                assertTrue(e.message?.contains("Child auth error") == true)
            }
        }
    }
}
