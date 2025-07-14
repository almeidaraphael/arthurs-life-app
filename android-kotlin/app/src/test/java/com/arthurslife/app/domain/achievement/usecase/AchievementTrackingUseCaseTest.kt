package com.arthurslife.app.domain.achievement.usecase

import com.arthurslife.app.domain.TestCoroutineExtension
import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.achievement.Achievement
import com.arthurslife.app.domain.achievement.AchievementRepository
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.domain.shouldBeUnlocked
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Comprehensive test suite for AchievementTrackingUseCase.
 *
 * Tests cover:
 * - Achievement progress tracking after task completion
 * - First Steps achievement logic
 * - Century Club achievement progression
 * - Token Collector achievement tracking
 * - Task Master achievement completion
 * - 3-Day Streak achievement (simplified implementation)
 * - Achievement retrieval operations
 * - Error handling and edge cases
 * - Repository interaction validation
 */
@ExtendWith(TestCoroutineExtension::class)
@DisplayName("AchievementTrackingUseCase Tests")
class AchievementTrackingUseCaseTest {

    private lateinit var achievementRepository: AchievementRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase

    @BeforeEach
    fun setup() {
        achievementRepository = mockk()
        taskRepository = mockk()

        // Mock the initialize method for all tests
        coEvery { achievementRepository.initializeAchievementsForUser(any()) } returns Unit

        achievementTrackingUseCase = AchievementTrackingUseCase(
            achievementRepository = achievementRepository,
            taskRepository = taskRepository,
        )
    }

    @Nested
    @DisplayName("Update Achievements After Task Completion")
    inner class UpdateAchievementsAfterTaskCompletion {

        @Test
        @DisplayName("Should unlock First Steps achievement on first task completion")
        fun shouldUnlockFirstStepsAchievementOnFirstTaskCompletion() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val firstStepsAchievement = TestDataFactory.createAchievement(
                AchievementType.FIRST_STEPS,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.FIRST_STEPS to firstStepsAchievement),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns 1
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock one achievement")
            val unlockedAchievement = result.first()
            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                AchievementType.FIRST_STEPS,
                unlockedAchievement.type,
                "Should unlock First Steps achievement",
            )

