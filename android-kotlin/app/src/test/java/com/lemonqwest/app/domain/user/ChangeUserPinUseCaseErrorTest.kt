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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import kotlin.OptIn

/**
 * Focused test suite for ChangeUserPinUseCase error handling scenarios.
 *
 * Tests cover:
 * - IllegalArgumentException handling
 * - Repository errors during PIN change
 * - Repository errors during PIN removal
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("ChangeUserPinUseCase Error Tests")
@Execution(ExecutionMode.SAME_THREAD)
class ChangeUserPinUseCaseErrorTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: ChangeUserPinUseCase

    private val testUserId = "user123"
    private val testCurrentPin = "9876" // Secure PIN, not in weak list
    private val testNewPin = "4782" // Secure PIN, not sequential or weak

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        clearMocks(userRepository)
        useCase = ChangeUserPinUseCase(userRepository)
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

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
