package com.lemonqwest.app.presentation.screens.onboarding

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
 * State management, current avatar display, and edge case tests for AvatarSelectionDialog.
 */
@HiltAndroidTest
class AvatarSelectionDialogStateManagementTest : ComposeUiTestBase() {

    private lateinit var mockConfig: AvatarSelectionDialogConfig
    private var selectedAvatarId: String? = null
    private var selectedAvatarType: AvatarType? = null
    private var confirmClicked = false

    @BeforeEach
    fun setup() {
        selectedAvatarId = null
        selectedAvatarType = null
        confirmClicked = false
        mockConfig = AvatarSelectionDialogConfig(
            isVisible = true,
            currentAvatarId = "default_child",
            currentAvatarType = AvatarType.PREDEFINED,
            onAvatarSelected = { avatarId, avatarType ->
                selectedAvatarId = avatarId
                selectedAvatarType = avatarType
            },
            onDismiss = {},
            onConfirm = { confirmClicked = true },
        )
    }

    @Test
    fun testCurrentAvatarDisplay() {
        var currentConfig = mockConfig
        setContentWithTheme { avatarSelectionDialog(config = currentConfig) }
        composeTestRule.onNodeWithContentDescription("Current avatar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Avatar toad_child").performClick()
        currentConfig = currentConfig.copy(currentAvatarId = "toad_child", currentAvatarType = AvatarType.PREDEFINED)
        setContentWithTheme { avatarSelectionDialog(config = currentConfig) }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Current avatar").assertIsDisplayed()
    }

    @Test
    fun testAvatarSelectionStateManagement() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Upload Custom").performClick()
        composeTestRule.onNodeWithContentDescription("Choose from gallery").performClick()
        assert(selectedAvatarType == AvatarType.CUSTOM)
        composeTestRule.onNodeWithText("Choose from Gallery").performClick()
        composeTestRule.onNodeWithContentDescription("Avatar star_child").performClick()
        assert(selectedAvatarId == "star_child")
        assert(selectedAvatarType == AvatarType.PREDEFINED)
    }

    @Test
    fun testDialogEdgeCases() {
        val noAvatarConfig = mockConfig.copy(
            currentAvatarId = "",
            currentAvatarType = AvatarType.PREDEFINED,
        )
        setContentWithTheme { avatarSelectionDialog(config = noAvatarConfig) }
        composeTestRule.onNodeWithText("Select Avatar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Avatar mario_child").performClick()
        composeTestRule.onNodeWithContentDescription("Avatar luigi_child").performClick()
        composeTestRule.onNodeWithContentDescription("Avatar peach_child").performClick()
        assert(selectedAvatarId == "peach_child")
        composeTestRule.onNodeWithText("Confirm").performClick()
        assert(confirmClicked)
    }
}
