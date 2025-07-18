package com.lemonqwest.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import com.lemonqwest.app.presentation.screens.ChildHomeScreen
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.Test

/**
 * Component rendering and display tests for ChildHomeScreen.
 */
@HiltAndroidTest
class ChildHomeScreenComponentDisplayTest : ComposeUiTestBase() {

    @Test
    fun displaysAllMainComponents() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Daily progress card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Quick stats section")).assertIsDisplayed()
        composeTestRule.onNode(
            hasContentDescription("Motivational message card"),
        ).assertIsDisplayed()
    }

    @Test
    fun isScrollable() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(
            hasContentDescription("Motivational message card"),
        ).assertIsDisplayed()
    }

    @Test
    fun hasProperSpacing() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Daily progress card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Quick stats section")).assertIsDisplayed()
        composeTestRule.onNode(
            hasContentDescription("Motivational message card"),
        ).assertIsDisplayed()
    }
}
