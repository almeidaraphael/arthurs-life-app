package com.arthurslife.app.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.components.SemanticIconType

/**
 * Contract for all app themes in Arthur's Life.
 * Implement this interface for each theme variant.
 */
interface BaseAppTheme {
    val colorScheme: ColorScheme
    val shapes: Shapes
    val typography: Typography
    val icons: @Composable (SemanticIconType) -> ImageVector
    val displayName: String
    val description: String
    val useOriginalIconColors: Boolean // true = use Color.Unspecified for icons
    val avatarContent: String // Emoji or string for avatar

    // Theme-specific content and labels
    val taskLabel: String // "Tasks" vs "Quests"
    val tokenLabel: String // "Tokens" vs "Coins"
    val achievementLabel: String // "Badges" vs "Coins"
    val actionSectionTitle: String // "Quick Actions" vs "Quest Actions"
    val roleSelectionPrompt: String // "Who are you?" vs "Choose Your Character!"
    val profileTitle: String // "Profile" vs "Player Profile"
    val settingsTitle: String // "Settings" vs "Quest Settings"
    val listItemPrefix: String // "•" vs "⭐"
    val progressTitle: String // "Daily Progress" vs "Quest Progress"
    val displaySettingsText: String // "Theme & Display" vs "World & Display Settings"
    val notificationSettingsText: String // "Notifications" vs "Power-Up Alerts"
    val accessibilitySettingsText: String // "Accessibility" vs "Castle Accessibility"
    val switchToCaregiverText: String // "Switch to Caregiver Mode" vs "Switch to Castle Guardian Mode"
    val pinRequiredText: String // "PIN required for caregiver access" vs "Castle code required for guardian access"
    fun motivationalMessage(): String
    fun roleButtonText(role: UserRole): String
    fun statValueFormatter(value: String): String

    // Background support
    val backgroundImage: (@Composable () -> Unit)?
    val backgroundTint: Color?

    // Navigation/dialog UI contract additions
    val dialogShape: androidx.compose.foundation.shape.CornerBasedShape
    val pinFieldShape: androidx.compose.foundation.shape.CornerBasedShape
    val buttonShape: androidx.compose.foundation.shape.CornerBasedShape
    val pinLabel: String
    val pinEntryPrompt: String
    val childAccessPrompt: String
    val cancelButtonText: String
    val switchButtonText: String
    val authenticateButtonText: String
    fun getRoleSwitchHeader(targetRole: UserRole): String
    fun pinEntryHeader(targetRole: UserRole): String

    // Profile customization properties
    val profileText: ProfileText
    val containerColors: ColorScheme
    val textColors: ColorScheme

    @Composable
    fun outlinedTextFieldColors(): TextFieldColors
}

/**
 * Container for profile-related text strings
 */
data class ProfileText(
    val customizationTitle: String = "Customize Profile",
    val displayNameTitle: String = "Display Name",
    val displayNameDescription: String = "Choose how you want to be called",
    val displayNameLabel: String = "Display Name",
    val customizationMenuText: String = "Profile Customization",
    val pinSectionTitle: String = "PIN Settings",
    val pinSetDescription: String = "PIN is currently set",
    val pinNotSetDescription: String = "No PIN set",
    val changePinButton: String = "Change PIN",
    val removePinButton: String = "Remove PIN",
    val currentPinLabel: String = "Current PIN",
    val newPinLabel: String = "New PIN",
    val confirmPinLabel: String = "Confirm PIN",
    val changeButton: String = "Change",
    val removeButton: String = "Remove",
    val cancelButton: String = "Cancel",
    val setPinButton: String = "Set PIN",
    val avatarSectionTitle: String = "Avatar",
    val predefinedAvatarsTitle: String = "Choose Avatar",
    val customAvatarTitle: String = "Custom Photo",
    val favoriteColorTitle: String = "Favorite Color",
    val pinChangeSuccessMessage: String = "PIN changed successfully",
)
