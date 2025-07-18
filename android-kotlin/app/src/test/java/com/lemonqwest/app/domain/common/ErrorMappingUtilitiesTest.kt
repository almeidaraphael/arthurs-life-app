package com.lemonqwest.app.domain.common

import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for domain error mapping utilities.
 *
 * Tests cover:
 * - Mapping to task exceptions with operation and cause
 * - Mapping to user exceptions with operation and cause
 * - Mapping to presentation exceptions with operation and cause
 * - Handling null cause messages in mapping
 * - Preserving original exceptions in mapping
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Error Mapping Utilities Tests")
class ErrorMappingUtilitiesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @BeforeEach
    fun setUp() {
        // Test setup - no mocks needed for domain exception tests
    }

    @Test
    @DisplayName("Should map to task exception with operation and cause")
    fun shouldMapToTaskExceptionWithOperationAndCause() {
        // Arrange
        val operation = "Task completion"
        val cause = RuntimeException("Database error")

        // Act
        val exception = mapToTaskException(operation, cause)

        // Assert
        assertEquals("Task completion failed: Database error", exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(exception is TaskDomainException)
    }

    @Test
    @DisplayName("Should map to user exception with operation and cause")
    fun shouldMapToUserExceptionWithOperationAndCause() {
        // Arrange
        val operation = "User creation"
        val cause = IllegalArgumentException("Invalid user data")

        // Act
        val exception = mapToUserException(operation, cause)

        // Assert
        assertEquals("User creation failed: Invalid user data", exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(exception is UserDomainException)
    }

    @Test
    @DisplayName("Should map to presentation exception with operation and cause")
    fun shouldMapToPresentationExceptionWithOperationAndCause() {
        // Arrange
        val operation = "State update"
        val cause = NullPointerException("State reference is null")

        // Act
        val exception = mapToPresentationException(operation, cause)

        // Assert
        assertEquals("State update failed: State reference is null", exception.message)
        assertEquals(cause, exception.cause)
        assertTrue(exception is PresentationException)
    }

    @Test
    @DisplayName("Should handle null cause message in mapping")
    fun shouldHandleNullCauseMessageInMapping() {
        // Arrange
        val operation = "Test operation"
        val cause = RuntimeException() // No message

        // Act
        val taskException = mapToTaskException(operation, cause)
        val userException = mapToUserException(operation, cause)
        val presentationException = mapToPresentationException(operation, cause)

        // Assert
        assertEquals("Test operation failed: null", taskException.message)
        assertEquals("Test operation failed: null", userException.message)
        assertEquals("Test operation failed: null", presentationException.message)
    }

    @Test
    @DisplayName("Should preserve original exception in mapping")
    fun shouldPreserveOriginalExceptionInMapping() {
        // Arrange
        val operation = "Complex operation"
        val originalCause = SecurityException("Security violation")

        // Act
        val mappedException = mapToTaskException(operation, originalCause)

        // Assert
        assertEquals(originalCause, mappedException.cause)
        assertEquals("Security violation", mappedException.cause?.message)
        assertTrue(mappedException.cause is SecurityException)
    }
}
