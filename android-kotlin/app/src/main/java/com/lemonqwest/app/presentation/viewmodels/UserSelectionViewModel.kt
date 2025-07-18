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
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the User Selection screen that manages user list display and user selection.
 *
 * This ViewModel exposes the list of all users in the family system and tracks the currently
 * selected user. It handles real-time updates from the repository and provides a clean
 * interface for the UI to observe user changes. Supports both initial login and in-app
 * user switching scenarios.
 *
 * The ViewModel follows the StateFlow pattern for reactive UI updates and uses Hilt for
 * dependency injection to maintain clean architecture separation.
 */
@Suppress("TooGenericExceptionCaught")
@HiltViewModel
class UserSelectionViewModel
@Inject
constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUsers()
    }

    /**
     * Loads all users from the repository and updates the users StateFlow.
     *
     * This method is called during initialization and can be called again to refresh
     * the user list. It handles loading states and error scenarios gracefully.
     */
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val userList = userRepository.getAllUsers()
                Timber.i("Loaded ${userList.size} users from repository")
                userList.forEachIndexed { index, user ->
                    Timber.d(
                        "User $index - ID: ${user.id}, Name: ${user.name}, Role: ${user.role}",
                    )
                }
                _users.value = userList

                // If no user is selected and we have users, don't auto-select
                // Let the user choose explicitly
                if (_selectedUser.value == null && userList.isNotEmpty()) {
                    // Keep selectedUser as null to force explicit selection
                }
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "IllegalArgumentException loading users")
                _errorMessage.value = "Failed to load users: ${e.message}"
                _users.value = emptyList()
            } catch (e: IllegalStateException) {
                Timber.e(e, "IllegalStateException loading users")
                _errorMessage.value = "Failed to load users: ${e.message}"
                _users.value = emptyList()
            } catch (e: Exception) {
                Timber.e(e, "Exception loading users")
                _errorMessage.value = "Failed to load users: ${e.message}"
                _users.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Selects a specific user.
     *
     * This method updates the selected user state and can be observed by other
     * components like the Profile screen to show the correct user information.
     *
     * @param user The user to select
     */
    fun selectUser(user: User) {
        _selectedUser.value = user
        Timber.i("User selected: ${user.name} (${user.role})")
    }

    /**
     * Clears the currently selected user.
     *
     * This method can be used when logging out or returning to an unselected state.
     */
    fun clearSelection() {
        _selectedUser.value = null
        Timber.i("User selection cleared")
    }

    /**
     * Refreshes the user list from the repository.
     *
     * This method can be called when the UI needs to force a refresh of the user data,
     * such as after user profile changes or when returning from other screens.
     */
    fun refreshUsers() {
        Timber.i("Refreshing user list")
        loadUsers()
    }

    /**
     * Clears any error messages.
     *
     * This method allows the UI to dismiss error states after they have been displayed.
     */
    fun clearError() {
        _errorMessage.value = null
        Timber.d("Error message cleared")
    }
}
