package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.di.TaskUseCases
import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.task.usecase.CalculateDailyProgressUseCase
import com.lemonqwest.app.domain.task.usecase.CompleteTaskUseCase
import com.lemonqwest.app.domain.task.usecase.TaskManagementUseCases
import com.lemonqwest.app.domain.task.usecase.UndoTaskUseCase
import com.lemonqwest.app.testutils.ViewModelTestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for TaskManagementViewModel task deletion functionality.
 *
 * Tests cover:
 * - Successful task deletion
 * - Task deletion failure handling
 * - Loading state during deletion
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Task Deletion Tests")
class TaskManagementViewModelTaskDeletionTest : ViewModelTestBase() {

    private lateinit var mockTaskUseCases: TaskUseCases
    private lateinit var mockTaskManagementUseCases: TaskManagementUseCases
    private lateinit var mockCompleteTaskUseCase: CompleteTaskUseCase
    private lateinit var mockUndoTaskUseCase: UndoTaskUseCase
    private lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAchievementEventManager: AchievementEventManager
    private lateinit var mockCalculateDailyProgressUseCase: CalculateDailyProgressUseCase

    private lateinit var viewModel: TaskManagementViewModel

    private val testChild = TestDataFactory.createChildUser()

    @BeforeEach
    override fun setUpViewModel() {
        super.setUpViewModel()

        mockTaskUseCases = mockk()
        mockTaskManagementUseCases = mockk()
        mockCompleteTaskUseCase = mockk()
        mockUndoTaskUseCase = mockk()
        mockAchievementTrackingUseCase = mockk()
        mockAuthenticationSessionService = mockk()
        mockAchievementEventManager = mockk()
        mockCalculateDailyProgressUseCase = mockk()

        // Set up default mocks
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testChild

        mockTaskUseCases = TaskUseCases(
            taskManagementUseCases = mockTaskManagementUseCases,
            completeTaskUseCase = mockCompleteTaskUseCase,
            undoTaskUseCase = mockUndoTaskUseCase,
            calculateDailyProgressUseCase = mockCalculateDailyProgressUseCase,
        )

        // Note: ViewModel creation moved to individual test methods to avoid IllegalStateException
    }

    private fun createViewModel(): TaskManagementViewModel {
        val dependencies = TaskManagementDependencies(
            taskUseCases = mockTaskUseCases,
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            authenticationSessionService = mockAuthenticationSessionService,
            achievementEventManager = mockAchievementEventManager,
        )
        return TaskManagementViewModel(dependencies, testDispatcher, testScope)
    }

    @Test
    fun `should delete task successfully`() = runViewModelTest {
        // Create ViewModel inside runViewModelTest context
        viewModel = createViewModel()

        val taskId = "task-1"
        coEvery { mockTaskManagementUseCases.deleteTask(taskId) } returns Result.success(Unit)
        coEvery { mockTaskManagementUseCases.getTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getTaskStats(testChild.id) } returns Result.success(
            com.lemonqwest.app.domain.task.usecase.TaskStats(
                totalCompletedTasks = 0,
                totalTokensEarned = 0,
                incompleteTasks = 0,
                completionRate = 0,
                currentStreak = 0,
            ),
        )
        coEvery { mockCalculateDailyProgressUseCase(testChild.id) } returns 0.0f
        coEvery { mockAchievementTrackingUseCase.getUnlockedAchievements(testChild.id) } returns emptyList()

        // Initialize the ViewModel explicitly
        viewModel.initialize()
        advanceUntilIdle()

        viewModel.deleteTask(taskId)

        // Allow all coroutines to complete
        advanceUntilIdle()

        coVerify { mockTaskManagementUseCases.deleteTask(taskId) }

        val uiState = viewModel.uiState.first()
        assertEquals("Task deleted successfully", uiState.successMessage)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should handle task deletion failure`() = runViewModelTest {
        // Create ViewModel inside runViewModelTest context
        viewModel = createViewModel()

        val taskId = "task-1"
        val errorMessage = "Deletion failed"
        coEvery { mockTaskManagementUseCases.deleteTask(taskId) } returns Result.failure(Exception(errorMessage))
        coEvery { mockTaskManagementUseCases.getTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getTaskStats(testChild.id) } returns Result.success(
            com.lemonqwest.app.domain.task.usecase.TaskStats(
                totalCompletedTasks = 0,
                totalTokensEarned = 0,
                incompleteTasks = 0,
                completionRate = 0,
                currentStreak = 0,
            ),
        )
        coEvery { mockCalculateDailyProgressUseCase(testChild.id) } returns 0.0f
        coEvery { mockAchievementTrackingUseCase.getUnlockedAchievements(testChild.id) } returns emptyList()

        // Initialize the ViewModel explicitly
        viewModel.initialize()
        advanceUntilIdle()

        viewModel.deleteTask(taskId)

        // Allow all coroutines to complete
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertEquals("Failed to delete task: $errorMessage", uiState.error)
        assertFalse(uiState.isLoading)
    }
}
