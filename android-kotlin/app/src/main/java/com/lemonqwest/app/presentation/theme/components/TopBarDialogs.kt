package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.presentation.theme.BaseAppTheme

/**
 * Data class for settings dialog actions.
 */
data class SettingsDialogActions(
    val onDismiss: () -> Unit,
    val onSwitchUsersClick: () -> Unit,
    val onLanguageClick: () -> Unit,
    val onThemeClick: () -> Unit,
)

/**
 * Settings dialog accessible from the top navigation bar.
 */
@Composable
fun SettingsDialog(
    actions: SettingsDialogActions,
    theme: BaseAppTheme,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = actions.onDismiss,
        theme = theme,
        title = {
            Text(
                text = "Settings",
                style = theme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            SettingsContent(actions, theme)
        },
        confirmButton = {
            ThemeAwareDialogTextButton(
                text = "Close",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

/**
 * Data class for user profile dialog state.
 */
data class UserProfileDialogState(
    val user: User,
    val editedName: String,
    val isLoading: Boolean = false,
)

/**
 * Data class for user profile dialog actions.
 */
data class UserProfileDialogActions(
    val onDismiss: () -> Unit,
    val onSave: () -> Unit,
    val onCancel: () -> Unit,
    val onChangeAvatar: () -> Unit,
    val onChangePIN: () -> Unit,
    val onNameChange: (String) -> Unit,
)

/**
 * User profile dialog accessible from the top navigation bar avatar.
 */
@Composable
fun UserProfileDialog(
    state: UserProfileDialogState,
    actions: UserProfileDialogActions,
    theme: BaseAppTheme,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = actions.onDismiss,
        theme = theme,
        title = {
            Text(
                text = "User Profile",
                style = theme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            UserProfileContent(state, actions, theme)
        },
        confirmButton = {
            ThemeAwareDialogTextButton(
                text = "Save",
                onClick = actions.onSave,
                theme = theme,
                enabled = !state.isLoading && state.editedName.isNotBlank(),
            )
        },
        dismissButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onCancel,
                theme = theme,
                enabled = !state.isLoading,
            )
        },
    )
}

/**
 * Data class for user selector dialog actions.
 */
data class UserSelectorDialogActions(
    val onDismiss: () -> Unit,
    val onUserSelected: (User) -> Unit,
)

/**
 * User selector dialog for switching between users.
 */
@Composable
fun UserSelectorDialog(
    users: List<User>,
    currentUserId: String,
    actions: UserSelectorDialogActions,
    theme: BaseAppTheme,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = actions.onDismiss,
        theme = theme,
        title = {
            Text(
                text = "Switch User",
                style = theme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            UserSelectorContent(users, currentUserId, actions, theme)
        },
        confirmButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

/**
 * Data class for child selector dialog actions.
 */
data class ChildSelectorDialogActions(
    val onDismiss: () -> Unit,
    val onChildSelected: (User) -> Unit,
)

/**
 * Child selector dialog for caregivers.
 */
@Composable
fun ChildSelectorDialog(
    children: List<User>,
    selectedChildId: String?,
    actions: ChildSelectorDialogActions,
    theme: BaseAppTheme,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = actions.onDismiss,
        theme = theme,
        title = {
            Text(
                text = "Select Child",
                style = theme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            ChildSelectorContent(children, selectedChildId, actions, theme)
        },
        confirmButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

/**
 * Data class representing a language option.
 */
data class LanguageOption(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
)

/**
 * Data class for language selector dialog actions.
 */
data class LanguageSelectorDialogActions(
    val onDismiss: () -> Unit,
    val onLanguageSelected: (String) -> Unit,
)

/**
 * Language selector dialog for changing app language.
 */
@Composable
fun LanguageSelectorDialog(
    currentLanguage: String,
    actions: LanguageSelectorDialogActions,
    theme: BaseAppTheme,
    availableLanguages: List<LanguageOption> = DEFAULT_LANGUAGES,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = actions.onDismiss,
        theme = theme,
        title = {
            Text(
                text = "Language",
                style = theme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            LanguageSelectorContent(currentLanguage, availableLanguages, actions, theme)
        },
        confirmButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

// Content composable to keep main functions under the line limit

@Composable
private fun SettingsContent(
    actions: SettingsDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        SettingsOption(
            iconType = SemanticIconType.SWITCH_ACCOUNT,
            title = "Switch Users",
            description = "Change to a different user account",
            theme = theme,
            onClick = actions.onSwitchUsersClick,
        )

        SettingsOption(
            iconType = SemanticIconType.CONSTRUCTION,
            title = "Language",
            description = "Change app language",
            theme = theme,
            onClick = actions.onLanguageClick,
        )

        SettingsOption(
            iconType = SemanticIconType.CONSTRUCTION,
            title = "Theme & Display",
            description = "Customize app appearance",
            theme = theme,
            onClick = actions.onThemeClick,
        )
    }
}

@Composable
private fun UserProfileContent(
    state: UserProfileDialogState,
    actions: UserProfileDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_CONTENT_SPACING),
    ) {
        // Avatar section
        ProfileAvatarSection(state.user, actions.onChangeAvatar, theme)

        // Name editing section
        ProfileNameSection(state.editedName, actions.onNameChange, theme)

        // Role display section
        ProfileRoleSection(state.user, theme)

        // Change PIN section (caregiver only)
        if (state.user.role == com.lemonqwest.app.domain.user.UserRole.CAREGIVER) {
            ProfilePinSection(actions.onChangePIN, theme)
        }
    }
}

@Composable
private fun UserSelectorContent(
    users: List<User>,
    currentUserId: String,
    actions: UserSelectorDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        users.forEach { user ->
            UserSelectionItem(
                user = user,
                isSelected = user.id == currentUserId,
                theme = theme,
                onClick = { actions.onUserSelected(user) },
            )
        }
    }
}

@Composable
private fun ChildSelectorContent(
    children: List<User>,
    selectedChildId: String?,
    actions: ChildSelectorDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        children.forEach { child ->
            UserSelectionItem(
                user = child,
                isSelected = child.id == selectedChildId,
                theme = theme,
                onClick = { actions.onChildSelected(child) },
            )
        }
    }
}

@Composable
private fun LanguageSelectorContent(
    currentLanguage: String,
    availableLanguages: List<LanguageOption>,
    actions: LanguageSelectorDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        availableLanguages.forEach { language ->
            LanguageSelectionItem(
                language = language,
                isSelected = language.code == currentLanguage,
                theme = theme,
                onClick = { actions.onLanguageSelected(language.code) },
            )
        }
    }
}

// Helper composable for profile sections

@Composable
private fun ProfileAvatarSection(
    @Suppress("UnusedParameter") user: User, // Reserved for future avatar display
    onChangeAvatar: () -> Unit,
    theme: BaseAppTheme,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "User avatar, tap to change"
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        ThemeAwareIconButton(
            onClick = onChangeAvatar,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.AVATAR,
                theme = theme,
                modifier = Modifier.size(PROFILE_AVATAR_SIZE),
                contentDescription = null,
            )
        }

        Column {
            Text(
                text = "Avatar",
                style = theme.typography.labelLarge,
                color = theme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "Tap to change",
                style = theme.typography.labelSmall,
                color = theme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileNameSection(
    editedName: String,
    onNameChange: (String) -> Unit,
    theme: BaseAppTheme,
) {
    Column {
        Text(
            text = "Name",
            style = theme.typography.labelLarge,
            color = theme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
        )
        ThemeAwareOutlinedTextField(
            value = editedName,
            onValueChange = onNameChange,
            theme = theme,
            placeholder = "Enter your name",
            modifier = Modifier.padding(top = FIELD_SPACING),
        )
    }
}

@Composable
private fun ProfileRoleSection(
    user: User,
    theme: BaseAppTheme,
) {
    Column {
        Text(
            text = "Role",
            style = theme.typography.labelLarge,
            color = theme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = when (user.role) {
                com.lemonqwest.app.domain.user.UserRole.CHILD -> "Child"
                com.lemonqwest.app.domain.user.UserRole.CAREGIVER -> {
                    if (user.isAdmin) "Family Admin" else "Caregiver"
                }
            },
            style = theme.typography.bodyMedium,
            color = theme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = FIELD_SPACING),
        )
    }
}

@Composable
private fun ProfilePinSection(
    onChangePIN: () -> Unit,
    theme: BaseAppTheme,
) {
    SettingsOption(
        iconType = SemanticIconType.CONSTRUCTION,
        title = "Change PIN",
        description = "Update your security PIN",
        theme = theme,
        onClick = onChangePIN,
    )
}

// Reusable item composable

@Composable
private fun SettingsOption(
    iconType: SemanticIconType,
    title: String,
    description: String,
    theme: BaseAppTheme,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title: $description"
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        ThemeAwareIconButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = theme.icons(iconType),
                contentDescription = null,
                tint = theme.colorScheme.primary,
                modifier = Modifier.size(SETTINGS_ICON_SIZE),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = theme.typography.bodyLarge,
                color = theme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = description,
                style = theme.typography.bodySmall,
                color = theme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun UserSelectionItem(
    user: User,
    isSelected: Boolean,
    theme: BaseAppTheme,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = buildString {
                    append("${user.displayName ?: user.name}, ")
                    append(
                        when (user.role) {
                            com.lemonqwest.app.domain.user.UserRole.CHILD -> "Child"
                            com.lemonqwest.app.domain.user.UserRole.CAREGIVER -> "Caregiver"
                        },
                    )
                    if (isSelected) append(", currently selected")
                }
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        ThemeAwareIconButton(
            onClick = onClick,
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.AVATAR,
                theme = theme,
                modifier = Modifier.size(USER_AVATAR_SIZE),
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = user.displayName ?: user.name,
                style = theme.typography.bodyLarge,
                color = theme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            )
            Text(
                text = when (user.role) {
                    com.lemonqwest.app.domain.user.UserRole.CHILD -> "Child"
                    com.lemonqwest.app.domain.user.UserRole.CAREGIVER -> "Caregiver"
                },
                style = theme.typography.bodySmall,
                color = theme.colorScheme.onSurfaceVariant,
            )
        }

        if (isSelected) {
            Icon(
                imageVector = theme.icons(SemanticIconType.CHECK_SELECTED),
                contentDescription = "Selected",
                tint = theme.colorScheme.primary,
                modifier = Modifier.size(SELECTION_ICON_SIZE),
            )
        }
    }
}

@Composable
private fun LanguageSelectionItem(
    language: LanguageOption,
    isSelected: Boolean,
    theme: BaseAppTheme,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${language.name}${if (isSelected) ", currently selected" else ""}"
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        ThemeAwareIconButton(
            onClick = onClick,
        ) {
            Text(
                text = language.flag,
                style = theme.typography.headlineMedium,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = language.name,
                style = theme.typography.bodyLarge,
                color = theme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            )
            Text(
                text = language.nativeName,
                style = theme.typography.bodySmall,
                color = theme.colorScheme.onSurfaceVariant,
            )
        }

        if (isSelected) {
            Icon(
                imageVector = theme.icons(SemanticIconType.CHECK_SELECTED),
                contentDescription = "Selected",
                tint = theme.colorScheme.primary,
                modifier = Modifier.size(SELECTION_ICON_SIZE),
            )
        }
    }
}

// Design constants
private val DIALOG_ITEM_SPACING = 12.dp
private val DIALOG_CONTENT_SPACING = 16.dp
private val FIELD_SPACING = 4.dp

private val PROFILE_AVATAR_SIZE = 48.dp
private val USER_AVATAR_SIZE = 40.dp
private val SETTINGS_ICON_SIZE = 24.dp
private val SELECTION_ICON_SIZE = 20.dp

/**
 * Data class for theme selector dialog actions.
 */
data class ThemeSelectorDialogActions(
    val onDismiss: () -> Unit,
    val onThemeSelected: (BaseAppTheme) -> Unit,
)

/**
 * Theme selector dialog for changing app theme.
 */
@Composable
fun ThemeSelectorDialog(
    currentTheme: BaseAppTheme,
    availableThemes: List<BaseAppTheme>,
    actions: ThemeSelectorDialogActions,
    theme: BaseAppTheme,
) {
    ThemeAwareAlertDialog(
        onDismissRequest = actions.onDismiss,
        theme = theme,
        title = {
            Text(
                text = "Theme & Display",
                style = theme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            ThemeSelectorContent(
                currentTheme = currentTheme,
                availableThemes = availableThemes,
                actions = actions,
                theme = theme,
            )
        },
        confirmButton = {
            ThemeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

@Composable
private fun ThemeSelectorContent(
    currentTheme: BaseAppTheme,
    availableThemes: List<BaseAppTheme>,
    actions: ThemeSelectorDialogActions,
    @Suppress("UnusedParameter") theme: BaseAppTheme,
) {
    ThemeSelector(
        currentTheme = currentTheme,
        availableThemes = availableThemes,
        onThemeSelected = actions.onThemeSelected,
        modifier = Modifier.fillMaxWidth(),
    )
}

// Default data
private val DEFAULT_LANGUAGES = listOf(
    LanguageOption("en-US", "English", "English", "🇺🇸"),
    LanguageOption("pt-BR", "Portuguese", "Português", "🇧🇷"),
)
