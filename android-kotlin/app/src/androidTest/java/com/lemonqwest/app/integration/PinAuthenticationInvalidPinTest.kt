package com.lemonqwest.app.integration

import com.lemonqwest.app.domain.auth.AuthResult
import com.lemonqwest.app.domain.auth.AuthenticationState
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.screens.PinEntryScreen
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel
import com.lemonqwest.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests PIN authentication failure handling.
 */
@HiltAndroidTest
class PinAuthenticationInvalidPinTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    private val mockAuthViewModel = mockk<AuthViewModel>()
    private val authStateFlow = MutableStateFlow(AuthenticationState.Unauthenticated)

    @BeforeEach
    fun setup() {
        coEvery { mockAuthViewModel.authenticationState } returns authStateFlow
        coEvery { mockAuthViewModel.authenticateWithPin(any()) } returns Unit
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
