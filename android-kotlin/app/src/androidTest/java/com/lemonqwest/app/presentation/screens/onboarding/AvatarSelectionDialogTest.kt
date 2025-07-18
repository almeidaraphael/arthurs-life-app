package com.lemonqwest.app.presentation.screens.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.user.AvatarType
import com.lemonqwest.app.presentation.components.AvatarSelectionDialogConfig
import com.lemonqwest.app.presentation.components.avatarSelectionDialog
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Compose UI tests for avatar selection dialog component.
 *
 * Tests the avatar selection dialog functionality including:
 * - Dialog display and dismissal
 * - Predefined avatar selection
 * - Custom avatar options (camera/gallery)
 * - Avatar type switching
 * - User interaction and state management
 * - Accessibility features
 * - Theme compatibility
 */
@HiltAndroidTest
class AvatarSelectionDialogTest : ComposeUiTestBase() {

    @get:Rule(order = 0)
    override val hiltRule = HiltAndroidRule(this)

    private lateinit var mockConfig: AvatarSelectionDialogConfig
    private var selectedAvatarId: String? = null
    private var selectedAvatarType: AvatarType? = null
    private var dialogDismissed = false
    private var confirmClicked = false

    @BeforeEach
    override fun setup() {
        super.setup()

        selectedAvatarId = null
        selectedAvatarType = null
        dialogDismissed = false
        confirmClicked = false

        mockConfig = AvatarSelectionDialogConfig(
            isVisible = true,
            currentAvatarId = "default_child",
            currentAvatarType = AvatarType.PREDEFINED,
            onAvatarSelected = { avatarId, avatarType ->
                selectedAvatarId = avatarId
                selectedAvatarType = avatarType
            },
            onDismiss = { dialogDismissed = true },
            onConfirm = { confirmClicked = true },
        )
    }

    /**
     * Tests basic avatar selection dialog display.
     *
     * Given: Avatar selection dialog is configured to be visible
     * When: Dialog is displayed
     * Then: Dialog content and controls are visible
     */
    @Test
    fun testAvatarDialogDisplay() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Verify dialog is displayed
        composeTestRule.onNodeWithText("Select Avatar")
            .assertIsDisplayed()

        // Verify predefined avatars section
        composeTestRule.onNodeWithText("Choose from Gallery")
            .assertIsDisplayed()

        // Verify current avatar display
        composeTestRule.onNodeWithContentDescription("Current avatar")
            .assertIsDisplayed()

        // Verify action buttons
        composeTestRule.onNodeWithText("Cancel")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithText("Confirm")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    /**
     * Tests predefined avatar selection functionality.
     *
     * Given: Dialog is displayed with predefined avatars
     * When: User selects a predefined avatar
     * Then: Avatar selection is updated and callback is triggered
     */
    @Test
    fun testPredefinedAvatarSelection() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Select Mario avatar
        composeTestRule.onNodeWithContentDescription("Avatar mario_child")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        // Verify selection callback was triggered
        assert(selectedAvatarId == "mario_child") {
            "Expected mario_child, but got $selectedAvatarId"
        }
        assert(selectedAvatarType == AvatarType.PREDEFINED) {
            "Expected PREDEFINED type, but got $selectedAvatarType"
        }

        // Test another avatar selection
        composeTestRule.onNodeWithContentDescription("Avatar luigi_child")
            .performClick()

