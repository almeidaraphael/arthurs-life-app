package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Comprehensive test suite for User PIN authentication.
 *
 * Tests cover:
 * - PIN verification logic
 * - PIN security between users
 * - Different PIN values handling
 * - Authentication workflows
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User PIN Authentication Tests")
class UserPinAuthenticationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private var testPin: PIN? = null
    private var caregiverWithPin: User? = null

    @BeforeEach
    fun setUp() {
        testPin = PIN.create("1234")
        caregiverWithPin = TestDataFactory.createCaregiverUser(pin = testPin!!)
    }

    @Test
    @DisplayName("Should verify correct PIN for caregiver")
    fun shouldVerifyCorrectPin() = runTest {
        assertNotNull(caregiverWithPin!!.pin, "Caregiver should have a PIN")
        assertTrue(caregiverWithPin!!.pin!!.verify("1234"), "Should verify correct PIN")
    }

    @Test
    @DisplayName("Should reject incorrect PIN for caregiver")
    fun shouldRejectIncorrectPin() = runTest {
        assertNotNull(caregiverWithPin!!.pin, "Caregiver should have a PIN")
        assertTrue(!caregiverWithPin!!.pin!!.verify("5678"), "Should reject incorrect PIN")
    }

    @Test
    @DisplayName("Should handle PIN creation with different values")
    fun shouldHandleDifferentPinValues() = runTest {
        val pins = listOf("0000", "1111", "9999", "4321", "8765")

        pins.forEach { pinValue ->
            val pin = PIN.create(pinValue)
            val user = TestDataFactory.createCaregiverUser(pin = pin)

            assertTrue(user.pin!!.verify(pinValue), "Should verify PIN $pinValue")
        }
    }

    @Test
    @DisplayName("Should maintain PIN security between users")
    fun shouldMaintainPinSecurity() = runTest {
        val user1 = TestDataFactory.createCaregiverUser(pin = PIN.create("1234"))
        val user2 = TestDataFactory.createCaregiverUser(pin = PIN.create("5678"))

        assertTrue(user1.pin!!.verify("1234"), "User 1 should verify their PIN")
        assertTrue(!user1.pin!!.verify("5678"), "User 1 should not verify User 2's PIN")

        assertTrue(user2.pin!!.verify("5678"), "User 2 should verify their PIN")
        assertTrue(!user2.pin!!.verify("1234"), "User 2 should not verify User 1's PIN")
    }
}
