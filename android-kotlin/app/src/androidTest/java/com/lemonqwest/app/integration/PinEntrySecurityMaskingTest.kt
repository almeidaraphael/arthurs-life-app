package com.lemonqwest.app.integration

import com.lemonqwest.app.domain.user.UserRole
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
 * Tests PIN entry security features.
 */
@HiltAndroidTest
class PinEntrySecurityMaskingTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    private val mockAuthViewModel = mockk<AuthViewModel>()

    @BeforeEach
    override fun setup() {
        // override
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
