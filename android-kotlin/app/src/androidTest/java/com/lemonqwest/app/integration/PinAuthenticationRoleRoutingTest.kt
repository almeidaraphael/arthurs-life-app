package com.lemonqwest.app.integration

import androidx.compose.runtime.Composable
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
 * Tests role-based authentication routing with complete test isolation.
 */
class PinAuthenticationRoleRoutingTest : AndroidTestBase() {
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
    fun testRoleBasedAuthenticationRouting() = runTest {
        val caregiverPin = "1234"
        val caregiverUserId = "caregiver-user"
        coEvery {
            mockAuthViewModel.authenticateWithPin(caregiverPin)
        } coAnswers {
            authStateFlow.value = AuthenticationState.Authenticated(
                userId = caregiverUserId,
                userRole = UserRole.CAREGIVER,
            )
        }
        var caregiverRouteTriggered = false
        setContentWithTheme {
            PinEntryScreen(
                selectedRole = UserRole.CAREGIVER,
                viewModel = mockAuthViewModel,
                onAuthenticationSuccess = { userId, role ->
                    if (role == UserRole.CAREGIVER && userId == caregiverUserId) {
                        caregiverRouteTriggered = true
                    }
                },
                onNavigateBack = {},
            )
        }
        caregiverPin.forEach { digit ->
            composeTestRule.onNodeWithText(digit.toString()).performClick()
        }
        composeTestRule.waitForIdle()
        assert(caregiverRouteTriggered) {
            "Caregiver authentication should trigger caregiver-specific routing"
        }
    }
}
