package com.lemonqwest.app.domain.common

import androidx.lifecycle.ViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for ViewModel error handling functionality.
 *
 * Tests cover:
 * - Handling domain exceptions with original messages
 * - Handling domain exceptions with null messages
 * - Handling generic exceptions with operation context
 * - Handling generic exceptions with null messages
 * - Consistent handling of different domain exception types
 * - Providing user-friendly error messages
 */
@DisplayName("ViewModel Error Handling Tests")
class ViewModelErrorHandlingTest {

    private val testViewModel = object : ViewModel() {}

    @Test
    @DisplayName("Should handle domain exception with original message")
    fun shouldHandleDomainExceptionWithOriginalMessage() {
        // Arrange
        val viewModel = testViewModel
        val operation = "Test operation"
        val domainException = TaskDomainException("Task validation failed")

        // Act
        val result = viewModel.handleError(operation, domainException)

        // Assert
        assertEquals("Task validation failed", result)
    }

    @Test
    @DisplayName("Should handle domain exception with null message")
    fun shouldHandleDomainExceptionWithNullMessage() {
        // Arrange
        val viewModel = testViewModel
        val operation = "Test operation"
        // Use a concrete domain exception type instead of abstract
        val domainException = TaskDomainException("")

        // Act
        val result = viewModel.handleError(operation, domainException)

        // Assert
        assertEquals("", result) // Empty string message should be returned as-is
    }

    @Test
    @DisplayName("Should handle generic exception with operation context")
    fun shouldHandleGenericExceptionWithOperationContext() {
        // Arrange
        val viewModel = testViewModel
        val operation = "Data loading"
        val genericException = RuntimeException("Network timeout")

        // Act
        val result = viewModel.handleError(operation, genericException)

        // Assert
        assertEquals("Data loading failed: Network timeout", result)
    }

    @Test
    @DisplayName("Should handle generic exception with null message")
    fun shouldHandleGenericExceptionWithNullMessage() {
        // Arrange
        val viewModel = testViewModel
        val operation = "File processing"
        val exceptionWithoutMessage = RuntimeException()

        // Act
        val result = viewModel.handleError(operation, exceptionWithoutMessage)

        // Assert
        assertEquals("File processing failed: Unknown error", result)
    }

    @Test
    @DisplayName("Should handle different domain exception types consistently")
    fun shouldHandleDifferentDomainExceptionTypesConsistently() {
        // Arrange
        val viewModel = testViewModel
        val operation = "Multi-domain operation"
        val exceptions = listOf(
            TaskDomainException("Task error"),
            UserDomainException("User error"),
            AuthDomainException("Auth error"),
            PresentationException("UI error"),
        )

        // Act & Assert
        exceptions.forEach { exception ->
            val result = viewModel.handleError(operation, exception)
            assertEquals(exception.message, result)
        }
    }

    @Test
    @DisplayName("Should provide user-friendly error messages")
    fun shouldProvideUserFriendlyErrorMessages() {
        // Arrange
        val viewModel = testViewModel
        val scenarios = mapOf(
            "Login" to IllegalArgumentException("Invalid credentials"),
            "Task completion" to TaskDomainException("Task already completed"),
            "Profile update" to UserDomainException("User not found"),
            "Theme switching" to PresentationException("Theme resource not found"),
        )

        // Act & Assert
        scenarios.forEach { (operation, exception) ->
            val result = viewModel.handleError(operation, exception)
            assertNotNull(result)
            assertTrue(result.isNotEmpty())

            when (exception) {
                is DomainException -> assertEquals(exception.message, result)
                else -> assertTrue(result.startsWith("$operation failed:"))
            }
        }
    }
}
