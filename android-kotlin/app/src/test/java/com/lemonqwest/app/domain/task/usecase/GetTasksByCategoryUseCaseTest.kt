package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.task.TaskCategory
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 * Focused test suite for getting tasks by category use case.
 *
 * Tests cover:
 * - Task retrieval by specific category
 * - All category types validation
 * - Empty category handling
 */
@DisplayName("Get Tasks by Category Use Case Tests")
@Execution(ExecutionMode.SAME_THREAD)
class GetTasksByCategoryUseCaseTest {

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

    @ParameterizedTest
    @EnumSource(TaskCategory::class)
    @DisplayName("Should get tasks by category")
    fun shouldGetTasksByCategory(category: TaskCategory) = mainDispatcherRule.runTest {
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
    fun shouldReturnEmptyListWhenNoTasksInCategory() = mainDispatcherRule.runTest {
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
