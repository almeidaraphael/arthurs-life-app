package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.PresentationException
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for AchievementViewModel refresh functionality.
 *
 * Tests cover:
 * - Successful refresh
 * - Refresh error handling
 * - Refresh with missing child user
 * - Loading state during refresh
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel Refresh Tests")
class AchievementViewModelRefreshTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var mockAchievementRepository: AchievementRepository
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockAchievementEventManager: AchievementEventManager

    private lateinit var viewModel: AchievementViewModel

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
    fun setUpAchievementViewModelRefresh() {
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
    }

    @AfterEach
    fun tearDownAchievementViewModelRefresh() {
        unmockkAll()
    }

    private fun createViewModel(): AchievementViewModel {
        return AchievementViewModel(
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            achievementRepository = mockAchievementRepository,
            userRepository = mockUserRepository,
            achievementEventManager = mockAchievementEventManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )
    }

    @Test
    fun `should refresh achievements successfully`() {
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        coVerify { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) }
        val newAchievements = testAchievements + TestDataFactory.createAchievement(
            type = AchievementType.TOKEN_COLLECTOR,
            userId = testChild.id,
            isUnlocked = true,
        )
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns newAchievements
        viewModel.refresh()
        advanceUntilIdle()
        coVerify(atLeast = 2) { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) }
        val achievements = viewModel.achievements.first()
        assertEquals(newAchievements.size, achievements.size)
        assertEquals(newAchievements, achievements)
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `should handle refresh error gracefully`() {
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val errorMessage = "Refresh failed"
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } throws PresentationException(errorMessage)
        viewModel.refresh()
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertFalse(uiState.isLoading)
        assertTrue(viewModel.achievements.first().isEmpty())
    }

    @Test
    fun `should handle refresh when child user not found`() {
        coEvery { mockUserRepository.findByRole(UserRole.CHILD) } returns null
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val initialState = viewModel.uiState.first()
        assertEquals("No child user found", initialState.error)
        viewModel.clearError()
        viewModel.refresh()
        advanceUntilIdle()
        val refreshedState = viewModel.uiState.first()
        assertEquals("No child user found", refreshedState.error)
        assertTrue(viewModel.achievements.first().isEmpty())
    }

    @Test
    fun `should show loading state during refresh`() {
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns testAchievements
        viewModel.refresh()
        advanceUntilIdle()
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
    }
}
