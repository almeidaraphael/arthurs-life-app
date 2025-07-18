package com.lemonqwest.app.integration

import com.lemonqwest.app.presentation.viewmodels.OnboardingViewModel
import com.lemonqwest.app.testutils.AndroidTestBase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests error handling during onboarding completion with complete test isolation.
 */
class OnboardingCompletionErrorHandlingTest : AndroidTestBase() {
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockOnboardingViewModel.completeOnboarding() } throws RuntimeException("Failed to create user account")
    }

    @Test
    fun testOnboardingCompletionErrorHandling() = runTest {
        onboardingStateFlow.value = onboardingStateFlow.value.copy(isLoading = false, error = "Failed to create user account")
        // ...simulate error and retry...
    }
}
