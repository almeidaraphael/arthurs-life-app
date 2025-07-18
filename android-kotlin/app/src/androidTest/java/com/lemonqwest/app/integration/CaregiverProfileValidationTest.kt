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
 * Tests caregiver profile validation during family setup with complete test isolation.
 */
class CaregiverProfileValidationTest : AndroidTestBase() {
    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val onboardingStateFlow = MutableStateFlow(
        com.lemonqwest.app.presentation.viewmodels.OnboardingUiState(),
    )

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockOnboardingViewModel.validateCaregiverSetup() } returns false
    }

    @Test
    fun testCaregiverProfileValidation() = runTest {
        onboardingStateFlow.value = onboardingStateFlow.value.copy(validationErrors = listOf("Name is required", "PIN must be 4 digits"))
        // ...simulate validation and UI...
    }
}
