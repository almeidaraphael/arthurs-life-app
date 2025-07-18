package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldHaveProperties
import com.lemonqwest.app.domain.task.InvalidTaskDataException
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.TaskNotFoundException
import com.lemonqwest.app.domain.task.TaskRepository
import com.lemonqwest.app.domain.task.TaskRepositoryException
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID

/**
 * Focused test suite for task update use case.
 *
 * Tests cover:
 * - Task updates with validation
 * - Category and reward updates
 * - Error handling for invalid inputs
 * - Repository interaction validation
 */
@DisplayName("Update Task Use Case Tests")
class UpdateTaskUseCaseTest {

    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskManagementUseCases: TaskManagementUseCases

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        taskRepository = mockk()
        taskManagementUseCases = TaskManagementUseCases(taskRepository)
        // No global default stubs; each test must stub required calls explicitly
    }

    @Test
    @DisplayName("Should update task successfully")
    fun shouldUpdateTaskSuccessfully() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask(
            title = "Old title",
            category = TaskCategory.PERSONAL_CARE,
        )
        val newTitle = "New title"
        val newCategory = TaskCategory.HOMEWORK

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.updateTask(any()) } returns Unit

        // When
        val result = taskManagementUseCases.updateTask(existingTask.id, newTitle, newCategory)

        // Then
        assertTrue(result.isSuccess, "Task update should succeed")

        val updatedTask = result.getOrThrow()
        updatedTask.shouldHaveProperties(newTitle, newCategory, newCategory.defaultTokenReward)
        assertEquals(existingTask.id, updatedTask.id, "Task ID should remain unchanged")
        assertEquals(
            existingTask.assignedToUserId,
            updatedTask.assignedToUserId,
            "Assignment should remain unchanged",
        )
        assertEquals(
            existingTask.isCompleted,
            updatedTask.isCompleted,
            "Completion status should remain unchanged",
        )

        coVerify { taskRepository.findById(existingTask.id) }
        coVerify {
            taskRepository.updateTask(
                match { it.title == newTitle && it.category == newCategory },
            )
        }
    }

    @Test
    @DisplayName("Should update task category and token reward")
    fun shouldUpdateTaskCategoryAndTokenReward() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask(
            category = TaskCategory.PERSONAL_CARE,
        ) // 5 tokens
        val newCategory = TaskCategory.HOMEWORK // 15 tokens

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.updateTask(any()) } returns Unit

        // When
        val result = taskManagementUseCases.updateTask(
            existingTask.id,
            existingTask.title,
            newCategory,
        )

        // Then
        assertTrue(result.isSuccess, "Task update should succeed")

        val updatedTask = result.getOrThrow()
        assertEquals(newCategory, updatedTask.category, "Category should be updated")
        assertEquals(
            15,
            updatedTask.tokenReward,
            "Token reward should be updated to match new category",
        )

        coVerify { taskRepository.updateTask(match { it.tokenReward == 15 }) }
    }

    @Test
    @DisplayName("Should trim whitespace from updated title")
    fun shouldTrimWhitespaceFromUpdatedTitle() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask()
        val titleWithWhitespace = "  Updated title  "
        val expectedTitle = "Updated title"

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.updateTask(any()) } returns Unit

        // When
        val result = taskManagementUseCases.updateTask(
            existingTask.id,
            titleWithWhitespace,
            existingTask.category,
        )

        // Then
        assertTrue(result.isSuccess, "Task update should succeed")

        val updatedTask = result.getOrThrow()
        assertEquals(expectedTitle, updatedTask.title, "Updated title should be trimmed")

        coVerify { taskRepository.updateTask(match { it.title == expectedTitle }) }
    }

    @Test
    @DisplayName("Should fail when task not found")
    fun shouldFailWhenTaskNotFound() = mainDispatcherRule.runTest {
        // Given
        val nonExistentTaskId = UUID.randomUUID().toString()
        val newTitle = "New title"
        val newCategory = TaskCategory.HOMEWORK

        coEvery { taskRepository.findById(nonExistentTaskId) } returns null

        // When
        val result = taskManagementUseCases.updateTask(nonExistentTaskId, newTitle, newCategory)

        // Then
        assertTrue(result.isFailure, "Task update should fail when task not found")

        val exception = result.exceptionOrNull()
        assertTrue(exception is TaskNotFoundException, "Should throw TaskNotFoundException")
        assertEquals(
            nonExistentTaskId,
            (exception as TaskNotFoundException).taskId,
            "Exception should contain task ID",
        )

        coVerify { taskRepository.findById(nonExistentTaskId) }
        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    @DisplayName("Should fail when updated title is blank")
    fun shouldFailWhenUpdatedTitleIsBlank() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask()
        val blankTitle = ""

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask

        // When
        val result = taskManagementUseCases.updateTask(
            existingTask.id,
            blankTitle,
            existingTask.category,
        )

        // Then
        assertTrue(result.isFailure, "Task update should fail for blank title")

        val exception = result.exceptionOrNull()
        assertTrue(
            exception is InvalidTaskDataException,
            "Should throw InvalidTaskDataException",
        )
        assertEquals(
            "Invalid task data: Task title cannot be blank",
            exception?.message,
            "Should have correct error message",
        )

        coVerify { taskRepository.findById(existingTask.id) }
        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    @DisplayName("Should handle repository exception during update")
    fun shouldHandleRepositoryExceptionDuringUpdate() = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask()
        val repositoryException = TaskRepositoryException("Update failed")

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.updateTask(any()) } throws repositoryException

        // When
        val result = taskManagementUseCases.updateTask(
            existingTask.id,
            "New title",
            TaskCategory.HOMEWORK,
        )

        // Then
        assertTrue(result.isFailure, "Task update should fail when repository throws exception")
        assertEquals(
            repositoryException,
            result.exceptionOrNull(),
            "Should propagate repository exception",
        )
    }

    @ParameterizedTest
    @EnumSource(TaskCategory::class)
    @DisplayName("Should update to all categories correctly")
    fun shouldUpdateToAllCategoriesCorrectly(
        newCategory: TaskCategory,
    ) = mainDispatcherRule.runTest {
        // Given
        val existingTask = TestDataFactory.createTask(category = TaskCategory.PERSONAL_CARE)

        coEvery { taskRepository.findById(existingTask.id) } returns existingTask
        coEvery { taskRepository.updateTask(any()) } returns Unit

        // When
        val result = taskManagementUseCases.updateTask(
            existingTask.id,
            existingTask.title,
            newCategory,
        )

        // Then
        assertTrue(
            result.isSuccess,
            "Task update should succeed for category ${newCategory.displayName}",
        )

        val updatedTask = result.getOrThrow()
        assertEquals(
            newCategory,
            updatedTask.category,
            "Category should be updated to ${newCategory.displayName}",
        )
        assertEquals(
            newCategory.defaultTokenReward,
            updatedTask.tokenReward,
            "Token reward should match new category",
        )
    }
}
