package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Focused test suite for business rule validation.
 *
 * Tests cover:
 * - Task property preservation during completion
 * - User property preservation during token updates
 * - Token arithmetic validation
 * - Immutability enforcement
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("CompleteTaskUseCase - Business Rules Tests")
class CompleteTaskUseCaseBusinessRulesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @BeforeEach
    fun setUp() {
        // Create fresh mocks with relaxed behavior for parallel execution safety
        taskRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        achievementTrackingUseCase = mockk(relaxed = true)

        // Explicit stubs to override relaxed defaults where needed
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
            tokenBalance = TokenBalance.create(50),
        )
        val task = TestDataFactory.createTask(
            category = TaskCategory.HOUSEHOLD, // 10 tokens
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
                match {
                    it.id == originalUser.id &&
                        it.name == originalUser.name &&
                        it.role == originalUser.role &&
                        it.pin == originalUser.pin &&
                        it.tokenBalance.getValue() == 60
                },
            )
        }
    }

    @Test
    @DisplayName("Should validate token arithmetic is correct")
    fun shouldValidateTokenArithmeticIsCorrect() = runTest {
        // Test with PERSONAL_CARE category (5 tokens)
        val user1 = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(0))
        val task1 = TestDataFactory.createTask(
            category = TaskCategory.PERSONAL_CARE,
            assignedToUserId = user1.id,
        )

        coEvery { taskRepository.findById(task1.id) } returns task1
        coEvery { userRepository.findById(user1.id) } returns user1
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(user1.id) } returns emptyList()

        val result1 = completeTaskUseCase(task1.id)
        assertTrue(result1.isSuccess, "Task completion should succeed for 0 + 5")
        val completion1 = result1.getOrThrow()
        assertEquals(5, completion1.tokensAwarded, "Should award 5 tokens for PERSONAL_CARE")
        assertEquals(5, completion1.newTokenBalance, "Final balance should be 5")
    }

    // ...existing code...
}
