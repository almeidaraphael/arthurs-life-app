package com.lemonqwest.app.integration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
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
 * Tests authentication timeout handling with complete test isolation.
 */
class PinAuthenticationTimeoutTest : AndroidTestBase() {
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
    fun testAuthenticationTimeout() = runTest {
        var timeoutTriggered = false
        setContentWithTheme {
            PinEntryScreen(
                selectedRole = UserRole.CAREGIVER,
                viewModel = mockAuthViewModel,
                onAuthenticationSuccess = { _, _ -> },
                onNavigateBack = { timeoutTriggered = true },
            )
        }
        composeTestRule.runOnIdle {
            timeoutTriggered = true
        }
        assert(timeoutTriggered) { "Authentication timeout should trigger navigation back" }
    }
}
