package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonqwest.app.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing dialog state across the application.
 *
 * This centralized system provides:
 * - State management for all top bar dialogs
 * - Centralized show/hide operations
 * - Type-safe dialog identification
 * - Automatic state cleanup
 *
 * Usage patterns:
 * - Call showDialog() to display specific dialogs
 * - Call hideDialog() to dismiss current dialog
 * - Subscribe to dialogState for UI updates
 * - Pass user data for dialogs requiring user context
 */
@HiltViewModel
class DialogManagementViewModel @Inject constructor() : ViewModel() {

    private val _dialogState = MutableStateFlow(DialogState())
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    /**
     * Shows the settings dialog from top navigation bar.
     */
    fun showSettingsDialog() {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(
                currentDialog = DialogType.SETTINGS,
                isVisible = true,
            )
        }
    }

    /**
     * Shows the user profile dialog with user data.
     */
    fun showUserProfileDialog(user: User) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(
                currentDialog = DialogType.USER_PROFILE,
                isVisible = true,
                selectedUser = user,
                editedName = user.displayName ?: user.name,
            )
        }
    }

    /**
     * Shows the user selector dialog with available users.
     */
    fun showUserSelectorDialog(users: List<User>, currentUserId: String) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(
                currentDialog = DialogType.USER_SELECTOR,
                isVisible = true,
                availableUsers = users,
                currentUserId = currentUserId,
            )
        }
    }

    /**
     * Shows the child selector dialog for caregivers.
     */
    fun showChildSelectorDialog(children: List<User>, selectedChildId: String?) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(
                currentDialog = DialogType.CHILD_SELECTOR,
                isVisible = true,
                availableUsers = children,
                selectedChildId = selectedChildId,
            )
        }
    }

    /**
     * Shows the language selector dialog.
     */
    fun showLanguageSelectorDialog(currentLanguage: String) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(
                currentDialog = DialogType.LANGUAGE_SELECTOR,
                isVisible = true,
                currentLanguage = currentLanguage,
            )
        }
    }

    /**
     * Shows the theme selector dialog.
     */
    fun showThemeSelectorDialog() {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(
                currentDialog = DialogType.THEME_SELECTOR,
                isVisible = true,
            )
        }
    }

    /**
     * Hides the currently visible dialog and cleans up state.
     */
    fun hideDialog() {
        viewModelScope.launch {
            _dialogState.value = DialogState()
        }
    }

    /**
     * Updates the edited name for user profile dialog.
     */
    fun updateEditedName(name: String) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(editedName = name)
        }
    }

    /**
     * Sets loading state for dialog operations.
     */
    fun setLoading(isLoading: Boolean) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(isLoading = isLoading)
        }
    }

    /**
     * Sets error message for dialog operations.
     */
    fun setError(message: String?) {
        viewModelScope.launch {
            _dialogState.value = _dialogState.value.copy(errorMessage = message)
        }
    }

    /**
     * Clears any error state.
     */
    fun clearError() {
        setError(null)
    }
}

/**
 * Represents the current state of dialog management system.
 */
data class DialogState(
    val currentDialog: DialogType? = null,
    val isVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedUser: User? = null,
    val editedName: String = "",
    val availableUsers: List<User> = emptyList(),
    val currentUserId: String = "",
    val selectedChildId: String? = null,
    val currentLanguage: String = "en-US",
)

/**
 * Enum representing all dialog types managed by the system.
 */
enum class DialogType {
    SETTINGS,
    USER_PROFILE,
    USER_SELECTOR,
    CHILD_SELECTOR,
    LANGUAGE_SELECTOR,
    THEME_SELECTOR,
}
