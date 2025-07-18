package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.di.TaskUseCases
import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.PresentationException
import com.lemonqwest.app.domain.task.TaskCategory
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for TaskManagementViewModel task creation functionality.
 *
 * Tests cover:
 * - Successful task creation
 * - Task creation failure handling
 * - Loading state management
 * - Domain exception handling
 * - Missing user ID validation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Task Creation Tests")
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
    fun `should create task successfully`() {
        viewModel = createViewModel()
        val taskTitle = "New Task"
        val taskCategory = TaskCategory.HOUSEHOLD
        coEvery {
            mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
        } returns Result.success(TestDataFactory.createTask(title = taskTitle, category = taskCategory))
        viewModel.createTask(taskTitle, taskCategory)
        coVerify {
            mockTaskManagementUseCases.createTask(
                taskTitle,
                taskCategory,
                testChild.id,
            )
        }
        val uiState = viewModel.uiState.first()
        assertEquals("Task created successfully", uiState.successMessage)
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `should handle task creation failure`() {
        viewModel = createViewModel()
        val taskTitle = "New Task"
        val taskCategory = TaskCategory.HOUSEHOLD
        val errorMessage = "Task creation failed"
        coEvery {
            mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
        } returns Result.failure(Exception(errorMessage))
        viewModel.createTask(taskTitle, taskCategory)
        val uiState = viewModel.uiState.first()
        assertEquals("Failed to create task: $errorMessage", uiState.error)
        assertFalse(uiState.isLoading)
        assertNull(uiState.successMessage)
    }

    @Test
    fun `should show loading state during task creation`() {
        viewModel = createViewModel()
        val taskTitle = "New Task"
        val taskCategory = TaskCategory.HOUSEHOLD
        coEvery {
            mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
        } returns Result.success(TestDataFactory.createTask(title = taskTitle, category = taskCategory))
        viewModel.createTask(taskTitle, taskCategory)
        val uiState = viewModel.uiState.first()
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should handle domain exception during task creation`() {
        viewModel = createViewModel()
        val taskTitle = "New Task"
        val taskCategory = TaskCategory.HOUSEHOLD
        val errorMessage = "Domain validation failed"
        coEvery {
            mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
        } throws PresentationException(errorMessage)
        viewModel.createTask(taskTitle, taskCategory)
        val uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should not create task when child user ID is null`() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        viewModel = createViewModel()
        viewModel.createTask("Test Task", TaskCategory.HOUSEHOLD)
        coVerify(exactly = 0) { mockTaskManagementUseCases.createTask(any(), any(), any()) }
    }
}
