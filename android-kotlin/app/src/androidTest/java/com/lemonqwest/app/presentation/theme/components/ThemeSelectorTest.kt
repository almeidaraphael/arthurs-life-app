package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import com.lemonqwest.app.presentation.theme.mario.MarioClassicTheme
import com.lemonqwest.app.presentation.theme.materialdark.MaterialDarkTheme
import com.lemonqwest.app.presentation.theme.materiallight.MaterialLightTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ThemeSelectorTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private var selectedTheme: BaseAppTheme? = null

    @BeforeEach
    fun setup() {
        hiltRule.inject()
        selectedTheme = null
    }

    @Test
    fun `theme selector displays available themes and allows selection`() {
        val availableThemes = listOf(MaterialLightTheme, MaterialDarkTheme, MarioClassicTheme)
        val currentTheme = MaterialLightTheme

        composeTestRule.setContent {
            LemonQwestTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { selectedTheme = it },
                )
            }
        }

        // Verify all themes are displayed and clickable
        composeTestRule.onNodeWithText("Material Light").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Material Dark").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Mario Classic").assertIsDisplayed().assertHasClickAction()

        // Verify current selection is indicated
        composeTestRule.onNodeWithContentDescription("Selected").assertIsDisplayed()
    }

    @Test
    fun `theme selection triggers callback with selected theme`() {
        val availableThemes = listOf(MaterialLightTheme, MaterialDarkTheme)
        val currentTheme = MaterialLightTheme

        composeTestRule.setContent {
            LemonQwestTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { selectedTheme = it },
                )
            }
        }

        // Select a different theme
        composeTestRule.onNodeWithText("Material Dark").performClick()

        // Verify callback was triggered with correct theme
        assert(selectedTheme == MaterialDarkTheme)
    }

    @Test
    fun `theme selector handles empty theme list gracefully`() {
        composeTestRule.setContent {
            LemonQwestTheme {
                ThemeSelector(
                    currentTheme = MaterialLightTheme,
                    availableThemes = emptyList(),
                    onThemeSelected = { selectedTheme = it },
                )
            }
        }

        // Should still display the header
        composeTestRule.onNodeWithText("Choose Theme").assertIsDisplayed()
    }
}
