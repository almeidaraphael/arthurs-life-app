package com.lemonqwest.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lemonqwest.app.presentation.theme.AppTheme
import com.lemonqwest.app.presentation.theme.BaseAppTheme
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
 * - Theme application utilities
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
abstract class ComposeUiTestBase {

    @get:Rule(order = 0)
    open val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Before
    open fun setup() {
        hiltRule.inject()
    }

    /**
     * Sets content with the app theme applied.
     *
     * @param content The composable content to test
     */
    protected fun setContentWithTheme(
        content: @Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            AppTheme {
                content()
            }
        }
    }

    /**
     * Sets content with a specific theme applied.
     *
     * @param theme The theme to apply
     * @param content The composable content to test
     */
    protected fun setContentWithSpecificTheme(
        theme: BaseAppTheme,
        content: @Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            theme {
                content()
            }
        }
    }
}
