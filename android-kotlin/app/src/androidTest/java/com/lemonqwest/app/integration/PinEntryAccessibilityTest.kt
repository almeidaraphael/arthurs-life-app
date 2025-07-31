package com.lemonqwest.app.integration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.lemonqwest.app.presentation.screens.PinEntryScreen
import com.lemonqwest.app.presentation.theme.AppTheme
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel
import com.lemonqwest.app.testutils.AndroidTestBase
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests PIN entry accessibility features with complete test isolation.
 */
class PinEntryAccessibilityTest : AndroidTestBase() {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    private val mockAuthViewModel = mockk<AuthViewModel>()

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
    }

    private fun setContentWithTheme(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            AppTheme {
                content()
            }
        }
    }

    @Test
    fun testPinEntryAccessibility() = runTest {
        setContentWithTheme {
            PinEntryScreen(
                targetRole = TODO(),
                themeViewModel = TODO(),
                onCancel = TODO(),
                authViewModel = mockAuthViewModel,
            )
        }
        composeTestRule.onNodeWithContentDescription("PIN entry").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("PIN display").assertIsDisplayed()
        (0..9).forEach { digit ->
            composeTestRule.onNodeWithContentDescription(
                "PIN digit $digit",
            ).assertIsDisplayed().assertHasClickAction()
        }
        composeTestRule.onNodeWithContentDescription(
            "Delete PIN digit",
        ).assertIsDisplayed().assertHasClickAction()
    }
}
