package com.lemonqwest.app.domain.task.usecase

import com.lemonqwest.app.domain.common.TaskDomainException
import com.lemonqwest.app.domain.shouldBeAssignedTo
import com.lemonqwest.app.domain.shouldHaveDefaultReward
import com.lemonqwest.app.domain.shouldHaveProperties
import com.lemonqwest.app.domain.shouldNotBeCompleted
import com.lemonqwest.app.domain.task.InvalidTaskDataException
import com.lemonqwest.app.domain.task.TaskCategory
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.util.UUID

/**
 * Focused test suite for task creation use case.
 *
 * Tests cover:
 * - Task creation with validation
 * - Input sanitization and validation
 * - Error handling for invalid inputs
 * - Repository interaction validation
 */
@DisplayName("Create Task Use Case Tests")
@Execution(ExecutionMode.SAME_THREAD)
class CreateTaskUseCaseTest {

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
    @DisplayName("Should create task successfully")
    fun shouldCreateTaskSuccessfully() = mainDispatcherRule.runTest {
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
    fun shouldCreateTasksForAllCategories(category: TaskCategory) = mainDispatcherRule.runTest {
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
    fun shouldTrimWhitespaceFromTaskTitle() = mainDispatcherRule.runTest {
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
    fun shouldFailWhenTitleIsBlank() = mainDispatcherRule.runTest {
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
    fun shouldFailWhenTitleIsOnlyWhitespace() = mainDispatcherRule.runTest {
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
    fun shouldHandleRepositoryExceptionDuringSave() = mainDispatcherRule.runTest {
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
    fun shouldHandleDomainExceptionDuringCreate() = mainDispatcherRule.runTest {
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
