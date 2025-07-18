package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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
 * Dialog display, controls, dismissal, and confirmation tests for AvatarSelectionDialog.
 */
@HiltAndroidTest
class AvatarSelectionDialogDisplayTest : ComposeUiTestBase() {

    private lateinit var mockConfig: AvatarSelectionDialogConfig
    private var dialogDismissed = false
    private var confirmClicked = false

    @BeforeEach
    fun setup() {
        dialogDismissed = false
        confirmClicked = false
        mockConfig = AvatarSelectionDialogConfig(
            isVisible = true,
            currentAvatarId = "default_child",
            currentAvatarType = com.lemonqwest.app.domain.user.AvatarType.PREDEFINED,
            onAvatarSelected = { _, _ -> },
            onDismiss = { dialogDismissed = true },
            onConfirm = { confirmClicked = true },
        )
    }

    @Test
    fun testAvatarDialogDisplay() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Select Avatar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Choose from Gallery").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Current avatar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun testDialogDismissal() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Cancel").performClick()
        assert(dialogDismissed) { "Dialog should be dismissed when cancel is clicked" }
        dialogDismissed = false
        val updatedConfig = mockConfig.copy(isVisible = false)
        setContentWithTheme { avatarSelectionDialog(config = updatedConfig) }
        composeTestRule.onNodeWithText("Select Avatar").assertIsNotDisplayed()
    }

    @Test
    fun testDialogConfirmation() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithText("Confirm").performClick()
        assert(confirmClicked) { "Confirm callback should be triggered" }
    }
}
