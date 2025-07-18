package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for AchievementViewModel achievement loading functionality.
 *
 * Tests cover:
 * - Loading all achievements correctly
 * - Loading empty achievements list
 * - Maintaining achievement order from use case
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("AchievementViewModel Loading Tests")
class AchievementViewModelLoadingTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

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
        TestDataFactory.createAchievement(
            type = AchievementType.THREE_DAY_STREAK,
            userId = "child-1",
            isUnlocked = true,
        ),
    )

    @BeforeEach
    fun setUpAchievementViewModelLoading() {
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
    fun tearDownAchievementViewModelLoading() {
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
    fun `should load all achievements correctly`() = mainDispatcherRule.runTest {
        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()

        val achievements = viewModel.achievements.first()
        assertEquals(testAchievements.size, achievements.size)
        assertEquals(testAchievements, achievements)

        val unlockedAchievements = achievements.filter { it.isUnlocked }
        val lockedAchievements = achievements.filter { !it.isUnlocked }

        assertEquals(2, unlockedAchievements.size)
        assertEquals(1, lockedAchievements.size)
    }

    @Test
    fun `should load empty achievements list`() = mainDispatcherRule.runTest {
        coEvery { mockAchievementTrackingUseCase.getAllAchievements(testChild.id) } returns emptyList()

        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()

        val achievements = viewModel.achievements.first()
        assertTrue(achievements.isEmpty())

        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `should maintain achievement order from use case`() = mainDispatcherRule.runTest {
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

        val viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()

        val achievements = viewModel.achievements.first()
        assertEquals(orderedAchievements.size, achievements.size)
        assertEquals(AchievementType.FIRST_STEPS, achievements[0].type)
        assertEquals(AchievementType.TASK_MASTER, achievements[1].type)
        assertEquals(AchievementType.THREE_DAY_STREAK, achievements[2].type)
    }
}
