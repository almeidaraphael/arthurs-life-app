package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.task.TaskRepository
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
 * Focused test suite for getting completed tasks for user use case.
 *
 * Tests cover:
 * - Completed task retrieval for specific user
 * - Empty list handling when no tasks completed
 * - Task completion status validation
 */
@DisplayName("Get Completed Tasks for User Use Case Tests")
@Execution(ExecutionMode.SAME_THREAD)
class GetCompletedTasksForUserUseCaseTest {

    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskManagementUseCases: TaskManagementUseCases

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        taskRepository = mockk()
        taskManagementUseCases = TaskManagementUseCases(taskRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @DisplayName("Should get completed tasks for user")
    fun shouldGetCompletedTasksForUser() = mainDispatcherRule.runTest {
        // Given
        val userId = UUID.randomUUID().toString()
        val completedTasks = (1..5).map {
            TestDataFactory.createCompletedTask(
                assignedToUserId = userId,
            )
        }

        coEvery { taskRepository.findCompletedByUserId(userId) } returns completedTasks

        // When
        val result = taskManagementUseCases.getCompletedTasksForUser(userId)

        // Then
        assertTrue(result.isSuccess, "Should succeed getting completed tasks")

        val tasks = result.getOrThrow()
        assertEquals(completedTasks.size, tasks.size, "Should return all completed tasks")
        assertTrue(tasks.all { it.isCompleted }, "All returned tasks should be completed")

        coVerify { taskRepository.findCompletedByUserId(userId) }
    }

    @Test
    @DisplayName("Should return empty list when no tasks are completed")
    fun shouldReturnEmptyListWhenNoTasksAreCompleted() = mainDispatcherRule.runTest {
        // Given
        val userId = UUID.randomUUID().toString()

        coEvery { taskRepository.findCompletedByUserId(userId) } returns emptyList()

        // When
        val result = taskManagementUseCases.getCompletedTasksForUser(userId)

        // Then
        assertTrue(result.isSuccess, "Should succeed when no tasks are completed")

        val tasks = result.getOrThrow()
        assertTrue(tasks.isEmpty(), "Should return empty list")
    }
}
