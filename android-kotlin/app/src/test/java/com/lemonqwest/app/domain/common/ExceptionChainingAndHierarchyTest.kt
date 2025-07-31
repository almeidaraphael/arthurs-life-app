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
 * Focused test suite for exception chaining and hierarchy functionality.
 *
 * Tests cover:
 * - Multi-level exception chaining support
 * - Proper exception hierarchy maintenance
 * - Polymorphic exception handling support
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Exception Chaining and Hierarchy Tests")
class ExceptionChainingAndHierarchyTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @BeforeEach
    fun setUp() {
        // Test setup - no mocks needed for domain exception tests
    }

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
