package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.user.AvatarType
import com.lemonqwest.app.presentation.components.AvatarSelectionDialogConfig
import com.lemonqwest.app.presentation.components.avatarSelectionDialog
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Custom avatar options and display tests for AvatarSelectionDialog.
 */
@HiltAndroidTest
class AvatarSelectionDialogCustomAvatarTest : ComposeUiTestBase() {

    private lateinit var mockConfig: AvatarSelectionDialogConfig
    private var selectedAvatarType: AvatarType? = null

    @BeforeEach
    fun setup() {
        selectedAvatarType = null
        mockConfig = AvatarSelectionDialogConfig(
            isVisible = true,
            currentAvatarId = "default_child",
            currentAvatarType = AvatarType.PREDEFINED,
            onAvatarSelected = { _, avatarType -> selectedAvatarType = avatarType },
            onDismiss = {},
            onConfirm = {},
        )
    }

    @Test
    fun testCustomAvatarOptions() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Upload Custom").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Take photo with camera")
            .assertIsDisplayed().assertHasClickAction().performClick()
        composeTestRule.onNodeWithContentDescription("Choose from gallery")
            .assertIsDisplayed().assertHasClickAction()
        assert(selectedAvatarType == AvatarType.CUSTOM)
    }

    @Test
    fun testCustomAvatarDisplay() {
        val customConfig = mockConfig.copy(
            currentAvatarType = AvatarType.CUSTOM,
            currentAvatarId = "custom_base64_data",
        )
        setContentWithTheme { avatarSelectionDialog(config = customConfig) }
        composeTestRule.onNodeWithContentDescription("Current avatar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Upload Custom").performClick()
        composeTestRule.onNodeWithContentDescription("Take photo with camera").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Choose from gallery").assertIsDisplayed()
    }
}
