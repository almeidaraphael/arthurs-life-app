package com.lemonqwest.app.presentation.theme.mario

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.ProfileText
import com.lemonqwest.app.presentation.theme.components.SemanticIconType

private val MARIO_RED = Color(0xFFE60012)
private val MARIO_BLUE = Color(0xFF0066CC)
private val MARIO_GREEN = Color(0xFF00A652)
private val MARIO_YELLOW = Color(0xFFFFD700)
private val MARIO_ORANGE = Color(0xFFFF8C00)

private val MarioClassicColors = lightColorScheme(
    primary = MARIO_RED,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE6E6), // Light red background with proper contrast
    onPrimaryContainer = Color(0xFF8B0000), // Dark red text for accessibility
    secondary = MARIO_BLUE,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6F0FF), // Light blue background with proper contrast
    onSecondaryContainer = Color(0xFF003366), // Dark blue text for accessibility
    tertiary = MARIO_GREEN,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6F5E6), // Light green background with proper contrast
    onTertiaryContainer = Color(0xFF004D00), // Dark green text for accessibility
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFFFF8E1), // Light yellow background with proper contrast
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF8B6914), // Darker orange for better contrast
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val PixelShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)

object MarioTheme : BaseAppTheme {
    override val colorScheme: ColorScheme = MarioClassicColors
    override val shapes: Shapes = PixelShapes
    override val typography: Typography = Typography().copy(
        headlineLarge = Typography().headlineLarge.copy(fontFamily = FontFamily.Monospace),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = FontFamily.Monospace),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = FontFamily.Monospace),
        titleLarge = Typography().titleLarge.copy(fontFamily = FontFamily.Monospace),
        titleMedium = Typography().titleMedium.copy(fontFamily = FontFamily.Monospace),
        titleSmall = Typography().titleSmall.copy(fontFamily = FontFamily.Monospace),
    )
    override val icons: @Composable (
        SemanticIconType,
    ) -> ImageVector = { type ->
        MarioThemeIcons.getIconForType(
            type,
        )
    }
    override val displayName: String = "Mario Classic"
    override val description: String = "A playful Mario-inspired theme with bold colors and pixel shapes."
    override val useOriginalIconColors: Boolean = true
    override val avatarContent: String = "🍄"

    // Theme-specific content and labels
    override val taskLabel: String = "Quests"
    override val tokenLabel: String = "Coins"
    override val achievementLabel: String = "Coins"
    override val actionSectionTitle: String = "Quest Actions"
    override val roleSelectionPrompt: String = "Choose Your Character!"
    override val profileTitle: String = "Player Profile"
    override val settingsTitle: String = "Quest Settings"
    override val listItemPrefix: String = "⭐"
    override val progressTitle: String = "Quest Progress"
    override val displaySettingsText: String = "World & Display Settings"
    override val notificationSettingsText: String = "Power-Up Alerts"
    override val accessibilitySettingsText: String = "Castle Accessibility"
    override val switchToCaregiverText: String = "Switch to Castle Guardian Mode"
    override val pinRequiredText: String = "Castle code required for guardian access"
    override fun motivationalMessage(): String = "Keep collecting coins! You're on fire! 🔥"
    override fun roleButtonText(role: UserRole): String = when (role) {
        UserRole.CHILD -> "Player"
        UserRole.CAREGIVER -> "Castle Guardian"
    }
    override fun statValueFormatter(value: String): String = "⭐ $value"

    // Background support
    override val backgroundImage: (@Composable () -> Unit)? = null
    override val backgroundTint: Color = MARIO_YELLOW.copy(alpha = 0.05f)

    // Navigation/dialog UI contract implementations
    override val dialogShape: CornerBasedShape = RoundedCornerShape(0.dp)
    override val pinFieldShape: CornerBasedShape = RoundedCornerShape(0.dp)
    override val buttonShape: CornerBasedShape = RoundedCornerShape(0.dp)
    override val pinLabel: String = "Castle PIN"
    override val pinEntryPrompt: String = "Enter your castle PIN to continue"
    override val childAccessPrompt: String = "Castle access requires PIN"
    override val cancelButtonText: String = "Back"
    override val switchButtonText: String = "Warp"
    override val authenticateButtonText: String = "Enter Castle"
    override fun getRoleSwitchHeader(
        targetRole: UserRole,
    ): String = "Warp to ${targetRole.name.replaceFirstChar { it.uppercase() }}"
    override fun pinEntryHeader(
        targetRole: UserRole,
    ): String = "Castle PIN for ${targetRole.name.replaceFirstChar { it.uppercase() }}"

    // Profile customization properties
    override val profileText: ProfileText = ProfileText(
        customizationTitle = "Customize Player Profile",
        displayNameTitle = "Player Name",
        displayNameDescription = "Choose your quest name",
        displayNameLabel = "Player Name",
        customizationMenuText = "Player Customization",
        pinSectionTitle = "Castle PIN Settings",
        pinSetDescription = "Castle PIN is currently set",
        pinNotSetDescription = "No Castle PIN set",
        changePinButton = "Change Castle PIN",
        removePinButton = "Remove Castle PIN",
        currentPinLabel = "Current Castle PIN",
        newPinLabel = "New Castle PIN",
        confirmPinLabel = "Confirm Castle PIN",
        changeButton = "Change",
        removeButton = "Remove",
        cancelButton = "Cancel",
        setPinButton = "Set Castle PIN",
        avatarSectionTitle = "Character Avatar",
        predefinedAvatarsTitle = "Choose Character",
        customAvatarTitle = "Custom Character Photo",
        favoriteColorTitle = "Favorite Power-Up Color",
        pinChangeSuccessMessage = "Castle PIN changed successfully!",
    )

    override val containerColors: ColorScheme = MarioClassicColors
    override val textColors: ColorScheme = MarioClassicColors

    @Composable
    override fun outlinedTextFieldColors(): TextFieldColors = TextFieldDefaults.colors(
        focusedIndicatorColor = MARIO_RED,
        focusedLabelColor = MARIO_RED,
        cursorColor = MARIO_RED,
        unfocusedIndicatorColor = MARIO_ORANGE.copy(alpha = 0.6f),
        unfocusedLabelColor = MARIO_ORANGE.copy(alpha = 0.8f),
    )
}
