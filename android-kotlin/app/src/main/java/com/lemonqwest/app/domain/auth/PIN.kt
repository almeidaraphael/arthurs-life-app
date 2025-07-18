package com.lemonqwest.app.domain.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlinx.serialization.Serializable

/**
 * Secure PIN value object for LemonQwest authentication system.
 *
 * This class implements secure PIN storage and verification using BCrypt hashing,
 * ensuring that raw PIN values are never stored in memory or database. The PIN
 * system provides child-friendly authentication without requiring complex passwords.
 *
 * PINs are restricted to exactly 4 digits for usability while maintaining
 * reasonable security for a family application context. The BCrypt implementation
 * provides salting and adaptive hashing to resist brute force attacks.
 *
 * @constructor Private constructor ensuring PINs can only be created through factory methods
 * @property hashedValue The BCrypt-hashed PIN value, never the raw PIN
 *
 * @sample
 * ```kotlin
 * // Create a new PIN
 * val pin = PIN.create("1234")
 *
 * // Verify a PIN attempt
 * if (pin.verify("1234")) {
 *     // Authentication successful
 * }
 *
 * // Restore PIN from stored hash
 * val storedPin = PIN.fromHash(savedHashFromDatabase)
 * ```
 */
@Serializable
@JvmInline
value class PIN private constructor(
    private val hashedValue: String,
) {
    companion object {
        /** BCrypt cost factor balancing security and performance for mobile devices */
        private const val BCRYPT_COST = 12

        /** Required PIN length for consistency and usability */
        private const val PIN_LENGTH = 4

        /**
         * Creates a new PIN from a raw PIN string.
         *
         * This factory method validates the PIN format (exactly 4 digits) and
         * immediately hashes it using BCrypt. The raw PIN is not stored and
         * should be cleared from memory as soon as possible after this call.
         *
         * @param rawPin The raw PIN string, must be exactly 4 digits
         * @return A new PIN instance with the hashed value
         * @throws IllegalArgumentException if PIN doesn't meet format requirements
         *
         * @sample
         * ```kotlin
         * val validPin = PIN.create("1234") // Valid
         * val invalidLength = PIN.create("12345") // Throws exception
         * val invalidChars = PIN.create("12ab") // Throws exception
         * ```
         */
        fun create(rawPin: String): PIN {
            require(rawPin.length == PIN_LENGTH) { "PIN must be exactly $PIN_LENGTH digits" }
            require(rawPin.all { it.isDigit() }) { "PIN must contain only digits" }

            val hashedPin = BCrypt.withDefaults().hashToString(BCRYPT_COST, rawPin.toCharArray())
            return PIN(hashedPin)
        }

        /**
         * Creates a PIN instance from an existing hash value.
         *
         * This factory method is used when loading PIN data from persistent storage.
         * It bypasses the hashing process since the value is already a BCrypt hash.
         * Used primarily for deserializing PIN data from database or preferences.
         *
         * @param hashedValue The BCrypt hash string from storage
         * @return A PIN instance ready for verification
         * @throws IllegalArgumentException if hashedValue is blank
         *
         * @sample
         * ```kotlin
         * val hashFromDb = "$2a$12$abc123..." // BCrypt hash from database
         * val pin = PIN.fromHash(hashFromDb)
         * ```
         */
        fun fromHash(hashedValue: String): PIN {
            require(hashedValue.isNotBlank()) { "Hashed PIN cannot be blank" }
            return PIN(hashedValue)
        }
    }

    /**
     * Verifies a raw PIN attempt against this PIN's stored hash.
     *
     * This method uses BCrypt's secure verification process, which includes
     * timing attack resistance and automatic salt handling. The verification
     * process is intentionally slow to prevent brute force attacks.
     *
     * @param rawPin The PIN attempt to verify
     * @return true if the raw PIN matches this PIN's hash, false otherwise
     *
     * @sample
     * ```kotlin
     * val pin = PIN.create("1234")
     * val isValid = pin.verify("1234") // true
     * val isInvalid = pin.verify("5678") // false
     * ```
     */
    fun verify(
        rawPin: String,
    ): Boolean = BCrypt.verifyer().verify(rawPin.toCharArray(), hashedValue).verified

    /**
     * Returns the BCrypt hash value for storage purposes.
     *
     * This method provides access to the hashed PIN value for persistence
     * to database or secure preferences. The returned hash is safe to store
     * as it cannot be reversed to obtain the original PIN.
     *
     * @return The BCrypt hash string suitable for storage
     *
     * @sample
     * ```kotlin
     * val pin = PIN.create("1234")
     * val hashToStore = pin.getHash() // "$2a$12$abc123..."
     * // Store hashToStore in database
     * ```
     */
    fun getHash(): String = hashedValue
}
