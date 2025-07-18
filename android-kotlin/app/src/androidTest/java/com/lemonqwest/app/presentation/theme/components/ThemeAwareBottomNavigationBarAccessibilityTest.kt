package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.BottomNavItem
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ThemeAwareBottomNavigationBarAccessibilityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun accessibilityContentDescriptionsAreCorrect() {
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Bottom Navigation").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Home").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Quests").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Awards").assertIsDisplayed()
    }

    @Test
    fun accessibilityClickActionsAreAvailable() {
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Navigate to Home").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Quests").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Rewards").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Awards").assertHasClickAction()
    }
}
