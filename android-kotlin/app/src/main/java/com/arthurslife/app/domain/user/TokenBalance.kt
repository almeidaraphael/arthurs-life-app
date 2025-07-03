package com.arthurslife.app.domain.user

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class TokenBalance private constructor(
    private val amount: Int,
) {
    companion object {
        fun create(amount: Int): TokenBalance {
            require(amount >= 0) { "Token balance cannot be negative" }
            return TokenBalance(amount)
        }

        fun zero(): TokenBalance = TokenBalance(0)
    }

    fun getValue(): Int = amount

    fun add(tokens: Int): TokenBalance {
        require(tokens >= 0) { "Cannot add negative tokens" }
        return TokenBalance(amount + tokens)
    }

    fun subtract(tokens: Int): TokenBalance {
        require(tokens >= 0) { "Cannot subtract negative tokens" }
        require(tokens <= amount) { "Insufficient tokens. Available: $amount, Requested: $tokens" }
        return TokenBalance(amount - tokens)
    }

    fun canAfford(cost: Int): Boolean = amount >= cost

    override fun toString(): String = "$amount tokens"
}
