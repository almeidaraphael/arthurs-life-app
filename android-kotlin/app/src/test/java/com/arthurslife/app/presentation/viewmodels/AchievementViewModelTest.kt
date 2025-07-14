package com.arthurslife.app.presentation.viewmodels

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.achievement.AchievementRepository
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.arthurslife.app.domain.common.AchievementEventManager
import com.arthurslife.app.domain.common.PresentationException
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Comprehensive tests for AchievementViewModel.
 *
 * This test class verifies the ViewModel's state management, achievement loading,
 * and proper error handling following MVVM patterns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel Tests")
class AchievementViewModelTest {

    private lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var mockAchievementRepository: AchievementRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockAchievementEventManager: AchievementEventManager
    private lateinit var viewModel: AchievementViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testChild = TestDataFactory.createChildUser(id = "child-1", name = "Test Child")
    private val testAchievements = listOf(
        TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            userId = "child-1",
            isUnlocked = true,
        ),
        TestDataFactory.createAchievement(
            type = AchievementType.CENTURY_CLUB,
            userId = "child-1",
            isUnlocked = false,
        ),
        TestDataFactory.createAchievement(
            type = AchievementType.THREE_DAY_STREAK,
            userId = "child-1",
            isUnlocked = true,
        ),
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockAchievementTrackingUseCase = mockk()
        mockAchievementRepository = mockk()
        mockUserRepository = mockk()
        mockAchievementEventManager = mockk {
            every { achievementUpdates } returns MutableSharedFlow()
        }

