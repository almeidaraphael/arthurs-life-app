package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.BottomNavItem
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ThemeAwareBottomNavigationBarSelectionStateTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun currentRouteShowsSelectedState() {
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/tasks"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Quests").assertIsSelected()
        composeTestRule.onNodeWithText("Home").assertExists()
    }

    @Test
    fun withSelectedItemMaintainsSelection() {
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)
        val currentRoute = "caregiver/progress"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = caregiverItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Rewards").assertIsSelected()
        composeTestRule.onNodeWithText("Dashboard").assertExists()
        composeTestRule.onNodeWithText("Tasks").assertExists()
        composeTestRule.onNodeWithText("Children").assertExists()
    }

    @Test
    fun withDifferentCurrentRoutesUpdatesSelection() {
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        var currentRoute = "child/home"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Home").assertIsSelected()
        // Update current route
        currentRoute = "child/tasks"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Quests").assertIsSelected()
    }
}
