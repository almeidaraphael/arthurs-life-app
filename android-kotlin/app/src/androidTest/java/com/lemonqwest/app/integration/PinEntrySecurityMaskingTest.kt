package com.lemonqwest.app.integration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.user.UserRole
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
 * Tests PIN entry security features with complete test isolation.
 */
class PinEntrySecurityMaskingTest : AndroidTestBase() {
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
    fun testPinEntrySecurityMasking() = runTest {
        setContentWithTheme {
            PinEntryScreen(
                selectedRole = UserRole.CAREGIVER,
                viewModel = mockAuthViewModel,
                onAuthenticationSuccess = { _, _ -> },
                onNavigateBack = {},
            )
        }
        val testPin = "1234"
        testPin.forEach { digit ->
            composeTestRule.onNodeWithText(digit.toString()).performClick()
        }
        composeTestRule.onNodeWithContentDescription("PIN display").assertIsDisplayed()
        testPin.forEach { digit ->
            composeTestRule.onNodeWithText(
                digit.toString(),
                useUnmergedTree = true,
            ).assertDoesNotExist()
        }
    }
}
