package com.lemonqwest.app.presentation.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.viewmodels.TopBarScreen
import com.lemonqwest.app.presentation.viewmodels.TopBarState
import com.lemonqwest.app.presentation.viewmodels.TopBarViewModel

/**
 * Theme-aware top navigation bar with ViewModel integration.
 * Automatically manages role-based content and screen-specific elements.
 *
 * This composable provides a transparent top navigation bar that adapts its content
 * based on the current user role (Child/Caregiver) and screen context. It integrates
 * with the TopBarViewModel for reactive state management.
 *
 * @param theme Current app theme for styling and semantic icons
 * @param currentScreen Current screen context for determining visible elements
 * @param onAvatarClick Callback when user avatar is clicked (opens profile dialog)
 * @param onSettingsClick Callback when settings icon is clicked (opens settings dialog)
 * @param onChildSelected Callback when selected child is clicked (caregiver mode only)
 * @param modifier Modifier for the top bar container
 * @param viewModel TopBarViewModel for state management
 */
@Composable
fun TopNavigationBar(
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
    currentScreen: TopBarScreen = TopBarScreen.HOME,
    onAvatarClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onChildSelected: (String) -> Unit = {},
    viewModel: TopBarViewModel = hiltViewModel(),
) {
    val topBarState by viewModel.topBarState.collectAsState()
    val isVisible by viewModel.isTopBarVisible.collectAsState()

    // Update current screen in ViewModel when it changes
    viewModel.updateCurrentScreen(currentScreen)

    // Only show top navigation when user is authenticated
    if (isVisible) {
        ThemeAwareTopNavigationBar(
            state = topBarState,
            theme = theme,
            onAvatarClick = onAvatarClick,
            onSettingsClick = onSettingsClick,
            onChildSelected = onChildSelected,
            modifier = modifier,
        )
    }
}

/**
 * Theme-aware top navigation bar composable that renders UI based on TopBarState.
 * This is a stateless composable that receives state and callbacks.
 *
 * @param state Current top bar state containing all display elements
 * @param theme Current app theme for styling and semantic icons
 * @param onAvatarClick Callback when user avatar is clicked
 * @param onSettingsClick Callback when settings icon is clicked
 * @param onChildSelected Callback when selected child is clicked
 * @param modifier Modifier for the top bar container
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeAwareTopNavigationBar(
    state: TopBarState,
    theme: BaseAppTheme,
    onAvatarClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onChildSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            // Content area - shows different elements based on state
            TopBarContent(
                state = state,
                theme = theme,
                onChildSelected = onChildSelected,
            )
        },
        navigationIcon = {
            // Left side - User avatar
            UserAvatarSection(
                state = state,
                theme = theme,
                onAvatarClick = onAvatarClick,
            )
        },
        actions = {
            // Right side - Settings button
            SettingsSection(
                state = state,
                theme = theme,
                onSettingsClick = onSettingsClick,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = theme.colorScheme.onSurface,
            actionIconContentColor = theme.colorScheme.onSurface,
            navigationIconContentColor = theme.colorScheme.onSurface,
        ),
        windowInsets = WindowInsets.statusBars,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .semantics {
                contentDescription = buildTopBarContentDescription(state)
            },
    )
}

/**
 * Renders the main content area of the top bar based on current state.
 */
@Composable
private fun TopBarContent(
    state: TopBarState,
    theme: BaseAppTheme,
    onChildSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CONTENT_SPACING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            // Child mode content
            state.isChildMode -> {
                ChildModeContent(
                    state = state,
                    theme = theme,
                )
            }
            // Caregiver mode content
            state.isCaregiverMode -> {
                CaregiverModeContent(
                    state = state,
                    theme = theme,
                    onChildSelected = onChildSelected,
                )
            }
        }
    }
}

/**
 * Content for child mode - tokens, progress, achievements based on screen.
 */
