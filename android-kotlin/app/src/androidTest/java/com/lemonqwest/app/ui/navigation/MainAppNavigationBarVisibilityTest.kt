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
 * Navigation bar visibility tests for MainAppNavigation.
 */
@HiltAndroidTest
class MainAppNavigationBarVisibilityTest : ComposeUiTestBase() {

    private var roleSwitchCalled = false
    private var switchedToRole: UserRole? = null

    private fun resetRoleSwitchTracking() {
        roleSwitchCalled = false
        switchedToRole = null
    }

    @Test
    fun navigationBarVisibility_childRole() {
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
        val navigationItems = listOf("Home", "Tasks", "Rewards", "Achievements", "Profile")
        navigationItems.forEach { item ->
            composeTestRule.onNodeWithText(item).assertIsDisplayed()
        }
    }
}
