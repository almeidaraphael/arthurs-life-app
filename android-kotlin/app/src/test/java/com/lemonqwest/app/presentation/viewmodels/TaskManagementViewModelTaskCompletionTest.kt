package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.di.TaskUseCases
import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.AchievementType
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.task.usecase.CalculateDailyProgressUseCase
import com.lemonqwest.app.domain.task.usecase.CompleteTaskUseCase
import com.lemonqwest.app.domain.task.usecase.TaskManagementUseCases
import com.lemonqwest.app.domain.task.usecase.UndoTaskUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for TaskManagementViewModel task completion functionality.
 *
 * Tests cover:
 * - Successful task completion
 * - Task completion with achievements
 * - Task completion failure handling
 * - Loading state during completion
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Task Completion Tests")
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
    fun `should complete task successfully`() {
        viewModel = createViewModel()
        val taskId = "task-1"
        val completionResult = TestDataFactory.createTaskCompletionResult(
            taskId = taskId,
            tokensAwarded = 10,
            newlyUnlockedAchievements = emptyList(),
        )
        coEvery { mockCompleteTaskUseCase(taskId) } returns Result.success(completionResult)
        viewModel.completeTask(taskId)
        coVerify { mockCompleteTaskUseCase(taskId) }
        val uiState = viewModel.uiState.first()
        assertTrue(uiState.successMessage!!.contains("Task completed!"))
        assertTrue(uiState.successMessage!!.contains("Earned 10 tokens"))
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should complete task with achievements successfully`() {
        viewModel = createViewModel()
        val taskId = "task-1"
        val achievements = listOf(
            TestDataFactory.createAchievement(
                type = AchievementType.FIRST_STEPS,
                userId = testChild.id,
            ),
        )
        val completionResult = TestDataFactory.createTaskCompletionResult(
            taskId = taskId,
            tokensAwarded = 15,
            newlyUnlockedAchievements = achievements,
        )
        coEvery { mockCompleteTaskUseCase(taskId) } returns Result.success(completionResult)
        viewModel.completeTask(taskId)
        val uiState = viewModel.uiState.first()
        assertTrue(uiState.successMessage!!.contains("Task completed!"))
        assertTrue(uiState.successMessage!!.contains("Earned 15 tokens"))
        assertTrue(uiState.successMessage!!.contains("Unlocked 1 achievement(s)!"))
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should handle task completion failure`() {
        viewModel = createViewModel()
        val taskId = "task-1"
        val errorMessage = "Task already completed"
        coEvery { mockCompleteTaskUseCase(taskId) } returns Result.failure(Exception(errorMessage))
        viewModel.completeTask(taskId)
        val uiState = viewModel.uiState.first()
        assertEquals("Failed to complete task: $errorMessage", uiState.error)
        assertFalse(uiState.isLoading)
    }
}
