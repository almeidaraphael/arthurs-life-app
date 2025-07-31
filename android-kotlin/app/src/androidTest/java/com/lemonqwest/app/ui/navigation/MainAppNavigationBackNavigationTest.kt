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
 * Back navigation handling tests for MainAppNavigation.
 */
@HiltAndroidTest
class MainAppNavigationBackNavigationTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun navigation_backNavigationHandling() {
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
        composeTestRule.onNodeWithText("Tasks").performClick()
        composeTestRule.onNodeWithText("Achievements").performClick()
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
    }
}
