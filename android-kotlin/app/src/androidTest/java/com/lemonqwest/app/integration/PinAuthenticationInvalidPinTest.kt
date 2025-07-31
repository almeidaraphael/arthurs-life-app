package com.lemonqwest.app.integration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.auth.AuthResult
import com.lemonqwest.app.domain.auth.AuthenticationState
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.screens.PinEntryScreen
import com.lemonqwest.app.presentation.theme.AppTheme
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel
import com.lemonqwest.app.testutils.AndroidTestBase
import io.mockk.any
import io.mockk.coAnswers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests PIN authentication failure handling with complete test isolation.
 */
class PinAuthenticationInvalidPinTest : AndroidTestBase() {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    private val mockAuthViewModel = mockk<AuthViewModel>()
    private val authStateFlow = MutableStateFlow(AuthenticationState.Unauthenticated)

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        coEvery { mockAuthViewModel.authenticationState } returns authStateFlow
        coEvery { mockAuthViewModel.authenticateWithPin(any()) } returns Unit
    }

    private fun setContentWithTheme(content: @Composable () -> Unit) {
        composeTestRule.setContent {
            AppTheme {
                content()
            }
        }
    }

    @Test
    fun testInvalidPinAuthenticationError() = runTest {
        val invalidPin = "9999"
        coEvery {
            mockAuthViewModel.authenticateWithPin(invalidPin)
        } coAnswers {
            authStateFlow.value = AuthenticationState.AuthenticationFailed(
                AuthResult.InvalidCredentials("Invalid PIN"),
            )
        }
        setContentWithTheme {
            PinEntryScreen(
                selectedRole = UserRole.CAREGIVER,
                viewModel = mockAuthViewModel,
                onAuthenticationSuccess = { _, _ ->
                    throw AssertionError(
                        "Authentication should not succeed",
                    )
                },
                onNavigateBack = {},
            )
        }
        invalidPin.forEach { digit ->
            composeTestRule.onNodeWithText(digit.toString()).performClick()
        }
        composeTestRule.waitForIdle()
        assert(authStateFlow.value is AuthenticationState.AuthenticationFailed)
        val failedState = authStateFlow.value as AuthenticationState.AuthenticationFailed
        assert(failedState.error is AuthResult.InvalidCredentials)
    }
}
