package com.lemonqwest.app.presentation.viewmodels

import com.lemonqwest.app.di.TaskUseCases
import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.PresentationException
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import com.lemonqwest.app.testutils.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for TaskManagementViewModel initialization.
 *
 * Tests cover:
 * - Default state initialization
 * - Child user and task loading
 * - Missing user graceful handling
 * - Initialization error handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Initialization Tests")
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
                totalTokensEarned = testTasks.filter { it.isCompleted }.sumOf { it.tokenReward },
                incompleteTasks = testTasks.count { !it.isCompleted },
                completionRate = 50,
                currentStreak = 2,
            ),
        )
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
    fun `should initialize with correct default state`() {
        viewModel = createViewModel()
        val uiState = viewModel.uiState.first()
        assertEquals(false, uiState.isLoading)
        assertNull(uiState.error)
        assertNull(uiState.successMessage)
    }

    @Test
    fun `should load child user and tasks on initialization`() {
        viewModel = createViewModel()
        viewModel.initialize()
        coVerify { mockAuthenticationSessionService.getCurrentUser() }
        coVerify { mockTaskManagementUseCases.getTasksForUser(testChild.id) }
        coVerify { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) }
        coVerify { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) }
        coVerify { mockTaskManagementUseCases.getTaskStats(testChild.id) }
        assertEquals(testTasks, viewModel.allTasks.first())
        assertEquals(testTasks.filter { !it.isCompleted }, viewModel.incompleteTasks.first())
        assertEquals(testTasks.filter { it.isCompleted }, viewModel.completedTasks.first())
        assertNotNull(viewModel.taskStats.first())
    }

    @Test
    fun `should handle missing child user gracefully`() {
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
        viewModel = createViewModel()
        viewModel.initialize()
        coVerify(exactly = 0) { mockTaskManagementUseCases.getTasksForUser(any()) }
        assertTrue(viewModel.allTasks.first().isEmpty())
        assertTrue(viewModel.incompleteTasks.first().isEmpty())
        assertTrue(viewModel.completedTasks.first().isEmpty())
        assertNull(viewModel.taskStats.first())
    }

    @Test
    fun `should handle child user loading error`() {
        val errorMessage = "Database connection failed"
        coEvery { mockAuthenticationSessionService.getCurrentUser() } throws PresentationException(errorMessage)
        viewModel = createViewModel()
        viewModel.initialize()
        val uiState = viewModel.uiState.first()
        assertEquals(errorMessage, uiState.error)
        assertFalse(uiState.isLoading)
    }
}
