package com.lemonqwest.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.MainAppNavigation
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.Test

/**
 * Theme-aware navigation tests for MainAppNavigation.
 */
@HiltAndroidTest
class MainAppNavigationThemeTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun navigation_worksWithMarioTheme() {
        resetRoleSwitchTracking()
        setContentWithSpecificTheme("mario_classic") {
            MainAppNavigation(
                userRole = UserRole.CHILD,
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSwitch = { role ->
                    roleSwitchCalled = true
                    switchedToRole = role
                },
            )
        }
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Achievements").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun navigation_worksWithMaterialTheme() {
        resetRoleSwitchTracking()
        setContentWithSpecificTheme("material_light") {
            MainAppNavigation(
                userRole = UserRole.CHILD,
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSwitch = { role ->
                    roleSwitchCalled = true
                    switchedToRole = role
                },
            )
        }
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Achievements").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }
}
