package com.arthurslife.app.presentation.theme.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.ArthursLifeTheme
import com.arthurslife.app.presentation.theme.mario.MarioClassicTheme
import com.arthurslife.app.presentation.theme.materialdark.MaterialDarkTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TopBarDialogsTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private var dismissCount = 0
    private var themeClickCount = 0
    private var languageClickCount = 0
    private var switchUsersClickCount = 0
    private var saveProfileCount = 0

    @Before
    fun setup() {
        hiltRule.inject()
        dismissCount = 0
        themeClickCount = 0
        languageClickCount = 0
        switchUsersClickCount = 0
        saveProfileCount = 0
    }

    @Test
    fun settingsDialog_displaysAllOptions() {
        // Given
        val actions = SettingsDialogActions(
            onDismiss = { dismissCount++ },
            onSwitchUsersClick = { switchUsersClickCount++ },
            onLanguageClick = { languageClickCount++ },
            onThemeClick = { themeClickCount++ },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                settingsDialog(
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Switch Users")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Language")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Theme & Display")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun settingsDialog_themeOptionClick_triggersCallback() {
        // Given
        val actions = SettingsDialogActions(
            onDismiss = { dismissCount++ },
            onSwitchUsersClick = { switchUsersClickCount++ },
            onLanguageClick = { languageClickCount++ },
            onThemeClick = { themeClickCount++ },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                settingsDialog(
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithText("Theme & Display")
            .performClick()

        // Then
        assert(themeClickCount == 1)
    }

    @Test
    fun settingsDialog_languageOptionClick_triggersCallback() {
        // Given
        val actions = SettingsDialogActions(
            onDismiss = { dismissCount++ },
            onSwitchUsersClick = { switchUsersClickCount++ },
            onLanguageClick = { languageClickCount++ },
            onThemeClick = { themeClickCount++ },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                settingsDialog(
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithText("Language")
            .performClick()

        // Then
        assert(languageClickCount == 1)
    }

    @Test
    fun settingsDialog_switchUsersClick_triggersCallback() {
        // Given
        val actions = SettingsDialogActions(
            onDismiss = { dismissCount++ },
            onSwitchUsersClick = { switchUsersClickCount++ },
            onLanguageClick = { languageClickCount++ },
            onThemeClick = { themeClickCount++ },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                settingsDialog(
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithText("Switch Users")
            .performClick()

        // Then
        assert(switchUsersClickCount == 1)
    }

    @Test
    fun userProfileDialog_displaysUserInformation() {
        // Given
        val testUser = createTestUser()
        val state = UserProfileDialogState(
            user = testUser,
            editedName = testUser.displayName ?: testUser.name,
        )
        val actions = UserProfileDialogActions(
            onDismiss = { dismissCount++ },
            onSave = { saveProfileCount++ },
            onCancel = { dismissCount++ },
            onChangeAvatar = { },
            onChangePIN = { },
            onNameChange = { },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                userProfileDialog(
                    state = state,
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("User Profile")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Child")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Save")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun userProfileDialog_saveClick_triggersCallback() {
        // Given
        val testUser = createTestUser()
        val state = UserProfileDialogState(
            user = testUser,
            editedName = testUser.displayName ?: testUser.name,
        )
        val actions = UserProfileDialogActions(
            onDismiss = { dismissCount++ },
            onSave = { saveProfileCount++ },
            onCancel = { dismissCount++ },
            onChangeAvatar = { },
            onChangePIN = { },
            onNameChange = { },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                userProfileDialog(
                    state = state,
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithText("Save")
            .performClick()

        // Then
        assert(saveProfileCount == 1)
    }

    @Test
    fun languageSelectorDialog_displaysLanguageOptions() {
        // Given
        val actions = LanguageSelectorDialogActions(
            onDismiss = { dismissCount++ },
            onLanguageSelected = { },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                languageSelectorDialog(
                    currentLanguage = "en-US",
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Language")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("English (US)")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Português (BR)")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelectorDialog_displaysThemeOptions() {
        // Given
        val actions = ThemeSelectorDialogActions(
            onDismiss = { dismissCount++ },
            onThemeSelected = { },
        )
        val availableThemes = listOf(
            ArthursLifeTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeSelectorDialog(
                    currentTheme = ArthursLifeTheme,
                    availableThemes = availableThemes,
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Theme & Display")
            .assertIsDisplayed()

        // Theme options should be displayed
        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertIsDisplayed()
    }

    @Test
    fun childSelectorDialog_displaysChildOptions() {
        // Given
        val actions = ChildSelectorDialogActions(
            onDismiss = { dismissCount++ },
            onChildSelected = { },
        )
        val children = listOf(
            createTestUser().copy(id = "child-1", name = "Emma"),
            createTestUser().copy(id = "child-2", name = "Alex"),
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                childSelectorDialog(
                    children = children,
                    selectedChildId = "child-1",
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Select Child")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Emma")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Alex")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun userSelectorDialog_displaysUserOptions() {
        // Given
        val actions = UserSelectorDialogActions(
            onDismiss = { dismissCount++ },
            onUserSelected = { },
        )
        val users = listOf(
            createTestUser(),
            createTestCaregiverUser(),
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                userSelectorDialog(
                    users = users,
                    currentUserId = "test-child-123",
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Switch User")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Test Child")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Test Caregiver")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun dialogs_withMarioTheme_displayCorrectStyling() {
        // Given
        val actions = SettingsDialogActions(
            onDismiss = { dismissCount++ },
            onSwitchUsersClick = { switchUsersClickCount++ },
            onLanguageClick = { languageClickCount++ },
            onThemeClick = { themeClickCount++ },
        )

        // When
        composeTestRule.setContent {
            MarioClassicTheme {
                settingsDialog(
                    actions = actions,
                    theme = MarioClassicTheme,
                )
            }
        }

        // Then
        // Verify basic elements are still displayed with Mario theme
        composeTestRule
            .onNodeWithText("Settings")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Switch Users")
            .assertIsDisplayed()
    }

    @Test
    fun dialogs_accessibilitySupport_isCorrect() {
        // Given
        val actions = SettingsDialogActions(
            onDismiss = { dismissCount++ },
            onSwitchUsersClick = { switchUsersClickCount++ },
            onLanguageClick = { languageClickCount++ },
            onThemeClick = { themeClickCount++ },
        )

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                settingsDialog(
                    actions = actions,
                    theme = ArthursLifeTheme,
                )
            }
        }

        // Then
        // Check that dialog options have proper accessibility support
        composeTestRule
            .onNode(hasContentDescription("Switch to different user account"))
            .assertIsDisplayed()

        composeTestRule
            .onNode(hasContentDescription("Change app language"))
            .assertIsDisplayed()

        composeTestRule
            .onNode(hasContentDescription("Customize app appearance"))
            .assertIsDisplayed()
    }

    private fun createTestUser() = com.arthurslife.app.domain.user.User(
        id = "test-child-123",
        name = "Test Child",
        displayName = "Test Child",
        role = UserRole.CHILD,
        avatarData = "test_avatar",
        tokenBalance = TokenBalance.fromInt(150),
        pin = com.arthurslife.app.domain.auth.PIN.create("1234").getOrThrow(),
        isAdmin = false,
    )

    private fun createTestCaregiverUser() = com.arthurslife.app.domain.user.User(
        id = "test-caregiver-123",
        name = "Test Caregiver",
        displayName = "Test Caregiver",
        role = UserRole.CAREGIVER,
        avatarData = "caregiver_avatar",
        tokenBalance = TokenBalance.fromInt(0),
        pin = com.arthurslife.app.domain.auth.PIN.create("5678").getOrThrow(),
        isAdmin = true,
    )
}
