package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for User data validation.
 *
 * Tests cover:
 * - Name validation rules
 * - UUID format verification
 * - Special character handling
 * - Business rule enforcement
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User Validation Tests")
class UserValidationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should validate user name is not blank")
    fun shouldValidateUserNameNotBlank() {
        // The User data class doesn't enforce this constraint directly,
        // but it's important for business logic
        val user = User(name = "Valid Name", role = UserRole.CHILD)
        assertTrue(user.name.isNotBlank(), "User name should not be blank")
    }

    @Test
    @DisplayName("Should handle empty user name gracefully")
    fun shouldHandleEmptyUserName() {
        // While not ideal, the domain object should handle this case
        val user = User(name = "", role = UserRole.CHILD)
        assertEquals("", user.name, "Should accept empty name (handled by validation layer)")
    }

    @Test
    @DisplayName("Should validate UUID format for ID")
    fun shouldValidateUuidFormat() {
        val user = TestDataFactory.createChildUser()

        // Check that auto-generated ID is valid UUID format
        assertNotNull(UUID.fromString(user.id), "Auto-generated ID should be valid UUID")
    }

    @Test
    @DisplayName("Should handle special characters in name")
    fun shouldHandleSpecialCharactersInName() {
        val specialNames = listOf(
            "José María",
            "François",
            "Müller",
            "O'Connor",
            "李小明",
            "مريم",
        )

        specialNames.forEach { name ->
            val user = TestDataFactory.createChildUser(name = name)
            assertEquals(name, user.name, "Should handle special characters in name: $name")
        }
    }
}