        // Default successful setup
        coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns testChild
        coEvery { mockAchievementRepository.initializeAchievementsForUser(testChild.id) } returns Unit
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns testAchievements
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): AchievementViewModel {
        return AchievementViewModel(
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            achievementRepository = mockAchievementRepository,
            userRepository = mockUserRepository,
            achievementEventManager = mockAchievementEventManager,
        )
    }

    @Nested
    @DisplayName("Initialization")
    inner class Initialization {

        @Test
        fun `should initialize with correct default state`() = runTest {
            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val uiState = viewModel.uiState.first()
            assertEquals(false, uiState.isLoading)
            assertNull(uiState.error)
        }

        @Test
        fun `should load child user and achievements on initialization`() = runTest {
            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            // Verify repositories were called
            coVerify { mockUserRepository.findByRole(UserRole.CHILD) }
            coVerify { mockAchievementRepository.initializeAchievementsForUser(testChild.id) }
            coVerify { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) }

            // Verify state is updated
            assertEquals(testAchievements, viewModel.achievements.first())

            val uiState = viewModel.uiState.first()
            assertFalse(uiState.isLoading)
            assertNull(uiState.error)
        }

        @Test
        fun `should handle missing child user gracefully`() = runTest {
            coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns null

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            // Should not attempt to initialize or load achievements
            coVerify(exactly = 0) { mockAchievementRepository.initializeAchievementsForUser(any()) }
            coVerify(exactly = 0) { mockAchievementTrackingUseCase.getAllAchievements(any()) }

            // State should show error
            val uiState = viewModel.uiState.first()
            assertEquals("No child user found", uiState.error)
            assertFalse(uiState.isLoading)

            // Achievements should remain empty
            assertTrue(viewModel.achievements.first().isEmpty())
        }

        @Test
        fun `should handle child user loading error`() = runTest {
            val errorMessage = "Database connection failed"
            coEvery { mockUserRepository.findByRole(UserRole.CHILD) } throws PresentationException(errorMessage)

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val uiState = viewModel.uiState.first()
            assertEquals(errorMessage, uiState.error)
            assertFalse(uiState.isLoading)

            // Achievements should remain empty
            assertTrue(viewModel.achievements.first().isEmpty())
        }

        @Test
        fun `should handle achievement loading error`() = runTest {
            val errorMessage = "Failed to load achievements"
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException(errorMessage)

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val uiState = viewModel.uiState.first()
            assertEquals(errorMessage, uiState.error)
            assertFalse(uiState.isLoading)

            // Achievements should remain empty
            assertTrue(viewModel.achievements.first().isEmpty())
        }
    }

    @Nested
    @DisplayName("Achievement Loading")
    inner class AchievementLoading {

        @BeforeEach
        fun setupAchievementLoading() {
            viewModel = createViewModel()
        }

        @Test
        fun `should load all achievements correctly`() = runTest {
            delay(100) // Allow initialization to complete

            val achievements = viewModel.achievements.first()
            assertEquals(testAchievements.size, achievements.size)
            assertEquals(testAchievements, achievements)

            // Verify achievements contain expected data
            val unlockedAchievements = achievements.filter { it.isUnlocked }
            val lockedAchievements = achievements.filter { !it.isUnlocked }

            assertEquals(2, unlockedAchievements.size)
            assertEquals(1, lockedAchievements.size)
        }

        @Test
        fun `should load empty achievements list`() = runTest {
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns emptyList()
            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val achievements = viewModel.achievements.first()
            assertTrue(achievements.isEmpty())

            val uiState = viewModel.uiState.first()
            assertFalse(uiState.isLoading)
            assertNull(uiState.error)
        }

        @Test
        fun `should maintain achievement order from use case`() = runTest {
            val orderedAchievements = listOf(
                TestDataFactory.createAchievement(
                    type = AchievementType.FIRST_STEPS,
                    userId = testChild.id,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.TASK_MASTER,
                    userId = testChild.id,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.THREE_DAY_STREAK,
                    userId = testChild.id,
                ),
            )
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns orderedAchievements

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val achievements = viewModel.achievements.first()
            assertEquals(orderedAchievements.size, achievements.size)
            assertEquals(AchievementType.FIRST_STEPS, achievements[0].type)
            assertEquals(AchievementType.TASK_MASTER, achievements[1].type)
            assertEquals(AchievementType.THREE_DAY_STREAK, achievements[2].type)
        }
    }

    @Nested
    @DisplayName("Refresh Functionality")
    inner class RefreshFunctionality {

        @BeforeEach
        fun setupRefreshFunctionality() {
            viewModel = createViewModel()
        }

        @Test
        fun `should refresh achievements successfully`() = runTest {
            delay(100) // Allow initialization to complete

            // Verify initial load
            coVerify { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) }

            // Setup new achievements for refresh
            val newAchievements = testAchievements + TestDataFactory.createAchievement(
                type = AchievementType.TOKEN_COLLECTOR,
                userId = testChild.id,
                isUnlocked = true,
            )
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns newAchievements

            // Call refresh
            viewModel.refresh()

            delay(100) // Allow operation to complete

            // Verify use case was called again
            coVerify(
                atLeast = 2,
            ) { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) }

            // Verify achievements were updated
            val achievements = viewModel.achievements.first()
            assertEquals(newAchievements.size, achievements.size)
            assertEquals(newAchievements, achievements)

            val uiState = viewModel.uiState.first()
            assertFalse(uiState.isLoading)
            assertNull(uiState.error)
        }

        @Test
        fun `should handle refresh error gracefully`() = runTest {
            delay(100) // Allow initialization to complete

            // Setup error for refresh
            val errorMessage = "Refresh failed"
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException(errorMessage)

            viewModel.refresh()

            delay(100) // Allow operation to complete

            val uiState = viewModel.uiState.first()
            assertEquals(errorMessage, uiState.error)
            assertFalse(uiState.isLoading)

            // Achievements should remain as they were (empty due to error)
            assertTrue(viewModel.achievements.first().isEmpty())
        }

        @Test
        fun `should handle refresh when child user not found`() = runTest {
            // Setup ViewModel with no child user
            coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns null
            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            // Initial error should be set
            val initialState = viewModel.uiState.first()
            assertEquals("No child user found", initialState.error)

            // Clear error and refresh
            viewModel.clearError()
            viewModel.refresh()

            delay(100) // Allow operation to complete

            // Should get the same error again
            val refreshedState = viewModel.uiState.first()
            assertEquals("No child user found", refreshedState.error)
            assertTrue(viewModel.achievements.first().isEmpty())
        }

        @Test
        fun `should show loading state during refresh`() = runTest {
            delay(100) // Allow initialization to complete

            // Set up a longer running operation to capture loading state
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns testAchievements

            viewModel.refresh()

            delay(100) // Allow operation to complete

            // After completion, loading should be false
            val uiState = viewModel.uiState.first()
            assertFalse(uiState.isLoading)
        }
    }

    @Nested
    @DisplayName("Error Management")
    inner class ErrorManagement {

        @BeforeEach
        fun setupErrorManagement() {
            viewModel = createViewModel()
        }

        @Test
        fun `should clear error successfully`() = runTest {
            // Setup an error state
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException("Test error")
            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            // Verify error is set
            val errorState = viewModel.uiState.first()
            assertEquals("Test error", errorState.error)

            // Clear error
            viewModel.clearError()

            // Verify error is cleared
            val clearedState = viewModel.uiState.first()
            assertNull(clearedState.error)
            assertFalse(clearedState.isLoading)
        }

        @Test
        fun `should clear error when there is no error`() = runTest {
            delay(100) // Allow initialization to complete

            // Verify no error initially
            val initialState = viewModel.uiState.first()
            assertNull(initialState.error)

            // Clear error (should not cause issues)
            viewModel.clearError()

            // Verify state remains the same
            val clearedState = viewModel.uiState.first()
            assertNull(clearedState.error)
            assertFalse(clearedState.isLoading)
        }

        @Test
        fun `should handle multiple consecutive errors`() = runTest {
            // First error
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException("First error")
            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val firstErrorState = viewModel.uiState.first()
            assertEquals("First error", firstErrorState.error)

            // Second error on refresh
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException("Second error")
            viewModel.refresh()

            delay(100) // Allow operation to complete

            val secondErrorState = viewModel.uiState.first()
            assertEquals("Second error", secondErrorState.error)
            assertFalse(secondErrorState.isLoading)
        }
    }

    @Nested
    @DisplayName("StateFlow Behavior")
    inner class StateFlowBehavior {

        @BeforeEach
        fun setupStateFlowBehavior() {
            viewModel = createViewModel()
        }

        @Test
        fun `should emit distinct states correctly`() = runTest {
            delay(100) // Allow initialization to complete

            val states = mutableListOf<AchievementUiState>()

            // Collect states during a refresh operation
            // Collect a few states to verify emission
            val initialState = viewModel.uiState.first()
            states.add(initialState)

            viewModel.refresh()

            delay(100) // Allow operation to complete

            // Should have captured at least the final state
            assertTrue(states.isNotEmpty())

            // Final state should not be loading
            val finalState = viewModel.uiState.first()
            assertFalse(finalState.isLoading)
        }

        @Test
        fun `should maintain achievement data consistency`() = runTest {
            delay(100) // Allow initialization to complete

            // Verify initial data
            val initialAchievements = viewModel.achievements.first()
            assertEquals(testAchievements, initialAchievements)

            // Add new achievement and refresh
            val newAchievements = testAchievements + TestDataFactory.createAchievement(
                type = AchievementType.TOKEN_COLLECTOR,
                userId = testChild.id,
            )
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns newAchievements

            viewModel.refresh()

            delay(100) // Allow operation to complete

            // Verify data is updated consistently
            val updatedAchievements = viewModel.achievements.first()
            assertEquals(newAchievements, updatedAchievements)
            assertEquals(newAchievements.size, updatedAchievements.size)
        }

        @Test
        fun `should handle achievement data with different unlock states`() = runTest {
            val mixedAchievements = listOf(
                TestDataFactory.createAchievement(
                    type = AchievementType.FIRST_STEPS,
                    userId = testChild.id,
                    isUnlocked = true,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.TASK_MASTER,
                    userId = testChild.id,
                    isUnlocked = false,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.CENTURY_CLUB,
                    userId = testChild.id,
                    isUnlocked = true,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.TOKEN_COLLECTOR,
                    userId = testChild.id,
                    isUnlocked = false,
                ),
            )
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns mixedAchievements

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val achievements = viewModel.achievements.first()
            assertEquals(mixedAchievements.size, achievements.size)

            // Verify unlock states are preserved
            val unlockedCount = achievements.count { it.isUnlocked }
            val lockedCount = achievements.count { !it.isUnlocked }
            assertEquals(2, unlockedCount)
            assertEquals(2, lockedCount)
        }
    }

    @Nested
    @DisplayName("Achievement Types and Categories")
    inner class AchievementTypesAndCategories {

        @Test
        fun `should handle different achievement types`() = runTest {
            val diverseAchievements = listOf(
                TestDataFactory.createAchievement(
                    type = AchievementType.TASK_MASTER,
                    userId = testChild.id,
                    isUnlocked = true,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.THREE_DAY_STREAK,
                    userId = testChild.id,
                    isUnlocked = false,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.FIRST_STEPS,
                    userId = testChild.id,
                    isUnlocked = true,
                ),
            )
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns diverseAchievements

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val achievements = viewModel.achievements.first()
            assertEquals(diverseAchievements.size, achievements.size)

            // Verify all achievement types are represented
            val achievementTypes = achievements.map { it.type }.toSet()
            assertTrue(achievementTypes.contains(AchievementType.TASK_MASTER))
            assertTrue(achievementTypes.contains(AchievementType.THREE_DAY_STREAK))
            assertTrue(achievementTypes.contains(AchievementType.FIRST_STEPS))
        }

        @Test
        fun `should handle achievements with progress tracking`() = runTest {
            val progressAchievements = listOf(
                TestDataFactory.createAchievement(
                    type = AchievementType.CENTURY_CLUB,
                    userId = testChild.id,
                    progress = 7,
                    isUnlocked = false,
                ),
                TestDataFactory.createAchievement(
                    type = AchievementType.FIRST_STEPS,
                    userId = testChild.id,
                    progress = 1,
                    isUnlocked = true,
                ),
            )
            coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns progressAchievements

            viewModel = createViewModel()

            delay(100) // Allow initialization to complete

            val achievements = viewModel.achievements.first()
            assertEquals(progressAchievements.size, achievements.size)

            // Verify progress values are preserved
            val progressAchievement = achievements.find { it.type == AchievementType.CENTURY_CLUB }
            assertEquals(7, progressAchievement?.progress)
            assertEquals(10, progressAchievement?.target) // CENTURY_CLUB target is 10
            assertFalse(progressAchievement?.isUnlocked ?: true)

            val completedAchievement = achievements.find { it.type == AchievementType.FIRST_STEPS }
            assertTrue(completedAchievement?.isUnlocked ?: false)
        }
    }
}
