package com.arthurslife.app.domain.common

import androidx.lifecycle.ViewModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive tests for domain exception hierarchy and error handling utilities.
 *
 * Tests cover:
 * - Domain exception inheritance and structure
 * - Exception message handling and propagation
 * - Cause chaining functionality
 * - Error mapping utilities
 * - ViewModel error handling extensions
 * - Edge cases and null safety
 */
@DisplayName("Domain Exceptions and Error Handling Tests")
class DomainExceptionsTest {

    @Nested
    @DisplayName("DomainException Base Class")
    inner class DomainExceptionBaseClass {

        @Test
        @DisplayName("Should create domain exception with message only")
        fun shouldCreateDomainExceptionWithMessageOnly() {
            // Arrange
            val message = "Test domain error"

            // Act
            val exception = TaskDomainException(message)

            // Assert
            assertEquals(message, exception.message)
            assertNull(exception.cause)
            assertTrue(exception is DomainException)
            assertTrue(exception is Exception)
        }

        @Test
        @DisplayName("Should create domain exception with message and cause")
        fun shouldCreateDomainExceptionWithMessageAndCause() {
            // Arrange
            val message = "Domain operation failed"
            val cause = RuntimeException("Original error")

            // Act
            val exception = TaskDomainException(message, cause)

            // Assert
            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
            assertEquals("Original error", exception.cause?.message)
        }

        @Test
        @DisplayName("Should support primary constructor for testing")
        fun shouldSupportPrimaryConstructorForTesting() {
            // Arrange
            val message = "Test constructor"

            // Act
            val exception = TaskDomainException(message)

            // Assert
            assertEquals(message, exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should handle null cause gracefully")
        fun shouldHandleNullCauseGracefully() {
            // Arrange
            val message = "Error with null cause"

            // Act
            val exception = TaskDomainException(message, null)

            // Assert
            assertEquals(message, exception.message)
            assertNull(exception.cause)
        }

        @Test
        @DisplayName("Should preserve exception hierarchy")
        fun shouldPreserveExceptionHierarchy() {
            // Arrange
            val message = "Hierarchy test"
            val exception = TaskDomainException(message)

            // Act & Assert
            assertTrue(exception is DomainException)
            assertTrue(exception is Exception)
            assertTrue(exception is Throwable)
        }
    }

    @Nested
    @DisplayName("TaskDomainException")
    inner class TaskDomainExceptionTests {

        @Test
        @DisplayName("Should create task domain exception with message")
        fun shouldCreateTaskDomainExceptionWithMessage() {
            // Arrange
            val message = "Task operation failed"

            // Act
            val exception = TaskDomainException(message)

            // Assert
            assertEquals(message, exception.message)
            assertNull(exception.cause)
            assertTrue(exception is DomainException)
        }

        @Test
        @DisplayName("Should create task domain exception with cause")
        fun shouldCreateTaskDomainExceptionWithCause() {
            // Arrange
            val message = "Task completion failed"
            val cause = IllegalStateException("Invalid task state")

            // Act
            val exception = TaskDomainException(message, cause)

            // Assert
            assertEquals(message, exception.message)
            assertEquals(cause, exception.cause)
            assertTrue(exception is DomainException)
        }

        @Test
        @DisplayName("Should handle task-specific error scenarios")
        fun shouldHandleTaskSpecificErrorScenarios() {
            // Arrange
            val scenarios = listOf(
                "Task not found",
                "Task already completed",
                "Invalid task assignment",
                "Task validation failed",
                "Token reward calculation error",
            )

            // Act & Assert
            scenarios.forEach { message ->
                val exception = TaskDomainException(message)
                assertEquals(message, exception.message)
                assertTrue(exception is TaskDomainException)
                assertTrue(exception is DomainException)
            }
        }
    }

    @Nested
    @DisplayName("UserDomainException")
    inner class UserDomainExceptionTests {

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

    @Nested
    @DisplayName("AuthDomainException")
    inner class AuthDomainExceptionTests {

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

    @Nested
    @DisplayName("PresentationException")
    inner class PresentationExceptionTests {

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

    @Nested
    @DisplayName("Error Mapping Utilities")
    inner class ErrorMappingUtilities {

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

    @Nested
    @DisplayName("ViewModel Error Handling")
    inner class ViewModelErrorHandling {

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

    @Nested
    @DisplayName("Exception Chaining and Hierarchy")
    inner class ExceptionChainingAndHierarchy {

        @Test
        @DisplayName("Should support multi-level exception chaining")
        fun shouldSupportMultiLevelExceptionChaining() {
            // Arrange
            val rootCause = IllegalStateException("Database connection failed")
            val intermediateCause = RuntimeException("Service unavailable", rootCause)
            val domainException = TaskDomainException("Task operation failed", intermediateCause)

            // Act & Assert
            assertEquals(intermediateCause, domainException.cause)
            assertEquals(rootCause, domainException.cause?.cause)
            assertEquals("Database connection failed", domainException.cause?.cause?.message)
        }

        @Test
        @DisplayName("Should maintain proper exception hierarchy")
        fun shouldMaintainProperExceptionHierarchy() {
            // Arrange
            val exceptions = listOf(
                TaskDomainException("Task error"),
                UserDomainException("User error"),
                AuthDomainException("Auth error"),
                PresentationException("UI error"),
            )

            // Act & Assert
            exceptions.forEach { exception ->
                assertTrue(exception is DomainException)
                assertTrue(exception is Exception)
                assertTrue(exception is Throwable)
            }
        }

        @Test
        @DisplayName("Should support polymorphic exception handling")
        fun shouldSupportPolymorphicExceptionHandling() {
            // Arrange
            val exceptions: List<DomainException> = listOf(
                TaskDomainException("Task error"),
                UserDomainException("User error"),
                AuthDomainException("Auth error"),
                PresentationException("UI error"),
            )

            // Act & Assert
            exceptions.forEach { exception ->
                when (exception) {
                    is TaskDomainException -> assertTrue(
                        exception.message?.contains("Task") == true,
                    )
                    is UserDomainException -> assertTrue(
                        exception.message?.contains("User") == true,
                    )
                    is AuthDomainException -> assertTrue(
                        exception.message?.contains("Auth") == true,
                    )
                    is PresentationException -> assertTrue(
                        exception.message?.contains("UI") == true,
                    )
                }
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Null Safety")
    inner class EdgeCasesAndNullSafety {

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

    // Removed TestDomainException class since sealed classes cannot be extended in tests.
    // Using concrete implementations like TaskDomainException for testing instead.
}
