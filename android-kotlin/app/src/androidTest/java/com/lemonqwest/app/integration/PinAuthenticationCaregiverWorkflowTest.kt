package com.lemonqwest.app.integration

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
 * Tests complete caregiver authentication workflow with complete test isolation.
 */
class PinAuthenticationCaregiverWorkflowTest : AndroidTestBase() {
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
    fun testCaregiverPinAuthenticationWorkflow() = runTest {
        val validCaregiverPin = "1234"
        val expectedUserId = "caregiver-user-id"
        coEvery {
            mockAuthViewModel.authenticateWithPin(validCaregiverPin)
        } coAnswers {
            authStateFlow.value = AuthenticationState.Authenticated(
                userId = expectedUserId,
                userRole = UserRole.CAREGIVER,
            )
        }
        setContentWithTheme {
            PinEntryScreen(
                selectedRole = UserRole.CAREGIVER,
                viewModel = mockAuthViewModel,
                onAuthenticationSuccess = { userId, role ->
                    assert(userId == expectedUserId)
                    assert(role == UserRole.CAREGIVER)
                },
                onNavigateBack = {},
            )
        }
        composeTestRule.onNodeWithText("Enter PIN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Caregiver Access").assertIsDisplayed()
        validCaregiverPin.forEach { digit ->
            composeTestRule.onNodeWithText(
                digit.toString(),
            ).assertIsDisplayed().assertHasClickAction().performClick()
        }
        composeTestRule.waitForIdle()
        assert(authStateFlow.value is AuthenticationState.Authenticated)
        val authState = authStateFlow.value as AuthenticationState.Authenticated
        assert(authState.userId == expectedUserId)
        assert(authState.userRole == UserRole.CAREGIVER)
    }
}
