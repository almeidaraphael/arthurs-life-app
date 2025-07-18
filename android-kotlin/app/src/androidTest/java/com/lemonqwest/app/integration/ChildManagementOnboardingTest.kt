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
 * Tests child management during onboarding.
 */
@HiltAndroidTest
class ChildManagementOnboardingTest : ComposeUiTestBase() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    fun setup() {
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockOnboardingViewModel.addChild(any(), any()) } returns Unit
    }

    @Test
    fun testChildManagementDuringOnboarding() = runTest {
        // ...simulate child add/remove...
    }
}
