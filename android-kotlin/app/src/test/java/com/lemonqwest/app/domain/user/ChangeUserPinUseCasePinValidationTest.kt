package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

/**
 * Focused test suite for ChangeUserPinUseCase PIN validation rules.
 *
 * Tests cover:
 * - Current PIN validation
 * - New PIN format validation (length, digits-only)
 * - PIN security validation (weak PINs, patterns, repeated digits)
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("ChangeUserPinUseCase PIN Validation Tests")
@Execution(ExecutionMode.SAME_THREAD)
class ChangeUserPinUseCasePinValidationTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: ChangeUserPinUseCase

    private val testUserId = "user123"
    private val testCurrentPin = "9876" // Secure PIN, not in weak list
    private val testNewPin = "4782" // Secure PIN, not sequential or weak

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepository)
        useCase = ChangeUserPinUseCase(userRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
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
