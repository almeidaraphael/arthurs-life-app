package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for User business rules.
 *
 * Tests cover:
 * - Token constraints enforcement
 * - PIN format validation
 * - Immutability constraints
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User Business Rules Tests")
class UserBusinessRulesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should enforce token balance constraints")
    fun shouldEnforceTokenBalanceConstraints() {
        // Valid token balances
        val validBalances = listOf(0, 1, 50, 100, 1000)

        validBalances.forEach { amount ->
            val balance = TokenBalance.create(amount)
            val user = TestDataFactory.createChildUser(tokenBalance = balance)
            assertEquals(
                amount,
                user.tokenBalance.getValue(),
                "Should accept valid token balance: $amount",
            )
        }
    }

    @Test
    @DisplayName("Should validate PIN format constraints")
    fun shouldValidatePinFormatConstraints() {
        val validPins = listOf("0000", "1234", "9999", "4567")

        validPins.forEach { pinValue ->
            val pin = PIN.create(pinValue)
            val user = TestDataFactory.createCaregiverUser(pin = pin)
            assertNotNull(user.pin, "Should accept valid PIN: $pinValue")
        }
    }

    @Test
    @DisplayName("Should maintain immutability of user objects")
    fun shouldMaintainImmutability() {
        val user = TestDataFactory.createChildUser()
        val originalName = user.name
        val originalTokens = user.tokenBalance.getValue()

        // User is a data class, so it's immutable by default
        // Any modifications would create a new instance
        val modifiedUser = user.copy(name = "Modified Name")

        assertEquals(originalName, user.name, "Original user should remain unchanged")
        assertEquals(
            originalTokens,
            user.tokenBalance.getValue(),
            "Original token balance should remain unchanged",
        )
        assertEquals("Modified Name", modifiedUser.name, "Modified user should have new name")
    }
}
