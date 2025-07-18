package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for User PIN authentication.
 *
 * Tests cover:
 * - PIN verification logic
 * - Security between users
 * - Different PIN values
 * - Authentication flows
 */
@DisplayName("User PIN Authentication Tests")
class UserPinAuthenticationTest {

    @Test
    @DisplayName("Should verify correct PIN for caregiver")
    fun shouldVerifyCorrectPin() {
        val pin = PIN.create("1234")
        val caregiver = TestDataFactory.createCaregiverUser(pin = pin)

        assertNotNull(caregiver.pin)
        assertTrue(caregiver.pin!!.verify("1234"), "Should verify correct PIN")
    }

    @Test
    @DisplayName("Should reject incorrect PIN for caregiver")
    fun shouldRejectIncorrectPin() {
        val pin = PIN.create("1234")
        val caregiver = TestDataFactory.createCaregiverUser(pin = pin)

        assertNotNull(caregiver.pin)
        assertTrue(!caregiver.pin!!.verify("5678"), "Should reject incorrect PIN")
    }

    @Test
    @DisplayName("Should handle PIN creation with different values")
    fun shouldHandleDifferentPinValues() {
        val pins = listOf("0000", "1111", "9999", "4321", "8765")

        pins.forEach { pinValue ->
            val pin = PIN.create(pinValue)
            val user = TestDataFactory.createCaregiverUser(pin = pin)

            assertTrue(user.pin!!.verify(pinValue), "Should verify PIN $pinValue")
        }
    }

    @Test
    @DisplayName("Should maintain PIN security between users")
    fun shouldMaintainPinSecurity() {
        val user1 = TestDataFactory.createCaregiverUser(pin = PIN.create("1234"))
        val user2 = TestDataFactory.createCaregiverUser(pin = PIN.create("5678"))

        assertTrue(user1.pin!!.verify("1234"), "User 1 should verify their PIN")
        assertTrue(!user1.pin!!.verify("5678"), "User 1 should not verify User 2's PIN")

        assertTrue(user2.pin!!.verify("5678"), "User 2 should verify their PIN")
        assertTrue(!user2.pin!!.verify("1234"), "User 2 should not verify User 1's PIN")
    }
}
