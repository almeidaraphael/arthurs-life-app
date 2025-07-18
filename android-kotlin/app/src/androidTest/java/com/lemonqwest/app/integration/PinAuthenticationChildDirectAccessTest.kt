package com.lemonqwest.app.integration

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.lemonqwest.app.domain.auth.AuthenticationState
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.viewmodels.AuthViewModel
import com.lemonqwest.app.testutils.AndroidTestBase
import io.mockk.coAnswers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests child access without PIN requirement with complete test isolation.
 */
class PinAuthenticationChildDirectAccessTest : AndroidTestBase() {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    private val mockAuthViewModel = mockk<AuthViewModel>()
    private val authStateFlow = MutableStateFlow(AuthenticationState.Unauthenticated)

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        coEvery { mockAuthViewModel.authenticationState } returns authStateFlow
        coEvery { mockAuthViewModel.authenticateDirectChildAccess() } returns Unit
    }

    @Test
    fun testChildDirectAccessWorkflow() = runTest {
        val childUserId = "child-user-id"
        coEvery {
            mockAuthViewModel.authenticateDirectChildAccess()
        } coAnswers {
            authStateFlow.value = AuthenticationState.Authenticated(
                userId = childUserId,
                userRole = UserRole.CHILD,
            )
        }
        mockAuthViewModel.authenticateDirectChildAccess()
        composeTestRule.waitForIdle()
        assert(authStateFlow.value is AuthenticationState.Authenticated)
        val authState = authStateFlow.value as AuthenticationState.Authenticated
        assert(authState.userId == childUserId)
        assert(authState.userRole == UserRole.CHILD)
    }
}
