package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.task.TaskAlreadyCompletedException
import com.lemonqwest.app.domain.task.TaskRepository
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
 * Focused test suite for task already completed scenarios.
 *
 * Tests cover:
 * - Single task already completed handling
 * - Consistent failure for repeated attempts
 * - Proper exception handling and repository interaction
 */
@DisplayName("CompleteTaskUseCase - Task Already Completed Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CompleteTaskUseCaseTaskAlreadyCompletedTest {

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
    @DisplayName("Should fail when task is already completed")
    fun shouldFailWhenTaskIsAlreadyCompleted() = mainDispatcherRule.runTest {
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
    fun shouldFailConsistentlyForCompletedTasks() = mainDispatcherRule.runTest {
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
