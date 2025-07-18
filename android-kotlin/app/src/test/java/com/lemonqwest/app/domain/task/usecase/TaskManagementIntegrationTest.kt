package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

/**
 * Integration tests for task management use cases.
 *
 * Tests cover:
 * - Complete task lifecycle scenarios
 * - Bulk operations validation
 * - End-to-end workflow testing
 */
@DisplayName("Task Management Integration Tests")
class TaskManagementIntegrationTest {

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

    @Test
    @DisplayName("Should handle complete task lifecycle")
    fun shouldHandleCompleteTaskLifecycle() = mainDispatcherRule.runTest {
        // Given
        val userId = UUID.randomUUID().toString()
        val title = "Test Task"
        val category = TaskCategory.HOMEWORK

        // Create task
        coEvery { taskRepository.saveTask(any()) } returns Unit

        val createResult = taskManagementUseCases.createTask(title, category, userId)
        val createdTask = createResult.getOrThrow()

        // Update task
        val newTitle = "Updated Task"
        val newCategory = TaskCategory.HOUSEHOLD

        coEvery { taskRepository.findById(createdTask.id) } returns createdTask
        coEvery { taskRepository.updateTask(any()) } returns Unit

        val updateResult = taskManagementUseCases.updateTask(
            createdTask.id,
            newTitle,
            newCategory,
        )
        val updatedTask = updateResult.getOrThrow()

        // Delete task
        coEvery { taskRepository.findById(updatedTask.id) } returns updatedTask
        coEvery { taskRepository.deleteTask(updatedTask.id) } returns Unit

        val deleteResult = taskManagementUseCases.deleteTask(updatedTask.id)

        // Then
        assertTrue(createResult.isSuccess, "Task creation should succeed")
        assertTrue(updateResult.isSuccess, "Task update should succeed")
        assertTrue(deleteResult.isSuccess, "Task deletion should succeed")

        assertEquals(newTitle, updatedTask.title, "Task should be updated with new title")
        assertEquals(
            newCategory,
            updatedTask.category,
            "Task should be updated with new category",
        )
    }

    @Test
    @DisplayName("Should handle bulk operations correctly")
    fun shouldHandleBulkOperationsCorrectly() = mainDispatcherRule.runTest {
        // Given
        val userId = UUID.randomUUID().toString()
        val tasks = (1..10).map { index ->
            TestDataFactory.createTask(
                title = "Task $index",
                category = TaskCategory.values()[index % 3],
                assignedToUserId = userId,
            )
        }

        // Create multiple tasks
        tasks.forEach { task ->
            coEvery { taskRepository.saveTask(match { it.title == task.title }) } returns Unit
        }

        val createResults = tasks.map { task ->
            taskManagementUseCases.createTask(task.title, task.category, userId)
        }

        // Get all tasks for user
        coEvery { taskRepository.findByUserId(userId) } returns tasks

        val getAllResult = taskManagementUseCases.getTasksForUser(userId)

        // Then
        assertTrue(createResults.all { it.isSuccess }, "All task creations should succeed")
        assertTrue(getAllResult.isSuccess, "Getting all tasks should succeed")

        val retrievedTasks = getAllResult.getOrThrow()
        assertEquals(tasks.size, retrievedTasks.size, "Should retrieve all created tasks")
    }
}
