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
 * Focused test suite for AuthDomainException functionality.
 *
 * Tests cover:
 * - Auth domain exception creation with message
 * - Auth domain exception creation with cause
 * - Authentication-specific error scenarios handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AuthDomainException Tests")
class AuthDomainExceptionTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @BeforeEach
    fun setUp() {
        // Test setup - no mocks needed for domain exception tests
    }

    @Test
    @DisplayName("Should create auth domain exception with message")
    fun shouldCreateAuthDomainExceptionWithMessage() {
        // Arrange
        val message = "Authentication failed"

        // Act
        val exception = AuthDomainException(message)

        // Assert
        assertEquals(message, exception.message)
        assertNull(exception.cause)
        assertTrue(exception is DomainException)
    }

    @Test
    @DisplayName("Should create auth domain exception with cause")
    fun shouldCreateAuthDomainExceptionWithCause() {
        // Arrange
        val message = "PIN validation failed"
        val cause = IllegalArgumentException("Invalid PIN format")

        // Act
        val exception = AuthDomainException(message, cause)

        // Assert
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(exception is DomainException)
    }

    @Test
    @DisplayName("Should handle authentication-specific error scenarios")
    fun shouldHandleAuthenticationSpecificErrorScenarios() {
        // Arrange
        val scenarios = listOf(
            "Invalid PIN",
            "Authentication expired",
            "Role switch forbidden",
            "Session not found",
            "Authentication service unavailable",
        )

        // Act & Assert
        scenarios.forEach { message ->
            val exception = AuthDomainException(message)
            assertEquals(message, exception.message)
            assertTrue(exception is AuthDomainException)
            assertTrue(exception is DomainException)
        }
    }
}
