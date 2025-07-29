package com.arthurslife.app.presentation.theme.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.presentation.viewmodels.DialogManagementViewModel
import com.arthurslife.app.presentation.viewmodels.DialogType

/**
 * Centralized dialog manager that renders the appropriate dialog based on current state.
 *
 * This composable observes the DialogManagementViewModel and automatically renders
 * the correct dialog when one should be shown. It handles all dialog types used
 * in the top navigation bar system.
 *
 * @param theme Current app theme for consistent styling
 * @param onUserSelected Callback when a user is selected from user/child selector
 * @param onLanguageSelected Callback when a language is selected
 * @param onSaveProfile Callback when user profile is saved
 * @param onChangeAvatar Callback when user wants to change avatar
 * @param onChangePIN Callback when user wants to change PIN
 * @param onSwitchUsers Callback when user wants to switch users
 * @param onThemeClick Callback when user wants to change theme
 * @param viewModel Dialog management ViewModel
 */
@Composable
fun dialogManager(
    theme: BaseAppTheme,
    onUserSelected: (com.arthurslife.app.domain.user.User) -> Unit = {},
    onLanguageSelected: (String) -> Unit = {},
    onSaveProfile: () -> Unit = {},
    onChangeAvatar: () -> Unit = {},
    onChangePIN: () -> Unit = {},
    onSwitchUsers: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onThemeSelected: (com.arthurslife.app.presentation.theme.BaseAppTheme) -> Unit = {},
    viewModel: DialogManagementViewModel = hiltViewModel(),
) {
    val dialogState by viewModel.dialogState.collectAsState()

    if (dialogState.isVisible && dialogState.currentDialog != null) {
        val currentDialog = dialogState.currentDialog!!
        renderCurrentDialog(
            dialogType = currentDialog,
            dialogState = dialogState,
            viewModel = viewModel,
            theme = theme,
            callbacks = DialogCallbacks(
                onUserSelected = onUserSelected,
                onLanguageSelected = onLanguageSelected,
                onSaveProfile = onSaveProfile,
                onChangeAvatar = onChangeAvatar,
                onChangePIN = onChangePIN,
                onSwitchUsers = onSwitchUsers,
                onThemeClick = onThemeClick,
                onThemeSelected = onThemeSelected,
            ),
        )
    }
}

/**
 * Extension function to show settings dialog from any composable.
 */
@Composable
fun DialogManagementViewModel.showSettings() {
    showSettingsDialog()
}

/**
 * Extension function to show user profile dialog from any composable.
 */
@Composable
fun DialogManagementViewModel.showProfile(user: com.arthurslife.app.domain.user.User) {
    showUserProfileDialog(user)
}

/**
 * Extension function to show user selector dialog from any composable.
 */
@Composable
fun DialogManagementViewModel.showUserSelector(
    users: List<com.arthurslife.app.domain.user.User>,
    currentUserId: String,
) {
    showUserSelectorDialog(users, currentUserId)
}

/**
 * Extension function to show child selector dialog from any composable.
 */
@Composable
fun DialogManagementViewModel.showChildSelector(
    children: List<com.arthurslife.app.domain.user.User>,
    selectedChildId: String?,
) {
    showChildSelectorDialog(children, selectedChildId)
}

/**
 * Extension function to show language selector dialog from any composable.
 */
@Composable
fun DialogManagementViewModel.showLanguage(currentLanguage: String) {
    showLanguageSelectorDialog(currentLanguage)
}

@Composable
private fun themeSelectorDialogContent(
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    onThemeSelected: (com.arthurslife.app.presentation.theme.BaseAppTheme) -> Unit,
) {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val availableThemes by themeViewModel.availableThemes.collectAsState()

    themeSelectorDialog(
        currentTheme = theme,
        availableThemes = availableThemes,
        actions = ThemeSelectorDialogActions(
            onDismiss = viewModel::hideDialog,
            onThemeSelected = { selectedTheme ->
                viewModel.hideDialog()
                onThemeSelected(selectedTheme)
            },
        ),
        theme = theme,
    )
}

data class DialogCallbacks(
    val onUserSelected: (com.arthurslife.app.domain.user.User) -> Unit,
    val onLanguageSelected: (String) -> Unit,
    val onSaveProfile: () -> Unit,
    val onChangeAvatar: () -> Unit,
    val onChangePIN: () -> Unit,
    val onSwitchUsers: () -> Unit,
    val onThemeClick: () -> Unit,
    val onThemeSelected: (com.arthurslife.app.presentation.theme.BaseAppTheme) -> Unit,
)

