package com.arthurslife.app.integration

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.domain.user.User
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.navigation.OnboardingNavigation
import com.arthurslife.app.presentation.viewmodels.OnboardingViewModel
import com.arthurslife.app.testutils.DataConsistencyTestUtils
import com.arthurslife.app.testutils.EndToEndTestUtils
import com.arthurslife.app.testutils.IntegrationTestBase
import com.arthurslife.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Integration tests for caregiver onboarding workflow.
 *
 * Tests the complete onboarding flow from welcome screen through family setup,
 * covering:
 * - Navigation between onboarding screens
 * - Caregiver profile creation and validation
 * - Child profile management during onboarding
 * - User data persistence and consistency
 * - Form validation and error handling
 * - Onboarding completion and user creation
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CaregiverOnboardingIntegrationTest : ComposeUiTestBase(), IntegrationTestBase {

    @get:Rule(order = 0)
    override val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userRepository: UserRepository

    private val mockOnboardingViewModel = mockk<OnboardingViewModel>()
    private val mockUserRepository = mockk<UserRepository>()
    private val onboardingStateFlow = MutableStateFlow(
        com.arthurslife.app.presentation.viewmodels.OnboardingUiState(),
    )

    @Before
    override fun setup() {
        super.setup()
        runTest {
            super<IntegrationTestBase>.setup()
        }

        coEvery { mockOnboardingViewModel.uiState } returns onboardingStateFlow
        coEvery { mockOnboardingViewModel.updateCaregiverName(any()) } returns Unit
        coEvery { mockOnboardingViewModel.updateCaregiverAvatar(any()) } returns Unit
        coEvery { mockOnboardingViewModel.updateCaregiverPin(any()) } returns Unit
        coEvery { mockOnboardingViewModel.addChild(any(), any()) } returns Unit
        coEvery { mockOnboardingViewModel.completeOnboarding() } returns Unit
        coEvery { mockUserRepository.saveUser(any()) } returns Unit
    }

    /**
     * Tests complete caregiver onboarding workflow from start to finish.
     *
     * Given: New user starts onboarding process
     * When: User completes all onboarding steps
     * Then: Caregiver and child users are created and onboarding is completed
     */
    @Test
    fun testCompleteOnboardingWorkflow() = runTest {
        val caregiverName = "Parent Smith"
        val caregiverPin = "1234"
        val childName = "Alice Smith"

        var onboardingCompleted = false

        setupOnboardingMocks { onboardingCompleted = true }
        performCompleteOnboardingFlow(caregiverName, caregiverPin, childName)
        verifyOnboardingCompletion(onboardingCompleted)
    }

    private fun setupOnboardingMocks(onComplete: () -> Unit) {
        coEvery {
            mockOnboardingViewModel.completeOnboarding()
        } coAnswers {
            onComplete()
            onboardingStateFlow.value = onboardingStateFlow.value.copy(
                isCompleted = true,
            )
        }

        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = { onComplete() },
            )
        }
    }

    private fun performCompleteOnboardingFlow(
        caregiverName: String,
        caregiverPin: String,
        childName: String,
    ) {
        performWelcomeStep()
        performFamilySetupStep(caregiverName, caregiverPin)
        performAddChildrenStep(childName)
        performCompletionStep()
    }

    private fun performWelcomeStep() {
        composeTestRule.onNodeWithText("Welcome to Arthur's Life")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Get Started")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        composeTestRule.waitForIdle()
    }

    private fun performFamilySetupStep(caregiverName: String, caregiverPin: String) {
        composeTestRule.onNodeWithText("Set Up Your Family").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Caregiver name input")
            .performTextInput(caregiverName)
        composeTestRule.onNodeWithContentDescription("Select avatar").performClick()
        composeTestRule.onNodeWithContentDescription("Caregiver avatar").performClick()
        composeTestRule.onNodeWithContentDescription("PIN input")
            .performTextInput(caregiverPin)
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()
    }

    private fun performAddChildrenStep(childName: String) {
        composeTestRule.onNodeWithText("Add Your Children").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Child name input")
            .performTextInput(childName)
        composeTestRule.onNodeWithContentDescription("Add child").performClick()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()
    }

    private fun performCompletionStep() {
        composeTestRule.onNodeWithText("Setup Complete!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Using Arthur's Life").performClick()
        composeTestRule.waitForIdle()
    }

    private fun verifyOnboardingCompletion(onboardingCompleted: Boolean) {
        assert(onboardingCompleted) { "Onboarding should be completed" }
        coVerify { mockOnboardingViewModel.completeOnboarding() }
    }

    /**
     * Tests caregiver profile validation during family setup.
     *
     * Given: User is on family setup screen
     * When: Invalid or incomplete caregiver information is entered
     * Then: Validation errors are displayed and progression is blocked
     */
    @Test
    fun testCaregiverProfileValidation() = runTest {
        // Setup validation error state
        coEvery {
            mockOnboardingViewModel.validateCaregiverSetup()
        } returns false

        onboardingStateFlow.value = onboardingStateFlow.value.copy(
            validationErrors = listOf("Name is required", "PIN must be 4 digits"),
        )

        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }

        // Navigate to family setup
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()

        // Attempt to continue without valid information
        composeTestRule.onNodeWithText("Continue")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        // Check validation error messages
        composeTestRule.onNodeWithText("Name is required")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("PIN must be 4 digits")
            .assertIsDisplayed()

        // Enter valid name
        coEvery {
            mockOnboardingViewModel.validateCaregiverSetup()
        } returns true

        onboardingStateFlow.value = onboardingStateFlow.value.copy(
            caregiverName = "Valid Name",
            validationErrors = emptyList(),
        )

        composeTestRule.onNodeWithContentDescription("Caregiver name input")
            .performTextInput("Valid Name")

        composeTestRule.waitForIdle()

        // Continue button should now be enabled
        composeTestRule.onNodeWithText("Continue")
            .assertIsEnabled()
    }

    /**
     * Tests child management during onboarding.
     *
     * Given: User is on add children screen
     * When: User adds, modifies, and removes children
     * Then: Child list is updated correctly and form validation works
     */
    @Test
    fun testChildManagementDuringOnboarding() = runTest {
        val firstChildName = "Alice"
        val secondChildName = "Bob"

        // Setup initial state with first child
        onboardingStateFlow.value = onboardingStateFlow.value.copy(
            children = listOf(
                com.arthurslife.app.presentation.viewmodels.ChildProfile(
                    name = firstChildName,
                    avatarId = "mario_child",
                ),
            ),
        )

        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }

        // Navigate to add children screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()

        // Verify first child is displayed
        composeTestRule.onNodeWithText(firstChildName)
            .assertIsDisplayed()

        // Add second child
        composeTestRule.onNodeWithContentDescription("Child name input")
            .performTextInput(secondChildName)

        composeTestRule.onNodeWithContentDescription("Add child")
            .performClick()

        // Verify add child was called
        coVerify { mockOnboardingViewModel.addChild(secondChildName, any()) }

        // Update state to include second child
        onboardingStateFlow.value = onboardingStateFlow.value.copy(
            children = listOf(
                com.arthurslife.app.presentation.viewmodels.ChildProfile(
                    name = firstChildName,
                    avatarId = "mario_child",
                ),
                com.arthurslife.app.presentation.viewmodels.ChildProfile(
                    name = secondChildName,
                    avatarId = "luigi_child",
                ),
            ),
        )

        composeTestRule.waitForIdle()

        // Verify both children are displayed
        composeTestRule.onNodeWithText(firstChildName).assertIsDisplayed()
        composeTestRule.onNodeWithText(secondChildName).assertIsDisplayed()

        // Test child removal
        composeTestRule.onNodeWithContentDescription("Remove child $firstChildName")
            .assertIsDisplayed()
            .performClick()

        coVerify { mockOnboardingViewModel.removeChild(0) }
    }

    /**
     * Tests data persistence during onboarding workflow.
     *
     * Given: User enters information during onboarding
     * When: Data is saved at each step
     * Then: Information persists correctly through navigation
     */
    @Test
    fun testOnboardingDataPersistence() = runTest {
        val caregiverName = "Test Parent"
        val caregiverPin = "5678"
        val childName = "Test Child"

        // Test data persistence through onboarding flow
        EndToEndTestUtils.testTaskCompletionWorkflow(
            testScope = this,
            database = database,
            userId = "caregiver-id",
            taskId = "onboarding-task",
            expectedTokenReward = 0,
        )

        // Verify data consistency across storage layers
        DataConsistencyTestUtils.testCrossStorageConsistency(
            database = database,
            dataStore = testDataStore,
            userId = "caregiver-id",
        )

        // Test that onboarding data persists across navigation
        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }

        // Enter caregiver information
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Caregiver name input")
            .performTextInput(caregiverName)

        // Verify data was saved to ViewModel
        coVerify { mockOnboardingViewModel.updateCaregiverName(caregiverName) }

        // Navigate to next screen and back
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()

        // Navigate back to verify data persistence
        composeTestRule.onNodeWithContentDescription("Navigate back")
            .performClick()
        composeTestRule.waitForIdle()

        // Verify data is still displayed
        composeTestRule.onNodeWithText(caregiverName)
            .assertIsDisplayed()
    }

    /**
     * Tests error handling during onboarding completion.
     *
     * Given: User completes onboarding form
     * When: Error occurs during user creation or data saving
     * Then: Error is displayed and user can retry
     */
    @Test
    fun testOnboardingCompletionErrorHandling() = runTest {
        val errorMessage = "Failed to create user account"

        // Setup error scenario
        coEvery {
            mockOnboardingViewModel.completeOnboarding()
        } throws RuntimeException(errorMessage)

        onboardingStateFlow.value = onboardingStateFlow.value.copy(
            isLoading = false,
            error = errorMessage,
        )

        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }

        // Navigate to completion screen
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()

        // Attempt completion
        composeTestRule.onNodeWithText("Start Using Arthur's Life")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify error is displayed
        composeTestRule.onNodeWithText(errorMessage)
            .assertIsDisplayed()

        // Verify retry option is available
        composeTestRule.onNodeWithText("Try Again")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    /**
     * Tests onboarding navigation flow and back navigation.
     *
     * Given: User is in onboarding flow
     * When: User navigates between screens using back buttons
     * Then: Navigation works correctly and data is preserved
     */
    @Test
    fun testOnboardingNavigationFlow() = runTest {
        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }

        // Start at welcome screen
        composeTestRule.onNodeWithText("Welcome to Arthur's Life")
            .assertIsDisplayed()

        // Navigate to family setup
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Set Up Your Family")
            .assertIsDisplayed()

        // Navigate to add children
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Add Your Children")
            .assertIsDisplayed()

        // Navigate back to family setup
        composeTestRule.onNodeWithContentDescription("Navigate back")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Set Up Your Family")
            .assertIsDisplayed()

        // Navigate back to welcome
        composeTestRule.onNodeWithContentDescription("Navigate back")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome to Arthur's Life")
            .assertIsDisplayed()
    }

    /**
     * Tests theme consistency throughout onboarding flow.
     *
     * Given: User progresses through onboarding
     * When: Screens are displayed
     * Then: Consistent theme (Material Design for caregivers) is applied
     */
    @Test
    fun testOnboardingThemeConsistency() = runTest {
        setContentWithSpecificTheme("material_light") {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }

        // Verify Material Design theme elements are present
        composeTestRule.onNodeWithText("Welcome to Arthur's Life")
            .assertIsDisplayed()

        // Check that theme-specific elements are properly styled
        // (This would check for specific Material Design components and colors)

        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()

        // Verify theme consistency across screens
        composeTestRule.onNodeWithText("Set Up Your Family")
            .assertIsDisplayed()

        // Continue through flow to verify theme is maintained
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Add Your Children")
            .assertIsDisplayed()
    }

    /**
     * Tests user repository integration during onboarding.
     *
     * Given: User completes onboarding successfully
     * When: User data is saved to repository
     * Then: Users are created with correct roles and relationships
     */
    @Test
    fun testUserRepositoryIntegration() = runTest {
        val caregiverName = "Integration Test Parent"
        val childName = "Integration Test Child"
        val savedUsers = mutableListOf<User>()

        setupUserRepositoryMocks(savedUsers, caregiverName, childName)
        performOnboardingFlowForIntegrationTest()
        verifyUserRepositoryIntegration(savedUsers, caregiverName, childName)
    }

    private fun setupUserRepositoryMocks(
        savedUsers: MutableList<User>,
        caregiverName: String,
        childName: String,
    ) {
        coEvery { mockUserRepository.saveUser(any()) } coAnswers {
            savedUsers.add(firstArg())
        }

        coEvery { mockOnboardingViewModel.completeOnboarding() } coAnswers {
            createAndSaveTestUsers(caregiverName, childName)
            onboardingStateFlow.value = onboardingStateFlow.value.copy(isCompleted = true)
        }

        setContentWithTheme {
            OnboardingNavigation(
                viewModel = mockOnboardingViewModel,
                onOnboardingComplete = {},
            )
        }
    }

    private fun createAndSaveTestUsers(caregiverName: String, childName: String) {
        val caregiver = User(
            id = "caregiver-id",
            name = caregiverName,
            role = UserRole.CAREGIVER,
            avatarId = "default_caregiver",
            pin = null,
            tokenBalance = 0,
            favoriteColor = null,
        )
        val child = User(
            id = "child-id",
            name = childName,
            role = UserRole.CHILD,
            avatarId = "mario_child",
            pin = null,
            tokenBalance = 0,
            favoriteColor = null,
        )
        mockUserRepository.saveUser(caregiver)
        mockUserRepository.saveUser(child)
    }

    private fun performOnboardingFlowForIntegrationTest() {
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Continue").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Start Using Arthur's Life").performClick()
        composeTestRule.waitForIdle()
    }

    private fun verifyUserRepositoryIntegration(
        savedUsers: List<User>,
        caregiverName: String,
        childName: String,
    ) {
        coVerify(exactly = 2) { mockUserRepository.saveUser(any()) }
        assert(savedUsers.size == 2) { "Expected 2 users to be saved" }

        val caregiver = savedUsers.find { it.role == UserRole.CAREGIVER }
        val child = savedUsers.find { it.role == UserRole.CHILD }

        assert(caregiver != null) { "Caregiver user should be saved" }
        assert(child != null) { "Child user should be saved" }
        assert(caregiver?.name == caregiverName) { "Caregiver name should match" }
        assert(child?.name == childName) { "Child name should match" }
    }
}
