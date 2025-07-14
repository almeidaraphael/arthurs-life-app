package com.arthurslife.app.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.auth.PIN
import com.arthurslife.app.domain.user.AvatarType
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    var uiState by mutableStateOf(OnboardingUiState())
        private set

    fun updateCaregiverName(name: String) {
        uiState = uiState.copy(caregiverName = name)
    }

    fun updateCaregiverAvatar(avatarType: AvatarType, avatarData: String) {
        uiState = uiState.copy(
            caregiverAvatarType = avatarType,
            caregiverAvatarData = avatarData,
        )
    }

    fun updateCaregiverPin(pin: String) {
        uiState = uiState.copy(caregiverPin = pin)
    }

    fun addChild(name: String, avatarType: AvatarType, avatarData: String) {
        val child = ChildSetupData(
            name = name,
            avatarType = avatarType,
            avatarData = avatarData,
        )
        uiState = uiState.copy(children = uiState.children + child)
    }

    fun removeChild(index: Int) {
        val updatedChildren = uiState.children.toMutableList()
        if (index in updatedChildren.indices) {
            updatedChildren.removeAt(index)
            uiState = uiState.copy(children = updatedChildren)
        }
    }

    fun updateChildName(index: Int, name: String) {
        val updatedChildren = uiState.children.toMutableList()
        if (index in updatedChildren.indices) {
            updatedChildren[index] = updatedChildren[index].copy(name = name)
            uiState = uiState.copy(children = updatedChildren)
        }
    }

    fun updateChildAvatar(index: Int, avatarType: AvatarType, avatarData: String) {
        val updatedChildren = uiState.children.toMutableList()
        if (index in updatedChildren.indices) {
            updatedChildren[index] = updatedChildren[index].copy(
                avatarType = avatarType,
                avatarData = avatarData,
            )
            uiState = uiState.copy(children = updatedChildren)
        }
    }

    fun validateCaregiverSetup(): Boolean {
        return uiState.caregiverName.isNotBlank() &&
            uiState.caregiverPin.length >= MIN_PIN_LENGTH
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, error = null)
                val caregiver = createCaregiverUser()
                val children = createChildUsers()
                saveAllUsers(caregiver, children)
                uiState = uiState.copy(isLoading = false, isCompleted = true)
            } catch (e: IllegalStateException) {
                handleOnboardingError("Failed to create family: ${e.message}", e)
            } catch (e: SecurityException) {
                handleOnboardingError("Failed to create family: Security error", e)
            } catch (e: IllegalArgumentException) {
                handleOnboardingError("Failed to create family: ${e.message}", e)
            }
        }
    }

    private suspend fun saveAllUsers(caregiver: User, children: List<User>) {
        val allUsers = listOf(caregiver) + children
        Log.i(
            TAG,
            "Saving ${allUsers.size} users: ${allUsers.map { "${it.name} (${it.role})" }}",
        )
        userRepository.saveUsers(allUsers)
        Log.i(TAG, "Successfully saved all users")
        verifyUsersSaved()
    }

    private suspend fun verifyUsersSaved() {
        val savedUsers = userRepository.getAllUsers()
        Log.i(TAG, "Verification: Found ${savedUsers.size} users in database after save")
        savedUsers.forEach { user ->
            Log.d(TAG, "Saved user: ${user.name} (${user.role}) - ID: ${user.id}")
        }
    }

    private fun createCaregiverUser(): User {
        val caregiverPin = if (uiState.caregiverPin.isNotBlank()) {
            PIN.create(uiState.caregiverPin)
        } else {
            null
        }

        return User(
            name = uiState.caregiverName,
            role = UserRole.CAREGIVER,
            tokenBalance = TokenBalance.zero(),
            pin = caregiverPin,
            avatarType = uiState.caregiverAvatarType,
            avatarData = uiState.caregiverAvatarData,
        )
    }

    private fun createChildUsers(): List<User> {
        return uiState.children.map { childData ->
            User(
                name = childData.name,
                role = UserRole.CHILD,
                tokenBalance = TokenBalance.zero(),
                pin = null, // Children don't have PINs
                avatarType = childData.avatarType,
                avatarData = childData.avatarData,
            )
        }
    }

    private fun handleOnboardingError(message: String, exception: Exception) {
        Log.e(TAG, message, exception)
        uiState = uiState.copy(
            isLoading = false,
            error = message,
        )
    }

    fun clearError() {
        uiState = uiState.copy(error = null)
    }

    companion object {
        private const val TAG = "OnboardingViewModel"
        private const val MIN_PIN_LENGTH = 4
    }
}

data class OnboardingUiState(
    val caregiverName: String = "",
    val caregiverAvatarType: AvatarType = AvatarType.PREDEFINED,
    val caregiverAvatarData: String = "avatar_caregiver",
    val caregiverPin: String = "",
    val children: List<ChildSetupData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCompleted: Boolean = false,
)

data class ChildSetupData(
    val name: String,
    val avatarType: AvatarType,
    val avatarData: String,
)
