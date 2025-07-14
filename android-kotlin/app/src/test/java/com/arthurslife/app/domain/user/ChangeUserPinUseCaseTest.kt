package com.arthurslife.app.domain.user

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.auth.PIN
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("ChangeUserPinUseCase Tests")
class ChangeUserPinUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: ChangeUserPinUseCase

    private val testUserId = "user123"
    private val testCurrentPin = "9876" // Secure PIN, not in weak list
    private val testNewPin = "4782" // Secure PIN, not sequential or weak

    @BeforeEach
    fun setup() {
        clearMocks(userRepository)
        useCase = ChangeUserPinUseCase(userRepository)
    }

    @Nested
    @DisplayName("PIN Change Success Tests")
    inner class PinChangeSuccessTests {

        @Test
        @DisplayName("should change PIN successfully for caregiver without existing PIN")
        fun shouldChangePinSuccessfullyForCaregiverWithoutExistingPin() = runTest {
            // Given
            val userSlot = slot<User>()
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                name = "John Doe",
                pin = null, // No existing PIN
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase(testUserId, currentPin = null, newPin = testNewPin).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertEquals(caregiver.id, updatedUser.id)
            assertEquals(caregiver.name, updatedUser.name)
            assertEquals(UserRole.CAREGIVER, updatedUser.role)
            assertTrue(updatedUser.pin?.verify(testNewPin) == true)

            coVerify { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("should change PIN successfully for caregiver with existing PIN")
        fun shouldChangePinSuccessfullyForCaregiverWithExistingPin() = runTest {
            // Given
            val userSlot = slot<User>()
            val existingPin = PIN.create(testCurrentPin)

            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                name = "John Doe",
                pin = existingPin,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase(
                testUserId,
                currentPin = testCurrentPin,
                newPin = testNewPin,
            ).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertTrue(updatedUser.pin?.verify(testNewPin) == true)

            coVerify { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("should create secure PIN with proper validation")
        fun shouldCreateSecurePinWithProperValidation() = runTest {
            // Given
            val userSlot = slot<User>()
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            val securePin = "4782" // Not in weak PIN list

            // When
            val result = useCase(testUserId, currentPin = null, newPin = securePin).first()

            // Then
            assertTrue(result.isSuccess)
            val updatedUser = userSlot.captured
            assertTrue(updatedUser.pin?.verify(securePin) == true)
        }
    }

    @Nested
    @DisplayName("PIN Change Validation Tests")
    inner class PinChangeValidationTests {

        @Test
        @DisplayName("should fail when user not found")
        fun shouldFailWhenUserNotFound() = runTest {
            // Given
            coEvery { userRepository.getUserById(testUserId) } returns null

            // When
            val result = useCase(testUserId, currentPin = null, newPin = testNewPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("User not found with ID: $testUserId", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when user is not a caregiver")
        fun shouldFailWhenUserIsNotCaregiver() = runTest {
            // Given
            val child = TestDataFactory.createChildUser(
                id = testUserId,
                name = "Alice",
            )

            coEvery { userRepository.getUserById(testUserId) } returns child

            // When
            val result = useCase(testUserId, currentPin = null, newPin = testNewPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("Only caregivers can have PINs", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when current PIN is required but not provided")
        fun shouldFailWhenCurrentPinRequiredButNotProvided() = runTest {
            // Given
            val existingPin = PIN.create(testCurrentPin)
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = existingPin,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            // When
            val result = useCase(testUserId, currentPin = null, newPin = testNewPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("Current PIN is required to change PIN", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when current PIN is incorrect")
        fun shouldFailWhenCurrentPinIsIncorrect() = runTest {
            // Given
            val existingPin = PIN.create("1357") // Different PIN so verification fails

            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = existingPin,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            // When
            val result = useCase(
                testUserId,
                currentPin = testCurrentPin,
                newPin = testNewPin,
            ).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("Current PIN is incorrect", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when new PIN is too short")
        fun shouldFailWhenNewPinIsTooShort() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            val shortPin = "12" // Less than 4 digits

            // When
            val result = useCase(testUserId, currentPin = null, newPin = shortPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertTrue(result.exceptionOrNull()?.message!!.contains("must be at least 4 digits"))
        }

        @Test
        @DisplayName("should fail when new PIN is too long")
        fun shouldFailWhenNewPinIsTooLong() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            val longPin = "123456789" // More than 8 digits

            // When
            val result = useCase(testUserId, currentPin = null, newPin = longPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertTrue(result.exceptionOrNull()?.message!!.contains("cannot exceed 8 digits"))
        }

        @Test
        @DisplayName("should fail when new PIN contains non-digits")
        fun shouldFailWhenNewPinContainsNonDigits() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            val alphaPin = "12ab"

            // When
            val result = useCase(testUserId, currentPin = null, newPin = alphaPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("PIN must contain only digits", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when new PIN is weak common PIN")
        fun shouldFailWhenNewPinIsWeakCommonPin() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            val weakPin = "1234" // Common weak PIN

            // When
            val result = useCase(testUserId, currentPin = null, newPin = weakPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals(
                "PIN is not secure. Please choose a different PIN",
                result.exceptionOrNull()?.message,
            )
        }

        @Test
        @DisplayName("should fail when new PIN has sequential pattern")
        fun shouldFailWhenNewPinHasSequentialPattern() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            val sequentialPin = "3456" // Sequential ascending

            // When
            val result = useCase(testUserId, currentPin = null, newPin = sequentialPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals(
                "PIN is not secure. Please choose a different PIN",
                result.exceptionOrNull()?.message,
            )
        }

        @Test
        @DisplayName("should fail when new PIN has all same digits")
        fun shouldFailWhenNewPinHasAllSameDigits() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            val repeatingPin = "7777" // All same digits

            // When
            val result = useCase(testUserId, currentPin = null, newPin = repeatingPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals(
                "PIN is not secure. Please choose a different PIN",
                result.exceptionOrNull()?.message,
            )
        }
    }

    @Nested
    @DisplayName("PIN Removal Tests")
    inner class PinRemovalTests {

        @Test
        @DisplayName("should remove PIN successfully")
        fun shouldRemovePinSuccessfully() = runTest {
            // Given
            val userSlot = slot<User>()
            val existingPin = PIN.create(testCurrentPin)

            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = existingPin,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase.removePin(testUserId, testCurrentPin).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertNull(updatedUser.pin)

            coVerify { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("should fail to remove PIN when user not found")
        fun shouldFailToRemovePinWhenUserNotFound() = runTest {
            // Given
            coEvery { userRepository.getUserById(testUserId) } returns null

            // When
            val result = useCase.removePin(testUserId, testCurrentPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("User not found with ID: $testUserId", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail to remove PIN when user is not caregiver")
        fun shouldFailToRemovePinWhenUserIsNotCaregiver() = runTest {
            // Given
            val child = TestDataFactory.createChildUser(id = testUserId)

            coEvery { userRepository.getUserById(testUserId) } returns child

            // When
            val result = useCase.removePin(testUserId, testCurrentPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("Only caregivers can have PINs", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail to remove PIN when user has no PIN")
        fun shouldFailToRemovePinWhenUserHasNoPin() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null, // No PIN to remove
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            // When
            val result = useCase.removePin(testUserId, testCurrentPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("User does not have a PIN to remove", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail to remove PIN when current PIN is incorrect")
        fun shouldFailToRemovePinWhenCurrentPinIsIncorrect() = runTest {
            // Given
            val existingPin = PIN.create("1357") // Different PIN so verification fails

            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = existingPin,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            // When
            val result = useCase.removePin(testUserId, testCurrentPin).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is PinChangeException)
            assertEquals("Current PIN is incorrect", result.exceptionOrNull()?.message)
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("should handle IllegalArgumentException from PIN creation")
        fun shouldHandleIllegalArgumentExceptionFromPinCreation() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver

            // This would cause PIN.create to throw IllegalArgumentException
            val invalidPin = "invalid"

            // When
            val result = useCase(testUserId, currentPin = null, newPin = invalidPin).first()

            // Then
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is PinChangeException)
            assertEquals("PIN must contain only digits", exception?.message)
        }

        @Test
        @DisplayName("should handle repository errors during PIN change")
        fun shouldHandleRepositoryErrorsDuringPinChange() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = null,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver
            coEvery { userRepository.updateUser(any()) } throws RuntimeException("Database error")

            // When/Then
            assertThrows<RuntimeException> {
                useCase(testUserId, currentPin = null, newPin = testNewPin).first()
            }
        }

        @Test
        @DisplayName("should handle repository errors during PIN removal")
        fun shouldHandleRepositoryErrorsDuringPinRemoval() = runTest {
            // Given
            val existingPin = PIN.create(testCurrentPin)

            val caregiver = TestDataFactory.createCaregiverUser(
                id = testUserId,
                pin = existingPin,
            )

            coEvery { userRepository.getUserById(testUserId) } returns caregiver
            coEvery { userRepository.updateUser(any()) } throws RuntimeException("Database error")

            // When/Then
            assertThrows<RuntimeException> {
                useCase.removePin(testUserId, testCurrentPin).first()
            }
        }
    }
}
