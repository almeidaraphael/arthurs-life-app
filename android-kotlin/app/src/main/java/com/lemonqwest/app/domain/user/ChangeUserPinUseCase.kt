package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.auth.PIN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for changing user PIN.
 *
 * This use case handles PIN changes for caregivers, ensuring proper validation
 * and secure handling of PIN data. Only caregivers are allowed to have PINs.
 *
 * @property userRepository Repository for user data operations
 */
class ChangeUserPinUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * Changes the PIN for a caregiver user.
     *
     * @param userId ID of the user (must be a caregiver)
     * @param currentPin Current PIN for verification (if user has one)
     * @param newPin New PIN to set
     * @return Flow emitting the result of the PIN change
     */
    operator fun invoke(
        userId: String,
        currentPin: String? = null,
        newPin: String,
    ): Flow<Result<User>> = flow {
        try {
            val user = userRepository.getUserById(userId)
                ?: throw PinChangeException("User not found with ID: $userId")

            // Validate that user is a caregiver
            if (user.role != UserRole.CAREGIVER) {
                throw PinChangeException("Only caregivers can have PINs")
            }

            // Verify current PIN if user already has one
            user.pin?.let { existingPin ->
                if (currentPin == null) {
                    throw PinChangeException("Current PIN is required to change PIN")
                }
                if (!existingPin.verify(currentPin)) {
                    throw PinChangeException("Current PIN is incorrect")
                }
            }

            // Validate new PIN
            validateNewPin(newPin)

            // Create new PIN and update user
            val newPinObject = PIN.create(newPin)
            val updatedUser = user.copy(pin = newPinObject)

            userRepository.updateUser(updatedUser)

            emit(Result.success(updatedUser))
        } catch (e: PinChangeException) {
            emit(Result.failure(e))
        } catch (e: IllegalArgumentException) {
            emit(Result.failure(PinChangeException("Invalid PIN data: ${e.message}")))
        }
    }

    /**
     * Removes the PIN from a caregiver user.
     *
     * @param userId ID of the user (must be a caregiver)
     * @param currentPin Current PIN for verification
     * @return Flow emitting the result of the PIN removal
     */
    fun removePin(
        userId: String,
        currentPin: String,
    ): Flow<Result<User>> = flow {
        try {
            val user = userRepository.getUserById(userId)
                ?: throw PinChangeException("User not found with ID: $userId")

            // Validate that user is a caregiver
            if (user.role != UserRole.CAREGIVER) {
                throw PinChangeException("Only caregivers can have PINs")
            }

            // Verify current PIN
            val existingPin = user.pin
                ?: throw PinChangeException("User does not have a PIN to remove")

            if (!existingPin.verify(currentPin)) {
                throw PinChangeException("Current PIN is incorrect")
            }

            // Remove PIN from user
            val updatedUser = user.copy(pin = null)
            userRepository.updateUser(updatedUser)

            emit(Result.success(updatedUser))
        } catch (e: PinChangeException) {
            emit(Result.failure(e))
        } catch (e: IllegalArgumentException) {
            emit(Result.failure(PinChangeException("Invalid PIN data: ${e.message}")))
        }
    }

    private fun validateNewPin(pin: String) {
        validatePinLength(pin)
        validatePinSecurity(pin)
    }

    private fun validatePinLength(pin: String) {
        if (pin.length < MIN_PIN_LENGTH) {
            throw PinChangeException("PIN must be at least $MIN_PIN_LENGTH digits")
        }
        if (pin.length > MAX_PIN_LENGTH) {
            throw PinChangeException("PIN cannot exceed $MAX_PIN_LENGTH digits")
        }
    }

    private fun validatePinSecurity(pin: String) {
        if (!pin.all { it.isDigit() }) {
            throw PinChangeException("PIN must contain only digits")
        }
        if (WEAK_PINS.contains(pin) || isSequentialPattern(pin) || isAllSameDigits(pin)) {
            throw PinChangeException("PIN is not secure. Please choose a different PIN")
        }
    }

    private fun isSequentialPattern(pin: String): Boolean {
        if (pin.length < MIN_PATTERN_LENGTH) return false

        for (i in 0 until pin.length - 2) {
            val first = pin[i].digitToInt()
            val second = pin[i + 1].digitToInt()
            val third = pin[i + 2].digitToInt()

            // Check ascending sequence
            if (second == first + 1 && third == second + 1) return true

            // Check descending sequence
            if (second == first - 1 && third == second - 1) return true
        }

        return false
    }

    private fun isAllSameDigits(pin: String): Boolean {
        return pin.all { it == pin.first() }
    }

    companion object {
        private const val MIN_PIN_LENGTH = 4
        private const val MAX_PIN_LENGTH = 8
        private const val MIN_PATTERN_LENGTH = 3

        private val WEAK_PINS = setOf(
            "1234", "4321", "1111", "2222", "3333", "4444", "5555",
            "6666", "7777", "8888", "9999", "0000", "1122", "2233",
            "3344", "4455", "5566", "6677", "7788", "8899", "9900",
            "0011", "1212", "2323", "3434", "4545", "5656", "6767",
            "7878", "8989", "9090", "0101", "1357", "2468", "9753",
            "8642", "0987", "6543", "7890", "0123",
        )
    }
}

/**
 * Exception thrown when PIN change operations fail.
 */
class PinChangeException(message: String) : Exception(message)
