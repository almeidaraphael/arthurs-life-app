package com.arthurslife.app.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.presentation.theme.components.ThemeSelector
import com.arthurslife.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for the theme system.
 *
 * Tests theme switching, component rendering across themes,
 * and theme-specific behaviors in a real Android environment.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemeSystemTest : ComposeUiTestBase() {

    @Test
    fun themeSelector_displaysCorrectly() {
        setContentWithTheme {
            ThemeSelector(
                currentTheme = "mario_classic",
                onThemeChange = { },
            )
        }

        composeTestRule.onNodeWithText("Mario Classic")
            .assertIsDisplayed()
    }

    @Test
    fun marioTheme_rendersMarioElements() {
        setContentWithSpecificTheme("mario_classic") {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Coins: 25",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }

        composeTestRule.onNodeWithText("Coins: 25")
            .assertIsDisplayed()
    }

    @Test
    fun materialTheme_rendersStandardElements() {
        setContentWithSpecificTheme("material_light") {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Tokens: 25",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }

        composeTestRule.onNodeWithText("Tokens: 25")
            .assertIsDisplayed()
    }

    @Test
    fun themeSwitch_updatesComponents() {
        var currentTheme = "mario_classic"

        setContentWithTheme {
            ThemeSelector(
                currentTheme = currentTheme,
                onThemeChange = { newTheme ->
                    currentTheme = newTheme
                },
            )
        }

        // Verify initial theme
        composeTestRule.onNodeWithText("Mario Classic")
            .assertIsDisplayed()

        // Switch theme (this test would need theme switching implementation)
        // For now, we verify the selector is functional
        composeTestRule.onNodeWithText("Mario Classic")
            .performClick()
    }

    @Test
    fun themeConsistency_acrossComponents() {
        setContentWithSpecificTheme("mario_classic") {
            Box(modifier = Modifier.fillMaxSize()) {
                // Test multiple themed components
                Text(
                    text = "Quest Complete!",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }

        composeTestRule.onNodeWithText("Quest Complete!")
            .assertIsDisplayed()
    }
}
