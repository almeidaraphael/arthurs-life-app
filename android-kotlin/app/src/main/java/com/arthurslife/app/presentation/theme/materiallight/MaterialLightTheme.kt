package com.arthurslife.app.presentation.theme.materiallight

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.ProfileText
import com.arthurslife.app.presentation.theme.components.MaterialThemeIcons
import com.arthurslife.app.presentation.theme.components.SemanticIconType

private val MaterialLightColors = lightColorScheme()

private val StandardShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

object MaterialLightTheme : BaseAppTheme {
    override val colorScheme: ColorScheme = MaterialLightColors
    override val shapes: Shapes = StandardShapes
    override val typography: Typography = Typography()
    override val icons: @Composable (SemanticIconType) -> ImageVector = { type ->
        MaterialThemeIcons.getIconForType(type)
    }
    override val displayName: String = "Material Light"
    override val description: String = "A clean, light Material 3 theme for everyday use."
    override val useOriginalIconColors: Boolean = false
    override val avatarContent: String = "👤"

    // Theme-specific content and labels
    override val taskLabel: String = "Tasks"
    override val tokenLabel: String = "Tokens"
    override val achievementLabel: String = "Badges"
    override val actionSectionTitle: String = "Quick Actions"
    override val roleSelectionPrompt: String = "Who are you?"
    override val profileTitle: String = "Profile"
    override val settingsTitle: String = "Settings"
    override val listItemPrefix: String = "•"
    override val progressTitle: String = "Daily Progress"
    override val displaySettingsText: String = "Theme & Display"
    override val notificationSettingsText: String = "Notifications"
    override val accessibilitySettingsText: String = "Accessibility"
    override val switchToCaregiverText: String = "Switch to Caregiver Mode"
    override val pinRequiredText: String = "PIN required for caregiver access"
    override fun motivationalMessage(): String = "Keep going! You're doing great!"
    override fun roleButtonText(role: UserRole): String = when (role) {
        UserRole.CHILD -> "Child"
        UserRole.CAREGIVER -> "Caregiver"
    }
    override fun statValueFormatter(value: String): String = value

    // Background support
    override val backgroundImage: (@Composable () -> Unit)? = null
    override val backgroundTint: Color? = null

    // Navigation/dialog UI contract implementations
    override val dialogShape: CornerBasedShape = RoundedCornerShape(16.dp)
    override val pinFieldShape: CornerBasedShape = RoundedCornerShape(8.dp)
    override val buttonShape: CornerBasedShape = RoundedCornerShape(12.dp)
    override val pinLabel: String = "PIN Code"
    override val pinEntryPrompt: String = "Enter your PIN to continue"
    override val childAccessPrompt: String = "Child access requires PIN"
    override val cancelButtonText: String = "Cancel"
    override val switchButtonText: String = "Switch"
    override val authenticateButtonText: String = "Authenticate"
    override fun getRoleSwitchHeader(
        targetRole: UserRole,
    ): String = "Switch to ${targetRole.name.replaceFirstChar { it.uppercase() }}"
    override fun pinEntryHeader(
        targetRole: UserRole,
    ): String = "Enter PIN for ${targetRole.name.replaceFirstChar { it.uppercase() }}"

    // Profile customization properties
    override val profileText: ProfileText = ProfileText()
    override val containerColors: ColorScheme = MaterialLightColors
    override val textColors: ColorScheme = MaterialLightColors

    @Composable
    override fun outlinedTextFieldColors(): TextFieldColors = TextFieldDefaults.colors()
}
