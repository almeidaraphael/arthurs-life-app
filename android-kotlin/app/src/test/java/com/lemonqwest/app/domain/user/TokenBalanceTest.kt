package com.lemonqwest.app.domain.user

import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

@OptIn(ExperimentalCoroutinesApi::class)
class TokenBalanceTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 25, 50, 100])
    fun `create TokenBalance with valid amounts`(amount: Int) {
        val balance = TokenBalance.create(amount)
        assertEquals(amount, balance.getValue())
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, -10, -50])
    fun `create TokenBalance with negative amount throws exception`(amount: Int) {
        assertThrows(IllegalArgumentException::class.java) {
            TokenBalance.create(amount)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [-15, 0, 25, 100])
    fun `createAdmin allows any amount`(amount: Int) {
        val balance = TokenBalance.createAdmin(amount)
        assertEquals(amount, balance.getValue())
    }

    @Test
    fun `zero factory method creates balance with zero tokens`() {
        val balance = TokenBalance.zero()
        assertEquals(0, balance.getValue())
    }

    @ParameterizedTest
    @CsvSource("10,5,15", "0,10,10", "50,25,75")
    fun `add tokens increases balance correctly`(initial: Int, toAdd: Int, expected: Int) {
        val balance = TokenBalance.create(initial)
        val newBalance = balance.add(toAdd)
        assertEquals(expected, newBalance.getValue())
        assertEquals(initial, balance.getValue()) // Original unchanged
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, -5, -10])
    fun `add negative tokens throws exception`(amount: Int) {
        val balance = TokenBalance.create(10)
        assertThrows(IllegalArgumentException::class.java) {
            balance.add(amount)
        }
    }

    @ParameterizedTest
    @CsvSource("20,15,5", "20,20,0", "50,10,40")
    fun `subtract tokens with sufficient balance succeeds`(
        initial: Int,
        toSubtract: Int,
        expected: Int,
    ) {
        val balance = TokenBalance.create(initial)
        val newBalance = balance.subtract(toSubtract)
        assertEquals(expected, newBalance.getValue())
        assertEquals(initial, balance.getValue()) // Original unchanged
    }

    @ParameterizedTest
    @CsvSource("10,15", "5,10", "0,1")
    fun `subtract more tokens than available throws exception`(initial: Int, toSubtract: Int) {
        val balance = TokenBalance.create(initial)
        assertThrows(IllegalArgumentException::class.java) {
            balance.subtract(toSubtract)
        }
    }

    @ParameterizedTest
    @CsvSource("5,10,-5", "20,10,10", "0,5,-5")
    fun `adminSubtract allows negative balance`(initial: Int, toSubtract: Int, expected: Int) {
        val balance = TokenBalance.create(initial)
        val newBalance = balance.adminSubtract(toSubtract)
        assertEquals(expected, newBalance.getValue())
        assertEquals(initial, balance.getValue()) // Original unchanged
    }

    @ParameterizedTest
    @CsvSource("30,25,true", "30,30,true", "30,35,false", "10,20,false")
    fun `canAfford returns correct result`(balance: Int, cost: Int, expected: Boolean) {
        val tokenBalance = TokenBalance.create(balance)
        assertEquals(expected, tokenBalance.canAfford(cost))
    }

    @Test
    fun `negative balance canAfford returns false for positive costs`() {
        val balance = TokenBalance.createAdmin(-10)
        assertFalse(balance.canAfford(5))
        assertFalse(balance.canAfford(0))
    }

    @ParameterizedTest
    @CsvSource("25,'25 tokens'", "0,'0 tokens'", "-10,'-10 tokens'")
    fun `toString returns correct format`(amount: Int, expected: String) {
        val balance = if (amount >= 0) {
            TokenBalance.create(amount)
        } else {
            TokenBalance.createAdmin(amount)
        }
        assertEquals(expected, balance.toString())
    }
}
