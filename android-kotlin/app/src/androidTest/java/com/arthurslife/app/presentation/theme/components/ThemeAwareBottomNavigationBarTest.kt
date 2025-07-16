package com.arthurslife.app.presentation.theme.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arthurslife.app.domain.auth.UserRole
import com.arthurslife.app.presentation.navigation.BottomNavItem
import com.arthurslife.app.presentation.theme.ArthursLifeTheme
import com.arthurslife.app.presentation.theme.mario.MarioClassicTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ThemeAwareBottomNavigationBarTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun themeAwareBottomNavigationBar_withChildItems_displaysCorrectLabels() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quests").assertIsDisplayed()
        composeTestRule.onNodeWithText("Power-ups").assertIsDisplayed()
        composeTestRule.onNodeWithText("Awards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun themeAwareBottomNavigationBar_withCaregiverItems_displaysCorrectLabels() {
        // Given
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)
        val currentRoute = "caregiver/dashboard"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = caregiverItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tasks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("Children").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun themeAwareBottomNavigationBar_withEmptyItems_doesNotDisplay() {
        // Given
        val emptyItems = emptyList<BottomNavItem>()
        val currentRoute = ""
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = emptyItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNode(hasContentDescription("Bottom Navigation")).assertIsNotDisplayed()
    }

    @Test
    fun themeAwareBottomNavigationBar_clickingItem_triggersCallback() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Quests").performClick()

        // Then
        assert(clickedRoute == "child/tasks")
    }

    @Test
    fun themeAwareBottomNavigationBar_currentRoute_showsSelectedState() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/tasks"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Quests").assertIsSelected()
        composeTestRule.onNodeWithText("Home").assertExists()
    }

    @Test
    fun themeAwareBottomNavigationBar_accessibilityContentDescriptions_areCorrect() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Bottom Navigation").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Home").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Quests").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Power-ups").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Awards").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Navigate to Profile").assertIsDisplayed()
    }

    @Test
    fun themeAwareBottomNavigationBar_accessibilityClickActions_areAvailable() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Navigate to Home").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Quests").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Power-ups").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Awards").assertHasClickAction()
        composeTestRule.onNodeWithContentDescription("Navigate to Profile").assertHasClickAction()
    }

    @Test
    fun themeAwareBottomNavigationBar_inMarioClassicTheme_displaysCorrectly() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            MarioClassicTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quests").assertIsDisplayed()
        composeTestRule.onNodeWithText("Power-ups").assertIsDisplayed()
        composeTestRule.onNodeWithText("Awards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun themeAwareBottomNavigationBar_multipleClicks_triggerCorrectCallbacks() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        val clickedRoutes = mutableListOf<String>()

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoutes.add(route) },
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("Quests").performClick()
        composeTestRule.onNodeWithText("Power-ups").performClick()
        composeTestRule.onNodeWithText("Awards").performClick()

        // Then
        assert(clickedRoutes.size == 3)
        assert(clickedRoutes[0] == "child/tasks")
        assert(clickedRoutes[1] == "child/rewards")
        assert(clickedRoutes[2] == "child/achievements")
    }

    @Test
    fun themeAwareBottomNavigationBar_withSelectedItem_maintainsSelection() {
        // Given
        val caregiverItems = BottomNavItem.getItemsForRole(UserRole.CAREGIVER)
        val currentRoute = "caregiver/progress"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = caregiverItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Progress").assertIsSelected()
        composeTestRule.onNodeWithText("Dashboard").assertExists()
        composeTestRule.onNodeWithText("Tasks").assertExists()
        composeTestRule.onNodeWithText("Children").assertExists()
        composeTestRule.onNodeWithText("Profile").assertExists()
    }

    @Test
    fun themeAwareBottomNavigationBar_accessibilitySemantics_includeCorrectRoles() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        val currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        // Verify that all navigation items have proper accessibility semantics
        childItems.forEach { item ->
            val expectedContentDescription = "Navigate to ${item.label}"
            composeTestRule
                .onNodeWithContentDescription(expectedContentDescription)
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    @Test
    fun themeAwareBottomNavigationBar_withDifferentCurrentRoutes_updatesSelection() {
        // Given
        val childItems = BottomNavItem.getItemsForRole(UserRole.CHILD)
        var currentRoute = "child/home"
        var clickedRoute: String? = null

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Home").assertIsSelected()

        // When - Update current route
        currentRoute = "child/tasks"
        composeTestRule.setContent {
            ArthursLifeTheme {
                themeAwareBottomNavigationBar(
                    items = childItems,
                    currentRoute = currentRoute,
                    onItemClick = { route -> clickedRoute = route },
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Quests").assertIsSelected()
    }
}
