package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.auth.PIN
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for ChangeUserPinUseCase PIN removal functionality.
 *
 * Tests cover:
 * - Successful PIN removal
 * - User not found for removal
 * - Role validation for removal
 * - No PIN to remove scenarios
 * - Incorrect current PIN for removal
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("ChangeUserPinUseCase Removal Tests")
class ChangeUserPinUseCaseRemovalTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: ChangeUserPinUseCase

    private val testUserId = "user123"
    private val testCurrentPin = "9876" // Secure PIN, not in weak list

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepository)
        useCase = ChangeUserPinUseCase(userRepository)
        // Default stubs to prevent MockKExceptions for any un-stubbed calls
        io.mockk.coEvery { userRepository.getUserById(any()) } returns null
        io.mockk.coEvery { userRepository.updateUser(any()) } returns Unit
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

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
