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
 * Focused test suite for getting incomplete tasks for user use case.
 *
 * Tests cover:
 * - Incomplete task retrieval for specific user
 * - Empty list handling when all tasks completed
 * - Task completion status validation
 */
@DisplayName("Get Incomplete Tasks for User Use Case Tests")
@Execution(ExecutionMode.SAME_THREAD)
class GetIncompleteTasksForUserUseCaseTest {

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
    @DisplayName("Should get incomplete tasks for user")
    fun shouldGetIncompleteTasksForUser() = mainDispatcherRule.runTest {
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
    fun shouldReturnEmptyListWhenAllTasksAreCompleted() = mainDispatcherRule.runTest {
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
