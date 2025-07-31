package com.lemonqwest.app.domain.common

import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for UserDomainException functionality.
 *
 * Tests cover:
 * - User domain exception creation with message
 * - User domain exception creation with cause
 * - User-specific error scenarios handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("UserDomainException Tests")
class UserDomainExceptionTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @BeforeEach
    fun setUp() {
        // Test setup - no mocks needed for domain exception tests
    }

    @Test
    @DisplayName("Should create user domain exception with message")
    fun shouldCreateUserDomainExceptionWithMessage() {
        // Arrange
        val message = "User operation failed"

        // Act
        val exception = UserDomainException(message)

        // Assert
        assertEquals(message, exception.message)
        assertNull(exception.cause)
        assertTrue(exception is DomainException)
    }

    @Test
    @DisplayName("Should create user domain exception with cause")
    fun shouldCreateUserDomainExceptionWithCause() {
        // Arrange
        val message = "User authentication failed"
        val cause = SecurityException("Invalid credentials")

        // Act
        val exception = UserDomainException(message, cause)

        // Assert
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(exception is DomainException)
    }

    @Test
    @DisplayName("Should handle user-specific error scenarios")
    fun shouldHandleUserSpecificErrorScenarios() {
        // Arrange
        val scenarios = listOf(
            "User not found",
            "Invalid user role",
            "User already exists",
            "Token balance insufficient",
            "User permission denied",
        )

        // Act & Assert
        scenarios.forEach { message ->
            val exception = UserDomainException(message)
            assertEquals(message, exception.message)
            assertTrue(exception is UserDomainException)
            assertTrue(exception is DomainException)
        }
    }
}
