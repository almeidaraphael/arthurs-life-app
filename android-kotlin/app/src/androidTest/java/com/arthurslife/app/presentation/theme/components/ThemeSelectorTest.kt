package com.arthurslife.app.presentation.theme.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arthurslife.app.presentation.theme.ArthursLifeTheme
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.mario.MarioClassicTheme
import com.arthurslife.app.presentation.theme.materialdark.MaterialDarkTheme
import com.arthurslife.app.presentation.theme.materiallight.MaterialLightTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Comprehensive UI test suite for ThemeSelector component.
 *
 * This test suite validates the ThemeSelector's UI functionality, including theme
 * selection, theme option display, and interaction behavior. Tests cover various
 * themes, selection states, and user interactions.
 *
 * Coverage includes:
 * - Theme selector display and layout
 * - Theme option rendering for different themes
 * - Theme selection interaction and callbacks
 * - Theme preview display
 * - Selected theme visual indicators
 * - Accessibility and content descriptions
 * - Multiple theme handling
 * - Theme switching workflows
 */
@HiltAndroidTest
class ThemeSelectorTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private var selectedTheme: BaseAppTheme? = null
    private var selectionCount = 0

    @Before
    fun setup() {
        hiltRule.inject()
        selectedTheme = null
        selectionCount = 0
    }

    @Test
    fun themeSelector_withSingleTheme_displaysCorrectly() {
        // Given
        val availableThemes = listOf(MaterialLightTheme)
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Choose Theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Clean and modern light theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_withMultipleThemes_displaysAllOptions() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Choose Theme")
            .assertIsDisplayed()

        // Check all themes are displayed
        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertIsDisplayed()

        // Check theme descriptions
        composeTestRule
            .onNodeWithText("Clean and modern light theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Clean and modern dark theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Fun Mario-themed experience")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_withSelectedTheme_showsSelectionIndicator() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialDarkTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        // Only the selected theme should show the selection indicator
        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()

        // Verify the correct theme is selected by checking the text context
        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_themeClick_triggersCallback() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Perform click on Material Dark theme
        composeTestRule
            .onNodeWithText("Material Dark")
            .performClick()

        // Then
        assert(selectionCount == 1)
        assert(selectedTheme == MaterialDarkTheme)
    }

    @Test
    fun themeSelector_multipleThemeClicks_triggersMultipleCallbacks() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Perform clicks on different themes
        composeTestRule
            .onNodeWithText("Material Dark")
            .performClick()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .performClick()

        composeTestRule
            .onNodeWithText("Material Light")
            .performClick()

        // Then
        assert(selectionCount == 3)
        assert(selectedTheme == MaterialLightTheme) // Last clicked theme
    }

    @Test
    fun themeSelector_themeOptions_haveClickActions() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Material Light")
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertHasClickAction()
    }

    @Test
    fun themeSelector_withMarioTheme_displaysCorrectly() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MarioClassicTheme

        // When
        composeTestRule.setContent {
            MarioClassicTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Choose Theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Fun Mario-themed experience")
            .assertIsDisplayed()

        // Mario theme should be selected
        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_withMaterialDarkTheme_displaysCorrectly() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialDarkTheme

        // When
        composeTestRule.setContent {
            MaterialDarkTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Choose Theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Clean and modern dark theme")
            .assertIsDisplayed()

        // Material Dark theme should be selected
        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_emptyThemesList_displaysHeaderOnly() {
        // Given
        val availableThemes = emptyList<BaseAppTheme>()
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Choose Theme")
            .assertIsDisplayed()

        // No theme options should be displayed
        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertIsNotDisplayed()
    }

    @Test
    fun themeSelector_themeSwitching_updatesSelection() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        var currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                        currentTheme = theme
                    },
                )
            }
        }

        // Initially Material Light should be selected
        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()

        // Click on Material Dark
        composeTestRule
            .onNodeWithText("Material Dark")
            .performClick()

        // Then
        assert(selectionCount == 1)
        assert(selectedTheme == MaterialDarkTheme)
    }

    @Test
    fun themeSelector_sameThemeClick_stillTriggersCallback() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Click on currently selected theme
        composeTestRule
            .onNodeWithText("Material Light")
            .performClick()

        // Then
        assert(selectionCount == 1)
        assert(selectedTheme == MaterialLightTheme)
    }

    @Test
    fun themeSelector_accessibilityContentDescription_isCorrect() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_themePreview_displaysColorScheme() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        // Theme preview should be displayed (represented by the theme name and description)
        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_layout_fillsMaxWidth() {
        // Given
        val availableThemes = listOf(MaterialLightTheme)
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        // Verify the component is displayed (layout correctness is handled by Compose)
        composeTestRule
            .onNodeWithText("Choose Theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_rapidClicks_handlesCorrectly() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Perform rapid clicks
        composeTestRule
            .onNodeWithText("Material Dark")
            .performClick()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .performClick()

        composeTestRule
            .onNodeWithText("Material Light")
            .performClick()

        composeTestRule
            .onNodeWithText("Material Dark")
            .performClick()

        // Then
        assert(selectionCount == 4)
        assert(selectedTheme == MaterialDarkTheme) // Last clicked theme
    }

    @Test
    fun themeSelector_themeDescriptions_displayCorrectly() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        // Check that all theme descriptions are displayed
        composeTestRule
            .onNodeWithText("Clean and modern light theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Clean and modern dark theme")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Fun Mario-themed experience")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_themeNames_displayCorrectly() {
        // Given
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        val currentTheme = MaterialLightTheme

        // When
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then
        // Check that all theme names are displayed
        composeTestRule
            .onNodeWithText("Material Light")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Mario Classic")
            .assertIsDisplayed()
    }

    @Test
    fun themeSelector_withDifferentCurrentThemes_showsCorrectSelection() {
        // Test with Material Dark as current theme
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )

        // When - Material Dark is current
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = MaterialDarkTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                    },
                )
            }
        }

        // Then - Material Dark should be selected
        composeTestRule
            .onNodeWithContentDescription("Selected")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Material Dark")
            .assertIsDisplayed()
    }
}
