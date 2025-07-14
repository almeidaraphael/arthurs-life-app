package com.arthurslife.app.domain.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for updating user profile information including display name, avatar, and favorite color.
 *
 * This use case handles the business logic for profile updates while maintaining data integrity
 * and validation. It ensures that profile changes are persisted correctly and notifies
 * observers of the updates.
 *
 * @property userRepository Repository for user data operations
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    /**
     * Updates user profile with the provided information.
     *
     * @param userId ID of the user to update
     * @param displayName New display name (optional)
     * @param avatarType Type of avatar being set
     * @param avatarData Avatar data (predefined ID or custom image data)
     * @param favoriteColor User's favorite color (optional)
     * @return Flow emitting the result of the update operation
     */
    operator fun invoke(
        userId: String,
        displayName: String? = null,
        avatarType: AvatarType? = null,
        avatarData: String? = null,
        favoriteColor: String? = null,
    ): Flow<Result<User>> = flow {
        try {
            // Get current user
            val currentUser = userRepository.getUserById(userId)
                ?: throw ProfileUpdateException("User not found with ID: $userId")

            // Create updated user with new profile data
            val updatedUser = currentUser.copy(
                displayName = displayName ?: currentUser.displayName,
                avatarType = avatarType ?: currentUser.avatarType,
                avatarData = avatarData ?: currentUser.avatarData,
                favoriteColor = favoriteColor ?: currentUser.favoriteColor,
            )

            // Validate the updated user data
            validateProfileUpdate(updatedUser)

            // Save the updated user
            userRepository.updateUser(updatedUser)

            emit(Result.success(updatedUser))
        } catch (e: ProfileUpdateException) {
            emit(Result.failure(e))
        } catch (e: IllegalArgumentException) {
            emit(Result.failure(ProfileUpdateException("Invalid profile data: ${e.message}")))
        } catch (e: UserRepositoryException) {
            emit(Result.failure(ProfileUpdateException("Failed to update profile: ${e.message}")))
        }
    }

    private fun validateProfileUpdate(user: User) {
        validateDisplayName(user.displayName)
        validateAvatarData(user.avatarType, user.avatarData)
        validateFavoriteColor(user.favoriteColor)
    }

    private fun validateDisplayName(displayName: String?) {
        displayName?.let { name ->
            if (name.isBlank()) {
                throw ProfileUpdateException("Display name cannot be blank")
            }
            if (name.length > MAX_DISPLAY_NAME_LENGTH) {
                throw ProfileUpdateException(
                    "Display name cannot exceed $MAX_DISPLAY_NAME_LENGTH characters",
                )
            }
        }
    }

    private fun validateAvatarData(avatarType: AvatarType, avatarData: String) {
        validateAvatarType(avatarType, avatarData)
    }

    private fun validateAvatarType(avatarType: AvatarType, avatarData: String) {
        when (avatarType) {
            AvatarType.PREDEFINED -> validatePredefinedAvatar(avatarData)
            AvatarType.CUSTOM -> validateCustomAvatar(avatarData)
        }
    }

    private fun validatePredefinedAvatar(avatarData: String) {
        if (avatarData.isBlank()) {
            throw ProfileUpdateException("Predefined avatar ID cannot be blank")
        }
    }

    private fun validateCustomAvatar(avatarData: String) {
        if (avatarData.isBlank()) {
            throw ProfileUpdateException("Custom avatar data cannot be blank")
        }
        if (!isValidBase64Image(avatarData)) {
            throw ProfileUpdateException("Invalid custom avatar image format")
        }
    }

    private fun validateFavoriteColor(favoriteColor: String?) {
        favoriteColor?.let { color ->
            if (!isValidColorFormat(color)) {
                throw ProfileUpdateException("Invalid color format. Use hex format (#RRGGBB)")
            }
        }
    }

    private fun isValidBase64Image(data: String): Boolean {
        return try {
            // Basic validation - check if it starts with data:image/
            data.startsWith("data:image/") && data.contains("base64,")
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Invalid Base64 image format")
            false
        }
    }

    private fun isValidColorFormat(color: String): Boolean {
        return color.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))
    }

    companion object {
        private const val MAX_DISPLAY_NAME_LENGTH = 50
    }
}

/**
 * Exception thrown when profile update operations fail.
 */
class ProfileUpdateException(message: String) : Exception(message)
