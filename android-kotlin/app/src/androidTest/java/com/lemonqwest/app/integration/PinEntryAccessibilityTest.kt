package com.lemonqwest.app.integration

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import com.lemonqwest.app.presentation.screens.PinEntryScreen
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests PIN entry accessibility features.
 */
@HiltAndroidTest
class PinEntryAccessibilityTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    override val hiltRule = HiltAndroidRule(this)
    private val mockAuthViewModel = mockk<AuthViewModel>()

    @BeforeEach
    override fun setup() {
        // override
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
