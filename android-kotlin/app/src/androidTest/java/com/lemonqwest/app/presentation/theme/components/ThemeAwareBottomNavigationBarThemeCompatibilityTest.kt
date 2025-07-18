package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.navigation.BottomNavItem
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import com.lemonqwest.app.presentation.theme.mario.MarioClassicTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ThemeAwareBottomNavigationBarThemeCompatibilityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun displaysCorrectlyInLemonQwestTheme() {
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
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quests").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Awards").assertIsDisplayed()
    }

    @Test
    fun displaysCorrectlyInMarioClassicTheme() {
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        composeTestRule.setContent {
            MarioClassicTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quests").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Awards").assertIsDisplayed()
    }
}
