package com.lemonqwest.app.integration

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
 * Tests role-based authentication routing.
 */
@HiltAndroidTest
class PinAuthenticationRoleRoutingTest : ComposeUiTestBase() {
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
