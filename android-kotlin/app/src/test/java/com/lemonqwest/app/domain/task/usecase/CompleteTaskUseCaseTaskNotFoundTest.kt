package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.task.TaskNotFoundException
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
import java.util.UUID

/**
 * Focused test suite for task not found scenarios.
 *
 * Tests cover:
 * - Single task not found handling
 * - Multiple consecutive not found scenarios
 * - Proper exception handling and repository interaction
 */
@DisplayName("CompleteTaskUseCase - Task Not Found Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CompleteTaskUseCaseTaskNotFoundTest {

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
    @DisplayName("Should fail when task does not exist")
    fun shouldFailWhenTaskDoesNotExist() = mainDispatcherRule.runTest {
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
    fun shouldHandleMultipleConsecutiveTaskNotFoundCalls() = mainDispatcherRule.runTest {
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