            coVerify {
                achievementRepository.updateAchievement(
                    match { it.isUnlocked && it.type == AchievementType.FIRST_STEPS },
                )
            }
        }

        @Test
        @DisplayName("Should not unlock First Steps achievement if already unlocked")
        fun shouldNotUnlockFirstStepsAchievementIfAlreadyUnlocked() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val unlockedFirstSteps = TestDataFactory.createUnlockedAchievement(
                AchievementType.FIRST_STEPS,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.FIRST_STEPS to unlockedFirstSteps),
            )

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(
                result.isEmpty(),
                "Should not unlock any achievement when First Steps already unlocked",
            )

            coVerify(exactly = 0) { taskRepository.countCompletedTasks(userId) }
            coVerify(exactly = 0) { achievementRepository.updateAchievement(any()) }
        }

        @Test
        @DisplayName("Should update Century Club achievement progress")
        fun shouldUpdateCenturyClubAchievementProgress() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val centuryClubAchievement = TestDataFactory.createAchievement(
                AchievementType.CENTURY_CLUB,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.CENTURY_CLUB to centuryClubAchievement,
                ),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns 5
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(result.isEmpty(), "Should not unlock Century Club achievement yet")

            coVerify {
                achievementRepository.updateAchievement(
                    match {
                        it.type == AchievementType.CENTURY_CLUB &&
                            it.progress == 5 &&
                            !it.isUnlocked
                    },
                )
            }
        }

        @Test
        @DisplayName("Should unlock Century Club achievement at 10 tasks")
        fun shouldUnlockCenturyClubAchievementAt10Tasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val centuryClubAchievement = TestDataFactory.createAchievement(
                AchievementType.CENTURY_CLUB,
                userId,
                progress = 9,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.CENTURY_CLUB to centuryClubAchievement,
                ),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns 10
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock Century Club achievement")
            val unlockedAchievement = result.first()
            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                AchievementType.CENTURY_CLUB,
                unlockedAchievement.type,
                "Should unlock Century Club achievement",
            )

            coVerify {
                achievementRepository.updateAchievement(
                    match { it.isUnlocked && it.type == AchievementType.CENTURY_CLUB },
                )
            }
        }

        @Test
        @DisplayName("Should update Token Collector achievement progress")
        fun shouldUpdateTokenCollectorAchievementProgress() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val tokenCollectorAchievement = TestDataFactory.createAchievement(
                AchievementType.TOKEN_COLLECTOR,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.TOKEN_COLLECTOR to tokenCollectorAchievement,
                ),
            )
            coEvery { taskRepository.countTokensEarned(userId) } returns 25
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(result.isEmpty(), "Should not unlock Token Collector achievement yet")

            coVerify {
                achievementRepository.updateAchievement(
                    match {
                        it.type == AchievementType.TOKEN_COLLECTOR &&
                            it.progress == 25 &&
                            !it.isUnlocked
                    },
                )
            }
        }

        @Test
        @DisplayName("Should unlock Token Collector achievement at 50 tokens")
        fun shouldUnlockTokenCollectorAchievementAt50Tokens() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val tokenCollectorAchievement = TestDataFactory.createAchievement(
                AchievementType.TOKEN_COLLECTOR,
                userId,
                progress = 45,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.TOKEN_COLLECTOR to tokenCollectorAchievement,
                ),
            )
            coEvery { taskRepository.countTokensEarned(userId) } returns 50
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock Token Collector achievement")
            val unlockedAchievement = result.first()
            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                AchievementType.TOKEN_COLLECTOR,
                unlockedAchievement.type,
                "Should unlock Token Collector achievement",
            )

            coVerify {
                achievementRepository.updateAchievement(
                    match { it.isUnlocked && it.type == AchievementType.TOKEN_COLLECTOR },
                )
            }
        }

        @Test
        @DisplayName("Should unlock Task Master achievement when all tasks complete")
        fun shouldUnlockTaskMasterAchievementWhenAllTasksComplete() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val taskMasterAchievement = TestDataFactory.createAchievement(
                AchievementType.TASK_MASTER,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.TASK_MASTER to taskMasterAchievement),
            )
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns emptyList<Task>()
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock Task Master achievement")
            val unlockedAchievement = result.first()
            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                AchievementType.TASK_MASTER,
                unlockedAchievement.type,
                "Should unlock Task Master achievement",
            )

            coVerify {
                achievementRepository.updateAchievement(
                    match { it.isUnlocked && it.type == AchievementType.TASK_MASTER },
                )
            }
        }

        @Test
        @DisplayName("Should not unlock Task Master achievement when incomplete tasks exist")
        fun shouldNotUnlockTaskMasterAchievementWhenIncompleteTasksExist() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val taskMasterAchievement = TestDataFactory.createAchievement(
                AchievementType.TASK_MASTER,
                userId,
            )
            val incompleteTasks = listOf(TestDataFactory.createTask(assignedToUserId = userId))

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.TASK_MASTER to taskMasterAchievement),
            )
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns incompleteTasks

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(
                result.isEmpty(),
                "Should not unlock Task Master achievement when incomplete tasks exist",
            )

            coVerify(exactly = 0) { achievementRepository.updateAchievement(any()) }
        }

        @Test
        @DisplayName("Should unlock 3-Day Streak achievement after 6 tasks (simplified)")
        fun shouldUnlock3DayStreakAchievementAfter6Tasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val streakAchievement = TestDataFactory.createAchievement(
                AchievementType.THREE_DAY_STREAK,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.THREE_DAY_STREAK to streakAchievement),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns 6
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock 3-Day Streak achievement")
            val unlockedAchievement = result.first()
            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                AchievementType.THREE_DAY_STREAK,
                unlockedAchievement.type,
                "Should unlock 3-Day Streak achievement",
            )
            assertEquals(
                3,
                unlockedAchievement.progress,
                "Should set progress to 3 for 3-Day Streak",
            )

            coVerify {
                achievementRepository.updateAchievement(
                    match { it.isUnlocked && it.type == AchievementType.THREE_DAY_STREAK },
                )
            }
        }

        @Test
        @DisplayName("Should not unlock 3-Day Streak achievement before 6 tasks")
        fun shouldNotUnlock3DayStreakAchievementBefore6Tasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val streakAchievement = TestDataFactory.createAchievement(
                AchievementType.THREE_DAY_STREAK,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.THREE_DAY_STREAK to streakAchievement),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns 5

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(
                result.isEmpty(),
                "Should not unlock 3-Day Streak achievement before 6 tasks",
            )

            coVerify(exactly = 0) { achievementRepository.updateAchievement(any()) }
        }

        @Test
        @DisplayName("Should unlock multiple achievements simultaneously")
        fun shouldUnlockMultipleAchievementsSimultaneously() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val firstStepsAchievement = TestDataFactory.createAchievement(
                AchievementType.FIRST_STEPS,
                userId,
            )
            val centuryClubAchievement = TestDataFactory.createAchievement(
                AchievementType.CENTURY_CLUB,
                userId,
                progress = 9,
            )
            val tokenCollectorAchievement = TestDataFactory.createAchievement(
                AchievementType.TOKEN_COLLECTOR,
                userId,
                progress = 45,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.FIRST_STEPS to firstStepsAchievement,
                    AchievementType.CENTURY_CLUB to centuryClubAchievement,
                    AchievementType.TOKEN_COLLECTOR to tokenCollectorAchievement,
                ),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns 10
            coEvery { taskRepository.countTokensEarned(userId) } returns 50
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(3, result.size, "Should unlock three achievements")

            val unlockedTypes = result.map { it.type }.toSet()
            assertTrue(
                unlockedTypes.contains(AchievementType.FIRST_STEPS),
                "Should unlock First Steps",
            )
            assertTrue(
                unlockedTypes.contains(AchievementType.CENTURY_CLUB),
                "Should unlock Century Club",
            )
            assertTrue(
                unlockedTypes.contains(AchievementType.TOKEN_COLLECTOR),
                "Should unlock Token Collector",
            )

            result.forEach { it.shouldBeUnlocked() }
        }

        @Test
        @DisplayName("Should handle missing achievements gracefully")
        fun shouldHandleMissingAchievementsGracefully() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()

            // All achievements return null (not found)
            coEvery { achievementRepository.findByUserIdAndType(userId, any()) } returns null

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(result.isEmpty(), "Should return empty list when no achievements found")

            coVerify(exactly = 0) { taskRepository.countCompletedTasks(any()) }
            coVerify(exactly = 0) { taskRepository.countTokensEarned(any()) }
            coVerify(exactly = 0) { taskRepository.findIncompleteByUserId(any()) }
            coVerify(exactly = 0) { achievementRepository.updateAchievement(any()) }
        }
    }

    @Nested
    @DisplayName("Individual Achievement Logic")
    inner class IndividualAchievementLogic {

        @ParameterizedTest
        @ValueSource(ints = [1, 2, 5, 10, 50])
        @DisplayName("Should update Century Club progress correctly")
        fun shouldUpdateCenturyClubProgressCorrectly(completedTasks: Int) = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val centuryClubAchievement = TestDataFactory.createAchievement(
                AchievementType.CENTURY_CLUB,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.CENTURY_CLUB to centuryClubAchievement,
                ),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns completedTasks
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            if (completedTasks >= 10) {
                assertEquals(1, result.size, "Should unlock Century Club at $completedTasks tasks")
                result.first().shouldBeUnlocked()
            } else {
                assertTrue(
                    result.isEmpty(),
                    "Should not unlock Century Club at $completedTasks tasks",
                )
            }

            coVerify {
                achievementRepository.updateAchievement(
                    match {
                        it.type == AchievementType.CENTURY_CLUB &&
                            if (completedTasks >= 10) {
                                // When unlocked, progress is set to target (10)
                                it.progress == 10 && it.isUnlocked
                            } else {
                                // When not unlocked, progress equals actual completed tasks
                                it.progress == completedTasks && !it.isUnlocked
                            }
                    },
                )
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [10, 25, 40, 50, 75])
        @DisplayName("Should update Token Collector progress correctly")
        fun shouldUpdateTokenCollectorProgressCorrectly(tokensEarned: Int) = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val tokenCollectorAchievement = TestDataFactory.createAchievement(
                AchievementType.TOKEN_COLLECTOR,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.TOKEN_COLLECTOR to tokenCollectorAchievement,
                ),
            )
            coEvery { taskRepository.countTokensEarned(userId) } returns tokensEarned
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            if (tokensEarned >= 50) {
                assertEquals(
                    1,
                    result.size,
                    "Should unlock Token Collector at $tokensEarned tokens",
                )
                result.first().shouldBeUnlocked()
            } else {
                assertTrue(
                    result.isEmpty(),
                    "Should not unlock Token Collector at $tokensEarned tokens",
                )
            }

            coVerify {
                achievementRepository.updateAchievement(
                    match {
                        it.type == AchievementType.TOKEN_COLLECTOR &&
                            if (tokensEarned >= 50) {
                                // When unlocked, progress is set to target (50)
                                it.progress == 50 && it.isUnlocked
                            } else {
                                // When not unlocked, progress equals actual tokens earned
                                it.progress == tokensEarned && !it.isUnlocked
                            }
                    },
                )
            }
        }

        @Test
        @DisplayName("Should not unlock Task Master if already unlocked")
        fun shouldNotUnlockTaskMasterIfAlreadyUnlocked() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val unlockedTaskMaster = TestDataFactory.createUnlockedAchievement(
                AchievementType.TASK_MASTER,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.TASK_MASTER to unlockedTaskMaster),
            )
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns emptyList<Task>()

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(result.isEmpty(), "Should not unlock Task Master if already unlocked")

            coVerify(exactly = 0) { achievementRepository.updateAchievement(any()) }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 1, 3, 5, 6, 10])
        @DisplayName("Should handle 3-Day Streak threshold correctly")
        fun shouldHandle3DayStreakThresholdCorrectly(completedTasks: Int) = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val streakAchievement = TestDataFactory.createAchievement(
                AchievementType.THREE_DAY_STREAK,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.THREE_DAY_STREAK to streakAchievement),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns completedTasks
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            if (completedTasks >= 6) {
                assertEquals(1, result.size, "Should unlock 3-Day Streak at $completedTasks tasks")
                val unlockedAchievement = result.first()
                unlockedAchievement.shouldBeUnlocked()
                assertEquals(
                    3,
                    unlockedAchievement.progress,
                    "Should set progress to 3 for 3-Day Streak",
                )
            } else {
                assertTrue(
                    result.isEmpty(),
                    "Should not unlock 3-Day Streak at $completedTasks tasks",
                )
            }
        }
    }

    @Nested
    @DisplayName("Get All Achievements")
    inner class GetAllAchievements {

        @Test
        @DisplayName("Should get all achievements for user")
        fun shouldGetAllAchievementsForUser() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val allAchievements = TestDataFactory.createAchievementSet(userId = userId)

            coEvery { achievementRepository.findByUserId(userId) } returns allAchievements

            // When
            val result = achievementTrackingUseCase.getAllAchievements(userId)

            // Then
            assertEquals(allAchievements.size, result.size, "Should return all achievements")
            assertEquals(
                allAchievements,
                result,
                "Should return exact achievements from repository",
            )

            coVerify { achievementRepository.findByUserId(userId) }
        }

        @Test
        @DisplayName("Should return empty list when user has no achievements")
        fun shouldReturnEmptyListWhenUserHasNoAchievements() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()

            coEvery { achievementRepository.findByUserId(userId) } returns emptyList()

            // When
            val result = achievementTrackingUseCase.getAllAchievements(userId)

            // Then
            assertTrue(result.isEmpty(), "Should return empty list when no achievements found")

            coVerify { achievementRepository.findByUserId(userId) }
        }
    }

    @Nested
    @DisplayName("Get Unlocked Achievements")
    inner class GetUnlockedAchievements {

        @Test
        @DisplayName("Should get unlocked achievements for user")
        fun shouldGetUnlockedAchievementsForUser() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val unlockedAchievements = listOf(
                TestDataFactory.createUnlockedAchievement(AchievementType.FIRST_STEPS, userId),
                TestDataFactory.createUnlockedAchievement(AchievementType.TASK_MASTER, userId),
            )

            coEvery { achievementRepository.findUnlockedByUserId(userId) } returns unlockedAchievements

            // When
            val result = achievementTrackingUseCase.getUnlockedAchievements(userId)

            // Then
            assertEquals(
                unlockedAchievements.size,
                result.size,
                "Should return all unlocked achievements",
            )
            result.forEach { it.shouldBeUnlocked() }

            coVerify { achievementRepository.findUnlockedByUserId(userId) }
        }

        @Test
        @DisplayName("Should return empty list when user has no unlocked achievements")
        fun shouldReturnEmptyListWhenUserHasNoUnlockedAchievements() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()

            coEvery { achievementRepository.findUnlockedByUserId(userId) } returns emptyList()

            // When
            val result = achievementTrackingUseCase.getUnlockedAchievements(userId)

            // Then
            assertTrue(
                result.isEmpty(),
                "Should return empty list when no unlocked achievements found",
            )

            coVerify { achievementRepository.findUnlockedByUserId(userId) }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    inner class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle zero task completion counts")
        fun shouldHandleZeroTaskCompletionCounts() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val achievements = TestDataFactory.createAchievementSet(
                userId = userId,
                includeProgress = false,
            )

            achievements.forEach { achievement ->
                coEvery { achievementRepository.findByUserIdAndType(userId, achievement.type) } returns achievement
            }
            coEvery { taskRepository.countCompletedTasks(userId) } returns 0
            coEvery { taskRepository.countTokensEarned(userId) } returns 0
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns listOf(TestDataFactory.createTask())

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(result.isEmpty(), "Should not unlock any achievements with zero progress")

            coVerify(exactly = 0) { achievementRepository.updateAchievement(any()) }
        }

        @Test
        @DisplayName("Should handle very large task completion counts")
        fun shouldHandleVeryLargeTaskCompletionCounts() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val centuryClubAchievement = TestDataFactory.createAchievement(
                AchievementType.CENTURY_CLUB,
                userId,
            )
            val largeTaskCount = 1000

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.CENTURY_CLUB to centuryClubAchievement,
                ),
            )
            coEvery { taskRepository.countCompletedTasks(userId) } returns largeTaskCount
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock Century Club with large task count")
            val unlockedAchievement = result.first()
            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                10,
                unlockedAchievement.progress,
                "Progress should be set to target on unlock",
            )

            coVerify {
                achievementRepository.updateAchievement(
                    match {
                        it.type == AchievementType.CENTURY_CLUB &&
                            it.isUnlocked &&
                            it.progress == 10
                    },
                )
            }
        }

        @Test
        @DisplayName("Should handle user with no incomplete tasks for Task Master")
        fun shouldHandleUserWithNoIncompleteTasksForTaskMaster() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val taskMasterAchievement = TestDataFactory.createAchievement(
                AchievementType.TASK_MASTER,
                userId,
            )

            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(AchievementType.TASK_MASTER to taskMasterAchievement),
            )
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns emptyList<Task>()
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertEquals(1, result.size, "Should unlock Task Master when no incomplete tasks")
            result.first().shouldBeUnlocked()
            assertEquals(
                AchievementType.TASK_MASTER,
                result.first().type,
                "Should unlock Task Master achievement",
            )
        }

        @Test
        @DisplayName("Should handle concurrent achievement updates")
        fun shouldHandleConcurrentAchievementUpdates() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val achievements = TestDataFactory.createAchievementSet(
                userId = userId,
                includeProgress = false,
            )

            // Set up for multiple achievements to be unlocked
            setupAllAchievementMocks(
                userId = userId,
                specificAchievements = mapOf(
                    AchievementType.FIRST_STEPS to achievements.find { it.type == AchievementType.FIRST_STEPS },
                    AchievementType.CENTURY_CLUB to achievements.find { it.type == AchievementType.CENTURY_CLUB }?.updateProgress(9),
                    AchievementType.TOKEN_COLLECTOR to achievements.find { it.type == AchievementType.TOKEN_COLLECTOR }?.updateProgress(45),
                    AchievementType.TASK_MASTER to achievements.find { it.type == AchievementType.TASK_MASTER },
                    AchievementType.THREE_DAY_STREAK to achievements.find { it.type == AchievementType.THREE_DAY_STREAK },
                ),
            )

            coEvery { taskRepository.countCompletedTasks(userId) } returns 10
            coEvery { taskRepository.countTokensEarned(userId) } returns 50
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns emptyList<Task>()
            coEvery { achievementRepository.updateAchievement(any()) } returns Unit

            // When
            val result = achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(userId)

            // Then
            assertTrue(result.size >= 4, "Should unlock multiple achievements concurrently")
            result.forEach { it.shouldBeUnlocked() }

            coVerify(atLeast = 4) { achievementRepository.updateAchievement(any()) }
        }
    }

    /**
     * Helper method to set up mocks for all achievement types that the use case checks.
     * This prevents MockKException errors when the use case tries to access unmocked achievement types.
     */
    private fun setupAllAchievementMocks(
        userId: String,
        specificAchievements: Map<AchievementType, Achievement?> = emptyMap(),
    ) {
        // Set up mocks for all achievement types that the use case checks
        val allTypes = listOf(
            AchievementType.FIRST_STEPS,
            AchievementType.CENTURY_CLUB,
            AchievementType.TOKEN_COLLECTOR,
            AchievementType.TASK_MASTER,
            AchievementType.THREE_DAY_STREAK,
            AchievementType.TASK_CHAMPION,
            AchievementType.PERFECT_WEEK,
            AchievementType.SPEED_DEMON,
            AchievementType.EARLY_BIRD,
        )

        allTypes.forEach { type ->
            val mockValue = specificAchievements[type] ?: null
            coEvery { achievementRepository.findByUserIdAndType(userId, type) } returns mockValue
        }
    }
}
