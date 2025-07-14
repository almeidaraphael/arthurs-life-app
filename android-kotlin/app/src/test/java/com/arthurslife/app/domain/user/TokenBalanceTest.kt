package com.arthurslife.app.domain.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TokenBalanceTest {

    @Test
    fun `create TokenBalance with positive amount succeeds`() {
        val balance = TokenBalance.create(50)
        assertEquals(50, balance.getValue())
    }

    @Test
    fun `create TokenBalance with zero amount succeeds`() {
        val balance = TokenBalance.create(0)
        assertEquals(0, balance.getValue())
    }

    @Test
    fun `create TokenBalance with negative amount throws exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            TokenBalance.create(-10)
        }
    }

    @Test
    fun `createAdmin with negative amount succeeds`() {
        val balance = TokenBalance.createAdmin(-15)
        assertEquals(-15, balance.getValue())
    }

    @Test
    fun `createAdmin with positive amount succeeds`() {
        val balance = TokenBalance.createAdmin(25)
        assertEquals(25, balance.getValue())
    }

    @Test
    fun `zero factory method creates balance with zero tokens`() {
        val balance = TokenBalance.zero()
        assertEquals(0, balance.getValue())
    }

    @Test
    fun `add tokens increases balance correctly`() {
        val balance = TokenBalance.create(10)
        val newBalance = balance.add(5)
        assertEquals(15, newBalance.getValue())
        assertEquals(10, balance.getValue()) // Original unchanged
    }

    @Test
    fun `add negative tokens throws exception`() {
        val balance = TokenBalance.create(10)
        assertThrows(IllegalArgumentException::class.java) {
            balance.add(-5)
        }
    }

    @Test
    fun `subtract tokens with sufficient balance succeeds`() {
        val balance = TokenBalance.create(20)
        val newBalance = balance.subtract(15)
        assertEquals(5, newBalance.getValue())
        assertEquals(20, balance.getValue()) // Original unchanged
    }

    @Test
    fun `subtract exact balance amount succeeds`() {
        val balance = TokenBalance.create(20)
        val newBalance = balance.subtract(20)
        assertEquals(0, newBalance.getValue())
    }

    @Test
    fun `subtract more tokens than available throws exception`() {
        val balance = TokenBalance.create(10)
        assertThrows(IllegalArgumentException::class.java) {
            balance.subtract(15)
        }
    }

    @Test
    fun `subtract negative tokens throws exception`() {
        val balance = TokenBalance.create(10)
        assertThrows(IllegalArgumentException::class.java) {
            balance.subtract(-5)
        }
    }

    @Test
    fun `adminSubtract allows negative balance`() {
        val balance = TokenBalance.create(5)
        val newBalance = balance.adminSubtract(10)
        assertEquals(-5, newBalance.getValue())
        assertEquals(5, balance.getValue()) // Original unchanged
    }

    @Test
    fun `adminSubtract with sufficient balance works normally`() {
        val balance = TokenBalance.create(20)
        val newBalance = balance.adminSubtract(10)
        assertEquals(10, newBalance.getValue())
    }

    @Test
    fun `adminSubtract negative tokens throws exception`() {
        val balance = TokenBalance.create(10)
        assertThrows(IllegalArgumentException::class.java) {
            balance.adminSubtract(-5)
        }
    }

    @Test
    fun `canAfford returns true when balance is sufficient`() {
        val balance = TokenBalance.create(30)
        assertTrue(balance.canAfford(25))
        assertTrue(balance.canAfford(30))
    }

    @Test
    fun `canAfford returns false when balance is insufficient`() {
        val balance = TokenBalance.create(30)
        assertTrue(!balance.canAfford(35))
    }

    @Test
    fun `negative balance canAfford returns false for positive costs`() {
        val balance = TokenBalance.createAdmin(-10)
        assertTrue(!balance.canAfford(5))
        assertTrue(!balance.canAfford(0))
    }

    @Test
    fun `toString returns correct format`() {
        val positiveBalance = TokenBalance.create(25)
        assertEquals("25 tokens", positiveBalance.toString())

        val negativeBalance = TokenBalance.createAdmin(-10)
        assertEquals("-10 tokens", negativeBalance.toString())

        val zeroBalance = TokenBalance.zero()
        assertEquals("0 tokens", zeroBalance.toString())
    }
}
