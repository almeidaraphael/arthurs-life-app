package com.arthurslife.app.domain.task.usecase

import com.arthurslife.app.domain.TestCoroutineExtension
import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.common.TaskDomainException
import com.arthurslife.app.domain.shouldBeAssignedTo
import com.arthurslife.app.domain.shouldHaveDefaultReward
import com.arthurslife.app.domain.shouldHaveProperties
import com.arthurslife.app.domain.shouldNotBeCompleted
import com.arthurslife.app.domain.task.InvalidTaskDataException
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.TaskNotFoundException
import com.arthurslife.app.domain.task.TaskRepository
import com.arthurslife.app.domain.task.TaskRepositoryException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Comprehensive test suite for TaskManagementUseCases.
 *
 * Tests cover:
 * - Task creation with validation
 * - Task updates and modifications
 * - Task deletion
 * - Task retrieval operations
 * - Task statistics and reporting
 * - Error handling for various failure scenarios
 * - Repository interaction validation
 * - Business rule enforcement
 */
@ExtendWith(TestCoroutineExtension::class)
@DisplayName("TaskManagementUseCases Tests")
class TaskManagementUseCasesTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var taskManagementUseCases: TaskManagementUseCases

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        taskManagementUseCases = TaskManagementUseCases(taskRepository)
    }

    @Nested
    @DisplayName("Create Task")
    inner class CreateTask {

        @Test
        @DisplayName("Should create task successfully")
        fun shouldCreateTaskSuccessfully() = runTest {
            // Given
            val title = "Brush teeth"
            val category = TaskCategory.PERSONAL_CARE
            val userId = UUID.randomUUID().toString()

            coEvery { taskRepository.saveTask(any()) } returns Unit

            // When
            val result = taskManagementUseCases.createTask(title, category, userId)

            // Then
            assertTrue(result.isSuccess, "Task creation should succeed")

            val createdTask = result.getOrThrow()
            createdTask.shouldHaveProperties(title, category, category.defaultTokenReward)
            createdTask.shouldBeAssignedTo(userId)
            createdTask.shouldNotBeCompleted()
            createdTask.shouldHaveDefaultReward()

            coVerify {
                taskRepository.saveTask(
                    match { it.title == title && it.category == category },
                )
            }
        }

        @ParameterizedTest
        @EnumSource(TaskCategory::class)
        @DisplayName("Should create tasks for all categories")
        fun shouldCreateTasksForAllCategories(category: TaskCategory) = runTest {
            // Given
            val title = "Test task for ${category.displayName}"
            val userId = UUID.randomUUID().toString()

            coEvery { taskRepository.saveTask(any()) } returns Unit

            // When
            val result = taskManagementUseCases.createTask(title, category, userId)

            // Then
            assertTrue(
                result.isSuccess,
                "Task creation should succeed for category ${category.displayName}",
            )

            val createdTask = result.getOrThrow()
            assertEquals(category, createdTask.category, "Task should have correct category")
            assertEquals(
                category.defaultTokenReward,
                createdTask.tokenReward,
                "Task should have correct default reward",
            )
        }

        @Test
        @DisplayName("Should trim whitespace from task title")
        fun shouldTrimWhitespaceFromTaskTitle() = runTest {
            // Given
            val titleWithWhitespace = "  Brush teeth  "
            val expectedTitle = "Brush teeth"
            val category = TaskCategory.PERSONAL_CARE
            val userId = UUID.randomUUID().toString()

            coEvery { taskRepository.saveTask(any()) } returns Unit

            // When
            val result = taskManagementUseCases.createTask(titleWithWhitespace, category, userId)

            // Then
            assertTrue(result.isSuccess, "Task creation should succeed")

            val createdTask = result.getOrThrow()
            assertEquals(expectedTitle, createdTask.title, "Task title should be trimmed")

            coVerify { taskRepository.saveTask(match { it.title == expectedTitle }) }
        }

        @Test
        @DisplayName("Should fail when title is blank")
        fun shouldFailWhenTitleIsBlank() = runTest {
            // Given
            val blankTitle = ""
            val category = TaskCategory.PERSONAL_CARE
            val userId = UUID.randomUUID().toString()

            // When
            val result = taskManagementUseCases.createTask(blankTitle, category, userId)

            // Then
            assertTrue(result.isFailure, "Task creation should fail for blank title")

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

            coVerify(exactly = 0) { taskRepository.saveTask(any()) }
        }

        @Test
        @DisplayName("Should fail when title is only whitespace")
        fun shouldFailWhenTitleIsOnlyWhitespace() = runTest {
            // Given
            val whitespaceTitle = "   "
            val category = TaskCategory.PERSONAL_CARE
            val userId = UUID.randomUUID().toString()

            // When
            val result = taskManagementUseCases.createTask(whitespaceTitle, category, userId)

            // Then
            assertTrue(result.isFailure, "Task creation should fail for whitespace-only title")
            assertTrue(
                result.exceptionOrNull() is InvalidTaskDataException,
                "Should throw InvalidTaskDataException",
            )

            coVerify(exactly = 0) { taskRepository.saveTask(any()) }
        }

        @Test
        @DisplayName("Should handle repository exception during save")
        fun shouldHandleRepositoryExceptionDuringSave() = runTest {
            // Given
            val title = "Valid task"
            val category = TaskCategory.PERSONAL_CARE
            val userId = UUID.randomUUID().toString()
            val repositoryException = TaskRepositoryException("Database error")

            coEvery { taskRepository.saveTask(any()) } throws repositoryException

            // When
            val result = taskManagementUseCases.createTask(title, category, userId)

            // Then
            assertTrue(
                result.isFailure,
                "Task creation should fail when repository throws exception",
            )
            assertEquals(
                repositoryException,
                result.exceptionOrNull(),
                "Should propagate repository exception",
            )
        }

        @Test
        @DisplayName("Should handle domain exception during create")
        fun shouldHandleDomainExceptionDuringCreate() = runTest {
            // Given
            val title = "Valid task"
            val category = TaskCategory.PERSONAL_CARE
            val userId = UUID.randomUUID().toString()
            val domainException = TaskDomainException("Domain error")

            coEvery { taskRepository.saveTask(any()) } throws domainException

            // When
            val result = taskManagementUseCases.createTask(title, category, userId)

            // Then
            assertTrue(result.isFailure, "Task creation should fail when domain exception occurs")
            assertEquals(
                domainException,
                result.exceptionOrNull(),
                "Should propagate domain exception",
            )
        }
    }

    @Nested
    @DisplayName("Update Task")
    inner class UpdateTask {

        @Test
        @DisplayName("Should update task successfully")
        fun shouldUpdateTaskSuccessfully() = runTest {
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
        fun shouldUpdateTaskCategoryAndTokenReward() = runTest {
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
        fun shouldTrimWhitespaceFromUpdatedTitle() = runTest {
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
        fun shouldFailWhenTaskNotFound() = runTest {
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
        fun shouldFailWhenUpdatedTitleIsBlank() = runTest {
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
        fun shouldHandleRepositoryExceptionDuringUpdate() = runTest {
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
        fun shouldUpdateToAllCategoriesCorrectly(newCategory: TaskCategory) = runTest {
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

    @Nested
    @DisplayName("Delete Task")
    inner class DeleteTask {

        @Test
        @DisplayName("Should delete task successfully")
        fun shouldDeleteTaskSuccessfully() = runTest {
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
        fun shouldFailWhenTaskToDeleteNotFound() = runTest {
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
        fun shouldHandleRepositoryExceptionDuringDeletion() = runTest {
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
        fun shouldDeleteCompletedTaskSuccessfully() = runTest {
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

    @Nested
    @DisplayName("Get Tasks for User")
    inner class GetTasksForUser {

        @Test
        @DisplayName("Should get all tasks for user")
        fun shouldGetAllTasksForUser() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val userTasks = TestDataFactory.createTaskList(assignedToUserId = userId)

            coEvery { taskRepository.findByUserId(userId) } returns userTasks

            // When
            val result = taskManagementUseCases.getTasksForUser(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed getting tasks for user")

            val tasks = result.getOrThrow()
            assertEquals(userTasks.size, tasks.size, "Should return all user tasks")
            assertEquals(userTasks, tasks, "Should return exact tasks from repository")

            coVerify { taskRepository.findByUserId(userId) }
        }

        @Test
        @DisplayName("Should return empty list when user has no tasks")
        fun shouldReturnEmptyListWhenUserHasNoTasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()

            coEvery { taskRepository.findByUserId(userId) } returns emptyList()

            // When
            val result = taskManagementUseCases.getTasksForUser(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed even when no tasks found")

            val tasks = result.getOrThrow()
            assertTrue(tasks.isEmpty(), "Should return empty list")

            coVerify { taskRepository.findByUserId(userId) }
        }

        @Test
        @DisplayName("Should handle repository exception when getting tasks")
        fun shouldHandleRepositoryExceptionWhenGettingTasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val repositoryException = TaskRepositoryException("Database error")

            coEvery { taskRepository.findByUserId(userId) } throws repositoryException

            // When
            val result = taskManagementUseCases.getTasksForUser(userId)

            // Then
            assertTrue(result.isFailure, "Should fail when repository throws exception")
            assertEquals(
                repositoryException,
                result.exceptionOrNull(),
                "Should propagate repository exception",
            )
        }
    }

    @Nested
    @DisplayName("Get Incomplete Tasks for User")
    inner class GetIncompleteTasksForUser {

        @Test
        @DisplayName("Should get incomplete tasks for user")
        fun shouldGetIncompleteTasksForUser() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val incompleteTasks = TestDataFactory.createTaskList(
                assignedToUserId = userId,
                includeCompleted = false,
            )

            coEvery { taskRepository.findIncompleteByUserId(userId) } returns incompleteTasks

            // When
            val result = taskManagementUseCases.getIncompleteTasksForUser(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed getting incomplete tasks")

            val tasks = result.getOrThrow()
            assertEquals(incompleteTasks.size, tasks.size, "Should return all incomplete tasks")
            assertTrue(tasks.all { !it.isCompleted }, "All returned tasks should be incomplete")

            coVerify { taskRepository.findIncompleteByUserId(userId) }
        }

        @Test
        @DisplayName("Should return empty list when all tasks are completed")
        fun shouldReturnEmptyListWhenAllTasksAreCompleted() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()

            coEvery { taskRepository.findIncompleteByUserId(userId) } returns emptyList()

            // When
            val result = taskManagementUseCases.getIncompleteTasksForUser(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed when all tasks are completed")

            val tasks = result.getOrThrow()
            assertTrue(tasks.isEmpty(), "Should return empty list")
        }
    }

    @Nested
    @DisplayName("Get Completed Tasks for User")
    inner class GetCompletedTasksForUser {

        @Test
        @DisplayName("Should get completed tasks for user")
        fun shouldGetCompletedTasksForUser() = runTest {
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
        fun shouldReturnEmptyListWhenNoTasksAreCompleted() = runTest {
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

    @Nested
    @DisplayName("Get Tasks by Category")
    inner class GetTasksByCategory {

        @ParameterizedTest
        @EnumSource(TaskCategory::class)
        @DisplayName("Should get tasks by category")
        fun shouldGetTasksByCategory(category: TaskCategory) = runTest {
            // Given
            val tasksInCategory = (1..3).map { TestDataFactory.createTask(category = category) }

            coEvery { taskRepository.findByCategory(category) } returns tasksInCategory

            // When
            val result = taskManagementUseCases.getTasksByCategory(category)

            // Then
            assertTrue(
                result.isSuccess,
                "Should succeed getting tasks for category ${category.displayName}",
            )

            val tasks = result.getOrThrow()
            assertEquals(tasksInCategory.size, tasks.size, "Should return all tasks in category")
            assertTrue(
                tasks.all { it.category == category },
                "All returned tasks should be in correct category",
            )

            coVerify { taskRepository.findByCategory(category) }
        }

        @Test
        @DisplayName("Should return empty list when no tasks in category")
        fun shouldReturnEmptyListWhenNoTasksInCategory() = runTest {
            // Given
            val category = TaskCategory.HOMEWORK

            coEvery { taskRepository.findByCategory(category) } returns emptyList()

            // When
            val result = taskManagementUseCases.getTasksByCategory(category)

            // Then
            assertTrue(result.isSuccess, "Should succeed when no tasks in category")

            val tasks = result.getOrThrow()
            assertTrue(tasks.isEmpty(), "Should return empty list")
        }
    }

    @Nested
    @DisplayName("Get Task Statistics")
    inner class GetTaskStatistics {

        @Test
        @DisplayName("Should calculate task statistics correctly")
        fun shouldCalculateTaskStatisticsCorrectly() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val totalCompleted = 10
            val totalTokensEarned = 75
            val incompleteTasks = (1..5).map {
                TestDataFactory.createTask(
                    assignedToUserId = userId,
                )
            }
            val expectedCompletionRate = (10 * 100 / (10 + 5)) // 66%

            coEvery { taskRepository.countCompletedTasks(userId) } returns totalCompleted
            coEvery { taskRepository.countTokensEarned(userId) } returns totalTokensEarned
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns incompleteTasks
            coEvery { taskRepository.findCompletedByUserId(userId) } returns (1..totalCompleted).map {
                TestDataFactory.createCompletedTask(assignedToUserId = userId)
            }

            // When
            val result = taskManagementUseCases.getTaskStats(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed getting task statistics")

            val stats = result.getOrThrow()
            assertEquals(
                totalCompleted,
                stats.totalCompletedTasks,
                "Should have correct completed tasks count",
            )
            assertEquals(
                totalTokensEarned,
                stats.totalTokensEarned,
                "Should have correct tokens earned",
            )
            assertEquals(
                incompleteTasks.size,
                stats.incompleteTasks,
                "Should have correct incomplete tasks count",
            )
            assertEquals(
                expectedCompletionRate,
                stats.completionRate,
                "Should calculate completion rate correctly",
            )

            coVerify { taskRepository.countCompletedTasks(userId) }
            coVerify { taskRepository.countTokensEarned(userId) }
            coVerify { taskRepository.findIncompleteByUserId(userId) }
        }

        @Test
        @DisplayName("Should handle 100% completion rate when no incomplete tasks")
        fun shouldHandle100PercentCompletionRateWhenNoIncompleteTasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val totalCompleted = 10
            val totalTokensEarned = 75

            coEvery { taskRepository.countCompletedTasks(userId) } returns totalCompleted
            coEvery { taskRepository.countTokensEarned(userId) } returns totalTokensEarned
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns emptyList()
            coEvery { taskRepository.findCompletedByUserId(userId) } returns (1..totalCompleted).map {
                TestDataFactory.createCompletedTask(assignedToUserId = userId)
            }

            // When
            val result = taskManagementUseCases.getTaskStats(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed getting task statistics")

            val stats = result.getOrThrow()
            assertEquals(
                100,
                stats.completionRate,
                "Should have 100% completion rate when no incomplete tasks",
            )
            assertEquals(0, stats.incompleteTasks, "Should have 0 incomplete tasks")
        }

        @Test
        @DisplayName("Should handle zero completed tasks")
        fun shouldHandleZeroCompletedTasks() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val incompleteTasks = (1..3).map {
                TestDataFactory.createTask(
                    assignedToUserId = userId,
                )
            }

            coEvery { taskRepository.countCompletedTasks(userId) } returns 0
            coEvery { taskRepository.countTokensEarned(userId) } returns 0
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns incompleteTasks
            coEvery { taskRepository.findCompletedByUserId(userId) } returns emptyList()

            // When
            val result = taskManagementUseCases.getTaskStats(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed getting task statistics")

            val stats = result.getOrThrow()
            assertEquals(0, stats.totalCompletedTasks, "Should have 0 completed tasks")
            assertEquals(0, stats.totalTokensEarned, "Should have 0 tokens earned")
            assertEquals(3, stats.incompleteTasks, "Should have 3 incomplete tasks")
            assertEquals(0, stats.completionRate, "Should have 0% completion rate")
        }

        @Test
        @DisplayName("Should handle repository exception during stats calculation")
        fun shouldHandleRepositoryExceptionDuringStatsCalculation() = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val repositoryException = TaskRepositoryException("Stats calculation failed")

            coEvery { taskRepository.countCompletedTasks(userId) } throws repositoryException

            // When
            val result = taskManagementUseCases.getTaskStats(userId)

            // Then
            assertTrue(result.isFailure, "Should fail when repository throws exception")
            assertEquals(
                repositoryException,
                result.exceptionOrNull(),
                "Should propagate repository exception",
            )
        }

        @ParameterizedTest
        @ValueSource(ints = [1, 5, 10, 25, 50, 100])
        @DisplayName("Should calculate completion rates correctly for various scenarios")
        fun shouldCalculateCompletionRatesCorrectly(completedCount: Int) = runTest {
            // Given
            val userId = UUID.randomUUID().toString()
            val incompleteCount = 10
            val incompleteTasks = (1..incompleteCount).map {
                TestDataFactory.createTask(
                    assignedToUserId = userId,
                )
            }
            val expectedRate = (completedCount * 100 / (completedCount + incompleteCount))

            coEvery { taskRepository.countCompletedTasks(userId) } returns completedCount
            coEvery { taskRepository.countTokensEarned(userId) } returns completedCount * 10
            coEvery { taskRepository.findIncompleteByUserId(userId) } returns incompleteTasks
            coEvery { taskRepository.findCompletedByUserId(userId) } returns (1..completedCount).map {
                TestDataFactory.createCompletedTask(assignedToUserId = userId)
            }

            // When
            val result = taskManagementUseCases.getTaskStats(userId)

            // Then
            assertTrue(result.isSuccess, "Should succeed for $completedCount completed tasks")

            val stats = result.getOrThrow()
            assertEquals(
                expectedRate,
                stats.completionRate,
                "Should calculate correct completion rate for $completedCount completed tasks",
            )
        }
    }

    @Nested
    @DisplayName("Integration and End-to-End Scenarios")
    inner class IntegrationAndEndToEndScenarios {

        @Test
        @DisplayName("Should handle complete task lifecycle")
        fun shouldHandleCompleteTaskLifecycle() = runTest {
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
        fun shouldHandleBulkOperationsCorrectly() = runTest {
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
}