@Composable
private fun ChildModeContent(
    state: TopBarState,
    theme: BaseAppTheme,
) {
    // Token balance - always shown for children
    state.tokenBalance?.let { balance ->
        TokenDisplay(
            balance = balance,
            theme = theme,
        )
    }

    // Screen-specific content
    when (state.currentScreen) {
        TopBarScreen.HOME, TopBarScreen.TASKS -> {
            state.tasksProgress?.let { progress ->
                ProgressDisplay(
                    progress = progress,
                    theme = theme,
                    label = "Tasks",
                )
            }

            // Total tokens earned (Tasks screen only)
            if (state.currentScreen == TopBarScreen.TASKS) {
                state.totalTokensEarned?.let { earned ->
                    TokenDisplay(
                        balance = earned,
                        theme = theme,
                        label = "Earned",
                    )
                }
            }
        }
        TopBarScreen.REWARDS -> {
            state.rewardsAvailable?.let { count ->
                CountDisplay(
                    count = count,
                    theme = theme,
                    label = "Available",
                    iconType = SemanticIconType.REWARDS,
                )
            }
        }
        TopBarScreen.ACHIEVEMENTS -> {
            state.achievementsProgress?.let { progress ->
                ProgressDisplay(
                    progress = progress,
                    theme = theme,
                    label = "Unlocked",
                )
            }
        }
        else -> {
            // Other screens - no additional content
        }
    }
}

/**
 * Content for caregiver mode - selected child based on screen.
 */
@Composable
private fun CaregiverModeContent(
    state: TopBarState,
    theme: BaseAppTheme,
    onChildSelected: (String) -> Unit,
) {
    // Selected child - only on home screen
    if (state.currentScreen == TopBarScreen.HOME) {
        state.selectedChild?.let { child ->
            SelectedChildDisplay(
                child = child,
                theme = theme,
                onChildSelected = onChildSelected,
            )
        }
    }
}

/**
 * User avatar section on the left side of the top bar.
 */
@Composable
private fun UserAvatarSection(
    state: TopBarState,
    theme: BaseAppTheme,
    onAvatarClick: () -> Unit,
) {
    if (state.userAvatar.isClickable) {
        IconButton(
            onClick = onAvatarClick,
            modifier = Modifier.semantics {
                contentDescription = "Open profile for ${state.userAvatar.userName}"
                role = Role.Button
            },
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.AVATAR,
                theme = theme,
                modifier = Modifier.size(AVATAR_SIZE),
                contentDescription = null, // Handled by parent button
            )
        }
    } else {
        ThemeAwareIcon(
            semanticType = SemanticIconType.AVATAR,
            theme = theme,
            modifier = Modifier.size(AVATAR_SIZE),
            contentDescription = "User avatar",
        )
    }
}

/**
 * Settings section on the right side of the top bar.
 */
@Composable
private fun SettingsSection(
    state: TopBarState,
    theme: BaseAppTheme,
    onSettingsClick: () -> Unit,
) {
    if (state.isSettingsVisible) {
        ThemeAwareIconButton(
            onClick = onSettingsClick,
        ) {
            Icon(
                imageVector = theme.icons(SemanticIconType.CONSTRUCTION),
                contentDescription = "Open settings",
                tint = theme.colorScheme.onSurface,
                modifier = Modifier.size(SETTINGS_ICON_SIZE),
            )
        }
    }
}

/**
 * Displays token balance with appropriate theme styling.
 */
