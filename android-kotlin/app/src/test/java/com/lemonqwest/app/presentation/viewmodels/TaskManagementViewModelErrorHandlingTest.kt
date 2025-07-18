package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.di.TaskUseCases
import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.task.usecase.CalculateDailyProgressUseCase
import com.lemonqwest.app.domain.task.usecase.CompleteTaskUseCase
import com.lemonqwest.app.domain.task.usecase.TaskManagementUseCases
import com.lemonqwest.app.domain.task.usecase.TaskStats
import com.lemonqwest.app.domain.task.usecase.UndoTaskUseCase
import com.lemonqwest.app.testutils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for TaskManagementViewModel error handling functionality.
 *
 * Tests cover:
 * - Task loading error handling
 * - Multiple task loading error scenarios
 * - Error state management
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Error Handling Tests")
class TaskManagementViewModelErrorHandlingTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

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
    fun setUp() {
        mockTaskUseCases = mockk()
        mockTaskManagementUseCases = mockk()
        mockCompleteTaskUseCase = mockk()
        mockUndoTaskUseCase = mockk()
        mockAchievementTrackingUseCase = mockk()
        mockAuthenticationSessionService = mockk()
        mockAchievementEventManager = mockk()
        mockCalculateDailyProgressUseCase = mockk()
        mockTaskUseCases = TaskUseCases(
            taskManagementUseCases = mockTaskManagementUseCases,
            completeTaskUseCase = mockCompleteTaskUseCase,
            undoTaskUseCase = mockUndoTaskUseCase,
            calculateDailyProgressUseCase = mockCalculateDailyProgressUseCase,
        )
    }

    private fun createViewModel(): TaskManagementViewModel {
        val dependencies = TaskManagementDependencies(
            taskUseCases = mockTaskUseCases,
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            authenticationSessionService = mockAuthenticationSessionService,
            achievementEventManager = mockAchievementEventManager,
        )
        return TaskManagementViewModel(dependencies, mainDispatcherRule.testDispatcher)
    }

    @Test
    fun `should handle task loading errors gracefully`() {
        val errorMessage = "Network connection failed"
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testChild
        coEvery { mockTaskManagementUseCases.getTasksForUser(testChild.id) } returns Result.failure(Exception(errorMessage))
        coEvery { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) } returns Result.success(emptyList())
        coEvery { mockTaskManagementUseCases.getTaskStats(testChild.id) } returns Result.success(
            TaskStats(
                totalCompletedTasks = 0,
                totalTokensEarned = 0,
                incompleteTasks = 0,
                completionRate = 0,
                currentStreak = 0,
            ),
        )
        viewModel = createViewModel()
        val uiState = viewModel.uiState.first()
        assertEquals("Failed to load tasks: $errorMessage", uiState.error)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should handle multiple task loading errors`() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testChild
        coEvery { mockTaskManagementUseCases.getTasksForUser(testChild.id) } returns Result.failure(Exception("Tasks error"))
        coEvery {
            mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id)
        } returns Result.failure(Exception("Incomplete error"))
        coEvery {
            mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id)
        } returns Result.failure(Exception("Completed error"))
        coEvery { mockTaskManagementUseCases.getTaskStats(testChild.id) } returns Result.failure(Exception("Stats error"))
        viewModel = createViewModel()
        val uiState = viewModel.uiState.first()
        assertTrue(uiState.error!!.contains("Failed to load"))
        assertFalse(uiState.isLoading)
    }
}
