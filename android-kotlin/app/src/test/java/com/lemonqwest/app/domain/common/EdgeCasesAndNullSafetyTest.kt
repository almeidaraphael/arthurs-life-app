package com.lemonqwest.app.domain.common

import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for domain exception edge cases and null safety.
 *
 * Tests cover:
 * - Empty error message handling
 * - Very long error message handling
 * - Special character error message handling
 * - Unicode character error message handling
 * - Nested exception scenario handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Edge Cases and Null Safety Tests")
class EdgeCasesAndNullSafetyTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @BeforeEach
    fun setUp() {
        // Test setup - no mocks needed for domain exception tests
    }

    @Test
    @DisplayName("Should handle empty error messages")
    fun shouldHandleEmptyErrorMessages() {
        // Arrange
        val emptyMessage = ""

        // Act
        val taskException = TaskDomainException(emptyMessage)
        val userException = UserDomainException(emptyMessage)
        val authException = AuthDomainException(emptyMessage)
        val presentationException = PresentationException(emptyMessage)

        // Assert
        assertEquals(emptyMessage, taskException.message)
        assertEquals(emptyMessage, userException.message)
        assertEquals(emptyMessage, authException.message)
        assertEquals(emptyMessage, presentationException.message)
    }

    @Test
    @DisplayName("Should handle very long error messages")
    fun shouldHandleVeryLongErrorMessages() {
        // Arrange
        val longMessage = "A".repeat(1000)

        // Act
        val exception = TaskDomainException(longMessage)

        // Assert
        assertEquals(longMessage, exception.message)
        assertEquals(1000, exception.message?.length)
    }

    @Test
    @DisplayName("Should handle special characters in error messages")
    fun shouldHandleSpecialCharactersInErrorMessages() {
        // Arrange
        val specialMessage = "Error with special chars: !@#$%^&*()[]{}|;':\",./<>?"

        // Act
        val exception = UserDomainException(specialMessage)

        // Assert
        assertEquals(specialMessage, exception.message)
    }

    @Test
    @DisplayName("Should handle unicode characters in error messages")
    fun shouldHandleUnicodeCharactersInErrorMessages() {
        // Arrange
        val unicodeMessage = "Error message with unicode: 你好 🚀 ñoël"

        // Act
        val exception = AuthDomainException(unicodeMessage)

        // Assert
        assertEquals(unicodeMessage, exception.message)
    }

    @Test
    @DisplayName("Should handle nested exception scenarios")
    fun shouldHandleNestedExceptionScenarios() {
        // Arrange
        val deepCause = IllegalArgumentException("Deep validation error")
        val midCause = RuntimeException("Mid-level processing error", deepCause)
        val topException = PresentationException("Top-level UI error", midCause)

        // Act & Assert
        assertEquals("Top-level UI error", topException.message)
        assertEquals(midCause, topException.cause)
        assertEquals("Mid-level processing error", topException.cause?.message)
        assertEquals(deepCause, topException.cause?.cause)
        assertEquals("Deep validation error", topException.cause?.cause?.message)
    }
}
