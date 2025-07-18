package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for User edge cases.
 *
 * Tests cover:
 * - Maximum value handling
 * - Extreme scenarios
 * - Rapid creation patterns
 * - Boundary conditions
 */
@DisplayName("User Edge Cases Tests")
class UserEdgeCasesTest {

    @Test
    @DisplayName("Should handle maximum token balance")
    fun shouldHandleMaximumTokenBalance() {
        val maxBalance = TokenBalance.create(Int.MAX_VALUE)
        val user = TestDataFactory.createChildUser(tokenBalance = maxBalance)

        assertEquals(
            Int.MAX_VALUE,
            user.tokenBalance.getValue(),
            "Should handle maximum token balance",
        )
    }

    @Test
    @DisplayName("Should handle very long user names")
    fun shouldHandleVeryLongUserNames() {
        val longName = "A".repeat(1000)
        val user = TestDataFactory.createChildUser(name = longName)

        assertEquals(longName, user.name, "Should handle very long user names")
    }

    @Test
    @DisplayName("Should handle user creation with all minimum values")
    fun shouldHandleMinimumValues() {
        val user = User(
            name = "A",
            role = UserRole.CHILD,
            tokenBalance = TokenBalance.zero(),
            pin = null,
        )

        assertEquals("A", user.name)
        assertEquals(UserRole.CHILD, user.role)
        assertEquals(0, user.tokenBalance.getValue())
        assertNull(user.pin)
    }

    @Test
    @DisplayName("Should handle rapid user creation")
    fun shouldHandleRapidUserCreation() {
        val users = (1..100).map { TestDataFactory.createChildUser() }
        val uniqueIds = users.map { it.id }.toSet()

        assertEquals(100, uniqueIds.size, "All users should have unique IDs")
    }
}
