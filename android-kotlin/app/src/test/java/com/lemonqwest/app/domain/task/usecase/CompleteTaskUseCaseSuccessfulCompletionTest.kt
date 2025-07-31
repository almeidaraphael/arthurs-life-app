package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.shouldBeCompleted
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.user.TokenBalance
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for successful task completion scenarios.
 *
 * Tests cover:
 * - Basic task completion with token rewards
 * - Different task categories and reward amounts
 * - Edge cases with token balances
 * - Achievement integration scenarios
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("CompleteTaskUseCase - Successful Completion Tests")
class CompleteTaskUseCaseSuccessfulCompletionTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        userRepository = mockk()
        achievementTrackingUseCase = mockk()

        // Comprehensive default stubs to prevent MockKException for unmatched calls
        coEvery { taskRepository.findById(any()) } returns null
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.findById(any()) } returns null
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

        completeTaskUseCase = CompleteTaskUseCase(
            taskRepository,
            userRepository,
            achievementTrackingUseCase,
        )
    }

    @AfterEach
    fun tearDown() {
        // Explicit mock cleanup for parallel execution isolation
        clearMocks(taskRepository, userRepository, achievementTrackingUseCase)
    }

    @Test
    @DisplayName("Should complete task and award tokens")
    fun shouldCompleteTaskAndAwardTokens() = runTest {
        // Given
        val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(25))
        val task = TestDataFactory.createTask(
            title = "Brush teeth",
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = user.id,
        )
        val expectedNewlyUnlockedAchievements = listOf(
            TestDataFactory.createUnlockedAchievement(AchievementType.FIRST_STEPS, user.id),
        )

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery {
            achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id)
        } returns expectedNewlyUnlockedAchievements

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isSuccess, "Task completion should succeed")

        val completionResult = result.getOrThrow()
        assertEquals(task.id, completionResult.task.id, "Completed task should have same ID")
        completionResult.task.shouldBeCompleted()
        assertEquals(
            5,
            completionResult.tokensAwarded,
            "Should award 5 tokens for personal care task",
        )
        assertEquals(30, completionResult.newTokenBalance, "New balance should be 25 + 5 = 30")
        assertEquals(
            expectedNewlyUnlockedAchievements,
            completionResult.newlyUnlockedAchievements,
            "Should return newly unlocked achievements",
        )

        // Verify repository interactions
        coVerify { taskRepository.findById(task.id) }
        coVerify { userRepository.findById(user.id) }
        coVerify { taskRepository.updateTask(match { it.isCompleted && it.id == task.id }) }
        coVerify {
            userRepository.updateUser(
                match { it.tokenBalance.getValue() == 30 && it.id == user.id },
            )
        }
        coVerify { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) }
    }

    @Test
    @DisplayName("Should handle different task categories with correct rewards")
    fun shouldHandleDifferentTaskCategoriesWithCorrectRewards() = runTest {
        val testCases = listOf(
            Triple(TaskCategory.PERSONAL_CARE, 5, 30), // 25 + 5
            Triple(TaskCategory.HOUSEHOLD, 10, 35), // 25 + 10
            Triple(TaskCategory.HOMEWORK, 15, 40), // 25 + 15
        )

        testCases.forEach { (category, expectedReward, expectedBalance) ->
            // Clear any previous stubs to avoid conflicts
            clearMocks(taskRepository, userRepository, achievementTrackingUseCase)

            // Re-setup default stubs
            coEvery { taskRepository.findById(any()) } returns null
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.findById(any()) } returns null
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

            // Given
            val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(25))
            val task = TestDataFactory.createTask(
                category = category,
                assignedToUserId = user.id,
            )

            // Set up specific mocks for this iteration
            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(user.id) } returns user
            // Use any() matchers to avoid MockK conflicts in loops
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(
                result.isSuccess,
                "Task completion should succeed for category ${category.displayName}",
            )

            val completionResult = result.getOrThrow()
            assertEquals(
                expectedReward,
                completionResult.tokensAwarded,
                "Should award correct tokens for ${category.displayName}",
            )
            assertEquals(
                expectedBalance,
                completionResult.newTokenBalance,
                "Should have correct balance for ${category.displayName}",
            )
        }
    }

    @Test
    @DisplayName("Should handle user with zero initial tokens")
    fun shouldHandleUserWithZeroInitialTokens() = runTest {
        // Given
        val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.zero())
        val task = TestDataFactory.createTask(
            category = TaskCategory.HOMEWORK,
            assignedToUserId = user.id,
        )

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns emptyList()

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isSuccess, "Task completion should succeed with zero initial tokens")

        val completionResult = result.getOrThrow()
        assertEquals(15, completionResult.tokensAwarded, "Should award 15 tokens for homework")
        assertEquals(15, completionResult.newTokenBalance, "New balance should be 0 + 15 = 15")
    }

    @Test
    @DisplayName("Should handle user with large token balance")
    fun shouldHandleUserWithLargeTokenBalance() = runTest {
        // Given
        val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(999999))
        val task = TestDataFactory.createTask(assignedToUserId = user.id)

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns emptyList()

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isSuccess, "Task completion should succeed with large token balance")

        val completionResult = result.getOrThrow()
        assertEquals(
            1000004,
            completionResult.newTokenBalance,
            "Should handle large token balances correctly",
        )
    }

    @Test
    @DisplayName("Should complete task without newly unlocked achievements")
    fun shouldCompleteTaskWithoutNewlyUnlockedAchievements() = runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val task = TestDataFactory.createTask(assignedToUserId = user.id)

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns emptyList()

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isSuccess, "Task completion should succeed")

        val completionResult = result.getOrThrow()
        assertTrue(
            completionResult.newlyUnlockedAchievements.isEmpty(),
            "Should have no newly unlocked achievements",
        )
    }

    @Test
    @DisplayName("Should complete task with multiple newly unlocked achievements")
    fun shouldCompleteTaskWithMultipleNewlyUnlockedAchievements() = runTest {
        // Given
        val user = TestDataFactory.createChildUser()
        val task = TestDataFactory.createTask(assignedToUserId = user.id)
        val expectedAchievements = listOf(
            TestDataFactory.createUnlockedAchievement(AchievementType.FIRST_STEPS, user.id),
            TestDataFactory.createUnlockedAchievement(AchievementType.TASK_MASTER, user.id),
        )

        coEvery { taskRepository.findById(task.id) } returns task
        coEvery { userRepository.findById(user.id) } returns user
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns expectedAchievements

        // When
        val result = completeTaskUseCase(task.id)

        // Then
        assertTrue(result.isSuccess, "Task completion should succeed")

        val completionResult = result.getOrThrow()
        assertEquals(
            2,
            completionResult.newlyUnlockedAchievements.size,
            "Should have 2 newly unlocked achievements",
        )
        assertEquals(
            expectedAchievements,
            completionResult.newlyUnlockedAchievements,
            "Should return all newly unlocked achievements",
        )
    }
}
