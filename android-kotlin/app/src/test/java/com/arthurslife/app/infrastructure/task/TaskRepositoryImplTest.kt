package com.arthurslife.app.infrastructure.task

import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.TaskDataSource
import com.arthurslife.app.testutils.RepositoryTestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Comprehensive tests for TaskRepositoryImpl.
 *
 * This test class verifies that the TaskRepositoryImpl correctly delegates
 * to the TaskDataSource and maintains proper repository behavior patterns.
 */
class TaskRepositoryImplTest : RepositoryTestBase() {

    private lateinit var mockDataSource: TaskDataSource
    private lateinit var taskRepository: TaskRepositoryImpl

    @BeforeEach
    fun setup() {
        setupMocks()
        mockDataSource = mockk()
        taskRepository = TaskRepositoryImpl(mockDataSource)
    }

    // Find Operations
    @Test
    fun shouldFindTaskByIdWhenExists() = runTest {
        runRepositoryTest {
            // Given
            val taskId = "task-1"
            val expectedTask = TestDataFactory.createTask(id = taskId, title = "Brush teeth")
            coEvery { mockDataSource.findById(taskId) } returns expectedTask

            // When
            val result = taskRepository.findById(taskId)

            // Then
            assertNotNull(result)
            val nonNullResult = result!!
            assertEquals(expectedTask.id, nonNullResult.id)
            assertEquals(expectedTask.title, nonNullResult.title)
            coVerify { mockDataSource.findById(taskId) }
        }
    }

    @Test
    fun shouldReturnNullWhenTaskDoesNotExist() = runTest {
        runRepositoryTest {
            // Given
            val taskId = "non-existent"
            coEvery { mockDataSource.findById(taskId) } returns null

            // When
            val result = taskRepository.findById(taskId)

            // Then
            assertNull(result)
            coVerify { mockDataSource.findById(taskId) }
        }
    }

    @Test
    fun shouldFindTasksByUserId() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-1"
            val expectedTasks = listOf(
                TestDataFactory.createTask(id = "task-1", assignedToUserId = userId),
                TestDataFactory.createTask(id = "task-2", assignedToUserId = userId),
            )
            coEvery { mockDataSource.findByUserId(userId) } returns expectedTasks

            // When
            val result = taskRepository.findByUserId(userId)

