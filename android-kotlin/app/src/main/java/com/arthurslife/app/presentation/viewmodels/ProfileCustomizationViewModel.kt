package com.arthurslife.app.presentation.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.ChangeUserAvatarUseCase
import com.arthurslife.app.domain.user.ChangeUserPinUseCase
import com.arthurslife.app.domain.user.ProfileImageRepository
import com.arthurslife.app.domain.user.UpdateUserProfileUseCase
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRepositoryException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * User profile use cases grouped for better parameter management.
 */
data class UserProfileUseCases(
    val updateUserProfileUseCase: UpdateUserProfileUseCase,
    val changeUserAvatarUseCase: ChangeUserAvatarUseCase,
    val changeUserPinUseCase: ChangeUserPinUseCase,
)

/**
 * ViewModel for managing profile customization operations.
 *
 * This ViewModel handles all profile-related operations including avatar changes,
 * display name updates, PIN changes, and favorite color selection. It maintains
 * the UI state and coordinates with the domain layer for business logic.
 */
@HiltViewModel
class ProfileCustomizationViewModel @Inject constructor(
    private val useCases: UserProfileUseCases,
    private val profileImageRepository: ProfileImageRepository,
    private val userRepository: UserRepository,
    private val authenticationSessionService: com.arthurslife.app.domain.auth.AuthenticationSessionService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileCustomizationUiState())
    val uiState: StateFlow<ProfileCustomizationUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * Loads user data for profile customization.
     *
     * @param userId ID of the user to load, or "current_user" for authenticated user
     */
    fun loadUser(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val user = if (userId == "current_user") {
                    // Handle special case for current authenticated user
                    authenticationSessionService.getCurrentUser()
                } else {
                    // Handle specific user ID
                    userRepository.getUserById(userId)
                }

                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        displayName = user.displayName ?: "",
                        selectedColor = user.favoriteColor,
                        avatarType = user.avatarType,
                        avatarData = user.avatarData,
                    )

                    // Load profile image if it's custom
                    if (user.avatarType == AvatarType.CUSTOM) {
                        loadProfileImage(user.id, user.avatarData)
                    }
                } else {
                    val errorMessage = if (userId == "current_user") {
                        "No authenticated user found"
                    } else {
                        "User not found"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage,
                    )
                }
            } catch (e: IllegalArgumentException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid user data: ${e.message}",
                )
            } catch (e: UserRepositoryException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load user",
                )
            }
        }
    }

    /**
     * Updates the user's display name.
     *
     * @param displayName New display name
     */
    fun updateDisplayName(displayName: String) {
        val user = _currentUser.value ?: return

        useCases.updateUserProfileUseCase(
            userId = user.id,
            displayName = displayName.ifBlank { null },
        ).onEach { result ->
            result.onSuccess { updatedUser ->
                _currentUser.value = updatedUser
                _uiState.value = _uiState.value.copy(
                    displayName = displayName,
                    isSaving = false,
                    error = null,
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Failed to update display name",
                )
            }
        }.catch { error ->
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                error = error.message ?: "Failed to update display name",
            )
        }.launchIn(viewModelScope)
    }

    /**
     * Updates the user's favorite color.
     *
     * @param color Hex color string (#RRGGBB)
     */
    fun updateFavoriteColor(color: String?) {
        val user = _currentUser.value ?: return

        _uiState.value = _uiState.value.copy(isSaving = true)

        useCases.updateUserProfileUseCase(
            userId = user.id,
            favoriteColor = color,
        ).onEach { result ->
            result.onSuccess { updatedUser ->
                _currentUser.value = updatedUser
                _uiState.value = _uiState.value.copy(
                    selectedColor = color,
                    isSaving = false,
                    error = null,
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Failed to update favorite color",
                )
            }
        }.catch { error ->
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                error = error.message ?: "Failed to update favorite color",
            )
        }.launchIn(viewModelScope)
    }

    /**
     * Changes the user's avatar to a predefined option.
     *
     * @param avatarId ID of the predefined avatar
     */
    fun selectPredefinedAvatar(avatarId: String) {
        val user = _currentUser.value ?: return

        _uiState.value = _uiState.value.copy(isSaving = true)

        useCases.changeUserAvatarUseCase.changeToPredefinedAvatar(user.id, avatarId)
            .onEach { result ->
                result.onSuccess { updatedUser ->
                    _currentUser.value = updatedUser
                    _uiState.value = _uiState.value.copy(
                        avatarType = AvatarType.PREDEFINED,
                        avatarData = avatarId,
                        profileImage = null, // Clear custom image
                        isSaving = false,
                        error = null,
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = error.message ?: "Failed to change avatar",
                    )
                }
            }.catch { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Failed to change avatar",
                )
            }.launchIn(viewModelScope)
    }

    /**
     * Changes the user's avatar to a custom uploaded image.
     *
     * @param image Bitmap image to use as avatar
     */
    fun selectCustomAvatar(image: Bitmap) {
        val user = _currentUser.value ?: return

        _uiState.value = _uiState.value.copy(isSaving = true)

        viewModelScope.launch {
            try {
                // Store the image and get base64 data
                val imageData = profileImageRepository.storeImage(user.id, image)

                // Update user with custom avatar
                useCases.changeUserAvatarUseCase.changeToCustomAvatar(user.id, imageData)
                    .onEach { result ->
                        result.onSuccess { updatedUser ->
                            _currentUser.value = updatedUser
                            _uiState.value = _uiState.value.copy(
                                avatarType = AvatarType.CUSTOM,
                                avatarData = imageData,
                                profileImage = image,
                                isSaving = false,
                                error = null,
                            )
                        }.onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                error = error.message ?: "Failed to save custom avatar",
                            )
                        }
                    }.catch { error ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = error.message ?: "Failed to save custom avatar",
                        )
                    }.launchIn(viewModelScope)
            } catch (e: SecurityException) {
                Timber.e(e, "Permission denied during custom avatar selection")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Permission denied: ${e.message}",
                )
            } catch (e: OutOfMemoryError) {
                Timber.e(e, "Image too large to process for custom avatar")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Image too large to process",
                )
            } catch (e: IOException) {
                Timber.e(e, "Failed to process image for custom avatar")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to process image: ${e.message}",
                )
            }
        }
    }

    /**
     * Changes the user's PIN.
     *
     * @param currentPin Current PIN (if user has one)
     * @param newPin New PIN to set
     */
    fun changePin(currentPin: String?, newPin: String) {
        val user = _currentUser.value ?: return

        _uiState.value = _uiState.value.copy(isSaving = true)

        useCases.changeUserPinUseCase(user.id, currentPin, newPin)
            .onEach { result ->
                result.onSuccess { updatedUser ->
                    _currentUser.value = updatedUser
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = null,
                        pinChangeSuccess = true,
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = error.message ?: "Failed to change PIN",
                    )
                }
            }.catch { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Failed to change PIN",
                )
            }.launchIn(viewModelScope)
    }

    /**
     * Removes the user's PIN.
     *
     * @param currentPin Current PIN for verification
     */
    fun removePin(currentPin: String) {
        val user = _currentUser.value ?: return

        _uiState.value = _uiState.value.copy(isSaving = true)

        useCases.changeUserPinUseCase.removePin(user.id, currentPin)
            .onEach { result ->
                result.onSuccess { updatedUser ->
                    _currentUser.value = updatedUser
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = null,
                        pinChangeSuccess = true,
                    )
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = error.message ?: "Failed to remove PIN",
                    )
                }
            }.catch { error ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = error.message ?: "Failed to remove PIN",
                )
            }.launchIn(viewModelScope)
    }

    private suspend fun loadProfileImage(userId: String, base64Data: String) {
        try {
            val bitmap = profileImageRepository.getImage(userId, base64Data)
            _uiState.value = _uiState.value.copy(profileImage = bitmap)
        } catch (e: OutOfMemoryError) {
            Timber.e(e, "Image too large to load")
            _uiState.value = _uiState.value.copy(
                error = "Image too large to load",
            )
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Invalid image data")
            _uiState.value = _uiState.value.copy(
                error = "Invalid image data",
            )
        }
    }

    /**
     * Clears any error messages.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Clears the PIN change success flag.
     */
    fun clearPinChangeSuccess() {
        _uiState.value = _uiState.value.copy(pinChangeSuccess = false)
    }

    /**
     * Updates the display name in the UI state without saving.
     */
    fun updateDisplayNameLocal(displayName: String) {
        _uiState.value = _uiState.value.copy(displayName = displayName)
    }
}

/**
 * UI state for profile customization screen.
 */
data class ProfileCustomizationUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val displayName: String = "",
    val selectedColor: String? = null,
    val avatarType: AvatarType = AvatarType.PREDEFINED,
    val avatarData: String = "",
    val profileImage: Bitmap? = null,
    val pinChangeSuccess: Boolean = false,
)
