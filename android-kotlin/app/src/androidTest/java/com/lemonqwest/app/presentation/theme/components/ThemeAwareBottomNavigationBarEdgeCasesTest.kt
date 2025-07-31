package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
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
class ThemeAwareBottomNavigationBarEdgeCasesTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun withEmptyItemsDoesNotDisplay() {
        val emptyItems = emptyList<BottomNavItem>()
        val currentRoute = ""
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = emptyItems,
                    currentRoute = currentRoute,
                    onItemClick = {},
                )
            }
        }
        composeTestRule.onNodeWithContentDescription("Bottom Navigation").assertIsNotDisplayed()
    }

    @Test
    fun multipleClicksTriggerCorrectCallbacks() {
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        val clickedRoutes = mutableListOf<String>()
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoutes.add(route) },
                )
            }
        }
        composeTestRule.onNodeWithText("Quests").performClick()
        composeTestRule.onNodeWithText("Rewards").performClick()
        composeTestRule.onNodeWithText("Awards").performClick()
        assert(clickedRoutes.size == 3)
        assert(clickedRoutes[0] == "child/tasks")
        assert(clickedRoutes[1] == "child/rewards")
        assert(clickedRoutes[2] == "child/achievements")
    }
}