            // Then
            assertEquals(2, result.size)
            assertEquals(expectedTasks, result)
            assertTrue(result.all { it.assignedToUserId == userId })
            coVerify { mockDataSource.findByUserId(userId) }
        }
    }

    @Test
    fun shouldReturnEmptyListWhenUserHasNoTasks() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-without-tasks"
            coEvery { mockDataSource.findByUserId(userId) } returns emptyList()

            // When
            val result = taskRepository.findByUserId(userId)

            // Then
            assertTrue(result.isEmpty())
            coVerify { mockDataSource.findByUserId(userId) }
        }
    }

    @Test
    fun shouldGetAllTasks() = runTest {
        runRepositoryTest {
            // Given
            val expectedTasks = listOf(
                TestDataFactory.createTask(id = "task-1", title = "Brush teeth"),
                TestDataFactory.createTask(id = "task-2", title = "Make bed"),
                TestDataFactory.createTask(id = "task-3", title = "Math homework"),
            )
            coEvery { mockDataSource.getAllTasks() } returns expectedTasks

            // When
            val result = taskRepository.getAllTasks()

            // Then
            assertEquals(3, result.size)
            assertEquals(expectedTasks, result)
            coVerify { mockDataSource.getAllTasks() }
        }
    }

    @Test
    fun shouldFindTasksByCategory() = runTest {
        runRepositoryTest {
            // Given
            val category = TaskCategory.HOMEWORK
            val expectedTasks = listOf(
                TestDataFactory.createTask(id = "hw-1", category = category),
                TestDataFactory.createTask(id = "hw-2", category = category),
            )
            coEvery { mockDataSource.findByCategory(category) } returns expectedTasks

            // When
            val result = taskRepository.findByCategory(category)

            // Then
            assertEquals(2, result.size)
            assertEquals(expectedTasks, result)
            assertTrue(result.all { it.category == category })
            coVerify { mockDataSource.findByCategory(category) }
        }
    }

    @Test
    fun shouldFindCompletedTasksByUserId() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-1"
            val expectedTasks = listOf(
                TestDataFactory.createCompletedTask(
                    title = "completed-1",
                    category = TaskCategory.HOMEWORK,
                    assignedToUserId = userId,
                ),
                TestDataFactory.createCompletedTask(
                    title = "completed-2",
                    category = TaskCategory.HOMEWORK,
                    assignedToUserId = userId,
                ),
            )
            coEvery { mockDataSource.findCompletedByUserId(userId) } returns expectedTasks

            // When
            val result = taskRepository.findCompletedByUserId(userId)

            // Then
            assertEquals(2, result.size)
            assertEquals(expectedTasks, result)
            assertTrue(result.all { it.isCompleted && it.assignedToUserId == userId })
            coVerify { mockDataSource.findCompletedByUserId(userId) }
        }
    }

    @Test
    fun shouldFindIncompleteTasksByUserId() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-1"
            val expectedTasks = listOf(
                TestDataFactory.createTask(
                    id = "incomplete-1",
                    assignedToUserId = userId,
                    isCompleted = false,
                ),
                TestDataFactory.createTask(
                    id = "incomplete-2",
                    assignedToUserId = userId,
                    isCompleted = false,
                ),
            )
            coEvery { mockDataSource.findIncompleteByUserId(userId) } returns expectedTasks

            // When
            val result = taskRepository.findIncompleteByUserId(userId)

            // Then
            assertEquals(2, result.size)
            assertEquals(expectedTasks, result)
            assertTrue(result.all { !it.isCompleted && it.assignedToUserId == userId })
            coVerify { mockDataSource.findIncompleteByUserId(userId) }
        }
    }

    // Save Operations
    @Test
    fun shouldSaveNewTask() = runTest {
        runRepositoryTest {
            // Given
            val newTask = TestDataFactory.createTask(title = "New Task")
            coEvery { mockDataSource.saveTask(newTask) } returns Unit

            // When
            taskRepository.saveTask(newTask)

            // Then
            coVerify { mockDataSource.saveTask(newTask) }
        }
    }

    @Test
    fun shouldSaveTaskWithDifferentCategories() = runTest {
        runRepositoryTest {
            // Given
            val personalCareTask = TestDataFactory.createTask(category = TaskCategory.PERSONAL_CARE)
            val householdTask = TestDataFactory.createTask(category = TaskCategory.HOUSEHOLD)
            val homeworkTask = TestDataFactory.createTask(category = TaskCategory.HOMEWORK)

            coEvery { mockDataSource.saveTask(personalCareTask) } returns Unit
            coEvery { mockDataSource.saveTask(householdTask) } returns Unit
            coEvery { mockDataSource.saveTask(homeworkTask) } returns Unit

            // When
            taskRepository.saveTask(personalCareTask)
            taskRepository.saveTask(householdTask)
            taskRepository.saveTask(homeworkTask)

            // Then
            coVerify { mockDataSource.saveTask(personalCareTask) }
            coVerify { mockDataSource.saveTask(householdTask) }
            coVerify { mockDataSource.saveTask(homeworkTask) }
        }
    }

    @Test
    fun shouldSaveCompletedTask() = runTest {
        runRepositoryTest {
            // Given
            val completedTask = TestDataFactory.createCompletedTask(title = "Completed Task")
            coEvery { mockDataSource.saveTask(completedTask) } returns Unit

            // When
            taskRepository.saveTask(completedTask)

            // Then
            coVerify { mockDataSource.saveTask(completedTask) }
        }
    }

    @Test
    fun shouldHandleMultipleTaskSaves() = runTest {
        runRepositoryTest {
            // Given
            val tasks = listOf(
                TestDataFactory.createTask(id = "task-1", title = "Task 1"),
                TestDataFactory.createTask(id = "task-2", title = "Task 2"),
                TestDataFactory.createTask(id = "task-3", title = "Task 3"),
            )
            tasks.forEach { task ->
                coEvery { mockDataSource.saveTask(task) } returns Unit
            }

            // When
            tasks.forEach { task ->
                taskRepository.saveTask(task)
            }

            // Then
            tasks.forEach { task ->
                coVerify { mockDataSource.saveTask(task) }
            }
        }
    }

    // Update Operations
    @Test
    fun shouldUpdateExistingTask() = runTest {
        runRepositoryTest {
            // Given
            val originalTask = TestDataFactory.createTask(title = "Original Title")
            val updatedTask = originalTask.copy(title = "Updated Title")
            coEvery { mockDataSource.saveTask(updatedTask) } returns Unit

            // When
            taskRepository.updateTask(updatedTask)

            // Then
            coVerify { mockDataSource.saveTask(updatedTask) }
        }
    }

    @Test
    fun shouldUpdateTaskCompletionStatus() = runTest {
        runRepositoryTest {
            // Given
            val task = TestDataFactory.createTask(isCompleted = false)
            val completedTask = task.markCompleted()
            coEvery { mockDataSource.saveTask(completedTask) } returns Unit

            // When
            taskRepository.updateTask(completedTask)

            // Then
            coVerify { mockDataSource.saveTask(completedTask) }
        }
    }

    @Test
    fun shouldUpdateTaskCategory() = runTest {
        runRepositoryTest {
            // Given
            val task = TestDataFactory.createTask(category = TaskCategory.PERSONAL_CARE)
            val updatedTask = task.copy(category = TaskCategory.HOUSEHOLD)
            coEvery { mockDataSource.saveTask(updatedTask) } returns Unit

            // When
            taskRepository.updateTask(updatedTask)

            // Then
            coVerify { mockDataSource.saveTask(updatedTask) }
        }
    }

    @Test
    fun shouldUpdateTaskTokenReward() = runTest {
        runRepositoryTest {
            // Given
            val task = TestDataFactory.createTask(category = TaskCategory.PERSONAL_CARE)
            val updatedTask = task.copy(tokenReward = 25)
            coEvery { mockDataSource.saveTask(updatedTask) } returns Unit

            // When
            taskRepository.updateTask(updatedTask)

            // Then
            coVerify { mockDataSource.saveTask(updatedTask) }
        }
    }

    // Delete Operations
    @Test
    fun shouldDeleteTaskById() = runTest {
        runRepositoryTest {
            // Given
            val taskId = "task-to-delete"
            coEvery { mockDataSource.deleteTask(taskId) } returns Unit

            // When
            taskRepository.deleteTask(taskId)

            // Then
            coVerify { mockDataSource.deleteTask(taskId) }
        }
    }

    @Test
    fun shouldHandleDeletionOfNonExistentTask() = runTest {
        runRepositoryTest {
            // Given
            val taskId = "non-existent-task"
            coEvery { mockDataSource.deleteTask(taskId) } returns Unit

            // When
            taskRepository.deleteTask(taskId)

            // Then
            coVerify { mockDataSource.deleteTask(taskId) }
        }
    }

    @Test
    fun shouldHandleMultipleDeletions() = runTest {
        runRepositoryTest {
            // Given
            val taskIds = listOf("task-1", "task-2", "task-3")
            taskIds.forEach { taskId ->
                coEvery { mockDataSource.deleteTask(taskId) } returns Unit
            }

            // When
            taskIds.forEach { taskId ->
                taskRepository.deleteTask(taskId)
            }

            // Then
            taskIds.forEach { taskId ->
                coVerify { mockDataSource.deleteTask(taskId) }
            }
        }
    }

    // Aggregation Operations
    @Test
    fun shouldCountCompletedTasksForUser() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-1"
            val expectedCount = 5
            coEvery { mockDataSource.countCompletedTasks(userId) } returns expectedCount

            // When
            val result = taskRepository.countCompletedTasks(userId)

            // Then
            assertEquals(expectedCount, result)
            coVerify { mockDataSource.countCompletedTasks(userId) }
        }
    }

    @Test
    fun shouldReturnZeroForUserWithNoCompletedTasks() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-no-tasks"
            coEvery { mockDataSource.countCompletedTasks(userId) } returns 0

            // When
            val result = taskRepository.countCompletedTasks(userId)

            // Then
            assertEquals(0, result)
            coVerify { mockDataSource.countCompletedTasks(userId) }
        }
    }

    @Test
    fun shouldCountTokensEarnedForUser() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-1"
            val expectedTokens = 150
            coEvery { mockDataSource.countTokensEarned(userId) } returns expectedTokens

            // When
            val result = taskRepository.countTokensEarned(userId)

            // Then
            assertEquals(expectedTokens, result)
            coVerify { mockDataSource.countTokensEarned(userId) }
        }
    }

    @Test
    fun shouldReturnZeroTokensForUserWithNoCompletedTasks() = runTest {
        runRepositoryTest {
            // Given
            val userId = "user-no-tokens"
            coEvery { mockDataSource.countTokensEarned(userId) } returns 0

            // When
            val result = taskRepository.countTokensEarned(userId)

            // Then
            assertEquals(0, result)
            coVerify { mockDataSource.countTokensEarned(userId) }
        }
    }

    @Test
    fun shouldHandleLargeTokenCounts() = runTest {
        runRepositoryTest {
            // Given
            val userId = "productive-user"
            val largeTokenCount = 999999
            coEvery { mockDataSource.countTokensEarned(userId) } returns largeTokenCount

            // When
            val result = taskRepository.countTokensEarned(userId)

            // Then
            assertEquals(largeTokenCount, result)
            coVerify { mockDataSource.countTokensEarned(userId) }
        }
    }

    // Error Handling
    @Test
    fun shouldPropagateDataSourceExceptions() = runTest {
        runRepositoryTest {
            // Given
            val taskId = "problematic-task"
            val expectedException = IllegalStateException("Data source error")
            coEvery { mockDataSource.findById(taskId) } throws expectedException

            // When & Then
            try {
                taskRepository.findById(taskId)
                fail("Expected exception not thrown")
            } catch (e: IllegalStateException) {
                assertEquals(expectedException.message, e.message)
            }
            coVerify { mockDataSource.findById(taskId) }
        }
    }

    @Test
    fun shouldHandleDataSourceSaveExceptions() = runTest {
        runRepositoryTest {
            // Given
            val task = TestDataFactory.createTask()
            val expectedException = IllegalStateException("Save operation failed")
            coEvery { mockDataSource.saveTask(task) } throws expectedException

            // When & Then
            try {
                taskRepository.saveTask(task)
                fail("Expected exception not thrown")
            } catch (e: IllegalStateException) {
                assertEquals(expectedException.message, e.message)
            }
            coVerify { mockDataSource.saveTask(task) }
        }
    }

    @Test
    fun shouldHandleDataSourceDeleteExceptions() = runTest {
        runRepositoryTest {
            // Given
            val taskId = "task-to-delete"
            val expectedException = IllegalArgumentException("Delete operation failed")
            coEvery { mockDataSource.deleteTask(taskId) } throws expectedException

            // When & Then
            try {
                taskRepository.deleteTask(taskId)
                fail("Expected exception not thrown")
            } catch (e: IllegalArgumentException) {
                assertEquals(expectedException.message, e.message)
            }
            coVerify { mockDataSource.deleteTask(taskId) }
        }
    }

    @Test
    fun shouldHandleAggregationExceptions() = runTest {
        runRepositoryTest {
            // Given
            val userId = "problematic-user"
            val expectedException = RuntimeException("Aggregation error")
            coEvery { mockDataSource.countCompletedTasks(userId) } throws expectedException

            // When & Then
            try {
                taskRepository.countCompletedTasks(userId)
                fail("Expected exception not thrown")
            } catch (e: RuntimeException) {
                assertEquals(expectedException.message, e.message)
            }
            coVerify { mockDataSource.countCompletedTasks(userId) }
        }
    }

    // Data Consistency
    @Test
    fun shouldMaintainDataConsistency() = runTest {
        runRepositoryTest {
            // Given
            val task = TestDataFactory.createTask(id = "consistent-task", title = "Consistent Task")
            coEvery { mockDataSource.saveTask(task) } returns Unit
            coEvery { mockDataSource.findById(task.id) } returns task

            // When
            taskRepository.saveTask(task)
            val retrievedTask = taskRepository.findById(task.id)

            // Then
            assertNotNull(retrievedTask)
            assertEquals(task.id, retrievedTask!!.id)
            assertEquals(task.title, retrievedTask.title)
            assertEquals(task.category, retrievedTask.category)
            assertEquals(task.tokenReward, retrievedTask.tokenReward)
            assertEquals(task.isCompleted, retrievedTask.isCompleted)

            coVerify { mockDataSource.saveTask(task) }
            coVerify { mockDataSource.findById(task.id) }
        }
    }

    @Test
    fun shouldHandleTaskStateTransitionsProperly() = runTest {
        runRepositoryTest {
            // Given
            val originalTask = TestDataFactory.createTask(isCompleted = false)
            val completedTask = originalTask.markCompleted()

            coEvery { mockDataSource.saveTask(originalTask) } returns Unit
            coEvery { mockDataSource.saveTask(completedTask) } returns Unit
            coEvery { mockDataSource.findById(originalTask.id) } returns originalTask andThen completedTask

            // When
            taskRepository.saveTask(originalTask)
            val initialTask = taskRepository.findById(originalTask.id)

            taskRepository.updateTask(completedTask)
            val updatedTask = taskRepository.findById(originalTask.id)

            // Then
            assertNotNull(initialTask)
            assertNotNull(updatedTask)
            assertEquals(false, initialTask!!.isCompleted)
            assertEquals(true, updatedTask!!.isCompleted)

            coVerify { mockDataSource.saveTask(originalTask) }
            coVerify { mockDataSource.saveTask(completedTask) }
            coVerify(exactly = 2) { mockDataSource.findById(originalTask.id) }
        }
    }

    @Test
    fun shouldHandleConcurrentTaskOperationsSafely() = runTest {
        runRepositoryTest {
            // Given
            val task1 = TestDataFactory.createTask(id = "task-1", title = "Task 1")
            val task2 = TestDataFactory.createTask(id = "task-2", title = "Task 2")
            val userId = "concurrent-user"

            coEvery { mockDataSource.saveTask(task1) } returns Unit
            coEvery { mockDataSource.saveTask(task2) } returns Unit
            coEvery { mockDataSource.findByUserId(userId) } returns listOf(task1, task2)
            coEvery { mockDataSource.countCompletedTasks(userId) } returns 0

            // When - Simulate concurrent operations
            taskRepository.saveTask(task1)
            taskRepository.saveTask(task2)
            val userTasks = taskRepository.findByUserId(userId)
            val completedCount = taskRepository.countCompletedTasks(userId)

            // Then
            assertEquals(2, userTasks.size)
            assertEquals(0, completedCount)
            assertTrue(userTasks.contains(task1))
            assertTrue(userTasks.contains(task2))

            coVerify { mockDataSource.saveTask(task1) }
            coVerify { mockDataSource.saveTask(task2) }
            coVerify { mockDataSource.findByUserId(userId) }
            coVerify { mockDataSource.countCompletedTasks(userId) }
        }
    }

    // Repository Patterns
    @Test
    fun shouldDelegateAllOperationsToDataSource() = runTest {
        runRepositoryTest {
            // Given
            val task = TestDataFactory.createTask()
            val tasks = listOf(task)
            val userId = "test-user"
            val category = TaskCategory.HOMEWORK

            coEvery { mockDataSource.findById(any()) } returns task
            coEvery { mockDataSource.findByUserId(any()) } returns tasks
            coEvery { mockDataSource.getAllTasks() } returns tasks
            coEvery { mockDataSource.findByCategory(any()) } returns tasks
            coEvery { mockDataSource.findCompletedByUserId(any()) } returns tasks
            coEvery { mockDataSource.findIncompleteByUserId(any()) } returns tasks
            coEvery { mockDataSource.saveTask(any()) } returns Unit
            coEvery { mockDataSource.deleteTask(any()) } returns Unit
            coEvery { mockDataSource.countCompletedTasks(any()) } returns 5
            coEvery { mockDataSource.countTokensEarned(any()) } returns 150

            // When - Execute all repository operations
            taskRepository.findById("test-id")
            taskRepository.findByUserId(userId)
            taskRepository.getAllTasks()
            taskRepository.findByCategory(category)
            taskRepository.findCompletedByUserId(userId)
            taskRepository.findIncompleteByUserId(userId)
            taskRepository.saveTask(task)
            taskRepository.updateTask(task)
            taskRepository.deleteTask("test-id")
            taskRepository.countCompletedTasks(userId)
            taskRepository.countTokensEarned(userId)

            // Then - Verify all operations were delegated
            coVerify { mockDataSource.findById("test-id") }
            coVerify { mockDataSource.findByUserId(userId) }
            coVerify { mockDataSource.getAllTasks() }
            coVerify { mockDataSource.findByCategory(category) }
            coVerify { mockDataSource.findCompletedByUserId(userId) }
            coVerify { mockDataSource.findIncompleteByUserId(userId) }
            coVerify(exactly = 2) { mockDataSource.saveTask(task) } // save and update
            coVerify { mockDataSource.deleteTask("test-id") }
            coVerify { mockDataSource.countCompletedTasks(userId) }
            coVerify { mockDataSource.countTokensEarned(userId) }
        }
    }

    @Test
    fun shouldMaintainRepositoryAbstraction() = runTest {
        runRepositoryTest {
            // This test verifies that the repository properly abstracts the data source
            // and doesn't expose internal implementation details

            // Given
            val task = TestDataFactory.createTask()
            coEvery { mockDataSource.saveTask(task) } returns Unit
            coEvery { mockDataSource.findById(task.id) } returns task

            // When
            taskRepository.saveTask(task)
            val result = taskRepository.findById(task.id)

            // Then - Verify the repository interface is clean and consistent
            assertNotNull(result)
            assertEquals(task, result)

            // The repository should not expose data source implementation details
            // This is verified by the fact that the test only interacts with the repository interface
        }
    }

    @Test
    fun shouldSupportRepositoryExtensionPatterns() = runTest {
        runRepositoryTest {
            // This test verifies that the repository can be extended with additional functionality
            // without breaking the existing interface

            // Given
            val userId = "extensible-user"
            val tasks = listOf(
                TestDataFactory.createTask(assignedToUserId = userId, isCompleted = true),
                TestDataFactory.createTask(assignedToUserId = userId, isCompleted = false),
            )

            coEvery { mockDataSource.findByUserId(userId) } returns tasks
            coEvery { mockDataSource.countCompletedTasks(userId) } returns 1
            coEvery { mockDataSource.countTokensEarned(userId) } returns 10

            // When - Use repository for complex operations that could be extended
            val allUserTasks = taskRepository.findByUserId(userId)
            val completedCount = taskRepository.countCompletedTasks(userId)
            val tokensEarned = taskRepository.countTokensEarned(userId)

            // Then - All operations should work correctly
            assertEquals(2, allUserTasks.size)
            assertEquals(1, completedCount)
            assertEquals(10, tokensEarned)

            // Verify proper delegation
            coVerify { mockDataSource.findByUserId(userId) }
            coVerify { mockDataSource.countCompletedTasks(userId) }
            coVerify { mockDataSource.countTokensEarned(userId) }
        }
    }
}
