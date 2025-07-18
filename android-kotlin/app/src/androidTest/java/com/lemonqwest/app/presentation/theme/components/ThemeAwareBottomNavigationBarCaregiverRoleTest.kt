package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.BottomNavItem
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ThemeAwareBottomNavigationBarCaregiverRoleTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun displaysCorrectLabelsForCaregiverRole() {
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)
        val currentRoute = "caregiver/dashboard"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = caregiverItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Children").assertIsDisplayed()
    }

    @Test
    fun clickingItemTriggersCallbackForCaregiverRole() {
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)
        val currentRoute = "caregiver/dashboard"
        var clickedRoute: String? = null
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = caregiverItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }
        composeTestRule.onNodeWithText("Tasks").performClick()
        assert(clickedRoute == "caregiver/tasks")
    }
}
