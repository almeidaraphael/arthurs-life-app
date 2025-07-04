package com.arthurslife.app.domain.user

import kotlinx.serialization.Serializable

/**
 * Value object representing a user's digital token balance in Arthur's Life reward economy.
 *
 * This class implements the core token management business rules, ensuring tokens can never
 * be negative and all operations maintain data integrity. Tokens are earned through task
 * completion and spent on rewards, creating a gamified incentive system.
 *
 * The class uses an inline value class for performance while maintaining type safety,
 * preventing accidental mixing of token amounts with other integer values.
 *
 * @constructor Private constructor ensures tokens can only be created through factory methods
 * @property amount The number of tokens, guaranteed to be non-negative
 *
 * @sample
 * ```kotlin
 * // Create a token balance
 * val balance = TokenBalance.create(25)
 *
 * // Add tokens from task completion
 * val newBalance = balance.add(10) // 35 tokens
 *
 * // Check if user can afford a reward
 * if (balance.canAfford(20)) {
 *     val afterPurchase = balance.subtract(20) // 15 tokens
 * }
 * ```
 */
@Serializable
@JvmInline
value class TokenBalance private constructor(
    private val amount: Int,
) {
    companion object {
        /**
         * Creates a new TokenBalance with the specified amount.
         *
         * This factory method enforces the business rule that token balances
         * cannot be negative, throwing an exception if an invalid amount is provided.
         *
         * @param amount The initial token amount, must be non-negative
         * @return A new TokenBalance instance
         * @throws IllegalArgumentException if amount is negative
         *
         * @sample
         * ```kotlin
         * val balance = TokenBalance.create(50) // Valid
         * val invalid = TokenBalance.create(-10) // Throws exception
         * ```
         */
        fun create(amount: Int): TokenBalance {
            require(amount >= 0) { "Token balance cannot be negative" }
            return TokenBalance(amount)
        }

        /**
         * Creates a TokenBalance with zero tokens.
         *
         * Commonly used for new users or when initializing default states.
         *
         * @return A TokenBalance with 0 tokens
         */
        fun zero(): TokenBalance = TokenBalance(0)
    }

    /**
     * Returns the current token amount.
     *
     * @return The number of tokens as an integer
     */
    fun getValue(): Int = amount

    /**
     * Creates a new TokenBalance by adding the specified number of tokens.
     *
     * This method implements immutable value object principles - it returns a new
     * instance rather than modifying the existing balance. This ensures thread safety
     * and prevents accidental mutations.
     *
     * @param tokens Number of tokens to add, must be non-negative
     * @return A new TokenBalance with the increased amount
     * @throws IllegalArgumentException if tokens is negative
     *
     * @sample
     * ```kotlin
     * val original = TokenBalance.create(10)
     * val increased = original.add(5) // Returns new instance with 15 tokens
     * // original still has 10 tokens
     * ```
     */
    fun add(tokens: Int): TokenBalance {
        require(tokens >= 0) { "Cannot add negative tokens" }
        return TokenBalance(amount + tokens)
    }

    /**
     * Creates a new TokenBalance by subtracting the specified number of tokens.
     *
     * This method enforces business rules around token spending:
     * - Cannot subtract negative amounts (prevents accidental token addition)
     * - Cannot subtract more tokens than available (prevents overdraft)
     *
     * @param tokens Number of tokens to subtract, must be non-negative and <= current balance
     * @return A new TokenBalance with the decreased amount
     * @throws IllegalArgumentException if tokens is negative or exceeds current balance
     *
     * @sample
     * ```kotlin
     * val balance = TokenBalance.create(20)
     * val afterSpending = balance.subtract(15) // Returns new instance with 5 tokens
     * val overdraft = balance.subtract(25) // Throws exception - insufficient tokens
     * ```
     */
    fun subtract(tokens: Int): TokenBalance {
        require(tokens >= 0) { "Cannot subtract negative tokens" }
        require(tokens <= amount) { "Insufficient tokens. Available: $amount, Requested: $tokens" }
        return TokenBalance(amount - tokens)
    }

    /**
     * Checks if the current balance can afford a purchase of the specified cost.
     *
     * This method provides a safe way to validate purchases before attempting
     * to subtract tokens, preventing exceptions in business logic flows.
     *
     * @param cost The cost of the item or action in tokens
     * @return true if balance >= cost, false otherwise
     *
     * @sample
     * ```kotlin
     * val balance = TokenBalance.create(30)
     * val canBuyToy = balance.canAfford(25) // true
     * val canBuyExpensive = balance.canAfford(40) // false
     * ```
     */
    fun canAfford(cost: Int): Boolean = amount >= cost

    /**
     * Returns a human-readable string representation of the token balance.
     *
     * @return String in format "$amount tokens" for display purposes
     */
    override fun toString(): String = "$amount tokens"
}
