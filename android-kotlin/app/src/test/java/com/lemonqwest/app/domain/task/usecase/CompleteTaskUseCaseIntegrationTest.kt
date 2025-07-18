package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.shouldBeCompleted
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

/**
 * Focused test suite for integration and end-to-end scenarios.
 *
 * Tests cover:
 * - Complete task completion workflow
 * - Multiple task completions for same user
 * - Various token balance scenarios
 * - Full system integration testing
 */
@DisplayName("CompleteTaskUseCase - Integration Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CompleteTaskUseCaseIntegrationTest {

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
        // Default stubs to prevent MockKExceptions for any un-stubbed calls
        io.mockk.coEvery { taskRepository.findById(any()) } returns null
        io.mockk.coEvery { taskRepository.updateTask(any()) } returns Unit
        io.mockk.coEvery { userRepository.findById(any()) } returns null
        io.mockk.coEvery { userRepository.updateUser(any()) } returns Unit
        io.mockk.coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()

        coEvery { taskRepository.findById(any()) } returns null
        coEvery { taskRepository.updateTask(any()) } returns Unit
        coEvery { userRepository.findById(any()) } returns null
        coEvery { userRepository.updateUser(any()) } returns Unit
        coEvery { achievementTrackingUseCase.updateAchievementsAfterTaskCompletion(any()) } returns emptyList()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("Should handle complete task completion workflow")
    fun shouldHandleCompleteTaskCompletionWorkflow() = mainDispatcherRule.runTest {
        // Given - Complete scenario setup
        val (task, user, achievement) = TestDataFactory.createTaskCompletionScenario()
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
    fun shouldHandleMultipleTaskCompletionsForSameUser() = mainDispatcherRule.runTest {
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
    fun shouldHandleTaskCompletionWithVariousTokenScenarios() = mainDispatcherRule.runTest {
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
