package com.lemonqwest.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.MainAppNavigation
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.Test

/**
 * Tab navigation tests for MainAppNavigation.
 */
@HiltAndroidTest
class MainAppNavigationTabNavigationTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun tabSelectionStateManagement_childRole() {
        resetRoleSwitchTracking()
        setContentWithTheme {
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
        composeTestRule.onNodeWithText("Tasks").performClick()
        composeTestRule.onNode(hasContentDescription("Tasks screen")).assertIsDisplayed()
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
    }

    @Test
    fun tabSelectionStateManagement_caregiverRole() {
        resetRoleSwitchTracking()
        setContentWithTheme {
            MainAppNavigation(
                userRole = UserRole.CAREGIVER,
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSwitch = { role ->
                    roleSwitchCalled = true
                    switchedToRole = role
                },
            )
        }
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tasks").performClick()
        composeTestRule.onNode(hasContentDescription("Task management screen")).assertIsDisplayed()
        composeTestRule.onNodeWithText("Dashboard").performClick()
        composeTestRule.onNode(hasContentDescription("Dashboard screen")).assertIsDisplayed()
    }
}
