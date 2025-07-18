package com.lemonqwest.app.domain.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for AuthDomainException functionality.
 *
 * Tests cover:
 * - Auth domain exception creation with message
 * - Auth domain exception creation with cause
 * - Authentication-specific error scenarios handling
 */
@DisplayName("AuthDomainException Tests")
class AuthDomainExceptionTest {

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
