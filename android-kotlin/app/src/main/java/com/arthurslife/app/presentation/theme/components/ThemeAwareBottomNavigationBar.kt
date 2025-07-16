package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.presentation.navigation.BottomNavItem
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.viewmodels.BottomNavViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Theme-aware bottom navigation bar with ViewModel integration.
 * Automatically manages role-based navigation items and authentication state.
 */
@Composable
fun bottomNavigationBar(
    selectedItem: String,
    theme: BaseAppTheme,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BottomNavViewModel = hiltViewModel(),
) {
    val navigationItems by viewModel.navigationItems.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    // Only show bottom navigation when user is authenticated and has navigation items
    if (isAuthenticated && navigationItems.isNotEmpty()) {
        themeAwareBottomNavigationBar(
            navigationItems = navigationItems,
            selectedItem = selectedItem,
            theme = theme,
            onItemSelected = onItemSelected,
            modifier = modifier,
        )
    }
}

/**
 * Theme-aware bottom navigation bar composable that adapts to different app themes.
 * Supports role-based navigation with proper accessibility and Material Design 3 patterns.
 */
@Composable
fun themeAwareBottomNavigationBar(
    navigationItems: List<BottomNavItem>,
    selectedItem: String,
    theme: BaseAppTheme,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier
            .height(BOTTOM_NAV_HEIGHT)
            .background(theme.colorScheme.primary)
            .semantics {
                contentDescription = "Main navigation with ${navigationItems.size} options"
            },
        windowInsets = WindowInsets(0),
        containerColor = Color.Transparent,
    ) {
        navigationItems.forEach { item ->
            val isSelected = selectedItem == item.route
            val itemColors = getThemeAwareNavigationItemColors(theme)

            NavigationBarItem(
                modifier = Modifier
                    .padding(0.dp)
                    .semantics {
                        contentDescription = buildNavigationItemDescription(item.label, isSelected)
                    },
                selected = isSelected,
                label = createNavigationItemLabel(item.label, isSelected, theme),
                alwaysShowLabel = false,
                interactionSource = remember { NoRippleInteractionSource() },
                icon = {
                    createNavigationItemIcon(item, isSelected, theme)
                },
                colors = itemColors,
                onClick = { onItemSelected(item.route) },
            )
        }
    }
}

/**
 * Creates the label composable for navigation items.
 */
@Composable
private fun createNavigationItemLabel(
    label: String,
    isSelected: Boolean,
    theme: BaseAppTheme,
): (@Composable () -> Unit)? {
    return if (isSelected) {
        {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = getSelectedTextColor(theme),
            )
        }
    } else {
        null
    }
}

/**
 * Creates the icon composable for navigation items.
 */
@Composable
private fun createNavigationItemIcon(
    item: BottomNavItem,
    isSelected: Boolean,
    theme: BaseAppTheme,
) {
    Icon(
        imageVector = item.icon,
        contentDescription = null, // Handled by parent semantics
        modifier = Modifier.size(
            if (isSelected) SELECTED_ICON_SIZE else UNSELECTED_ICON_SIZE,
        ),
        tint = if (isSelected) {
            getSelectedIconColor(theme)
        } else {
            getUnselectedIconColor(theme)
        },
    )
}

/**
 * Builds accessibility description for navigation items.
 */
private fun buildNavigationItemDescription(label: String, isSelected: Boolean): String {
    return buildString {
        append("$label navigation tab")
        if (isSelected) {
            append(", currently selected")
        } else {
            append(", tap to navigate")
        }
    }
}

/**
 * Gets theme-aware navigation item colors based on the current theme.
 */
@Composable
private fun getThemeAwareNavigationItemColors(theme: BaseAppTheme) =
    NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent,
        selectedIconColor = getSelectedIconColor(theme),
        selectedTextColor = getSelectedTextColor(theme),
        unselectedIconColor = getUnselectedIconColor(theme),
        unselectedTextColor = getUnselectedTextColor(theme),
    )

/**
 * Gets the selected icon color based on theme.
 */
private fun getSelectedIconColor(theme: BaseAppTheme): Color =
    theme.colorScheme.onPrimary

/**
 * Gets the unselected icon color based on theme.
 */
private fun getUnselectedIconColor(theme: BaseAppTheme): Color =
    theme.colorScheme.onPrimary.copy(alpha = UNSELECTED_ALPHA)

/**
 * Gets the selected text color based on theme.
 */
private fun getSelectedTextColor(theme: BaseAppTheme): Color =
    theme.colorScheme.onPrimary

/**
 * Gets the unselected text color based on theme.
 */
private fun getUnselectedTextColor(theme: BaseAppTheme): Color =
    theme.colorScheme.onPrimary.copy(alpha = UNSELECTED_ALPHA)

/**
 * No ripple interaction source for cleaner navigation bar appearance.
 * This prevents the default Material ripple effect on navigation items.
 */
private class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<androidx.compose.foundation.interaction.Interaction> = emptyFlow()

    override suspend fun emit(interaction: androidx.compose.foundation.interaction.Interaction) {
        // Intentionally empty - we don't want to emit interactions for ripple effect
    }

    override fun tryEmit(
        interaction: androidx.compose.foundation.interaction.Interaction,
    ): Boolean = true
}

// Constants for consistent sizing and theming
private val BOTTOM_NAV_HEIGHT = 80.dp
private val SELECTED_ICON_SIZE = 28.dp
private val UNSELECTED_ICON_SIZE = 24.dp
private const val UNSELECTED_ALPHA = 0.6f
