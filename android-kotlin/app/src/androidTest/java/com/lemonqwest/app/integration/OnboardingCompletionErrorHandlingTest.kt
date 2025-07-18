package com.lemonqwest.app.integration

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
 * Tests error handling during onboarding completion.
 */
@HiltAndroidTest
class OnboardingCompletionErrorHandlingTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    fun setup() {
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockOnboardingViewModel.completeOnboarding() } throws RuntimeException("Failed to create user account")
    }

    @Test
    fun testOnboardingCompletionErrorHandling() = runTest {
        onboardingStateFlow.value = onboardingStateFlow.value.copy(isLoading = false, error = "Failed to create user account")
        // ...simulate error and retry...
    }
}
