package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import com.lemonqwest.app.domain.user.AvatarType
import com.lemonqwest.app.presentation.components.AvatarSelectionDialogConfig
import com.lemonqwest.app.presentation.components.avatarSelectionDialog
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Predefined avatar selection and display tests for AvatarSelectionDialog.
 */
@HiltAndroidTest
class AvatarSelectionDialogPredefinedAvatarTest : ComposeUiTestBase() {

    private lateinit var mockConfig: AvatarSelectionDialogConfig
    private var selectedAvatarId: String? = null
    private var selectedAvatarType: AvatarType? = null

    @BeforeEach
    fun setup() {
        selectedAvatarId = null
        selectedAvatarType = null
        mockConfig = AvatarSelectionDialogConfig(
            isVisible = true,
            currentAvatarId = "default_child",
            currentAvatarType = AvatarType.PREDEFINED,
            onAvatarSelected = { avatarId, avatarType ->
                selectedAvatarId = avatarId
                selectedAvatarType = avatarType
            },
            onDismiss = {},
            onConfirm = {},
        )
    }

    @Test
    fun testPredefinedAvatarSelection() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        composeTestRule.onNodeWithContentDescription("Avatar mario_child")
            .assertIsDisplayed().assertHasClickAction().performClick()
        assert(selectedAvatarId == "mario_child")
        assert(selectedAvatarType == AvatarType.PREDEFINED)
        composeTestRule.onNodeWithContentDescription("Avatar luigi_child").performClick()
        assert(selectedAvatarId == "luigi_child")
    }

    @Test
    fun testAllPredefinedAvatarsDisplayed() {
        setContentWithTheme { avatarSelectionDialog(config = mockConfig) }
        val expectedAvatars = listOf(
            "mario_child", "luigi_child", "peach_child", "toad_child", "koopa_child",
            "goomba_child", "star_child", "mushroom_child", "default_child",
        )
        expectedAvatars.forEach { avatarId ->
            composeTestRule.onNodeWithContentDescription("Avatar $avatarId")
                .assertIsDisplayed().assertHasClickAction()
        }
    }
}
