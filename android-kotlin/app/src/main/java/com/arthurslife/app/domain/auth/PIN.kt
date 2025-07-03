package com.arthurslife.app.domain.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PIN private constructor(
    private val hashedValue: String,
) {
    companion object {
        private const val BCRYPT_COST = 12
        private const val PIN_LENGTH = 4

        fun create(rawPin: String): PIN {
            require(rawPin.length == PIN_LENGTH) { "PIN must be exactly $PIN_LENGTH digits" }
            require(rawPin.all { it.isDigit() }) { "PIN must contain only digits" }

            val hashedPin = BCrypt.withDefaults().hashToString(BCRYPT_COST, rawPin.toCharArray())
            return PIN(hashedPin)
        }

        fun fromHash(hashedValue: String): PIN {
            require(hashedValue.isNotBlank()) { "Hashed PIN cannot be blank" }
            return PIN(hashedValue)
        }
    }

    fun verify(rawPin: String): Boolean = BCrypt.verifyer().verify(rawPin.toCharArray(), hashedValue).verified

    fun getHash(): String = hashedValue
}
