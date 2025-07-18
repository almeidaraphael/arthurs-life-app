package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
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
import kotlin.OptIn
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("ChangeUserPinUseCase User Validation Tests")
@Execution(ExecutionMode.SAME_THREAD)
class ChangeUserPinUseCaseUserValidationTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: ChangeUserPinUseCase

    private val testUserId = "user123"
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
}
