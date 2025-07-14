package com.arthurslife.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.navigation.MainAppNavigation
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive navigation tests for MainAppNavigation.
 *
 * Tests verify:
 * - Role-based navigation structure
 * - Tab navigation functionality
 * - Role switching capabilities
 * - Navigation state management
 * - Theme-aware navigation
 * - Accessibility compliance
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainAppNavigationTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun mainAppNavigation_childRole_displaysChildNavigation() {
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

        // Verify child navigation structure
        composeTestRule.onNodeWithText("Home")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Tasks")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Rewards")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Achievements")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_caregiverRole_displaysCaregiverNavigation() {
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

        // Verify caregiver navigation structure
        composeTestRule.onNodeWithText("Dashboard")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Tasks")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Progress")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Children")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_childRole_homeTabNavigation() {
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

        // Click on Home tab (should be selected by default)
        composeTestRule.onNodeWithText("Home")
            .performClick()

        // Verify home screen content is displayed
        composeTestRule.onNode(hasContentDescription("Token balance card"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_childRole_tasksTabNavigation() {
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

        // Click on Tasks tab
        composeTestRule.onNodeWithText("Tasks")
            .performClick()

        // Verify tasks screen is displayed
        // This assumes the ChildTaskScreen has identifiable content
        composeTestRule.onNode(hasContentDescription("Tasks screen"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_childRole_achievementsTabNavigation() {
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

        // Click on Achievements tab
        composeTestRule.onNodeWithText("Achievements")
            .performClick()

        // Verify achievements screen is displayed
        composeTestRule.onNode(hasContentDescription("Achievements screen"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_childRole_profileTabNavigation() {
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

        // Click on Profile tab
        composeTestRule.onNodeWithText("Profile")
            .performClick()

        // Verify profile screen is displayed
        composeTestRule.onNode(hasContentDescription("Profile screen"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_caregiverRole_dashboardTabNavigation() {
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

        // Click on Dashboard tab (should be selected by default)
        composeTestRule.onNodeWithText("Dashboard")
            .performClick()

        // Verify dashboard screen content is displayed
        composeTestRule.onNode(hasContentDescription("Dashboard screen"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_caregiverRole_taskManagementTabNavigation() {
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

        // Click on Tasks tab
        composeTestRule.onNodeWithText("Tasks")
            .performClick()

        // Verify task management screen is displayed
        composeTestRule.onNode(hasContentDescription("Task management screen"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_tabSelectionStateManagement() {
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

        // Home should be selected by default
        composeTestRule.onNodeWithText("Home")
            .assertIsDisplayed()

        // Navigate to Tasks
        composeTestRule.onNodeWithText("Tasks")
            .performClick()

        // Verify tasks content is shown
        composeTestRule.onNode(hasContentDescription("Tasks screen"))
            .assertIsDisplayed()

        // Navigate back to Home
        composeTestRule.onNodeWithText("Home")
            .performClick()

        // Verify home content is shown again
        composeTestRule.onNode(hasContentDescription("Token balance card"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_worksWithMarioTheme() {
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

        // Verify navigation works with Mario theme
        composeTestRule.onNodeWithText("Home")
            .assertIsDisplayed()

        // Mario theme should use game-appropriate terminology
        composeTestRule.onNodeWithText("Tasks")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Rewards")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Achievements")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_worksWithMaterialTheme() {
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

        // Verify navigation works with Material theme
        composeTestRule.onNodeWithText("Home")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Tasks")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Rewards")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Achievements")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Profile")
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_accessibilityLabels() {
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

        // Verify navigation items have proper accessibility labels
        composeTestRule.onNode(hasContentDescription("Home navigation"))
            .assertIsDisplayed()

        composeTestRule.onNode(hasContentDescription("Tasks navigation"))
            .assertIsDisplayed()

        composeTestRule.onNode(hasContentDescription("Rewards navigation"))
            .assertIsDisplayed()

        composeTestRule.onNode(hasContentDescription("Achievements navigation"))
            .assertIsDisplayed()

        composeTestRule.onNode(hasContentDescription("Profile navigation"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_roleSwitchingDialog() {
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

        // Look for role switching trigger (if present in the navigation)
        // This depends on the implementation of role switching in the navigation
        composeTestRule.onNode(hasContentDescription("Role switch button"))
            .performClick()

        // Verify role switching dialog appears
        composeTestRule.onNode(hasContentDescription("Role switching dialog"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_navigationBarVisibility() {
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

        // Verify navigation bar is visible and contains all expected tabs
        val navigationItems = listOf("Home", "Tasks", "Rewards", "Achievements", "Profile")

        navigationItems.forEach { item ->
            composeTestRule.onNodeWithText(item)
                .assertIsDisplayed()
        }
    }

    @Test
    fun mainAppNavigation_multipleTabSwitches() {
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

        // Perform multiple tab switches to test navigation robustness
        val tabs = listOf("Tasks", "Achievements", "Profile", "Home", "Rewards")

        tabs.forEach { tab ->
            composeTestRule.onNodeWithText(tab)
                .performClick()

            // Small delay to allow navigation to complete
            Thread.sleep(100)
        }

        // Verify we can still navigate normally after multiple switches
        composeTestRule.onNodeWithText("Home")
            .performClick()

        composeTestRule.onNode(hasContentDescription("Token balance card"))
            .assertIsDisplayed()
    }

    @Test
    fun mainAppNavigation_backNavigationHandling() {
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

        // Navigate to different tabs
        composeTestRule.onNodeWithText("Tasks")
            .performClick()

        composeTestRule.onNodeWithText("Achievements")
            .performClick()

        // Navigation should maintain proper state
        // (Back navigation behavior depends on implementation)
        composeTestRule.onNodeWithText("Home")
            .performClick()

        // Should return to home screen
        composeTestRule.onNode(hasContentDescription("Token balance card"))
            .assertIsDisplayed()
    }
}
