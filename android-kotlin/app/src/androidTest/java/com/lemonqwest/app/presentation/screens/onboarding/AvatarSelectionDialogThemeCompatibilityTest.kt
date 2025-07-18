package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.lemonqwest.app.presentation.components.AvatarSelectionDialogConfig
import com.lemonqwest.app.presentation.components.avatarSelectionDialog
import com.lemonqwest.app.presentation.theme.MarioClassicTheme
import com.lemonqwest.app.presentation.theme.MaterialDarkTheme
import com.lemonqwest.app.presentation.theme.MaterialLightTheme
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Theme compatibility tests for AvatarSelectionDialog.
 */
@HiltAndroidTest
class AvatarSelectionDialogThemeCompatibilityTest : ComposeUiTestBase() {

    private lateinit var mockConfig: AvatarSelectionDialogConfig

    @BeforeEach
    fun setup() {
        mockConfig = AvatarSelectionDialogConfig(
            isVisible = true,
            currentAvatarId = "default_child",
            currentAvatarType = com.lemonqwest.app.domain.user.AvatarType.PREDEFINED,
            onAvatarSelected = { _, _ -> },
            onDismiss = {},
            onConfirm = {},
        )
    }

    @Test
    fun testMaterialLightThemeCompatibility() {
        setContentWithTheme(
            theme = MaterialLightTheme,
        ) { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Select Avatar").assertIsDisplayed()
    }

    @Test
    fun testMaterialDarkThemeCompatibility() {
        setContentWithTheme(
            theme = MaterialDarkTheme,
        ) { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Select Avatar").assertIsDisplayed()
    }

    @Test
    fun testMarioClassicThemeCompatibility() {
        setContentWithTheme(
            theme = MarioClassicTheme,
        ) { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Select Avatar").assertIsDisplayed()
    }
}
