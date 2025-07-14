package com.arthurslife.app.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case specifically for changing user avatars.
 *
 * This use case handles both predefined avatar selection and custom image uploads,
 * ensuring proper validation and storage of avatar data.
 *
 * @property userRepository Repository for user data operations
 */
class ChangeUserAvatarUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * Changes user avatar to a predefined option.
     *
     * @param userId ID of the user
     * @param avatarId ID of the predefined avatar
     * @return Flow emitting the result of the avatar change
     */
    fun changeToPredefinedAvatar(
        userId: String,
        avatarId: String,
    ): Flow<Result<User>> = flow {
        try {
            validatePredefinedAvatarId(avatarId)

            val result = updateUserAvatar(
                userId = userId,
                avatarType = AvatarType.PREDEFINED,
                avatarData = avatarId,
            )

            emit(result)
        } catch (e: AvatarChangeException) {
            emit(Result.failure(e))
        } catch (e: IllegalArgumentException) {
            emit(Result.failure(AvatarChangeException("Invalid avatar data: ${e.message}")))
        }
    }

    /**
     * Changes user avatar to a custom uploaded image.
     *
     * @param userId ID of the user
     * @param imageData Base64 encoded image data
     * @return Flow emitting the result of the avatar change
     */
    fun changeToCustomAvatar(
        userId: String,
        imageData: String,
    ): Flow<Result<User>> = flow {
        try {
            validateCustomImageData(imageData)

            val result = updateUserAvatar(
                userId = userId,
                avatarType = AvatarType.CUSTOM,
                avatarData = imageData,
            )

            emit(result)
        } catch (e: AvatarChangeException) {
            emit(Result.failure(e))
        } catch (e: IllegalArgumentException) {
            emit(Result.failure(AvatarChangeException("Invalid image data: ${e.message}")))
        }
    }

    private suspend fun updateUserAvatar(
        userId: String,
        avatarType: AvatarType,
        avatarData: String,
    ): Result<User> {
        return try {
            val currentUser = userRepository.getUserById(userId)
                ?: throw AvatarChangeException("User not found with ID: $userId")

            val updatedUser = currentUser.copy(
                avatarType = avatarType,
                avatarData = avatarData,
            )

            userRepository.updateUser(updatedUser)
            Result.success(updatedUser)
        } catch (e: AvatarChangeException) {
            Result.failure(e)
        } catch (e: UserRepositoryException) {
            Result.failure(AvatarChangeException("Database error: ${e.message}"))
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.failure(AvatarChangeException("Unexpected error: ${e.message}"))
        }
    }

    private fun validatePredefinedAvatarId(avatarId: String) {
        if (avatarId.isBlank()) {
            throw AvatarChangeException("Avatar ID cannot be blank")
        }

        if (!PREDEFINED_AVATARS.contains(avatarId)) {
            throw AvatarChangeException("Invalid predefined avatar ID: $avatarId")
        }
    }

    private fun validateCustomImageData(imageData: String) {
        validateImageFormat(imageData)
        validateImageSize(imageData)
    }

    private fun validateImageFormat(imageData: String) {
        if (imageData.isBlank()) {
            throw AvatarChangeException("Image data cannot be blank")
        }
        if (!imageData.startsWith("data:image/") || !imageData.contains("base64,")) {
            throw AvatarChangeException("Image data must be a valid base64 data URL")
        }
    }

    private fun validateImageSize(imageData: String) {
        val base64Data = imageData.substringAfter("base64,")
        val estimatedSize = (base64Data.length * BASE64_SIZE_MULTIPLIER) / BASE64_SIZE_DIVISOR

        if (estimatedSize > MAX_IMAGE_SIZE_BYTES) {
            throw AvatarChangeException(
                "Image size too large. Maximum size: ${MAX_IMAGE_SIZE_BYTES / BYTES_TO_KB}KB",
            )
        }
    }

    companion object {
        private const val MAX_IMAGE_SIZE_BYTES = 1024 * 1024 // 1MB
        private const val BASE64_SIZE_MULTIPLIER = 3
        private const val BASE64_SIZE_DIVISOR = 4
        private const val BYTES_TO_KB = 1024

        val PREDEFINED_AVATARS = setOf(
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
    }
}

/**
 * Exception thrown when avatar change operations fail.
 */
class AvatarChangeException(message: String) : Exception(message)
