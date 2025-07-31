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
 * Navigation tests for MainAppNavigation with Caregiver role.
 */
@HiltAndroidTest
class MainAppNavigationCaregiverRoleTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun caregiverRole_displaysCaregiverNavigationTabs() {
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
        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("Children").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun caregiverRole_dashboardTabNavigation() {
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
        composeTestRule.onNodeWithText("Dashboard").performClick()
        composeTestRule.onNode(hasContentDescription("Dashboard screen")).assertIsDisplayed()
    }

    @Test
    fun caregiverRole_taskManagementTabNavigation() {
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
        composeTestRule.onNodeWithText("Tasks").performClick()
        composeTestRule.onNode(hasContentDescription("Task management screen")).assertIsDisplayed()
    }
}
