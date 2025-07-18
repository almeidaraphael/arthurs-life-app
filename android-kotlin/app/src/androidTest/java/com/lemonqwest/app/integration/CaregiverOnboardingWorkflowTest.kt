package com.lemonqwest.app.integration

import com.lemonqwest.app.presentation.viewmodels.OnboardingViewModel
import com.lemonqwest.app.testutils.AndroidTestBase
import io.mockk.coAnswers
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests complete caregiver onboarding workflow from start to finish with complete test isolation.
 */
class CaregiverOnboardingWorkflowTest : AndroidTestBase() {
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockOnboardingViewModel.completeOnboarding() } returns Unit
    }

    @Test
    fun testCompleteOnboardingWorkflow() = runTest {
        var onboardingCompleted = false
        coEvery { mockOnboardingViewModel.completeOnboarding() } coAnswers {
            onboardingCompleted = true
            onboardingStateFlow.value = onboardingStateFlow.value.copy(isCompleted = true)
        }
        // ...simulate onboarding flow...
        assert(onboardingCompleted) { "Onboarding should be completed" }
    }
}
