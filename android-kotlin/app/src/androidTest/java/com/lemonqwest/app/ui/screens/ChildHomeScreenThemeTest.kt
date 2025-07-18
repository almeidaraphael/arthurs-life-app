package com.lemonqwest.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import com.lemonqwest.app.presentation.screens.ChildHomeScreen
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.Test

/**
 * Theme-aware behavior tests for ChildHomeScreen.
 */
@HiltAndroidTest
class ChildHomeScreenThemeTest : ComposeUiTestBase() {

    @Test
    fun worksWithMarioTheme() {
        setContentWithSpecificTheme("mario_classic") {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Daily progress card")).assertIsDisplayed()
        composeTestRule.onNode(hasText("Coins", ignoreCase = true)).assertIsDisplayed()
    }

    @Test
    fun worksWithMaterialLightTheme() {
        setContentWithSpecificTheme("material_light") {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Daily progress card")).assertIsDisplayed()
        composeTestRule.onNode(hasText("Tokens", ignoreCase = true)).assertIsDisplayed()
    }

    @Test
    fun worksWithMaterialDarkTheme() {
        setContentWithSpecificTheme("material_dark") {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Daily progress card")).assertIsDisplayed()
        composeTestRule.onNode(hasText("Tokens", ignoreCase = true)).assertIsDisplayed()
    }

    @Test
    fun handlesThemeChanges() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Token balance card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Daily progress card")).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Quick stats section")).assertIsDisplayed()
    }
}
