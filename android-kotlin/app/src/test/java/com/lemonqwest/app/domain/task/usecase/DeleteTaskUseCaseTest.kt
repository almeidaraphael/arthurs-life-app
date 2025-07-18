package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.task.TaskNotFoundException
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.task.TaskRepositoryException
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
import java.util.UUID

/**
 * Focused test suite for task deletion use case.
 *
 * Tests cover:
 * - Task deletion validation
 * - Completed task deletion
 * - Error handling for missing tasks
 * - Repository exception handling
 */
@DisplayName("Delete Task Use Case Tests")
class DeleteTaskUseCaseTest {

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
    @DisplayName("Should delete task successfully")
    fun shouldDeleteTaskSuccessfully() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask()

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.deleteTask(existingTask.id) } returns Unit

        // When
        val result = taskManagementUseCases.deleteTask(existingTask.id)

        // Then
        assertTrue(result.isSuccess, "Task deletion should succeed")
        assertEquals(Unit, result.getOrThrow(), "Should return Unit on success")

        coVerify { taskRepository.findById(existingTask.id) }
        coVerify { taskRepository.deleteTask(existingTask.id) }
    }

    @Test
    @DisplayName("Should fail when task to delete not found")
    fun shouldFailWhenTaskToDeleteNotFound() = mainDispatcherRule.runTest {
        // Given
        val nonExistentTaskId = UUID.randomUUID().toString()

        coEvery { taskRepository.findById(nonExistentTaskId) } returns null

        // When
        val result = taskManagementUseCases.deleteTask(nonExistentTaskId)

        // Then
        assertTrue(result.isFailure, "Task deletion should fail when task not found")

        val exception = result.exceptionOrNull()
        assertTrue(exception is TaskNotFoundException, "Should throw TaskNotFoundException")
        assertEquals(
            nonExistentTaskId,
            (exception as TaskNotFoundException).taskId,
            "Exception should contain task ID",
        )

        coVerify { taskRepository.findById(nonExistentTaskId) }
        coVerify(exactly = 0) { taskRepository.deleteTask(any()) }
    }

    @Test
    @DisplayName("Should handle repository exception during deletion")
    fun shouldHandleRepositoryExceptionDuringDeletion() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask()
        val repositoryException = TaskRepositoryException("Deletion failed")

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.deleteTask(existingTask.id) } throws repositoryException

        // When
        val result = taskManagementUseCases.deleteTask(existingTask.id)

        // Then
        assertTrue(
            result.isFailure,
            "Task deletion should fail when repository throws exception",
        )
        assertEquals(
            repositoryException,
            result.exceptionOrNull(),
            "Should propagate repository exception",
        )
    }

    @Test
    @DisplayName("Should delete completed task successfully")
    fun shouldDeleteCompletedTaskSuccessfully() = mainDispatcherRule.runTest {
        // Given
        val completedTask = TestDataFactory.createCompletedTask()

        coEvery { taskRepository.findById(completedTask.id) } returns completedTask
        coEvery { taskRepository.deleteTask(completedTask.id) } returns Unit

        // When
        val result = taskManagementUseCases.deleteTask(completedTask.id)

        // Then
        assertTrue(result.isSuccess, "Should be able to delete completed tasks")

        coVerify { taskRepository.deleteTask(completedTask.id) }
    }
}
