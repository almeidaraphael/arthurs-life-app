package com.arthurslife.app.ui

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.presentation.theme.AppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Base class for Compose UI tests.
 *
 * Provides common setup for Compose testing including:
 * - Hilt dependency injection
 * - Compose test rule setup
 * - Theme application
 * - Common testing utilities
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
abstract class ComposeUiTestBase {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Before
    open fun setup() {
        hiltRule.inject()
    }

    /**
     * Sets content for testing with the app theme applied.
     *
     * @param content The composable content to test
     */
    protected fun setContentWithTheme(
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            AppTheme {
                content()
            }
        }
    }

    /**
     * Sets content for testing with a specific theme.
     *
     * @param themeName The theme to apply
     * @param content The composable content to test
     */
    protected fun setContentWithSpecificTheme(
        themeName: String = "mario_classic",
        content: @androidx.compose.runtime.Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            AppTheme(forcedTheme = themeName) {
                content()
            }
        }
    }
}
