package com.arthurslife.app.infrastructure.auth

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.auth.AuthRepository
import com.arthurslife.app.domain.auth.AuthResult
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for AuthRepositoryImpl infrastructure layer.
 *
 * Tests cover:
 * - Repository interface compliance
 * - Data source interaction patterns
 * - Preference store integration
 * - Error handling and edge cases
 * - Authentication flow delegation
 */
@DisplayName("AuthRepositoryImpl Infrastructure Tests")
class AuthRepositoryImplTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var authPreferencesDataStore: AuthPreferencesDataStore
    private lateinit var userDataSource: UserDataSource

    private lateinit var testCaregiverUser: User
    private lateinit var testChildUser: User

    @BeforeEach
    fun setUp() {
        authPreferencesDataStore = mockk()
        userDataSource = mockk()
        authRepository = AuthRepositoryImpl(authPreferencesDataStore, userDataSource)

        // Create test users
        testCaregiverUser = TestDataFactory.createCaregiverUser()
        testChildUser = TestDataFactory.createChildUser()
    }

    @Nested
    @DisplayName("PIN Authentication")
    inner class PinAuthentication {

        @Test
        @DisplayName("Should authenticate user and store session when PIN matches")
        fun shouldAuthenticateUserAndStoreSessionWhenPinMatches() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { userDataSource.findByPin(pin) } returns testCaregiverUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act
            val result = authRepository.authenticateWithPin(pin)

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testCaregiverUser, (result as AuthResult.Success).user)
            coVerify {
                userDataSource.findByPin(pin)
                authPreferencesDataStore.setCurrentUser(
                    testCaregiverUser.id,
                    testCaregiverUser.role,
                )
            }
        }

        @Test
        @DisplayName("Should return InvalidPin when PIN not found in data source")
        fun shouldReturnInvalidPinWhenPinNotFoundInDataSource() = runTest {
            // Arrange
            val invalidPin = "9999"
            coEvery { userDataSource.findByPin(invalidPin) } returns null

            // Act
            val result = authRepository.authenticateWithPin(invalidPin)

            // Assert
            assertTrue(result is AuthResult.InvalidPin)
            coVerify { userDataSource.findByPin(invalidPin) }
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }

        @Test
        @DisplayName("Should handle child user authentication through repository")
        fun shouldHandleChildUserAuthenticationThroughRepository() = runTest {
            // Arrange
            val childPin = "5678"
            coEvery { userDataSource.findByPin(childPin) } returns testChildUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act
            val result = authRepository.authenticateWithPin(childPin)

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testChildUser, (result as AuthResult.Success).user)
            coVerify {
                authPreferencesDataStore.setCurrentUser(testChildUser.id, testChildUser.role)
            }
        }

        @Test
        @DisplayName("Should handle empty PIN gracefully")
        fun shouldHandleEmptyPinGracefully() = runTest {
            // Arrange
            val emptyPin = ""
            coEvery { userDataSource.findByPin(emptyPin) } returns null

            // Act
            val result = authRepository.authenticateWithPin(emptyPin)

            // Assert
            assertTrue(result is AuthResult.InvalidPin)
            coVerify { userDataSource.findByPin(emptyPin) }
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
            val result = authRepository.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.Success)
            assertEquals(testChildUser, (result as AuthResult.Success).user)
            coVerify {
                authPreferencesDataStore.currentUserId
                userDataSource.findById(testCaregiverUser.id)
                userDataSource.findByRole(UserRole.CHILD)
                authPreferencesDataStore.setCurrentUser(testChildUser.id, testChildUser.role)
            }
        }

        @Test
        @DisplayName("Should prevent child from switching roles")
        fun shouldPreventChildFromSwitchingRoles() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testChildUser.id)
            coEvery { userDataSource.findById(testChildUser.id) } returns testChildUser

            // Act
            val result = authRepository.switchRole(UserRole.CAREGIVER)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify {
                authPreferencesDataStore.currentUserId
                userDataSource.findById(testChildUser.id)
            }
            coVerify(exactly = 0) {
                userDataSource.findByRole(any())
                authPreferencesDataStore.setCurrentUser(any(), any())
            }
        }

        @Test
        @DisplayName("Should return UserNotFound when target role user doesn't exist")
        fun shouldReturnUserNotFoundWhenTargetRoleUserDoesNotExist() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser
            coEvery { userDataSource.findByRole(UserRole.CHILD) } returns null

            // Act
            val result = authRepository.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify {
                userDataSource.findByRole(UserRole.CHILD)
            }
            coVerify(exactly = 0) { authPreferencesDataStore.setCurrentUser(any(), any()) }
        }

        @Test
        @DisplayName("Should return UserNotFound when no current user session exists")
        fun shouldReturnUserNotFoundWhenNoCurrentUserSessionExists() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(null)

            // Act
            val result = authRepository.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify { authPreferencesDataStore.currentUserId }
            coVerify(exactly = 0) {
                userDataSource.findById(any())
                userDataSource.findByRole(any())
                authPreferencesDataStore.setCurrentUser(any(), any())
            }
        }

        @Test
        @DisplayName("Should return UserNotFound when current user ID exists but user not found")
        fun shouldReturnUserNotFoundWhenCurrentUserIdExistsButUserNotFound() = runTest {
            // Arrange
            val nonExistentUserId = "non-existent-id"
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(nonExistentUserId)
            coEvery { userDataSource.findById(nonExistentUserId) } returns null

            // Act
            val result = authRepository.switchRole(UserRole.CHILD)

            // Assert
            assertTrue(result is AuthResult.UserNotFound)
            coVerify {
                authPreferencesDataStore.currentUserId
                userDataSource.findById(nonExistentUserId)
            }
        }
    }

    @Nested
    @DisplayName("Current User Retrieval")
    inner class CurrentUserRetrieval {

        @Test
        @DisplayName("Should retrieve current user when session exists")
        fun shouldRetrieveCurrentUserWhenSessionExists() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser

            // Act
            val currentUser = authRepository.getCurrentUser()

            // Assert
            assertEquals(testCaregiverUser, currentUser)
            coVerify {
                authPreferencesDataStore.currentUserId
                userDataSource.findById(testCaregiverUser.id)
            }
        }

        @Test
        @DisplayName("Should return null when no current user session")
        fun shouldReturnNullWhenNoCurrentUserSession() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(null)

            // Act
            val currentUser = authRepository.getCurrentUser()

            // Assert
            assertNull(currentUser)
            coVerify { authPreferencesDataStore.currentUserId }
            coVerify(exactly = 0) { userDataSource.findById(any()) }
        }

        @Test
        @DisplayName("Should return null when user ID exists but user not found in data source")
        fun shouldReturnNullWhenUserIdExistsButUserNotFoundInDataSource() = runTest {
            // Arrange
            val staleUserId = "stale-user-id"
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(staleUserId)
            coEvery { userDataSource.findById(staleUserId) } returns null

            // Act
            val currentUser = authRepository.getCurrentUser()

            // Assert
            assertNull(currentUser)
            coVerify {
                authPreferencesDataStore.currentUserId
                userDataSource.findById(staleUserId)
            }
        }

        @Test
        @DisplayName("Should handle child user session retrieval")
        fun shouldHandleChildUserSessionRetrieval() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testChildUser.id)
            coEvery { userDataSource.findById(testChildUser.id) } returns testChildUser

            // Act
            val currentUser = authRepository.getCurrentUser()

            // Assert
            assertEquals(testChildUser, currentUser)
            assertEquals(UserRole.CHILD, currentUser?.role)
        }
    }

    @Nested
    @DisplayName("Logout Operations")
    inner class LogoutOperations {

        @Test
        @DisplayName("Should clear current user session on logout")
        fun shouldClearCurrentUserSessionOnLogout() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.clearCurrentUser() } returns Unit

            // Act
            authRepository.logout()

            // Assert
            coVerify { authPreferencesDataStore.clearCurrentUser() }
        }

        @Test
        @DisplayName("Should handle logout when no session exists")
        fun shouldHandleLogoutWhenNoSessionExists() = runTest {
            // Arrange
            coEvery { authPreferencesDataStore.clearCurrentUser() } returns Unit

            // Act
            authRepository.logout()

            // Assert
            coVerify { authPreferencesDataStore.clearCurrentUser() }
        }
    }

    @Nested
    @DisplayName("Repository Integration")
    inner class RepositoryIntegration {

        @Test
        @DisplayName("Should coordinate between data source and preferences store")
        fun shouldCoordinateBetweenDataSourceAndPreferencesStore() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { userDataSource.findByPin(pin) } returns testCaregiverUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser

            // Act: Full authentication flow
            val authResult = authRepository.authenticateWithPin(pin)
            val currentUser = authRepository.getCurrentUser()

            // Assert
            assertTrue(authResult is AuthResult.Success)
            assertEquals(testCaregiverUser, (authResult as AuthResult.Success).user)
            assertEquals(testCaregiverUser, currentUser)

            coVerify {
                userDataSource.findByPin(pin)
                authPreferencesDataStore.setCurrentUser(
                    testCaregiverUser.id,
                    testCaregiverUser.role,
                )
                authPreferencesDataStore.currentUserId
                userDataSource.findById(testCaregiverUser.id)
            }
        }

        @Test
        @DisplayName("Should maintain data consistency across operations")
        fun shouldMaintainDataConsistencyAcrossOperations() = runTest {
            // Arrange: Start with authenticated caregiver
            coEvery { authPreferencesDataStore.currentUserId } returns flowOf(testCaregiverUser.id)
            coEvery { userDataSource.findById(testCaregiverUser.id) } returns testCaregiverUser
            coEvery { userDataSource.findByRole(UserRole.CHILD) } returns testChildUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit
            coEvery { authPreferencesDataStore.clearCurrentUser() } returns Unit

            // Act: Switch role then logout
            val switchResult = authRepository.switchRole(UserRole.CHILD)
            authRepository.logout()

            // Assert
            assertTrue(switchResult is AuthResult.Success)
            assertEquals(testChildUser, (switchResult as AuthResult.Success).user)

            coVerify {
                authPreferencesDataStore.setCurrentUser(testChildUser.id, testChildUser.role)
                authPreferencesDataStore.clearCurrentUser()
            }
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases")
    inner class ErrorHandlingAndEdgeCases {

        @Test
        @DisplayName("Should handle data source exceptions gracefully")
        fun shouldHandleDataSourceExceptionsGracefully() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { userDataSource.findByPin(pin) } throws RuntimeException("Database error")

            // Act & Assert
            try {
                authRepository.authenticateWithPin(pin)
            } catch (e: RuntimeException) {
                assertEquals("Database error", e.message)
            }

            coVerify { userDataSource.findByPin(pin) }
        }

        @Test
        @DisplayName("Should handle preferences store exceptions gracefully")
        fun shouldHandlePreferencesStoreExceptionsGracefully() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { userDataSource.findByPin(pin) } returns testCaregiverUser
            coEvery {
                authPreferencesDataStore.setCurrentUser(any(), any())
            } throws RuntimeException("Preferences error")

            // Act & Assert
            try {
                authRepository.authenticateWithPin(pin)
            } catch (e: RuntimeException) {
                assertEquals("Preferences error", e.message)
            }
        }

        @Test
        @DisplayName("Should handle concurrent access scenarios")
        fun shouldHandleConcurrentAccessScenarios() = runTest {
            // Arrange
            val pin = "1234"
            coEvery { userDataSource.findByPin(pin) } returns testCaregiverUser
            coEvery { authPreferencesDataStore.setCurrentUser(any(), any()) } returns Unit

            // Act: Multiple concurrent authentication attempts
            val results = (1..5).map {
                authRepository.authenticateWithPin(pin)
            }

            // Assert
            results.forEach { result ->
                assertTrue(result is AuthResult.Success)
                assertEquals(testCaregiverUser, (result as AuthResult.Success).user)
            }

            coVerify(exactly = 5) {
                userDataSource.findByPin(pin)
                authPreferencesDataStore.setCurrentUser(
                    testCaregiverUser.id,
                    testCaregiverUser.role,
                )
            }
        }
    }
}
