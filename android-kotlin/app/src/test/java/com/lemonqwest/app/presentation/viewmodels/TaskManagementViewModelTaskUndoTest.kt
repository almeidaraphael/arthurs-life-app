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
import com.lemonqwest.app.domain.user.UserRole
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
 * Focused test suite for TaskManagementViewModel task undo functionality.
 *
 * Tests cover:
 * - Successful task undo by child
 * - Successful task undo by caregiver
 * - Task undo failure handling
 * - Loading state during undo
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Task Undo Tests")
class TaskManagementViewModelTaskUndoTest { // Removed: : ViewModelTestBase()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope

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

    // No longer needed: mainDispatcherLock

    @BeforeEach
    fun setUpTaskManagementViewModelTaskUndo() {
        testDispatcher = UnconfinedTestDispatcher()
        testScope = TestScope(testDispatcher)
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

    // Complete teardown without ViewModelTestBase
    @AfterEach
    fun tearDownTaskManagementViewModelTaskUndo() {
        testScope.cancel()
        unmockkAll()
    }

    private fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        testScope.runTest(timeout = 5.seconds) {
            testBody()
        }
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
    fun `should undo task successfully by child`() = runViewModelTest {
        viewModel = createViewModel()
        val taskId = "task-2"
        val undoResult = TestDataFactory.createTaskUndoResult(
            taskId = taskId,
            tokensDeducted = 10,
            newTokenBalance = 40,
            undoneByRole = UserRole.CHILD,
        )
        coEvery { mockUndoTaskUseCase(taskId) } returns Result.success(undoResult)

        viewModel.undoTask(taskId)

        // Allow all coroutines to complete
        advanceUntilIdle()

        coVerify { mockUndoTaskUseCase(taskId) }

        val uiState = viewModel.uiState.first()
        assertTrue(uiState.successMessage!!.contains("Task undone!"))
        assertTrue(uiState.successMessage!!.contains("10 tokens were deducted"))
        assertTrue(uiState.successMessage!!.contains("New balance: 40 tokens"))
        assertFalse(uiState.successMessage!!.contains("Administrative override"))
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should undo task successfully by caregiver`() = runViewModelTest {
        viewModel = createViewModel()
        val taskId = "task-2"
        val undoResult = TestDataFactory.createTaskUndoResult(
            taskId = taskId,
            tokensDeducted = 10,
            newTokenBalance = 40,
            undoneByRole = UserRole.CAREGIVER,
        )
        coEvery { mockUndoTaskUseCase(taskId) } returns Result.success(undoResult)

        viewModel.undoTask(taskId)

        // Allow all coroutines to complete
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertTrue(uiState.successMessage!!.contains("Task undone!"))
        assertTrue(uiState.successMessage!!.contains("Administrative override"))
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should handle task undo failure`() = runViewModelTest {
        viewModel = createViewModel()
        val taskId = "task-2"
        val errorMessage = "Task not completed yet"
        coEvery { mockUndoTaskUseCase(taskId) } returns Result.failure(Exception(errorMessage))

        viewModel.undoTask(taskId)

        // Allow all coroutines to complete
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertEquals("Failed to undo task: $errorMessage", uiState.error)
        assertFalse(uiState.isLoading)
    }
}
