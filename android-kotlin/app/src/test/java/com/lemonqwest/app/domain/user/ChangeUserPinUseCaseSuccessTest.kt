package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.OptIn
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("ChangeUserPinUseCase Success Tests")
class ChangeUserPinUseCaseSuccessTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var userRepository: UserRepository
    private lateinit var useCase: ChangeUserPinUseCase

    private val testUserId = "user123"
    private val testNewPin = "4782"

    @BeforeEach
    fun setUp() {
        userRepository = mockk()

        // Setup mocks with simple matchers
        coEvery { userRepository.getUserById(any()) } returns null
        coEvery { userRepository.updateUser(any()) } returns Unit

        useCase = ChangeUserPinUseCase(userRepository)
    }

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
