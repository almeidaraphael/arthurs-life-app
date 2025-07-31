package com.lemonqwest.app.domain.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for PresentationException functionality.
 *
 * Tests cover:
 * - Presentation exception creation with message
 * - Presentation exception creation with cause
 * - Presentation-specific error scenarios handling
 */
@DisplayName("PresentationException Tests")
class PresentationExceptionTest {

    @Test
    @DisplayName("Should create presentation exception with message")
    fun shouldCreatePresentationExceptionWithMessage() {
        // Arrange
        val message = "UI operation failed"

        // Act
        val exception = PresentationException(message)

        // Assert
        assertEquals(message, exception.message)
        assertNull(exception.cause)
        assertTrue(exception is DomainException)
    }

    @Test
    @DisplayName("Should create presentation exception with cause")
    fun shouldCreatePresentationExceptionWithCause() {
        // Arrange
        val message = "ViewModel state update failed"
        val cause = NullPointerException("State is null")

        // Act
        val exception = PresentationException(message, cause)

        // Assert
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(exception is DomainException)
    }

    @Test
    @DisplayName("Should handle presentation-specific error scenarios")
    fun shouldHandlePresentationSpecificErrorScenarios() {
        // Arrange
        val scenarios = listOf(
            "Navigation failed",
            "State update failed",
            "UI binding error",
            "Theme switching failed",
            "Screen transition error",
        )

        // Act & Assert
        scenarios.forEach { message ->
            val exception = PresentationException(message)
            assertEquals(message, exception.message)
            assertTrue(exception is PresentationException)
            assertTrue(exception is DomainException)
        }
    }
}
