package com.arthurslife.app.domain.auth

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserDataSource
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.infrastructure.preferences.AuthPreferencesDataStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for the AuthenticationDomainService.
 *
 * Tests cover:
 * - PIN-based authentication flows
 * - Role switching authorization and logic
 * - Child direct authentication
 * - Session management and user retrieval
 * - Logout operations
 * - Error handling and edge cases
 */
@DisplayName("AuthenticationDomainService Tests")
class AuthenticationDomainServiceTest {

    private lateinit var authenticationService: AuthenticationDomainService
    private lateinit var userDataSource: UserDataSource
    private lateinit var authPreferencesDataStore: AuthPreferencesDataStore

    private lateinit var testCaregiverUser: User
    private lateinit var testChildUser: User

    @BeforeEach
    fun setUp() {
        userDataSource = mockk()
        authPreferencesDataStore = mockk()
        authenticationService = AuthenticationDomainService(userDataSource, authPreferencesDataStore)

        // Create test users
        testCaregiverUser = TestDataFactory.createCaregiverUser()
        testChildUser = TestDataFactory.createChildUser()
    }

    @Nested
    @DisplayName("PIN Authentication")
    inner class PinAuthentication {

        @Test
        @DisplayName("Should authenticate user with valid PIN")
        fun shouldAuthenticateUserWithValidPin() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { userDataSource.findByPin(pin) } returns testCaregiverUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act
            val result = authenticationService.authenticateWithPin(pin)

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testCaregiverUser, (result as AuthResult.Success).user)
            coVerify {
                authPreferencesDataStore.setCurrentUser(
                    testCaregiverUser.id,
                    testCaregiverUser.role,
                )
            }
        }

        @Test
        @DisplayName("Should return InvalidPin for non-existent PIN")
        fun shouldReturnInvalidPinForNonExistentPin() = runTest {
            // Arrange
            val invalidPin = "9999"
            coEvery { userDataSource.findByPin(invalidPin) } returns null

            // Act
            val result = authenticationService.authenticateWithPin(invalidPin)

            // Assert
            assertTrue(result is AuthResult.InvalidPin)
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }

        @Test
        @DisplayName("Should authenticate child user with PIN")
        fun shouldAuthenticateChildUserWithPin() = runTest {
            // Arrange
            val childPin = "5678"
            coEvery { userDataSource.findByPin(childPin) } returns testChildUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act
            val result = authenticationService.authenticateWithPin(childPin)

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testChildUser, (result as AuthResult.Success).user)
            coVerify {
                authPreferencesDataStore.setCurrentUser(
                    testChildUser.id,
                    testChildUser.role,
                )
            }
        }
    }

    @Nested
    @DisplayName("Role Switching")
    inner class RoleSwitching {

        @Test
        @DisplayName("Should allow caregiver to switch to child role")
        fun shouldAllowCaregiverToSwitchToChildRole() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser
            coEvery { userDataSource.findByRole(UserRole.CHILD) } returns testChildUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act
            val result = authenticationService.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testChildUser, (result as AuthResult.Success).user)
            coVerify {
                authPreferencesDataStore.setCurrentUser(
                    testChildUser.id,
                    testChildUser.role,
                )
            }
        }

        @Test
        @DisplayName("Should prevent child from switching roles")
        fun shouldPreventChildFromSwitchingRoles() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testChildUser.id)
            coEvery { userDataSource.findById(testChildUser.id) } returns testChildUser

            // Act
            val result = authenticationService.switchRole(UserRole.CAREGIVER)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }

        @Test
        @DisplayName("Should return UserNotFound if target role user doesn't exist")
        fun shouldReturnUserNotFoundIfTargetRoleUserDoesNotExist() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser
            coEvery { userDataSource.findByRole(UserRole.CHILD) } returns null

            // Act
            val result = authenticationService.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }

        @Test
        @DisplayName("Should return UserNotFound if no current user")
        fun shouldReturnUserNotFoundIfNoCurrentUser() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(null)

            // Act
            val result = authenticationService.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }
    }

    @Nested
    @DisplayName("Child Direct Authentication")
    inner class ChildDirectAuthentication {

        @Test
        @DisplayName("Should authenticate child without PIN")
        fun shouldAuthenticateChildWithoutPin() = runTest {
            // Arrange
            coEvery { userDataSource.findByRole(UserRole.CHILD) } returns testChildUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act
            val result = authenticationService.authenticateAsChild()

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testChildUser, (result as AuthResult.Success).user)
            coVerify {
                authPreferencesDataStore.setCurrentUser(
                    testChildUser.id,
                    testChildUser.role,
                )
            }
        }

        @Test
        @DisplayName("Should return UserNotFound if no child user exists")
        fun shouldReturnUserNotFoundIfNoChildUserExists() = runTest {
            // Arrange
            coEvery { userDataSource.findByRole(UserRole.CHILD) } returns null

            // Act
            val result = authenticationService.authenticateAsChild()

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }
    }

    @Nested
    @DisplayName("Session Management")
    inner class SessionManagement {

        @Test
        @DisplayName("Should retrieve current authenticated user")
        fun shouldRetrieveCurrentAuthenticatedUser() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser

            // Act
            val currentUser = authenticationService.getCurrentUser()

            // Assert
            assertEquals(testCaregiverUser, currentUser)
        }

        @Test
        @DisplayName("Should return null when no user authenticated")
        fun shouldReturnNullWhenNoUserAuthenticated() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(null)

            // Act
            val currentUser = authenticationService.getCurrentUser()

            // Assert
            assertNull(currentUser)
        }

        @Test
        @DisplayName("Should return null when user ID exists but user not found")
        fun shouldReturnNullWhenUserIdExistsButUserNotFound() = runTest {
            // Arrange
            val nonExistentUserId = "non-existent-id"
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(nonExistentUserId)
            coEvery { userDataSource.findById(nonExistentUserId) } returns null

            // Act
            val currentUser = authenticationService.getCurrentUser()

            // Assert
            assertNull(currentUser)
        }

        @Test
        @DisplayName("Should report authenticated when user exists")
        fun shouldReportAuthenticatedWhenUserExists() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testChildUser.id)
            coEvery { userDataSource.findById(testChildUser.id) } returns testChildUser

            // Act
            val isAuthenticated = authenticationService.isAuthenticated()

            // Assert
            assertTrue(isAuthenticated)
        }

        @Test
        @DisplayName("Should report not authenticated when no user")
        fun shouldReportNotAuthenticatedWhenNoUser() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(null)

            // Act
            val isAuthenticated = authenticationService.isAuthenticated()

            // Assert
            assertFalse(isAuthenticated)
        }
    }

    @Nested
    @DisplayName("Logout Operations")
    inner class LogoutOperations {

        @Test
        @DisplayName("Should clear current user on logout")
        fun shouldClearCurrentUserOnLogout() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.clearCurrentUser() } returns Unit

            // Act
            authenticationService.logout()

            // Assert
            coVerify { authPreferencesDataStore.clearCurrentUser() }
        }
    }
}
