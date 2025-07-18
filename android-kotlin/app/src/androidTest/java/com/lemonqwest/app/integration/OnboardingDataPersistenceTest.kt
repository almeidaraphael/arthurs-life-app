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
 * Tests data persistence during onboarding workflow with complete test isolation.
 */
class OnboardingDataPersistenceTest : AndroidTestBase() {
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
    }

    @Test
    fun testOnboardingDataPersistence() = runTest {
        // ...simulate data entry and navigation...
    }
}
