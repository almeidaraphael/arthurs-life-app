package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.presentation.theme.BaseAppTheme

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
fun settingsDialog(
    actions: SettingsDialogActions,
    theme: BaseAppTheme,
) {
    themeAwareAlertDialog(
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
            settingsContent(actions, theme)
        },
        confirmButton = {
            themeAwareDialogTextButton(
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
fun userProfileDialog(
    state: UserProfileDialogState,
    actions: UserProfileDialogActions,
    theme: BaseAppTheme,
) {
    themeAwareAlertDialog(
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
            userProfileContent(state, actions, theme)
        },
        confirmButton = {
            themeAwareDialogTextButton(
                text = "Save",
                onClick = actions.onSave,
                theme = theme,
                enabled = !state.isLoading && state.editedName.isNotBlank(),
            )
        },
        dismissButton = {
            themeAwareDialogTextButton(
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
fun userSelectorDialog(
    users: List<User>,
    currentUserId: String,
    actions: UserSelectorDialogActions,
    theme: BaseAppTheme,
) {
    themeAwareAlertDialog(
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
            userSelectorContent(users, currentUserId, actions, theme)
        },
        confirmButton = {
            themeAwareDialogTextButton(
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
fun childSelectorDialog(
    children: List<User>,
    selectedChildId: String?,
    actions: ChildSelectorDialogActions,
    theme: BaseAppTheme,
) {
    themeAwareAlertDialog(
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
            childSelectorContent(children, selectedChildId, actions, theme)
        },
        confirmButton = {
            themeAwareDialogTextButton(
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
fun languageSelectorDialog(
    currentLanguage: String,
    actions: LanguageSelectorDialogActions,
    theme: BaseAppTheme,
    availableLanguages: List<LanguageOption> = DEFAULT_LANGUAGES,
) {
    themeAwareAlertDialog(
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
            languageSelectorContent(currentLanguage, availableLanguages, actions, theme)
        },
        confirmButton = {
            themeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

// Content composables to keep main functions under the line limit

@Composable
private fun settingsContent(
    actions: SettingsDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        settingsOption(
            iconType = SemanticIconType.SWITCH_ACCOUNT,
            title = "Switch Users",
            description = "Change to a different user account",
            theme = theme,
            onClick = actions.onSwitchUsersClick,
        )

        settingsOption(
            iconType = SemanticIconType.CONSTRUCTION,
            title = "Language",
            description = "Change app language",
            theme = theme,
            onClick = actions.onLanguageClick,
        )

        settingsOption(
            iconType = SemanticIconType.CONSTRUCTION,
            title = "Theme & Display",
            description = "Customize app appearance",
            theme = theme,
            onClick = actions.onThemeClick,
        )
    }
}

@Composable
private fun userProfileContent(
    state: UserProfileDialogState,
    actions: UserProfileDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_CONTENT_SPACING),
    ) {
        // Avatar section
        profileAvatarSection(state.user, actions.onChangeAvatar, theme)

        // Name editing section
        profileNameSection(state.editedName, actions.onNameChange, theme)

        // Role display section
        profileRoleSection(state.user, theme)

        // Change PIN section (caregiver only)
        if (state.user.role == com.arthurslife.app.domain.user.UserRole.CAREGIVER) {
            profilePinSection(actions.onChangePIN, theme)
        }
    }
}

@Composable
private fun userSelectorContent(
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
            userSelectionItem(
                user = user,
                isSelected = user.id == currentUserId,
                theme = theme,
                onClick = { actions.onUserSelected(user) },
            )
        }
    }
}

@Composable
private fun childSelectorContent(
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
            userSelectionItem(
                user = child,
                isSelected = child.id == selectedChildId,
                theme = theme,
                onClick = { actions.onChildSelected(child) },
            )
        }
    }
}

@Composable
private fun languageSelectorContent(
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
            languageSelectionItem(
                language = language,
                isSelected = language.code == currentLanguage,
                theme = theme,
                onClick = { actions.onLanguageSelected(language.code) },
            )
        }
    }
}

// Helper composables for profile sections

@Composable
private fun profileAvatarSection(
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
        themeAwareIconButton(
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
private fun profileNameSection(
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
        themeAwareOutlinedTextField(
            value = editedName,
            onValueChange = onNameChange,
            theme = theme,
            placeholder = "Enter your name",
            modifier = Modifier.padding(top = FIELD_SPACING),
        )
    }
}

@Composable
private fun profileRoleSection(
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
                com.arthurslife.app.domain.user.UserRole.CHILD -> "Child"
                com.arthurslife.app.domain.user.UserRole.CAREGIVER -> {
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
private fun profilePinSection(
    onChangePIN: () -> Unit,
    theme: BaseAppTheme,
) {
    settingsOption(
        iconType = SemanticIconType.CONSTRUCTION,
        title = "Change PIN",
        description = "Update your security PIN",
        theme = theme,
        onClick = onChangePIN,
    )
}

// Reusable item composables

@Composable
private fun settingsOption(
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
        themeAwareIconButton(
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
private fun userSelectionItem(
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
                            com.arthurslife.app.domain.user.UserRole.CHILD -> "Child"
                            com.arthurslife.app.domain.user.UserRole.CAREGIVER -> "Caregiver"
                        },
                    )
                    if (isSelected) append(", currently selected")
                }
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        themeAwareIconButton(
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
                    com.arthurslife.app.domain.user.UserRole.CHILD -> "Child"
                    com.arthurslife.app.domain.user.UserRole.CAREGIVER -> "Caregiver"
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
private fun languageSelectionItem(
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
        themeAwareIconButton(
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
    val onThemeSelected: (com.arthurslife.app.presentation.theme.BaseAppTheme) -> Unit,
)

/**
 * Theme selector dialog for changing app theme.
 */
@Composable
fun themeSelectorDialog(
    currentTheme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    availableThemes: List<com.arthurslife.app.presentation.theme.BaseAppTheme>,
    actions: ThemeSelectorDialogActions,
    theme: BaseAppTheme,
) {
    themeAwareAlertDialog(
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
            themeSelectorContent(
                currentTheme = currentTheme,
                availableThemes = availableThemes,
                actions = actions,
                theme = theme,
            )
        },
        confirmButton = {
            themeAwareDialogTextButton(
                text = "Cancel",
                onClick = actions.onDismiss,
                theme = theme,
            )
        },
    )
}

@Composable
private fun themeSelectorContent(
    currentTheme: com.arthurslife.app.presentation.theme.BaseAppTheme,
    availableThemes: List<com.arthurslife.app.presentation.theme.BaseAppTheme>,
    actions: ThemeSelectorDialogActions,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        availableThemes.forEach { themeOption ->
            themeSelectionItem(
                themeOption = themeOption,
                isSelected = themeOption.displayName == currentTheme.displayName,
                theme = theme,
                onClick = { actions.onThemeSelected(themeOption) },
            )
        }
    }
}

@Composable
private fun themeSelectionItem(
    themeOption: com.arthurslife.app.presentation.theme.BaseAppTheme,
    isSelected: Boolean,
    theme: BaseAppTheme,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${themeOption.displayName}${if (isSelected) ", currently selected" else ""}"
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DIALOG_ITEM_SPACING),
    ) {
        themeAwareIconButton(
            onClick = onClick,
        ) {
            themeColorPreview(
                themeOption = themeOption,
                theme = theme,
            )
        }

        themeItemDetails(
            themeOption = themeOption,
            isSelected = isSelected,
            theme = theme,
        )

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
private fun themeColorPreview(
    themeOption: com.arthurslife.app.presentation.theme.BaseAppTheme,
    theme: BaseAppTheme,
) {
    Row {
        val colors = listOf(
            themeOption.colorScheme.primary,
            themeOption.colorScheme.secondary,
            themeOption.colorScheme.tertiary,
        )
        colors.forEach { color ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(theme.shapes.small)
                    .background(color),
            )
            if (color != colors.last()) {
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.width(2.dp),
                )
            }
        }
    }
}

@Composable
private fun RowScope.themeItemDetails(
    themeOption: com.arthurslife.app.presentation.theme.BaseAppTheme,
    isSelected: Boolean,
    theme: BaseAppTheme,
) {
    Column(
        modifier = Modifier.weight(1f),
    ) {
        Text(
            text = themeOption.displayName,
            style = theme.typography.bodyLarge,
            color = theme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        )
        Text(
            text = themeOption.description,
            style = theme.typography.bodySmall,
            color = theme.colorScheme.onSurfaceVariant,
        )
    }
}

// Default data
private val DEFAULT_LANGUAGES = listOf(
    LanguageOption("en-US", "English", "English", "🇺🇸"),
    LanguageOption("pt-BR", "Portuguese", "Português", "🇧🇷"),
)
