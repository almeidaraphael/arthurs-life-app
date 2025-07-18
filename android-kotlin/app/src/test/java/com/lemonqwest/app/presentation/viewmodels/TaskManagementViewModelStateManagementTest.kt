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
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for TaskManagementViewModel state management functionality.
 *
 * Tests cover:
 * - Message clearing
 * - Task refresh functionality
 * - Refresh when user ID is null
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - State Management Tests")
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
    private val testTasks = TestDataFactory.createTaskList(
        assignedToUserId = testChild.id,
        includeCompleted = true,
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = false)
        mockTaskUseCases = mockk()
        mockTaskManagementUseCases = mockk()
        mockCompleteTaskUseCase = mockk()
        mockUndoTaskUseCase = mockk()
        mockAchievementTrackingUseCase = mockk()
        mockAuthenticationSessionService = mockk()
        mockAchievementEventManager = mockk()
        mockCalculateDailyProgressUseCase = mockk()
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testChild
        coEvery { mockTaskManagementUseCases.getTasksForUser(testChild.id) } returns Result.success(testTasks)
        coEvery {
            mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id)
        } returns Result.success(testTasks.filter { !it.isCompleted })
        coEvery {
            mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id)
        } returns Result.success(testTasks.filter { it.isCompleted })
        coEvery { mockTaskManagementUseCases.getTaskStats(testChild.id) } returns Result.success(
            TaskStats(
                totalCompletedTasks = testTasks.count { it.isCompleted },
                totalTokensEarned = 50,
                incompleteTasks = testTasks.count { !it.isCompleted },
                completionRate = 33,
                currentStreak = 2,
            ),
        )
        coEvery { mockTaskManagementUseCases.deleteTask(any()) } returns Result.success(Unit)
        mockTaskUseCases = TaskUseCases(
            taskManagementUseCases = mockTaskManagementUseCases,
            completeTaskUseCase = mockCompleteTaskUseCase,
            undoTaskUseCase = mockUndoTaskUseCase,
            calculateDailyProgressUseCase = mockCalculateDailyProgressUseCase,
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
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
    fun `should clear messages successfully`() {
        viewModel = createViewModel()
        val taskId = "task-1"
        viewModel.deleteTask(taskId)
        val stateWithMessage = viewModel.uiState.first()
        assertNotNull(stateWithMessage.successMessage)
        viewModel.clearMessages()
        val clearedState = viewModel.uiState.first()
        assertNull(clearedState.error)
        assertNull(clearedState.successMessage)
        assertFalse(clearedState.isLoading)
    }

    @Test
    fun `should refresh tasks successfully`() {
        viewModel = createViewModel()
        coVerify { mockTaskManagementUseCases.getTasksForUser(testChild.id) }
        coVerify { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) }
        coVerify { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) }
        coVerify { mockTaskManagementUseCases.getTaskStats(testChild.id) }
        viewModel.refresh()
        coVerify(atLeast = 2) { mockTaskManagementUseCases.getTasksForUser(testChild.id) }
        coVerify(atLeast = 2) { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) }
        coVerify(atLeast = 2) { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) }
        coVerify(atLeast = 2) { mockTaskManagementUseCases.getTaskStats(testChild.id) }
    }

    @Test
    fun `should handle refresh when child user ID is null`() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        viewModel = createViewModel()
        viewModel.refresh()
        coVerify(exactly = 0) { mockTaskManagementUseCases.getTasksForUser(any()) }
    }
}
