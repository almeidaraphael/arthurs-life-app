package com.arthurslife.app.presentation.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Shared Material Design icons for both light and dark themes.
 */
object MaterialThemeIcons {
    @Composable
    @Suppress("CyclomaticComplexMethod") // Simple icon mapping - complexity is unavoidable
    fun getIconForType(semanticType: SemanticIconType): ImageVector {
        return when (semanticType) {
            // Core game/app icons
            SemanticIconType.TOKEN -> Icons.Default.Star
            SemanticIconType.AVATAR -> Icons.Default.AccountCircle
            SemanticIconType.START_TASK -> Icons.Default.PlayArrow
            SemanticIconType.REWARDS -> Icons.Default.ShoppingCart
            SemanticIconType.ACHIEVEMENTS -> Icons.Default.EmojiEvents
            SemanticIconType.PROGRESS_INDICATOR -> Icons.AutoMirrored.Filled.TrendingUp
            SemanticIconType.STREAK_FIRE -> Icons.Default.LocalFireDepartment

            // Navigation icons
            SemanticIconType.BACK_ARROW -> Icons.AutoMirrored.Filled.ArrowBack
            SemanticIconType.EXPAND_MORE -> Icons.Default.ExpandMore
            SemanticIconType.NAVIGATE_NEXT -> Icons.AutoMirrored.Filled.KeyboardArrowRight

            // UI icons
            SemanticIconType.CONSTRUCTION -> Icons.Default.Construction
            SemanticIconType.CHILD_CARE -> Icons.Default.ChildCare
            SemanticIconType.SWITCH_ACCOUNT -> Icons.Default.SwitchAccount
            SemanticIconType.CHECK_SELECTED -> Icons.Default.Check
        }
    }
}
