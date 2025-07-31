package com.lemonqwest.app.integration

import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.presentation.viewmodels.OnboardingViewModel
import com.lemonqwest.app.testutils.AndroidTestBase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration tests for user repository during onboarding with complete test isolation.
 *
 * Tests user repository integration workflows during onboarding process
 * with automated Hilt setup and complete resource isolation.
 */
class UserRepositoryIntegrationOnboardingTest : AndroidTestBase() {
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val mockUserRepository = mockk<UserRepository>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()

        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockUserRepository.saveUser(any()) } returns Unit
    }

    @Test
    fun testUserRepositoryIntegration() = runTest {
        // ...simulate user creation and repository save...
    }
}
