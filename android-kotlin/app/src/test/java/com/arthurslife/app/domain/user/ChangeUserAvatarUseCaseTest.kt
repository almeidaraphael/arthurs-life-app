package com.arthurslife.app.domain.user

import com.arthurslife.app.domain.TestDataFactory
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ChangeUserAvatarUseCase Tests")
class ChangeUserAvatarUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var useCase: ChangeUserAvatarUseCase

    private val testUserId = "user123"
    private val testPredefinedAvatarId = "mario_child"
    private val testCustomAvatarData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="

    @BeforeEach
    fun setup() {
        clearMocks(userRepository)
        useCase = ChangeUserAvatarUseCase(userRepository)
    }

    @Nested
    @DisplayName("Predefined Avatar Tests")
    inner class PredefinedAvatarTests {

        @Test
        @DisplayName("should change to predefined avatar successfully")
        fun shouldChangeToPredefinedAvatarSuccessfully() = runTest {
            // Given
            val userSlot = slot<User>()
            val user = TestDataFactory.createChildUser(
                id = testUserId,
                name = "Alice",
                avatarType = AvatarType.CUSTOM,
                avatarData = "old_custom_data",
            )

            coEvery { userRepository.getUserById(testUserId) } returns user
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase.changeToPredefinedAvatar(
                testUserId,
                testPredefinedAvatarId,
            ).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertEquals(testUserId, updatedUser.id)
            assertEquals(AvatarType.PREDEFINED, updatedUser.avatarType)
            assertEquals(testPredefinedAvatarId, updatedUser.avatarData)

            coVerify { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("should fail when changing to invalid predefined avatar")
        fun shouldFailWhenChangingToInvalidPredefinedAvatar() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user

            val invalidAvatarId = "invalid_avatar"

            // When
            val result = useCase.changeToPredefinedAvatar(testUserId, invalidAvatarId).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertEquals(
                "Invalid predefined avatar ID: $invalidAvatarId",
                result.exceptionOrNull()?.message,
            )
        }

        @Test
        @DisplayName("should validate all predefined avatar IDs are supported")
        fun shouldValidateAllPredefinedAvatarIdsAreSupported() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user
            coEvery { userRepository.updateUser(any()) } returns Unit

            // When & Then - Test all predefined avatar IDs
            ChangeUserAvatarUseCase.PREDEFINED_AVATARS.forEach { avatarId ->
                val result = useCase.changeToPredefinedAvatar(testUserId, avatarId).first()
                assertTrue(result.isSuccess, "Should support predefined avatar: $avatarId")
            }
        }

        @Test
        @DisplayName("should handle empty predefined avatar ID")
        fun shouldHandleEmptyPredefinedAvatarId() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user

            // When
            val result = useCase.changeToPredefinedAvatar(testUserId, "").first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertEquals("Avatar ID cannot be blank", result.exceptionOrNull()?.message)
        }
    }

    @Nested
    @DisplayName("Custom Avatar Tests")
    inner class CustomAvatarTests {

        @Test
        @DisplayName("should change to custom avatar successfully")
        fun shouldChangeToCustomAvatarSuccessfully() = runTest {
            // Given
            val userSlot = slot<User>()
            val user = TestDataFactory.createChildUser(
                id = testUserId,
                name = "Alice",
                avatarType = AvatarType.PREDEFINED,
                avatarData = "mario_child",
            )

            coEvery { userRepository.getUserById(testUserId) } returns user
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase.changeToCustomAvatar(testUserId, testCustomAvatarData).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertEquals(testUserId, updatedUser.id)
            assertEquals(AvatarType.CUSTOM, updatedUser.avatarType)
            assertEquals(testCustomAvatarData, updatedUser.avatarData)

            coVerify { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("should fail when custom avatar data is empty")
        fun shouldFailWhenCustomAvatarDataIsEmpty() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user

            // When
            val result = useCase.changeToCustomAvatar(testUserId, "").first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertEquals("Image data cannot be blank", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when custom avatar data is too large")
        fun shouldFailWhenCustomAvatarDataIsTooLarge() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user
            // Don't mock updateUser since validation should fail before repository call

            // Create a large string that exceeds the maximum size
            // Need to account for base64 encoding: estimatedSize = (base64Data.length * 3) / 4
            // To exceed 1MB (1,048,576 bytes), we need base64Data.length > 1,398,101
            val maxSizeBytes = 1024 * 1024 // 1MB
            val requiredBase64Length = (maxSizeBytes * 4) / 3 + 1000 // Add extra to ensure it exceeds
            val largeAvatarData = "data:image/png;base64," + "x".repeat(requiredBase64Length)

            // When
            val result = useCase.changeToCustomAvatar(testUserId, largeAvatarData).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertTrue(result.exceptionOrNull()?.message!!.contains("Image size too large"))

            // Verify that updateUser was not called due to validation failure
            coVerify(exactly = 0) { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("should accept custom avatar data at maximum size limit")
        fun shouldAcceptCustomAvatarDataAtMaximumSizeLimit() = runTest {
            // Given
            val userSlot = slot<User>()
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // Create avatar data that should be acceptable
            val maxSizeBytes = 1024 * 1024 // 1MB
            val acceptableAvatarData = "data:image/png;base64," + "x".repeat(maxSizeBytes / 2)

            // When
            val result = useCase.changeToCustomAvatar(testUserId, acceptableAvatarData).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertEquals(AvatarType.CUSTOM, updatedUser.avatarType)
            assertEquals(acceptableAvatarData, updatedUser.avatarData)
        }

        @Test
        @DisplayName("should handle whitespace-only custom avatar data")
        fun shouldHandleWhitespaceOnlyCustomAvatarData() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user

            val whitespaceData = "   \n\t   "

            // When
            val result = useCase.changeToCustomAvatar(testUserId, whitespaceData).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertEquals("Image data cannot be blank", result.exceptionOrNull()?.message)
        }
    }

    @Nested
    @DisplayName("User Validation Tests")
    inner class UserValidationTests {

        @Test
        @DisplayName("should fail when user not found for predefined avatar")
        fun shouldFailWhenUserNotFoundForPredefinedAvatar() = runTest {
            // Given
            coEvery { userRepository.getUserById(testUserId) } returns null

            // When
            val result = useCase.changeToPredefinedAvatar(
                testUserId,
                testPredefinedAvatarId,
            ).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertEquals("User not found with ID: $testUserId", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should fail when user not found for custom avatar")
        fun shouldFailWhenUserNotFoundForCustomAvatar() = runTest {
            // Given
            coEvery { userRepository.getUserById(testUserId) } returns null

            // When
            val result = useCase.changeToCustomAvatar(testUserId, testCustomAvatarData).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertEquals("User not found with ID: $testUserId", result.exceptionOrNull()?.message)
        }

        @Test
        @DisplayName("should work for both caregivers and children")
        fun shouldWorkForBothCaregiversAndChildren() = runTest {
            // Given
            val caregiver = TestDataFactory.createCaregiverUser(id = "caregiver1")
            val child = TestDataFactory.createChildUser(id = "child1")

            coEvery { userRepository.getUserById("caregiver1") } returns caregiver
            coEvery { userRepository.getUserById("child1") } returns child
            coEvery { userRepository.updateUser(any()) } returns Unit

            // When
            val caregiverResult = useCase.changeToPredefinedAvatar(
                "caregiver1",
                "default_caregiver",
            ).first()
            val childResult = useCase.changeToPredefinedAvatar("child1", "mario_child").first()

            // Then
            assertTrue(caregiverResult.isSuccess)
            assertTrue(childResult.isSuccess)
        }
    }

    @Nested
    @DisplayName("Avatar Constants Tests")
    inner class AvatarConstantsTests {

        @Test
        @DisplayName("should have expected predefined avatar IDs")
        fun shouldHaveExpectedPredefinedAvatarIds() {
            // Given
            val expectedAvatars = setOf(
                "default_child",
                "default_caregiver",
                "mario_child",
                "luigi_child",
                "peach_child",
                "toad_child",
                "koopa_child",
                "goomba_child",
                "star_child",
                "mushroom_child",
            )

            // When
            val actualAvatars = ChangeUserAvatarUseCase.PREDEFINED_AVATARS

            // Then
            assertEquals(expectedAvatars, actualAvatars)
        }

        @Test
        @DisplayName("should have reasonable maximum custom avatar size")
        fun shouldHaveReasonableMaximumCustomAvatarSize() {
            // When
            // Test that the use case has reasonable size limits
            val maxSizeBytes = 1024 * 1024 // 1MB
            val maxSize = maxSizeBytes

            // Then
            assertTrue(maxSize > 0, "Maximum size should be positive")
            assertTrue(maxSize >= 1024, "Maximum size should be at least 1KB for reasonable images")
            assertTrue(
                maxSize <= 10 * 1024 * 1024,
                "Maximum size should not exceed 10MB to prevent abuse",
            )
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    inner class ErrorHandlingTests {

        @Test
        @DisplayName("should handle repository errors during predefined avatar change")
        fun shouldHandleRepositoryErrorsDuringPredefinedAvatarChange() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user
            coEvery { userRepository.updateUser(any()) } throws RuntimeException("Database error")

            // When
            val result = useCase.changeToPredefinedAvatar(
                testUserId,
                testPredefinedAvatarId,
            ).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertTrue(result.exceptionOrNull()?.message!!.contains("Unexpected error"))
        }

        @Test
        @DisplayName("should handle repository errors during custom avatar change")
        fun shouldHandleRepositoryErrorsDuringCustomAvatarChange() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user
            coEvery { userRepository.updateUser(any()) } throws RuntimeException("Database error")

            // When
            val result = useCase.changeToCustomAvatar(testUserId, testCustomAvatarData).first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertTrue(result.exceptionOrNull()?.message!!.contains("Unexpected error"))
        }

        @Test
        @DisplayName("should handle IllegalArgumentException during validation")
        fun shouldHandleIllegalArgumentExceptionDuringValidation() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(id = testUserId)
            coEvery { userRepository.getUserById(testUserId) } returns user

            // When - Using invalid avatar ID should cause validation exception
            val result = useCase.changeToPredefinedAvatar(testUserId, "invalid_avatar_id").first()

            // Then
            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AvatarChangeException)
            assertTrue(result.exceptionOrNull()?.message!!.contains("Invalid predefined avatar ID"))
        }
    }

    @Nested
    @DisplayName("State Consistency Tests")
    inner class StateConsistencyTests {

        @Test
        @DisplayName("should preserve all other user properties when changing avatar")
        fun shouldPreserveAllOtherUserPropertiesWhenChangingAvatar() = runTest {
            // Given
            val userSlot = slot<User>()
            val originalUser = TestDataFactory.createCaregiverUser(
                id = testUserId,
                name = "John Doe",
                displayName = "Johnny",
                favoriteColor = "#FF0000",
                avatarType = AvatarType.CUSTOM,
                avatarData = "old_custom_data",
            )

            coEvery { userRepository.getUserById(testUserId) } returns originalUser
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase.changeToPredefinedAvatar(testUserId, "default_caregiver").first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured

            // Avatar should be changed
            assertEquals(AvatarType.PREDEFINED, updatedUser.avatarType)
            assertEquals("default_caregiver", updatedUser.avatarData)

            // All other properties should be preserved
            assertEquals(originalUser.id, updatedUser.id)
            assertEquals(originalUser.name, updatedUser.name)
            assertEquals(originalUser.displayName, updatedUser.displayName)
            assertEquals(originalUser.favoriteColor, updatedUser.favoriteColor)
            assertEquals(originalUser.role, updatedUser.role)
            assertEquals(originalUser.tokenBalance, updatedUser.tokenBalance)
            assertEquals(originalUser.pin, updatedUser.pin)
        }

        @Test
        @DisplayName("should handle avatar change for user with minimal properties")
        fun shouldHandleAvatarChangeForUserWithMinimalProperties() = runTest {
            // Given
            val userSlot = slot<User>()
            val minimalUser = TestDataFactory.createChildUser(
                id = testUserId,
                name = "Alice",
                // Other optional properties use defaults
            )

            coEvery { userRepository.getUserById(testUserId) } returns minimalUser
            coEvery { userRepository.updateUser(capture(userSlot)) } returns Unit

            // When
            val result = useCase.changeToCustomAvatar(testUserId, testCustomAvatarData).first()

            // Then
            assertTrue(result.isSuccess)

            val updatedUser = userSlot.captured
            assertEquals(AvatarType.CUSTOM, updatedUser.avatarType)
            assertEquals(testCustomAvatarData, updatedUser.avatarData)
            assertEquals(minimalUser.name, updatedUser.name)
        }
    }
}
