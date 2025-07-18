package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldAfford
import com.lemonqwest.app.domain.shouldNotAfford
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for User token balance integration.
 *
 * Tests cover:
 * - Token balance initialization
 * - Affordability operations
 * - Large balance support
 * - Negative balance handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("User Token Balance Tests")
class UserTokenBalanceTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should initialize with zero token balance")
    fun shouldInitializeWithZeroTokens() {
        val user = User(
            name = "Test User",
            role = UserRole.CHILD,
        )

        assertEquals(0, user.tokenBalance.getValue(), "Default token balance should be zero")
    }

    @Test
    @DisplayName("Should initialize with custom token balance")
    fun shouldInitializeWithCustomTokenBalance() {
        val customBalance = TokenBalance.create(75)
        val user = User(
            name = "Test User",
            role = UserRole.CHILD,
            tokenBalance = customBalance,
        )

        assertEquals(75, user.tokenBalance.getValue(), "Should use custom token balance")
    }

    @Test
    @DisplayName("Should handle token balance operations")
    fun shouldHandleTokenBalanceOperations() {
        val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(50))

        user.shouldAfford(30)
        user.shouldAfford(50)
        user.shouldNotAfford(51)
        user.shouldNotAfford(100)
    }

    @Test
    @DisplayName("Should support large token balances")
    fun shouldSupportLargeTokenBalances() {
        val largeBalance = TokenBalance.create(1000000)
        val user = TestDataFactory.createChildUser(tokenBalance = largeBalance)

        assertEquals(1000000, user.tokenBalance.getValue())
        user.shouldAfford(999999)
    }

    @Test
    @DisplayName("Should support negative token balances for admin operations")
    fun shouldSupportNegativeTokenBalances() {
        val negativeBalance = TokenBalance.createAdmin(-25)
        val user = TestDataFactory.createChildUser(tokenBalance = negativeBalance)

        assertEquals(-25, user.tokenBalance.getValue())
    }
}