@Composable
private fun TokenDisplay(
    balance: com.lemonqwest.app.domain.user.TokenBalance,
    theme: BaseAppTheme,
    label: String? = null,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.semantics {
            contentDescription = buildString {
                append(label?.let { "$it: " } ?: "")
                append("${balance.getValue()} ${theme.tokenLabel}")
            }
        },
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = TOKEN_PADDING_HORIZONTAL,
                vertical = TOKEN_PADDING_VERTICAL,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TOKEN_ICON_SPACING),
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.TOKEN,
                theme = theme,
                modifier = Modifier.size(TOKEN_ICON_SIZE),
                contentDescription = null,
            )
            Text(
                text = balance.getValue().toString(),
                style = theme.typography.labelMedium,
                color = theme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
            )
            label?.let {
                Text(
                    text = it,
                    style = theme.typography.labelSmall,
                    color = theme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

/**
 * Displays progress information (tasks or achievements).
 */
@Composable
private fun ProgressDisplay(
    progress: com.lemonqwest.app.presentation.viewmodels.TasksProgress,
    theme: BaseAppTheme,
    label: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.secondaryContainer,
        ),
        modifier = Modifier.semantics {
            contentDescription = "$label progress: ${progress.progressText}"
        },
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = PROGRESS_PADDING_HORIZONTAL,
                vertical = PROGRESS_PADDING_VERTICAL,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PROGRESS_ICON_SPACING),
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.PROGRESS_INDICATOR,
                theme = theme,
                modifier = Modifier.size(PROGRESS_ICON_SIZE),
                contentDescription = null,
            )
            Text(
                text = progress.progressText,
                style = theme.typography.labelMedium,
                color = theme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Displays progress information for achievements.
 */
@Composable
private fun ProgressDisplay(
    progress: com.lemonqwest.app.presentation.viewmodels.AchievementsProgress,
    theme: BaseAppTheme,
    label: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.tertiaryContainer,
        ),
        modifier = Modifier.semantics {
            contentDescription = "$label: ${progress.progressText}"
        },
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = PROGRESS_PADDING_HORIZONTAL,
                vertical = PROGRESS_PADDING_VERTICAL,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PROGRESS_ICON_SPACING),
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.ACHIEVEMENTS,
                theme = theme,
                modifier = Modifier.size(PROGRESS_ICON_SIZE),
                contentDescription = null,
            )
            Text(
                text = progress.progressText,
                style = theme.typography.labelMedium,
                color = theme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Displays count information (e.g., available rewards).
 */
@Composable
private fun CountDisplay(
    count: Int,
    theme: BaseAppTheme,
    label: String,
    iconType: SemanticIconType,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.semantics {
            contentDescription = "$label: $count"
        },
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = COUNT_PADDING_HORIZONTAL,
                vertical = COUNT_PADDING_VERTICAL,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(COUNT_ICON_SPACING),
        ) {
            ThemeAwareIcon(
                semanticType = iconType,
                theme = theme,
                modifier = Modifier.size(COUNT_ICON_SIZE),
                contentDescription = null,
            )
            Text(
                text = count.toString(),
                style = theme.typography.labelMedium,
                color = theme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = label,
                style = theme.typography.labelSmall,
                color = theme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Displays selected child in caregiver mode.
 */
@Composable
private fun SelectedChildDisplay(
    child: com.lemonqwest.app.presentation.viewmodels.SelectedChild,
    theme: BaseAppTheme,
    onChildSelected: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onChildSelected(child.childId) }
            .semantics {
                contentDescription = "Selected child: ${child.childName}, tap to change"
                role = Role.Button
            },
        color = theme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = CHILD_PADDING_HORIZONTAL,
                vertical = CHILD_PADDING_VERTICAL,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(CHILD_ICON_SPACING),
        ) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.CHILD_CARE,
                theme = theme,
                modifier = Modifier.size(CHILD_ICON_SIZE),
                contentDescription = null,
            )
            Text(
                text = child.childName,
                style = theme.typography.labelMedium,
                color = theme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Builds content description for the entire top bar for accessibility.
 */
private fun buildTopBarContentDescription(state: TopBarState): String {
    return buildString {
        append("Top navigation bar for ")
        append(if (state.isChildMode) "child" else "caregiver")
        append(" on ${state.currentScreen.name.lowercase()} screen")

        state.tokenBalance?.let {
            append(", ${it.getValue()} tokens")
        }

        state.tasksProgress?.let {
            append(", tasks progress ${it.progressText}")
        }

        state.selectedChild?.let {
            append(", selected child ${it.childName}")
        }
    }
}

// Design constants
private val AVATAR_SIZE = 32.dp
private val SETTINGS_ICON_SIZE = 24.dp
private val TOKEN_ICON_SIZE = 16.dp
private val PROGRESS_ICON_SIZE = 16.dp
private val COUNT_ICON_SIZE = 16.dp
private val CHILD_ICON_SIZE = 16.dp

private val CONTENT_SPACING = 8.dp
private val TOKEN_ICON_SPACING = 4.dp
private val PROGRESS_ICON_SPACING = 4.dp
private val COUNT_ICON_SPACING = 4.dp
private val CHILD_ICON_SPACING = 4.dp

private val TOKEN_PADDING_HORIZONTAL = 8.dp
private val TOKEN_PADDING_VERTICAL = 4.dp
private val PROGRESS_PADDING_HORIZONTAL = 8.dp
private val PROGRESS_PADDING_VERTICAL = 4.dp
private val COUNT_PADDING_HORIZONTAL = 8.dp
private val COUNT_PADDING_VERTICAL = 4.dp
private val CHILD_PADDING_HORIZONTAL = 12.dp
private val CHILD_PADDING_VERTICAL = 6.dp
