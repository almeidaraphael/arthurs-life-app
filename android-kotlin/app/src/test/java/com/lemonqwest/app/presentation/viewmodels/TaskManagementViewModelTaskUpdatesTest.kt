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
import com.lemonqwest.app.domain.task.usecase.UndoTaskUseCase
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.OptIn
import kotlin.time.Duration.Companion.seconds

/**
 * Focused test suite for TaskManagementViewModel task updates functionality.
 *
 * Tests cover:
 * - Successful task updates
 * - Task update failure handling
 * - Loading state during updates
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel - Task Updates Tests")
class TaskManagementViewModelTaskUpdatesTest { // Removed: : ViewModelTestBase()

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
    fun setUpTaskManagementViewModelTaskUpdates() {
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
    fun tearDownTaskManagementViewModelTaskUpdates() {
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
    fun `should update task successfully`() = runViewModelTest {
        // Create ViewModel inside runViewModelTest context
        viewModel = createViewModel()

        val taskId = "task-1"
        val newTitle = "Updated Task"
        val newCategory = TaskCategory.HOMEWORK
        coEvery {
            mockTaskManagementUseCases.updateTask(taskId, newTitle, newCategory)
        } returns Result.success(TestDataFactory.createTask(title = newTitle, category = newCategory))

        viewModel.updateTask(taskId, newTitle, newCategory)

        // Allow all coroutines to complete
        advanceUntilIdle()

        coVerify { mockTaskManagementUseCases.updateTask(taskId, newTitle, newCategory) }

        val uiState = viewModel.uiState.first()
        assertEquals("Task updated successfully", uiState.successMessage)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `should handle task update failure`() = runViewModelTest {
        // Create ViewModel inside runViewModelTest context
        viewModel = createViewModel()

        val taskId = "task-1"
        val newTitle = "Updated Task"
        val newCategory = TaskCategory.HOMEWORK
        val errorMessage = "Update failed"
        coEvery {
            mockTaskManagementUseCases.updateTask(taskId, newTitle, newCategory)
        } returns Result.failure(Exception(errorMessage))

        viewModel.updateTask(taskId, newTitle, newCategory)

        // Allow all coroutines to complete
        advanceUntilIdle()

        val uiState = viewModel.uiState.first()
        assertEquals("Failed to update task: $errorMessage", uiState.error)
        assertFalse(uiState.isLoading)
    }
}
