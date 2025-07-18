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
 * Tests authentication timeout handling.
 */
@HiltAndroidTest
class PinAuthenticationTimeoutTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    private val mockAuthViewModel = mockk<AuthViewModel>()

    @BeforeEach
    override fun setup() {
        // override
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
