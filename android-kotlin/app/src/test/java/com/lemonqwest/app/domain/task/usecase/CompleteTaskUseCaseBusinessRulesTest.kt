package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.user.TokenBalance
import com.lemonqwest.app.domain.user.UserRepository
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

/**
 * Focused test suite for business rule validation.
 *
 * Tests cover:
 * - Task property preservation during completion
 * - User property preservation during token updates
 * - Token arithmetic validation
 * - Immutability enforcement
 */
@DisplayName("CompleteTaskUseCase - Business Rules Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CompleteTaskUseCaseBusinessRulesTest {

    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var achievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        taskRepository = mockk()
        userRepository = mockk()
        achievementTrackingUseCase = mockk()
        completeTaskUseCase = CompleteTaskUseCase(
            taskRepository,
            userRepository,
            achievementTrackingUseCase,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("Should preserve task properties except completion status")
    fun shouldPreserveTaskPropertiesExceptCompletionStatus() = mainDispatcherRule.runTest {
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
    fun shouldPreserveUserPropertiesExceptTokenBalance() = mainDispatcherRule.runTest {
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
    fun shouldValidateTokenArithmeticIsCorrect() = mainDispatcherRule.runTest {
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
