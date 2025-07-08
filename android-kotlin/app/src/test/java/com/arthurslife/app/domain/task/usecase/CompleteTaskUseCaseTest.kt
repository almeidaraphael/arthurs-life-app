package com.arthurslife.app.domain.task.usecase

import com.arthurslife.app.domain.TestCoroutineExtension
import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.arthurslife.app.domain.common.TaskDomainException
import com.arthurslife.app.domain.shouldBeCompleted
import com.arthurslife.app.domain.task.TaskAlreadyCompletedException
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.TaskNotFoundException
import com.arthurslife.app.domain.task.TaskRepository
import com.arthurslife.app.domain.task.TaskRepositoryException
import com.arthurslife.app.domain.task.TaskUserNotFoundException
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.domain.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

/**
 * Comprehensive test suite for CompleteTaskUseCase.
 *
 * Tests cover:
 * - Successful task completion with token rewards
 * - Achievement tracking integration
 * - Error handling for various failure scenarios
 * - Repository interaction validation
 * - Business rule enforcement
 * - Edge cases and concurrent scenarios
 */
@ExtendWith(TestCoroutineExtension::class)
@DisplayName("CompleteTaskUseCase Tests")
class CompleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        userRepository = mockk()
        achievementTrackingUseCase = mockk()
        completeTaskUseCase = CompleteTaskUseCase(
            taskRepository = taskRepository,
            userRepository = userRepository,
            achievementTrackingUseCase = achievementTrackingUseCase,
        )
    }

    @Nested
    @DisplayName("Successful Task Completion")
    inner class SuccessfulTaskCompletion {

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
                // Given
                val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(25))
                val task = TestDataFactory.createTask(
                    category = category,
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

    @Nested
    @DisplayName("Task Not Found Scenarios")
    inner class TaskNotFoundScenarios {

        @Test
        @DisplayName("Should fail when task does not exist")
        fun shouldFailWhenTaskDoesNotExist() = runTest {
            // Given
            val nonExistentTaskId = UUID.randomUUID().toString()
            coEvery { taskRepository.findById(nonExistentTaskId) } returns null

            // When
            val result = completeTaskUseCase(nonExistentTaskId)

            // Then
            assertTrue(result.isFailure, "Should fail when task does not exist")

            val exception = result.exceptionOrNull()
            assertTrue(exception is TaskNotFoundException, "Should throw TaskNotFoundException")
            assertEquals(
                nonExistentTaskId,
                (exception as TaskNotFoundException).taskId,
                "Exception should contain task ID",
            )

            coVerify { taskRepository.findById(nonExistentTaskId) }
            coVerify(exactly = 0) { userRepository.findById(any()) }
            coVerify(exactly = 0) { taskRepository.updateTask(any()) }
            coVerify(exactly = 0) { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("Should handle multiple consecutive task not found calls")
        fun shouldHandleMultipleConsecutiveTaskNotFoundCalls() = runTest {
            // Given
            val taskIds = (1..5).map { UUID.randomUUID().toString() }
            taskIds.forEach { taskId ->
                coEvery { taskRepository.findById(taskId) } returns null
            }

            // When & Then
            taskIds.forEach { taskId ->
                val result = completeTaskUseCase(taskId)
                assertTrue(result.isFailure, "Should fail for non-existent task $taskId")
                assertTrue(
                    result.exceptionOrNull() is TaskNotFoundException,
                    "Should throw TaskNotFoundException for $taskId",
                )
            }
        }
    }

    @Nested
    @DisplayName("Task Already Completed Scenarios")
    inner class TaskAlreadyCompletedScenarios {

        @Test
        @DisplayName("Should fail when task is already completed")
        fun shouldFailWhenTaskIsAlreadyCompleted() = runTest {
            // Given
            val user = TestDataFactory.createChildUser()
            val completedTask = TestDataFactory.createCompletedTask(assignedToUserId = user.id)

            coEvery { taskRepository.findById(completedTask.id) } returns completedTask

            // When
            val result = completeTaskUseCase(completedTask.id)

            // Then
            assertTrue(result.isFailure, "Should fail when task is already completed")

            val exception = result.exceptionOrNull()
            assertTrue(
                exception is TaskAlreadyCompletedException,
                "Should throw TaskAlreadyCompletedException",
            )
            assertEquals(
                completedTask.id,
                (exception as TaskAlreadyCompletedException).taskId,
                "Exception should contain task ID",
            )

            coVerify { taskRepository.findById(completedTask.id) }
            coVerify(exactly = 0) { userRepository.findById(any()) }
            coVerify(exactly = 0) { taskRepository.updateTask(any()) }
            coVerify(exactly = 0) { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("Should fail consistently for completed tasks")
        fun shouldFailConsistentlyForCompletedTasks() = runTest {
            // Given
            val user = TestDataFactory.createChildUser()
            val completedTask = TestDataFactory.createCompletedTask(assignedToUserId = user.id)

            coEvery { taskRepository.findById(completedTask.id) } returns completedTask

            // When & Then - Multiple attempts should all fail
            repeat(3) {
                val result = completeTaskUseCase(completedTask.id)
                assertTrue(result.isFailure, "Should fail consistently for completed task")
                assertTrue(
                    result.exceptionOrNull() is TaskAlreadyCompletedException,
                    "Should consistently throw TaskAlreadyCompletedException",
                )
            }
        }
    }

    @Nested
    @DisplayName("User Not Found Scenarios")
    inner class UserNotFoundScenarios {

        @Test
        @DisplayName("Should fail when assigned user does not exist")
        fun shouldFailWhenAssignedUserDoesNotExist() = runTest {
            // Given
            val nonExistentUserId = UUID.randomUUID().toString()
            val task = TestDataFactory.createTask(assignedToUserId = nonExistentUserId)

            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(nonExistentUserId) } returns null

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(result.isFailure, "Should fail when assigned user does not exist")

            val exception = result.exceptionOrNull()
            assertTrue(
                exception is TaskUserNotFoundException,
                "Should throw TaskUserNotFoundException",
            )
            assertEquals(
                nonExistentUserId,
                (exception as TaskUserNotFoundException).userId,
                "Exception should contain user ID",
            )

            coVerify { taskRepository.findById(task.id) }
            coVerify { userRepository.findById(nonExistentUserId) }
            coVerify(exactly = 0) { taskRepository.updateTask(any()) }
            coVerify(exactly = 0) { userRepository.updateUser(any()) }
        }

        @Test
        @DisplayName("Should handle orphaned tasks gracefully")
        fun shouldHandleOrphanedTasksGracefully() = runTest {
            // Given
            val deletedUserId = UUID.randomUUID().toString()
            val orphanedTask = TestDataFactory.createTask(assignedToUserId = deletedUserId)

            coEvery { taskRepository.findById(orphanedTask.id) } returns orphanedTask
            coEvery { userRepository.findById(deletedUserId) } returns null

            // When
            val result = completeTaskUseCase(orphanedTask.id)

            // Then
            assertTrue(result.isFailure, "Should fail for orphaned task")
            assertTrue(
                result.exceptionOrNull() is TaskUserNotFoundException,
                "Should throw TaskUserNotFoundException for orphaned task",
            )
        }
    }

    @Nested
    @DisplayName("Repository Exception Scenarios")
    inner class RepositoryExceptionScenarios {

        @Test
        @DisplayName("Should handle task repository exception during find")
        fun shouldHandleTaskRepositoryExceptionDuringFind() = runTest {
            // Given
            val taskId = UUID.randomUUID().toString()
            val repositoryException = TaskRepositoryException("Database connection failed")

            coEvery { taskRepository.findById(taskId) } throws repositoryException

            // When
            val result = completeTaskUseCase(taskId)

            // Then
            assertTrue(result.isFailure, "Should fail when repository throws exception")
            assertEquals(
                repositoryException,
                result.exceptionOrNull(),
                "Should propagate repository exception",
            )
        }

        @Test
        @DisplayName("Should handle task repository exception during update")
        fun shouldHandleTaskRepositoryExceptionDuringUpdate() = runTest {
            // Given
            val user = TestDataFactory.createChildUser()
            val task = TestDataFactory.createTask(assignedToUserId = user.id)
            val repositoryException = TaskRepositoryException("Failed to update task")

            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(user.id) } returns user
            coEvery { taskRepository.updateTask(any()) } throws repositoryException

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(result.isFailure, "Should fail when task update throws exception")
            assertEquals(
                repositoryException,
                result.exceptionOrNull(),
                "Should propagate repository exception",
            )
        }

        @Test
        @DisplayName("Should handle user repository exception during update")
        fun shouldHandleUserRepositoryExceptionDuringUpdate() = runTest {
            // Given
            val user = TestDataFactory.createChildUser()
            val task = TestDataFactory.createTask(assignedToUserId = user.id)
            val repositoryException = TaskRepositoryException("Failed to update user")

            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(user.id) } returns user
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.updateUser(any()) } throws repositoryException

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(result.isFailure, "Should fail when user update throws exception")
            assertEquals(
                repositoryException,
                result.exceptionOrNull(),
                "Should propagate repository exception",
            )
        }

        @Test
        @DisplayName("Should handle achievement tracking exception")
        fun shouldHandleAchievementTrackingException() = runTest {
            // Given
            val user = TestDataFactory.createChildUser()
            val task = TestDataFactory.createTask(assignedToUserId = user.id)
            val achievementException = TaskDomainException("Achievement tracking failed")

            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(user.id) } returns user
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } throws achievementException

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(result.isFailure, "Should fail when achievement tracking throws exception")
            assertEquals(
                achievementException,
                result.exceptionOrNull(),
                "Should propagate achievement exception",
            )
        }
    }

    @Nested
    @DisplayName("Business Rule Validation")
    inner class BusinessRuleValidation {

        @Test
        @DisplayName("Should preserve task properties except completion status")
        fun shouldPreserveTaskPropertiesExceptCompletionStatus() = runTest {
            // Given
            val user = TestDataFactory.createChildUser()
            val originalTask = TestDataFactory.createTask(
                title = "Original Title",
                category = TaskCategory.HOMEWORK,
                assignedToUserId = user.id,
            )

            coEvery { taskRepository.findById(originalTask.id) } returns originalTask
            coEvery { userRepository.findById(user.id) } returns user
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns emptyList()

            // When
            val result = completeTaskUseCase(originalTask.id)

            // Then
            assertTrue(result.isSuccess, "Task completion should succeed")

            val completedTask = result.getOrThrow().task
            assertEquals(originalTask.id, completedTask.id, "Task ID should be preserved")
            assertEquals(originalTask.title, completedTask.title, "Task title should be preserved")
            assertEquals(
                originalTask.category,
                completedTask.category,
                "Task category should be preserved",
            )
            assertEquals(
                originalTask.tokenReward,
                completedTask.tokenReward,
                "Task reward should be preserved",
            )
            assertEquals(
                originalTask.assignedToUserId,
                completedTask.assignedToUserId,
                "Task assignment should be preserved",
            )
            assertTrue(completedTask.isCompleted, "Task should be marked as completed")
            assertFalse(
                originalTask.isCompleted,
                "Original task should remain unchanged (immutability)",
            )
        }

        @Test
        @DisplayName("Should preserve user properties except token balance")
        fun shouldPreserveUserPropertiesExceptTokenBalance() = runTest {
            // Given
            val originalUser = TestDataFactory.createChildUser(
                name = "Original Child",
                tokenBalance = TokenBalance.create(50),
            )
            val task = TestDataFactory.createTask(
                category = TaskCategory.HOUSEHOLD,
                assignedToUserId = originalUser.id,
            )

            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(originalUser.id) } returns originalUser
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(originalUser.id) } returns emptyList()

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(result.isSuccess, "Task completion should succeed")

            val completionResult = result.getOrThrow()
            assertEquals(
                60,
                completionResult.newTokenBalance,
                "Token balance should be updated to 50 + 10 = 60",
            )

            // Verify user update call preserves other properties
            coVerify {
                userRepository.updateUser(
                    match { updatedUser ->
                        updatedUser.id == originalUser.id &&
                            updatedUser.name == originalUser.name &&
                            updatedUser.role == originalUser.role &&
                            updatedUser.pin == originalUser.pin &&
                            updatedUser.tokenBalance.getValue() == 60
                    },
                )
            }
        }

        @Test
        @DisplayName("Should validate token arithmetic is correct")
        fun shouldValidateTokenArithmeticIsCorrect() = runTest {
            val testCases = listOf(
                Triple(0, TaskCategory.PERSONAL_CARE, 5),
                Triple(25, TaskCategory.PERSONAL_CARE, 30),
                Triple(50, TaskCategory.HOUSEHOLD, 60),
                Triple(100, TaskCategory.HOMEWORK, 115),
                Triple(999, TaskCategory.HOMEWORK, 1014),
            )

            testCases.forEach { (initialTokens, category, expectedFinalTokens) ->
                // Given
                val user = TestDataFactory.createChildUser(
                    tokenBalance = TokenBalance.create(initialTokens),
                )
                val task = TestDataFactory.createTask(
                    category = category,
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
                assertTrue(
                    result.isSuccess,
                    "Task completion should succeed for $initialTokens + ${category.defaultTokenReward}",
                )

                val completionResult = result.getOrThrow()
                assertEquals(
                    category.defaultTokenReward,
                    completionResult.tokensAwarded,
                    "Should award correct tokens for ${category.displayName}",
                )
                assertEquals(
                    expectedFinalTokens,
                    completionResult.newTokenBalance,
                    "Final balance should be $expectedFinalTokens",
                )
            }
        }
    }

    @Nested
    @DisplayName("Integration and End-to-End Scenarios")
    inner class IntegrationAndEndToEndScenarios {

        @Test
        @DisplayName("Should handle complete task completion workflow")
        fun shouldHandleCompleteTaskCompletionWorkflow() = runTest {
            // Given - Complete scenario setup
            val (user, task, achievement) = TestDataFactory.createTaskCompletionScenario()
            val expectedAchievements = listOf(achievement.unlock())

            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(user.id) } returns user
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns expectedAchievements

            // When
            val result = completeTaskUseCase(task.id)

            // Then
            assertTrue(result.isSuccess, "Complete workflow should succeed")

            val completionResult = result.getOrThrow()
            completionResult.task.shouldBeCompleted()
            assertEquals(
                task.tokenReward,
                completionResult.tokensAwarded,
                "Should award correct tokens",
            )
            assertTrue(
                completionResult.newTokenBalance > user.tokenBalance.getValue(),
                "Token balance should increase",
            )
            assertEquals(
                expectedAchievements,
                completionResult.newlyUnlockedAchievements,
                "Should unlock achievements",
            )

            // Verify all repository interactions occurred
            coVerify { taskRepository.findById(task.id) }
            coVerify { userRepository.findById(user.id) }
            coVerify { taskRepository.updateTask(any()) }
            coVerify { userRepository.updateUser(any()) }
            coVerify { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) }
        }

        @Test
        @DisplayName("Should handle multiple task completions for same user")
        fun shouldHandleMultipleTaskCompletionsForSameUser() = runTest {
            // Given
            val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(20))
            val tasks = TestDataFactory.createTaskList(
                assignedToUserId = user.id,
                includeCompleted = false,
            )
            var currentBalance = 20

            tasks.forEach { task ->
                coEvery { taskRepository.findById(task.id) } returns task
                coEvery { userRepository.findById(user.id) } returns user.copy(tokenBalance = TokenBalance.create(currentBalance))
                coEvery { taskRepository.updateTask(any()) } returns Unit
                coEvery { userRepository.updateUser(any()) } returns Unit
                coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns emptyList()

                // When
                val result = completeTaskUseCase(task.id)

                // Then
                assertTrue(
                    result.isSuccess,
                    "Task completion should succeed for task ${task.title}",
                )

                val completionResult = result.getOrThrow()
                assertEquals(
                    task.tokenReward,
                    completionResult.tokensAwarded,
                    "Should award correct tokens for ${task.title}",
                )
                currentBalance += task.tokenReward
                assertEquals(
                    currentBalance,
                    completionResult.newTokenBalance,
                    "Token balance should accumulate correctly",
                )
            }
        }

        @Test
        @DisplayName("Should handle task completion with various token scenarios")
        fun shouldHandleTaskCompletionWithVariousTokenScenarios() = runTest {
            val scenarios = listOf(
                Pair("Zero tokens", TokenBalance.zero()),
                Pair("Small amount", TokenBalance.create(5)),
                Pair("Medium amount", TokenBalance.create(50)),
                Pair("Large amount", TokenBalance.create(10000)),
            )

            scenarios.forEach { (scenarioName, initialBalance) ->
                // Given
                val user = TestDataFactory.createChildUser(tokenBalance = initialBalance)
                val task = TestDataFactory.createTask(assignedToUserId = user.id)

                coEvery { taskRepository.findById(task.id) } returns task
                coEvery { userRepository.findById(user.id) } returns user
                coEvery { taskRepository.updateTask(any()) } returns Unit
                coEvery { userRepository.updateUser(any()) } returns Unit
                coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user.id) } returns emptyList()

                // When
                val result = completeTaskUseCase(task.id)

                // Then
                assertTrue(result.isSuccess, "Should succeed for scenario: $scenarioName")

                val completionResult = result.getOrThrow()
                val expectedBalance = initialBalance.getValue() + task.tokenReward
                assertEquals(
                    expectedBalance,
                    completionResult.newTokenBalance,
                    "Should calculate correct balance for: $scenarioName",
                )
            }
        }
    }
}
