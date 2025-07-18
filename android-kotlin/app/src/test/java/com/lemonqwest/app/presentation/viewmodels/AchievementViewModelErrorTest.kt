package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.PresentationException
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for AchievementViewModel error management.
 *
 * Tests cover:
 * - Clear error successfully
 * - Clear error when no error exists
 * - Handle multiple consecutive errors
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel Error Tests")
class AchievementViewModelErrorTest { // Removed: : AchievementViewModelTestBase()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())
    private lateinit var testScope: TestScope

    private lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var mockAchievementRepository: AchievementRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockAchievementEventManager: AchievementEventManager

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
    )

    @BeforeEach
    fun setUpAchievementViewModelError() {
        testScope = TestScope(mainDispatcherRule.testDispatcher)
        MockKAnnotations.init(this, relaxUnitFun = false)
        mockAchievementTrackingUseCase = mockk()
        mockAchievementRepository = mockk()
        mockUserRepository = mockk()
        mockAchievementEventManager = mockk {
            every { achievementUpdates } returns MutableSharedFlow()
        }
        coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns testChild
        coEvery { mockAchievementRepository.initializeAchievementsForUser(testChild.id) } returns Unit
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns testAchievements
        coEvery { mockUserRepository.findById(any()) } returns testChild
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterEach
    fun tearDownAchievementViewModelError() {
        testScope.cancel()
        unmockkAll()
    }

    // Independent runViewModelTest implementation
    private fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        testScope.runTest(timeout = 5.seconds) {
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
            testBody()
            advanceUntilIdle()
            mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    private fun createViewModel(): AchievementViewModel {
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        mainDispatcherRule.testDispatcher.scheduler.advanceUntilIdle()
        return AchievementViewModel(
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            achievementRepository = mockAchievementRepository,
            userRepository = mockUserRepository,
            achievementEventManager = mockAchievementEventManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )
    }

    @Test
    fun `should clear error successfully`() = runViewModelTest {
        // Setup an error state
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException("Test error")

        // Create ViewModel inside test context
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle() // Allow initialization to complete

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
    fun `should clear error when there is no error`() = runViewModelTest {
        // Create ViewModel inside test context
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle() // Allow initialization to complete

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
    fun `should handle multiple consecutive errors`() = runViewModelTest {
        // First error
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException("First error")

        // Create ViewModel inside test context
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle() // Allow initialization to complete

        val firstErrorState = viewModel.uiState.first()
        assertEquals("First error", firstErrorState.error)

        // Second error on refresh
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException("Second error")
        viewModel.refresh()
        advanceUntilIdle() // Allow operation to complete

        val secondErrorState = viewModel.uiState.first()
        assertEquals("Second error", secondErrorState.error)
        assertFalse(secondErrorState.isLoading)
    }
}
