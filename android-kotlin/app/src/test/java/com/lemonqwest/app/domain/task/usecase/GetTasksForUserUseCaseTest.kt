package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
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
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.UUID
@DisplayName("Get Tasks for User Use Case Tests")
@Execution(ExecutionMode.SAME_THREAD)
class GetTasksForUserUseCaseTest {

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
    @DisplayName("Should get all tasks for user")
    fun shouldGetAllTasksForUser() = mainDispatcherRule.runTest {
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
    fun shouldReturnEmptyListWhenUserHasNoTasks() = mainDispatcherRule.runTest {
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
    fun shouldHandleRepositoryExceptionWhenGettingTasks() = mainDispatcherRule.runTest {
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
