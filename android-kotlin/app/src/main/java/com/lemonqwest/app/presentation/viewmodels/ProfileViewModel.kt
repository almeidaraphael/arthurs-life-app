package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Profile screen that manages user profile information display.
 *
 * This ViewModel exposes the selected user's profile information and handles
 * dynamic updates based on role selection. It provides a clean interface for
 * the Profile screen to observe user data changes.
 *
 * The ViewModel follows the StateFlow pattern for reactive UI updates and
 * maintains the selected user state that can be updated from the Role Selector.
 */
@Suppress("TooGenericExceptionCaught")
@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Sets the user whose profile should be displayed.
     *
     * This method is typically called when a user is selected from the Role Selector
     * screen. It updates the selected user state which triggers UI updates.
     *
     * @param user The user whose profile should be displayed
     */
    fun setSelectedUser(user: User) {
        _selectedUser.value = user
        _errorMessage.value = null
    }

    /**
     * Loads a user by ID and sets them as the selected user.
     *
     * This method is useful when you have a user ID but need to fetch the
     * complete user data from the repository.
     *
     * @param userId The ID of the user to load and display
     */
    fun loadUserById(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val user = userRepository.findById(userId)
                if (user != null) {
                    _selectedUser.value = user
                } else {
                    _errorMessage.value = "User not found"
                    _selectedUser.value = null
                }
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = "Failed to load user: ${e.message}"
                _selectedUser.value = null
            } catch (e: IllegalStateException) {
                _errorMessage.value = "Failed to load user: ${e.message}"
                _selectedUser.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user: ${e.message}"
                _selectedUser.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refreshes the currently selected user's data from the repository.
     *
     * This method re-fetches the user data to ensure the profile displays
     * the most up-to-date information, such as updated token balances or
     * profile changes.
     */
    fun refreshSelectedUser() {
        val currentUser = _selectedUser.value
        if (currentUser != null) {
            loadUserById(currentUser.id)
        }
    }

    /**
     * Clears the currently selected user.
     *
     * This method can be used when logging out or returning to a state
     * where no specific user profile should be displayed.
     */
    fun clearSelectedUser() {
        _selectedUser.value = null
        _errorMessage.value = null
    }

    /**
     * Clears any error messages.
     *
     * This method allows the UI to dismiss error states after they have
     * been displayed to the user.
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Gets the display name for the selected user.
     *
     * Returns the custom display name if set, otherwise falls back to the
     * regular name. Returns null if no user is selected.
     *
     * @return The display name for the selected user, or null if no user is selected
     */
    fun getDisplayName(): String? {
        val user = _selectedUser.value
        return user?.displayName ?: user?.name
    }

    /**
     * Gets the token balance for the selected user.
     *
     * Returns the current token balance as an integer, or null if no user
     * is selected. Useful for displaying token information in the UI.
     *
     * @return The token balance for the selected user, or null if no user is selected
     */
    fun getTokenBalance(): Int? {
        return _selectedUser.value?.tokenBalance?.getValue()
    }

    /**
     * Checks if the selected user has a PIN set.
     *
     * This is useful for determining whether to show PIN-related UI elements
     * such as PIN change options for caregivers.
     *
     * @return true if the selected user has a PIN set, false otherwise
     */
    fun hasPin(): Boolean {
        return _selectedUser.value?.pin != null
    }
}
