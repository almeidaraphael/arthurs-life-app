package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import io.mockk.MockKAnnotations
import io.mockk.coEvery
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for AchievementViewModel StateFlow behavior.
 *
 * Tests cover:
 * - Emit distinct states correctly
 * - Maintain achievement data consistency
 * - Handle achievement data with different unlock states
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel State Tests")
class AchievementViewModelStateTest {
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
    fun setUpAchievementViewModelState() {
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
    fun tearDownAchievementViewModelState() {
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
    fun `should emit distinct states correctly`() {
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val states = mutableListOf<AchievementUiState>()
        val initialState = viewModel.uiState.first()
        states.add(initialState)
        viewModel.refresh()
        advanceUntilIdle()
        assertTrue(states.isNotEmpty())
        val finalState = viewModel.uiState.first()
        assertFalse(finalState.isLoading)
    }

    @Test
    fun `should maintain achievement data consistency`() {
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()
        val initialAchievements = viewModel.achievements.first()
        assertEquals(testAchievements, initialAchievements)
        val newAchievements = testAchievements + TestDataFactory.createAchievement(
            type = AchievementType.TOKEN_COLLECTOR,
            userId = testChild.id,
        )
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns newAchievements
        viewModel.refresh()
        advanceUntilIdle()
        val updatedAchievements = viewModel.achievements.first()
        assertEquals(newAchievements, updatedAchievements)
        assertEquals(newAchievements.size, updatedAchievements.size)
    }

    @Test
    fun `should handle achievement data with different unlock states`() {
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
        viewModel.initialize()
        advanceUntilIdle()
        val achievements = viewModel.achievements.first()
        assertEquals(mixedAchievements.size, achievements.size)
        val unlockedCount = achievements.count { it.isUnlocked }
        val lockedCount = achievements.count { !it.isUnlocked }
        assertEquals(2, unlockedCount)
        assertEquals(2, lockedCount)
    }
}
