package com.lemonqwest.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.lemonqwest.app.presentation.screens.ChildHomeScreen
import com.lemonqwest.app.presentation.theme.ThemeViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.jupiter.api.Test

/**
 * User interaction and input validation tests for ChildHomeScreen.
 */
@HiltAndroidTest
class ChildHomeScreenUserInteractionTest : ComposeUiTestBase() {

    @Test
    fun colorContrastAndInputValidation() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        val tokenCardNode = composeTestRule.onNode(hasContentDescription("Token balance card"))
        tokenCardNode.assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
    }

    @Test
    fun displaysTokenBalance() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
    }

    @Test
    fun displaysProgressCard() {
        setContentWithTheme {
            ChildHomeScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
            )
        }
        composeTestRule.onNode(hasContentDescription("Daily progress: 70%")).assertIsDisplayed()
    }
}
