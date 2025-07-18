package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.domain.shouldAfford
import com.lemonqwest.app.domain.shouldNotAfford
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Focused test suite for User property-based testing.
 *
 * Tests cover:
 * - Parameterized token tests
 * - PIN value variations
 * - Property-based scenarios
 */
@DisplayName("User Property Tests")
class UserPropertyTest {

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 5, 10, 25, 50, 100, 500, 1000])
    @DisplayName("Should handle various token balance values")
    fun shouldHandleVariousTokenBalances(tokenAmount: Int) {
        val balance = TokenBalance.create(tokenAmount)
        val user = TestDataFactory.createChildUser(tokenBalance = balance)

        assertEquals(tokenAmount, user.tokenBalance.getValue())

        // Test affordability at different price points
        user.shouldAfford(0)
        if (tokenAmount > 0) {
            user.shouldAfford(1)
            user.shouldAfford(tokenAmount)
        }
        user.shouldNotAfford(tokenAmount + 1)
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999"],
    )
    @DisplayName("Should handle various PIN values")
    fun shouldHandleVariousPinValues(pinValue: String) {
        val pin = PIN.create(pinValue)
        val user = TestDataFactory.createCaregiverUser(pin = pin)

        assertNotNull(user.pin)
        assertTrue(user.pin!!.verify(pinValue), "Should verify PIN: $pinValue")
    }
}
