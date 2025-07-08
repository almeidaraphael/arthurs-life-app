package com.arthurslife.app.presentation.theme.mario

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Task
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.arthurslife.app.R
import com.arthurslife.app.presentation.theme.components.SemanticIconType

object MarioThemeIcons {
    @Composable
    @Suppress("CyclomaticComplexMethod") // Simple icon mapping - complexity is unavoidable
    fun getIconForType(semanticType: SemanticIconType): ImageVector {
        return when (semanticType) {
            // Core game/app icons - use custom Mario icons
            SemanticIconType.TOKEN -> ImageVector.vectorResource(R.drawable.ic_mario_coin)
            SemanticIconType.AVATAR -> ImageVector.vectorResource(R.drawable.ic_mario_face)
            SemanticIconType.START_TASK -> ImageVector.vectorResource(
                R.drawable.ic_mario_controller,
            )
            SemanticIconType.REWARDS -> ImageVector.vectorResource(
                R.drawable.ic_mario_treasure_chest,
            )
            SemanticIconType.ACHIEVEMENTS -> ImageVector.vectorResource(R.drawable.ic_mario_trophy)
            SemanticIconType.PROGRESS_INDICATOR -> ImageVector.vectorResource(
                R.drawable.ic_mario_star,
            )
            SemanticIconType.STREAK_FIRE -> Icons.Default.LocalFireDepartment

            // Navigation icons - use Material icons with Mario context
            SemanticIconType.BACK_ARROW -> Icons.AutoMirrored.Filled.KeyboardArrowLeft // Warp pipe entrance
            SemanticIconType.EXPAND_MORE -> Icons.Default.ExpandMore // Pipe opening
            SemanticIconType.NAVIGATE_NEXT -> Icons.AutoMirrored.Filled.KeyboardArrowRight // Warp pipe exit

            // UI icons - use Material icons with Mario context
            SemanticIconType.CONSTRUCTION -> Icons.Default.Build // Construction block
            SemanticIconType.CHILD_CARE -> Icons.Default.ChildCare // Player character
            SemanticIconType.SWITCH_ACCOUNT -> Icons.Default.SwitchAccount // Character switch
            SemanticIconType.CHECK_SELECTED -> Icons.Default.Check // Selection check

            // Task management icons - use Material icons with gaming context
            SemanticIconType.EDIT -> Icons.Default.Edit // Edit quest
            SemanticIconType.DELETE -> Icons.Default.Delete // Remove quest
            SemanticIconType.SUCCESS -> Icons.Default.CheckCircle // Quest complete
            SemanticIconType.TASKS -> Icons.Default.Task // Quest list
            SemanticIconType.UNDO -> Icons.AutoMirrored.Filled.Undo // Undo quest completion
        }
    }
}
