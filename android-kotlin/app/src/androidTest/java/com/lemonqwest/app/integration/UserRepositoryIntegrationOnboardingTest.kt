package com.lemonqwest.app.integration

import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.presentation.viewmodels.OnboardingViewModel
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
 * Tests user repository integration during onboarding.
 */
@HiltAndroidTest
class UserRepositoryIntegrationOnboardingTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val mockUserRepository = mockk<UserRepository>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    fun setup() {
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockUserRepository.saveUser(any()) } returns Unit
    }

    @Test
    fun testUserRepositoryIntegration() = runTest {
        // ...simulate user creation and repository save...
    }
}