        assert(selectedAvatarId == "luigi_child") {
            "Expected luigi_child, but got $selectedAvatarId"
        }
    }

    /**
     * Tests all available predefined avatars are displayed.
     *
     * Given: Dialog shows predefined avatar options
     * When: Dialog is displayed
     * Then: All predefined avatars are visible and selectable
     */
    @Test
    fun testAllPredefinedAvatarsDisplayed() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Verify all predefined child avatars are displayed
        val expectedAvatars = listOf(
            "mario_child",
            "luigi_child",
            "peach_child",
            "toad_child",
            "koopa_child",
            "goomba_child",
            "star_child",
            "mushroom_child",
            "default_child",
        )

        expectedAvatars.forEach { avatarId ->
            composeTestRule.onNodeWithContentDescription("Avatar $avatarId")
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    /**
     * Tests custom avatar options functionality.
     *
     * Given: Dialog is displayed
     * When: User selects custom avatar options
     * Then: Camera and gallery options are available
     */
    @Test
    fun testCustomAvatarOptions() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Switch to custom avatar tab
        composeTestRule.onNodeWithText("Upload Custom")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Verify custom avatar options are displayed
        composeTestRule.onNodeWithContentDescription("Take photo with camera")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithContentDescription("Choose from gallery")
            .assertIsDisplayed()
            .assertHasClickAction()

        // Test camera option
        composeTestRule.onNodeWithContentDescription("Take photo with camera")
            .performClick()

        // Verify custom avatar type selection
        assert(selectedAvatarType == AvatarType.CUSTOM) {
            "Expected CUSTOM type when camera is selected"
        }
    }

    /**
     * Tests dialog dismissal functionality.
     *
     * Given: Dialog is displayed
     * When: User clicks cancel or dismisses dialog
     * Then: Dialog is dismissed without saving changes
     */
    @Test
    fun testDialogDismissal() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Test cancel button
        composeTestRule.onNodeWithText("Cancel")
            .performClick()

        assert(dialogDismissed) {
            "Dialog should be dismissed when cancel is clicked"
        }

        // Reset for next test
        dialogDismissed = false

        // Test dismissing without cancel (e.g., clicking outside)
        val updatedConfig = mockConfig.copy(isVisible = false)
        setContentWithTheme {
            avatarSelectionDialog(config = updatedConfig)
        }

        // Dialog should not be visible
        composeTestRule.onNodeWithText("Select Avatar")
            .assertIsNotDisplayed()
    }

    /**
     * Tests dialog confirmation functionality.
     *
     * Given: User has selected an avatar
     * When: User clicks confirm button
     * Then: Selection is confirmed and dialog is closed
     */
    @Test
    fun testDialogConfirmation() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Select an avatar first
        composeTestRule.onNodeWithContentDescription("Avatar peach_child")
            .performClick()

        // Confirm selection
        composeTestRule.onNodeWithText("Confirm")
            .performClick()

        assert(confirmClicked) {
            "Confirm callback should be triggered"
        }
        assert(selectedAvatarId == "peach_child") {
            "Selected avatar should be peach_child"
        }
    }

    /**
     * Tests current avatar display updates.
     *
     * Given: Dialog shows current avatar
     * When: User selects different avatars
     * Then: Current avatar display updates to show selection
     */
    @Test
    fun testCurrentAvatarDisplay() {
        var currentConfig = mockConfig

        setContentWithTheme {
            avatarSelectionDialog(config = currentConfig)
        }

        // Verify initial current avatar
        composeTestRule.onNodeWithContentDescription("Current avatar")
            .assertIsDisplayed()

        // Select new avatar and update config
        composeTestRule.onNodeWithContentDescription("Avatar toad_child")
            .performClick()

        // Update config to reflect new selection
        currentConfig = currentConfig.copy(
            currentAvatarId = "toad_child",
            currentAvatarType = AvatarType.PREDEFINED,
        )

        setContentWithTheme {
            avatarSelectionDialog(config = currentConfig)
        }

        composeTestRule.waitForIdle()

        // Current avatar display should reflect the new selection
        composeTestRule.onNodeWithContentDescription("Current avatar")
            .assertIsDisplayed()
    }

    /**
     * Tests avatar selection state management.
     *
     * Given: Dialog manages avatar selection state
     * When: User interacts with different avatar options
     * Then: State is updated correctly and consistently
     */
    @Test
    fun testAvatarSelectionStateManagement() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Test switching between predefined and custom
        composeTestRule.onNodeWithText("Upload Custom")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Choose from gallery")
            .performClick()

        assert(selectedAvatarType == AvatarType.CUSTOM) {
            "Avatar type should switch to CUSTOM"
        }

        // Switch back to predefined
        composeTestRule.onNodeWithText("Choose from Gallery")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Avatar star_child")
            .performClick()

        assert(selectedAvatarId == "star_child") {
            "Should be able to select predefined after custom"
        }
        assert(selectedAvatarType == AvatarType.PREDEFINED) {
            "Avatar type should switch back to PREDEFINED"
        }
    }

    /**
     * Tests dialog accessibility features.
     *
     * Given: Dialog is displayed
     * When: Accessibility services interact with dialog
     * Then: Proper content descriptions and semantic roles are provided
     */
    @Test
    fun testDialogAccessibility() {
        setContentWithTheme {
            avatarSelectionDialog(config = mockConfig)
        }

        // Verify accessibility labels for main components
        composeTestRule.onNodeWithContentDescription("Avatar selection dialog")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Current avatar")
            .assertIsDisplayed()

        // Verify individual avatar accessibility
        composeTestRule.onNodeWithContentDescription("Avatar mario_child")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Avatar luigi_child")
            .assertIsDisplayed()

        // Verify custom option accessibility
        composeTestRule.onNodeWithText("Upload Custom")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Take photo with camera")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Choose from gallery")
            .assertIsDisplayed()

        // Verify action button accessibility
        composeTestRule.onNodeWithContentDescription("Cancel avatar selection")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Confirm avatar selection")
            .assertIsDisplayed()
    }

    /**
     * Tests dialog theme compatibility.
     *
     * Given: Dialog is displayed with different themes
     * When: Theme is applied
     * Then: Dialog renders correctly with theme-appropriate styling
     */
    @Test
    fun testDialogThemeCompatibility() {
        // Test with Mario Classic theme
        setContentWithSpecificTheme("mario_classic") {
            avatarSelectionDialog(config = mockConfig)
        }

        composeTestRule.onNodeWithText("Select Avatar")
            .assertIsDisplayed()

        // Test with Material Light theme
        setContentWithSpecificTheme("material_light") {
            avatarSelectionDialog(config = mockConfig)
        }

        composeTestRule.onNodeWithText("Select Avatar")
            .assertIsDisplayed()

        // Test with Material Dark theme
        setContentWithSpecificTheme("material_dark") {
            avatarSelectionDialog(config = mockConfig)
        }

        composeTestRule.onNodeWithText("Select Avatar")
            .assertIsDisplayed()

        // Verify theme-specific elements render correctly
        composeTestRule.onNodeWithContentDescription("Avatar mario_child")
            .assertIsDisplayed()
    }

    /**
     * Tests dialog interaction edge cases.
     *
     * Given: Dialog is in various states
     * When: User performs edge case interactions
     * Then: Dialog handles edge cases gracefully
     */
    @Test
    fun testDialogEdgeCases() {
        // Test dialog with no initial avatar
        val noAvatarConfig = mockConfig.copy(
            currentAvatarId = "",
            currentAvatarType = AvatarType.PREDEFINED,
        )

        setContentWithTheme {
            avatarSelectionDialog(config = noAvatarConfig)
        }

        // Dialog should still display correctly
        composeTestRule.onNodeWithText("Select Avatar")
            .assertIsDisplayed()

        // Test rapid avatar selection changes
        composeTestRule.onNodeWithContentDescription("Avatar mario_child")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Avatar luigi_child")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Avatar peach_child")
            .performClick()

        // Should end up with the last selection
        assert(selectedAvatarId == "peach_child") {
            "Should handle rapid selection changes correctly"
        }

        // Test confirm without selection change
        composeTestRule.onNodeWithText("Confirm")
            .performClick()

        assert(confirmClicked) {
            "Should be able to confirm even without changing selection"
        }
    }

    /**
     * Tests dialog with custom avatar display.
     *
     * Given: Dialog is configured with custom avatar
     * When: Dialog displays custom avatar state
     * Then: Custom avatar is shown correctly
     */
    @Test
    fun testCustomAvatarDisplay() {
        val customConfig = mockConfig.copy(
            currentAvatarType = AvatarType.CUSTOM,
            currentAvatarId = "custom_base64_data",
        )

        setContentWithTheme {
            avatarSelectionDialog(config = customConfig)
        }

        // Verify custom avatar is displayed
        composeTestRule.onNodeWithContentDescription("Current avatar")
            .assertIsDisplayed()

        // Custom avatar options should be available
        composeTestRule.onNodeWithText("Upload Custom")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Take photo with camera")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Choose from gallery")
            .assertIsDisplayed()
    }
}