@Composable
private fun renderCurrentDialog(
    dialogType: DialogType,
    dialogState: com.arthurslife.app.presentation.viewmodels.DialogState,
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    callbacks: DialogCallbacks,
) {
    when (dialogType) {
        DialogType.SETTINGS -> settingsDialogContent(
            viewModel = viewModel,
            theme = theme,
            onSwitchUsers = callbacks.onSwitchUsers,
            currentLanguage = dialogState.currentLanguage,
        )

        DialogType.USER_PROFILE -> userProfileDialogContent(
            viewModel = viewModel,
            theme = theme,
            dialogState = dialogState,
            callbacks = DialogProfileCallbacks(
                onSaveProfile = callbacks.onSaveProfile,
                onChangeAvatar = callbacks.onChangeAvatar,
                onChangePIN = callbacks.onChangePIN,
            ),
        )

        DialogType.USER_SELECTOR -> userSelectorDialogContent(
            viewModel = viewModel,
            theme = theme,
            dialogState = dialogState,
            onUserSelected = callbacks.onUserSelected,
        )

        DialogType.CHILD_SELECTOR -> childSelectorDialogContent(
            viewModel = viewModel,
            theme = theme,
            dialogState = dialogState,
            onUserSelected = callbacks.onUserSelected,
        )

        DialogType.LANGUAGE_SELECTOR -> languageSelectorDialogContent(
            viewModel = viewModel,
            theme = theme,
            currentLanguage = dialogState.currentLanguage,
            onLanguageSelected = callbacks.onLanguageSelected,
        )

        DialogType.THEME_SELECTOR -> themeSelectorDialogContent(
            viewModel = viewModel,
            theme = theme,
            onThemeSelected = callbacks.onThemeSelected,
        )
    }
}

@Composable
private fun settingsDialogContent(
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    onSwitchUsers: () -> Unit,
    currentLanguage: String,
) {
    settingsDialog(
        actions = SettingsDialogActions(
            onDismiss = viewModel::hideDialog,
            onSwitchUsersClick = {
                viewModel.hideDialog()
                onSwitchUsers()
            },
            onLanguageClick = {
                viewModel.hideDialog()
                viewModel.showLanguageSelectorDialog(currentLanguage)
            },
            onThemeClick = {
                viewModel.hideDialog()
                viewModel.showThemeSelectorDialog()
            },
        ),
        theme = theme,
    )
}

data class DialogProfileCallbacks(
    val onSaveProfile: () -> Unit,
    val onChangeAvatar: () -> Unit,
    val onChangePIN: () -> Unit,
)

@Composable
private fun userProfileDialogContent(
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    dialogState: com.arthurslife.app.presentation.viewmodels.DialogState,
    callbacks: DialogProfileCallbacks,
) {
    dialogState.selectedUser?.let { user ->
        userProfileDialog(
            state = UserProfileDialogState(
                user = user,
                editedName = dialogState.editedName,
                isLoading = dialogState.isLoading,
            ),
            actions = UserProfileDialogActions(
                onDismiss = viewModel::hideDialog,
                onSave = {
                    viewModel.setLoading(true)
                    callbacks.onSaveProfile()
                },
                onCancel = viewModel::hideDialog,
                onChangeAvatar = {
                    viewModel.hideDialog()
                    callbacks.onChangeAvatar()
                },
                onChangePIN = {
                    viewModel.hideDialog()
                    callbacks.onChangePIN()
                },
                onNameChange = viewModel::updateEditedName,
            ),
            theme = theme,
        )
    }
}

@Composable
private fun userSelectorDialogContent(
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    dialogState: com.arthurslife.app.presentation.viewmodels.DialogState,
    onUserSelected: (com.arthurslife.app.domain.user.User) -> Unit,
) {
    userSelectorDialog(
        users = dialogState.availableUsers,
        currentUserId = dialogState.currentUserId,
        actions = UserSelectorDialogActions(
            onDismiss = viewModel::hideDialog,
            onUserSelected = { user ->
                viewModel.hideDialog()
                onUserSelected(user)
            },
        ),
        theme = theme,
    )
}

@Composable
private fun childSelectorDialogContent(
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    dialogState: com.arthurslife.app.presentation.viewmodels.DialogState,
    onUserSelected: (com.arthurslife.app.domain.user.User) -> Unit,
) {
    childSelectorDialog(
        children = dialogState.availableUsers,
        selectedChildId = dialogState.selectedChildId,
        actions = ChildSelectorDialogActions(
            onDismiss = viewModel::hideDialog,
            onChildSelected = { child ->
                viewModel.hideDialog()
                onUserSelected(child)
            },
        ),
        theme = theme,
    )
}

@Composable
private fun languageSelectorDialogContent(
    viewModel: DialogManagementViewModel,
    theme: BaseAppTheme,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
) {
    languageSelectorDialog(
        currentLanguage = currentLanguage,
        actions = LanguageSelectorDialogActions(
            onDismiss = viewModel::hideDialog,
            onLanguageSelected = { language ->
                viewModel.hideDialog()
                onLanguageSelected(language)
            },
        ),
        theme = theme,
    )
}
