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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.UUID

/**
 * Focused test suite for task statistics calculation use case.
 *
 * Tests cover:
 * - Task statistics calculations
 * - Completion rate calculations
 * - Edge cases (0%, 100% completion)
 * - Repository exception handling
 */
@DisplayName("Get Task Statistics Use Case Tests")
@Execution(ExecutionMode.SAME_THREAD)
class GetTaskStatisticsUseCaseTest {

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
    @DisplayName("Should calculate task statistics correctly")
    fun shouldCalculateTaskStatisticsCorrectly() = mainDispatcherRule.runTest {
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
    fun shouldHandle100PercentCompletionRateWhenNoIncompleteTasks() = mainDispatcherRule.runTest {
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
    fun shouldHandleZeroCompletedTasks() = mainDispatcherRule.runTest {
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
    fun shouldHandleRepositoryExceptionDuringStatsCalculation() = mainDispatcherRule.runTest {
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
    fun shouldCalculateCompletionRatesCorrectly(completedCount: Int) = mainDispatcherRule.runTest {
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
