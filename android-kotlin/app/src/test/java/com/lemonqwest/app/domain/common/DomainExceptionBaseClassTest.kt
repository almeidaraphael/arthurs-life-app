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
 * Focused test suite for DomainException base class functionality.
 *
 * Tests cover:
 * - Domain exception creation with message only
 * - Domain exception creation with message and cause
 * - Primary constructor testing support
 * - Null cause handling
 * - Exception hierarchy preservation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("DomainException Base Class Tests")
class DomainExceptionBaseClassTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @BeforeEach
    fun setUp() {
        // Test setup - no mocks needed for domain exception tests
    }

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
