package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementRepository
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.MainDispatcherRule
import com.lemonqwest.app.testutils.ViewModelTestBase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.BeforeEach
import kotlin.OptIn

/**
 * Shared base class for AchievementViewModel tests.
 *
 * Provides common mock setup, test data, and helper methods
 * for all AchievementViewModel test classes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class AchievementViewModelTestBase : ViewModelTestBase() {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    protected lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    protected lateinit var mockAchievementRepository: AchievementRepository
    protected lateinit var mockUserRepository: UserRepository
    protected lateinit var mockAchievementEventManager: AchievementEventManager

    protected val testChild = TestDataFactory.createChildUser(id = "child-1", name = "Test Child")
    protected val testAchievements = listOf(
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
    override fun setUpViewModel() {
        super.setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
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
        testDispatcher.scheduler.advanceUntilIdle()
    }

    protected fun createViewModel(): AchievementViewModel {
        return AchievementViewModel(
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            achievementRepository = mockAchievementRepository,
            userRepository = mockUserRepository,
            achievementEventManager = mockAchievementEventManager,
            ioDispatcher = mainDispatcherRule.testDispatcher,
        )
    }
}
