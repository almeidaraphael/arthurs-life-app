package com.arthurslife.app.infrastructure.task

import com.arthurslife.app.domain.TestDataFactory
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Comprehensive tests for TaskRepositoryImpl.
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

    @Test
    fun shouldFindTaskByIdWhenExists() = runTest {
        runRepositoryTest {
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
    }

    @Test
    fun shouldReturnNullWhenTaskDoesNotExist() = runTest {
        runRepositoryTest {
            val taskId = "non-existent"
            coEvery { mockDataSource.findById(taskId) } returns null
            val result = taskRepository.findById(taskId)
            assertNull(result)
            coVerify { mockDataSource.findById(taskId) }
        }
    }

    @Test
    fun shouldFindTasksByUserId() = runTest {
        runRepositoryTest {
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
    }

    @Test
    fun shouldReturnEmptyListWhenUserHasNoTasks() = runTest {
        runRepositoryTest {
            val userId = "user-without-tasks"
            coEvery { mockDataSource.findByUserId(userId) } returns emptyList()
            val result = taskRepository.findByUserId(userId)
            assertTrue(result.isEmpty())
            coVerify { mockDataSource.findByUserId(userId) }
        }
    }

    @Test
    fun shouldGetAllTasks() = runTest {
        runRepositoryTest {
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
}
