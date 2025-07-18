package com.lemonqwest.app.infrastructure.task

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.task.TaskDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

/**
 * Comprehensive tests for TaskRepositoryImpl.
 * This test class verifies that the TaskRepositoryImpl correctly delegates
 * to the TaskDataSource and maintains proper repository behavior patterns.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Execution(ExecutionMode.SAME_THREAD)
class TaskRepositoryImplTest {
    @get:org.junit.Rule
    val mainDispatcherRule = com.lemonqwest.app.testutils.MainDispatcherRule(
        UnconfinedTestDispatcher(),
    )
    private lateinit var mockDataSource: TaskDataSource
    private lateinit var taskRepository: TaskRepositoryImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockDataSource = mockk()
        taskRepository = TaskRepositoryImpl(mockDataSource)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun shouldFindTaskByIdWhenExists() = runTest {
        val taskId = "task-1"
        val expectedTask = TestDataFactory.createTask(id = taskId, title = "Brush teeth")
        coEvery { mockDataSource.findById(taskId) } returns expectedTask
        val result = taskRepository.findById(taskId)
        assertNotNull(result)
        val nonNullResult = result!!
        assertEquals(expectedTask.id, nonNullResult.id)
        assertEquals(expectedTask.title, nonNullResult.title)
        coVerify { mockDataSource.findById(taskId) }
    }

    @Test
    fun shouldReturnNullWhenTaskDoesNotExist() = runTest {
        val taskId = "non-existent"
        coEvery { mockDataSource.findById(taskId) } returns null
        val result = taskRepository.findById(taskId)
        assertNull(result)
        coVerify { mockDataSource.findById(taskId) }
    }

    @Test
    fun shouldFindTasksByUserId() = runTest {
        val userId = "user-1"
        val expectedTasks = listOf(
            TestDataFactory.createTask(id = "task-1", assignedToUserId = userId),
            TestDataFactory.createTask(id = "task-2", assignedToUserId = userId),
        )
        coEvery { mockDataSource.findByUserId(userId) } returns expectedTasks
        val result = taskRepository.findByUserId(userId)
        assertEquals(2, result.size)
        assertEquals(expectedTasks, result)
        assertTrue(result.all { it.assignedToUserId == userId })
        coVerify { mockDataSource.findByUserId(userId) }
    }

    @Test
    fun shouldReturnEmptyListWhenUserHasNoTasks() = runTest {
        val userId = "user-without-tasks"
        coEvery { mockDataSource.findByUserId(userId) } returns emptyList()
        val result = taskRepository.findByUserId(userId)
        assertTrue(result.isEmpty())
        coVerify { mockDataSource.findByUserId(userId) }
    }

    @Test
    fun shouldGetAllTasks() = runTest {
        val expectedTasks = listOf(
            TestDataFactory.createTask(id = "task-1", title = "Brush teeth"),
            TestDataFactory.createTask(id = "task-2", title = "Make bed"),
            TestDataFactory.createTask(id = "task-3", title = "Math homework"),
        )
        coEvery { mockDataSource.getAllTasks() } returns expectedTasks
        val result = taskRepository.getAllTasks()
        assertEquals(3, result.size)
        assertEquals(expectedTasks, result)
    }
}
