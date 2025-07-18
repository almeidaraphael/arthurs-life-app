package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.presentation.components.AvatarSelectionDialogConfig
import com.lemonqwest.app.presentation.components.avatarSelectionDialog
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Accessibility features tests for AvatarSelectionDialog.
 */
@HiltAndroidTest
class AvatarSelectionDialogAccessibilityTest : ComposeUiTestBase() {

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
    fun testDialogAccessibility() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithContentDescription("Avatar selection dialog").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Current avatar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Avatar mario_child").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Avatar luigi_child").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upload Custom").performClick()
        composeTestRule.onNodeWithContentDescription("Take photo with camera").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Choose from gallery").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Cancel avatar selection").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Confirm avatar selection").assertIsDisplayed()
    }
}
