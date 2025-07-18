package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.di.TaskUseCases
import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.task.TaskCategory
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Focused test suite for TaskManagementViewModel StateFlow behavior.
 *
 * Tests cover:
 * - Distinct state emission
 * - Separate StateFlow data integrity
 * - State collection behavior
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - StateFlow Behavior Tests")
class TaskManagementViewModelStateFlowBehaviorTest {
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
        mockTaskUseCases = TaskUseCases(
            taskManagementUseCases = mockTaskManagementUseCases,
            completeTaskUseCase = mockCompleteTaskUseCase,
            undoTaskUseCase = mockUndoTaskUseCase,
            calculateDailyProgressUseCase = mockCalculateDailyProgressUseCase,
        )
        viewModel = createViewModel()
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
    fun `should emit distinct states correctly`() {
        val states = mutableListOf<TaskManagementUiState>()
        val job = kotlinx.coroutines.GlobalScope.launch {
            viewModel.uiState.collect { state ->
                states.add(state)
            }
        }
        coEvery {
            mockTaskManagementUseCases.createTask(any(), any(), any())
        } returns Result.success(TestDataFactory.createTask())
        viewModel.createTask("Test Task", TaskCategory.HOUSEHOLD)
        job.cancel()
        assertTrue(states.isNotEmpty())
        val successStates = states.filter { it.successMessage != null }
        assertTrue(successStates.isNotEmpty())
    }

    @Test
    fun `should maintain separate state flows for different data`() {
        assertEquals(testTasks, viewModel.allTasks.first())
        assertEquals(testTasks.filter { !it.isCompleted }, viewModel.incompleteTasks.first())
        assertEquals(testTasks.filter { it.isCompleted }, viewModel.completedTasks.first())
        assertNotNull(viewModel.taskStats.first())
        assertTrue(viewModel.allTasks.first().size > viewModel.completedTasks.first().size)
        assertTrue(viewModel.incompleteTasks.first().isNotEmpty())
    }
}
