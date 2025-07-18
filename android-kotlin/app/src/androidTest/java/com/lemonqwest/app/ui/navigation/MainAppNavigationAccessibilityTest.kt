package com.lemonqwest.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.hilt.navigation.compose.hiltViewModel
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.MainAppNavigation
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.Test

/**
 * Accessibility compliance tests for MainAppNavigation.
 */
@HiltAndroidTest
class MainAppNavigationAccessibilityTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun navigation_accessibilityLabels() {
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
        composeTestRule.onNode(hasContentDescription("Home navigation")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Tasks navigation")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Rewards navigation")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Achievements navigation")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Profile navigation")).assertIsDisplayed()
    }
}
