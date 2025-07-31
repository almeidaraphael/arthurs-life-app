package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
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
 * Focused test suite for integration and end-to-end scenarios.
 *
 * Tests cover:
 * - Complete task completion workflow
 * - Multiple task completions for same user
 * - Various token balance scenarios
 * - Full system integration testing
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("CompleteTaskUseCase - Integration Tests")
class CompleteTaskUseCaseIntegrationTest {

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
    @DisplayName("Should handle complete task completion workflow")
    fun shouldHandleCompleteTaskCompletionWorkflow() = runTest {
        // Given - Complete scenario setup
        val user = TestDataFactory.createChildUser()
        val task = TestDataFactory.createTask(
            assignedToUserId = user.id,
            isCompleted = false,
        ) // Not already completed
        val achievement = TestDataFactory.createAchievement(
            userId = user.id,
        )
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
        // Given - Test completing two tasks sequentially
        val user = TestDataFactory.createChildUser(tokenBalance = TokenBalance.create(20))
        val firstTask = TestDataFactory.createTask(
            title = "First Task",
            category = TaskCategory.PERSONAL_CARE, // 5 tokens
            assignedToUserId = user.id,
        )
        val secondTask = TestDataFactory.createTask(
            title = "Second Task",
            category = TaskCategory.HOUSEHOLD, // 10 tokens
            assignedToUserId = user.id,
        )

        // First task completion - clear and setup fresh mocks
        clearMocks(taskRepository, userRepository, achievementTrackingUseCase)

        // Re-setup default stubs
        coEvery { taskRepository.findById(any()) } returns null
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.findById(any()) } returns null
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

        // Setup specific mocks for first task
        coEvery { taskRepository.findById(firstTask.id) } returns firstTask
        coEvery { userRepository.findById(user.id) } returns user

        val firstResult = completeTaskUseCase(firstTask.id)
        assertTrue(firstResult.isSuccess, "First task completion should succeed")

        val firstCompletion = firstResult.getOrThrow()
        assertEquals(5, firstCompletion.tokensAwarded, "Should award 5 tokens for first task")
        assertEquals(25, firstCompletion.newTokenBalance, "Balance should be 20 + 5 = 25")

        // Second task completion - clear and setup fresh mocks again
        clearMocks(taskRepository, userRepository, achievementTrackingUseCase)

        // Re-setup default stubs
        coEvery { taskRepository.findById(any()) } returns null
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.findById(any()) } returns null
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

        // Setup specific mocks for second task
        val userAfterFirst = user.copy(tokenBalance = TokenBalance.create(25))
        coEvery { taskRepository.findById(secondTask.id) } returns secondTask
        coEvery { userRepository.findById(user.id) } returns userAfterFirst

        val secondResult = completeTaskUseCase(secondTask.id)
        assertTrue(secondResult.isSuccess, "Second task completion should succeed")

        val secondCompletion = secondResult.getOrThrow()
        assertEquals(10, secondCompletion.tokensAwarded, "Should award 10 tokens for second task")
        assertEquals(35, secondCompletion.newTokenBalance, "Balance should be 25 + 10 = 35")
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
            // Clear any previous stubs to avoid conflicts
            clearMocks(taskRepository, userRepository, achievementTrackingUseCase)

            // Re-setup default stubs
            coEvery { taskRepository.findById(any()) } returns null
            coEvery { taskRepository.updateTask(any()) } returns Unit
            coEvery { userRepository.findById(any()) } returns null
            coEvery { userRepository.updateUser(any()) } returns Unit
            coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

            // Given
            val user = TestDataFactory.createChildUser(tokenBalance = initialBalance)
            val task = TestDataFactory.createTask(assignedToUserId = user.id)

            // Set up specific mocks for this iteration
            coEvery { taskRepository.findById(task.id) } returns task
            coEvery { userRepository.findById(user.id) } returns user

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
