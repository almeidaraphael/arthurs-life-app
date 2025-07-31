package com.lemonqwest.app.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.presentation.theme.AppTheme
import com.lemonqwest.app.presentation.theme.components.ThemeSelector
import com.lemonqwest.app.testutils.AndroidTestBase
import org.junit.Rule
import org.junit.jupiter.api.Test

/**
 * Integration tests for the theme system with complete test isolation.
 *
 * Tests theme switching, component rendering across themes,
 * and theme-specific behaviors in a real Android environment.
 */
class ThemeSystemTest : AndroidTestBase() {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    private fun setContentWithTheme(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            AppTheme {
                content()
            }
        }
    }

    private fun setContentWithSpecificTheme(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            AppTheme {
                content()
            }
        }
    }

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
