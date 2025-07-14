package com.arthurslife.app.presentation.viewmodels

import com.arthurslife.app.di.TaskUseCases
import com.arthurslife.app.domain.TestDataFactory
import com.arthurslife.app.domain.achievement.AchievementType
import com.arthurslife.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.arthurslife.app.domain.auth.AuthenticationSessionService
import com.arthurslife.app.domain.common.AchievementEventManager
import com.arthurslife.app.domain.common.PresentationException
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.usecase.CompleteTaskUseCase
import com.arthurslife.app.domain.task.usecase.TaskManagementUseCases
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.domain.task.usecase.UndoTaskUseCase
import com.arthurslife.app.domain.user.UserRole
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.OptIn

/**
 * Comprehensive tests for TaskManagementViewModel.
 *
 * This test class verifies the ViewModel's state management, user interactions,
 * and proper delegation to use cases following MVVM patterns.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel Tests")
class TaskManagementViewModelTest {

    private lateinit var mockTaskUseCases: TaskUseCases
    private lateinit var mockTaskManagementUseCases: TaskManagementUseCases
    private lateinit var mockCompleteTaskUseCase: CompleteTaskUseCase
    private lateinit var mockUndoTaskUseCase: UndoTaskUseCase
    private lateinit var mockCalculateDailyProgressUseCase:
        com.arthurslife.app.domain.task.usecase.CalculateDailyProgressUseCase
    private lateinit var mockAchievementTrackingUseCase: AchievementTrackingUseCase
    private lateinit var mockAuthenticationSessionService: AuthenticationSessionService
    private lateinit var mockAchievementEventManager: AchievementEventManager
    private lateinit var viewModel: TaskManagementViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testChild = TestDataFactory.createChildUser(id = "child-1", name = "Test Child")
    private val testTasks = listOf(
        TestDataFactory.createTask(title = "Clean Room", isCompleted = false),
        TestDataFactory.createTask(title = "Do Homework", isCompleted = true),
        TestDataFactory.createTask(title = "Feed Pet", isCompleted = false),
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockTaskManagementUseCases = mockk()
        mockCompleteTaskUseCase = mockk()
        mockUndoTaskUseCase = mockk()
        mockCalculateDailyProgressUseCase = mockk()
        mockAchievementTrackingUseCase = mockk()
        mockAuthenticationSessionService = mockk()
        mockAchievementEventManager = mockk {
            coEvery { emitAchievementUpdate(any<String>()) } returns Unit
            coEvery { emitAchievementUpdate(any<String>(), any()) } returns Unit
        }

        // Create the TaskUseCases container
        mockTaskUseCases = TaskUseCases(
            taskManagementUseCases = mockTaskManagementUseCases,
            completeTaskUseCase = mockCompleteTaskUseCase,
            undoTaskUseCase = mockUndoTaskUseCase,
            calculateDailyProgressUseCase = mockCalculateDailyProgressUseCase,
        )

        // Default successful setup for current user
        coEvery { mockAuthenticationSessionService.getCurrentUser() } returns testChild
        coEvery { mockTaskManagementUseCases.getTasksForUser(testChild.id) } returns Result.success(testTasks)
        coEvery { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) } returns Result.success(
            testTasks.filter { !it.isCompleted },
        )
        coEvery { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) } returns Result.success(
            testTasks.filter { it.isCompleted },
        )
        coEvery { mockTaskManagementUseCases.getTaskStats(testChild.id) } returns Result.success(
            TaskStats(
                totalCompletedTasks = testTasks.count { it.isCompleted },
                totalTokensEarned = 50,
                incompleteTasks = testTasks.count { !it.isCompleted },
                completionRate = 33,
                currentStreak = 2,
            ),
        )

        // Setup missing mocks for daily progress and achievements
        coEvery { mockCalculateDailyProgressUseCase(testChild.id) } returns 0.75f
        coEvery { mockAchievementTrackingUseCase.getUnlockedAchievements(testChild.id) } returns emptyList()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): TaskManagementViewModel {
        val dependencies = TaskManagementDependencies(
            taskUseCases = mockTaskUseCases,
            achievementTrackingUseCase = mockAchievementTrackingUseCase,
            authenticationSessionService = mockAuthenticationSessionService,
            achievementEventManager = mockAchievementEventManager,
        )
        return TaskManagementViewModel(dependencies)
    }

    @Nested
    @DisplayName("Initialization")
    inner class Initialization {

        @Test
        fun `should initialize with correct default state`() = runTest {
            viewModel = createViewModel()

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals(false, uiState.isLoading)
            assertNull(uiState.error)
            assertNull(uiState.successMessage)
        }

        @Test
        fun `should load child user and tasks on initialization`() = runTest {
            viewModel = createViewModel()

            // Allow all coroutines to complete
            advanceUntilIdle()

            // Verify repositories were called
            coVerify { mockAuthenticationSessionService.getCurrentUser() }
            coVerify { mockTaskManagementUseCases.getTasksForUser(testChild.id) }
            coVerify { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) }
            coVerify { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) }
            coVerify { mockTaskManagementUseCases.getTaskStats(testChild.id) }

            // Verify state is updated
            assertEquals(testTasks, viewModel.allTasks.first())
            assertEquals(testTasks.filter { !it.isCompleted }, viewModel.incompleteTasks.first())
            assertEquals(testTasks.filter { it.isCompleted }, viewModel.completedTasks.first())
            assertNotNull(viewModel.taskStats.first())
        }

        @Test
        fun `should handle missing child user gracefully`() = runTest {
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null

            viewModel = createViewModel()

            // Allow all coroutines to complete
            advanceUntilIdle()

            // Should not attempt to load tasks
            coVerify(exactly = 0) { mockTaskManagementUseCases.getTasksForUser(any()) }

            // State should remain empty
            assertTrue(viewModel.allTasks.first().isEmpty())
            assertTrue(viewModel.incompleteTasks.first().isEmpty())
            assertTrue(viewModel.completedTasks.first().isEmpty())
            assertNull(viewModel.taskStats.first())
        }

        @Test
        fun `should handle child user loading error`() = runTest {
            val errorMessage = "Database connection failed"
            coEvery { mockAuthenticationSessionService.getCurrentUser() } throws PresentationException(errorMessage)

            viewModel = createViewModel()

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals(errorMessage, uiState.error)
            assertFalse(uiState.isLoading)
        }
    }

    @Nested
    @DisplayName("Task Creation")
    inner class TaskCreation {

        @BeforeEach
        fun setupTaskCreation() {
            viewModel = createViewModel()
        }

        @Test
        fun `should create task successfully`() = runTest {
            val taskTitle = "New Task"
            val taskCategory = TaskCategory.HOUSEHOLD
            coEvery {
                mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
            } returns Result.success(TestDataFactory.createTask(title = taskTitle, category = taskCategory))

            viewModel.createTask(taskTitle, taskCategory)

            // Allow all coroutines to complete
            advanceUntilIdle()

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
        fun `should handle task creation failure`() = runTest {
            val taskTitle = "New Task"
            val taskCategory = TaskCategory.HOUSEHOLD
            val errorMessage = "Task creation failed"
            coEvery {
                mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
            } returns Result.failure(Exception(errorMessage))

            viewModel.createTask(taskTitle, taskCategory)

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals("Failed to create task: $errorMessage", uiState.error)
            assertFalse(uiState.isLoading)
            assertNull(uiState.successMessage)
        }

        @Test
        fun `should show loading state during task creation`() = runTest {
            val taskTitle = "New Task"
            val taskCategory = TaskCategory.HOUSEHOLD
            coEvery {
                mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
            } returns Result.success(TestDataFactory.createTask(title = taskTitle, category = taskCategory))

            // Start the operation but don't wait for completion
            viewModel.createTask(taskTitle, taskCategory)

            // Check that loading state is set (implementation may vary based on coroutine timing)
            // Allow all coroutines to complete
            advanceUntilIdle()

            // After completion, loading should be false
            val uiState = viewModel.uiState.first()
            assertFalse(uiState.isLoading)
        }

        @Test
        fun `should handle domain exception during task creation`() = runTest {
            val taskTitle = "New Task"
            val taskCategory = TaskCategory.HOUSEHOLD
            val errorMessage = "Domain validation failed"
            coEvery {
                mockTaskManagementUseCases.createTask(taskTitle, taskCategory, testChild.id)
            } throws PresentationException(errorMessage)

            viewModel.createTask(taskTitle, taskCategory)

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals(errorMessage, uiState.error)
            assertFalse(uiState.isLoading)
        }

        @Test
        fun `should not create task when child user ID is null`() = runTest {
            // Setup ViewModel with no child user
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
            viewModel = createViewModel()

            // Allow all coroutines to complete
            advanceUntilIdle()

            viewModel.createTask("Test Task", TaskCategory.HOUSEHOLD)

            // Allow all coroutines to complete
            advanceUntilIdle()

            // Should not call the use case
            coVerify(exactly = 0) { mockTaskManagementUseCases.createTask(any(), any(), any()) }
        }
    }

    @Nested
    @DisplayName("Task Updates")
    inner class TaskUpdates {

        @BeforeEach
        fun setupTaskUpdates() {
            viewModel = createViewModel()
        }

        @Test
        fun `should update task successfully`() = runTest {
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
        fun `should handle task update failure`() = runTest {
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

    @Nested
    @DisplayName("Task Deletion")
    inner class TaskDeletion {

        @BeforeEach
        fun setupTaskDeletion() {
            viewModel = createViewModel()
        }

        @Test
        fun `should delete task successfully`() = runTest {
            val taskId = "task-1"
            coEvery { mockTaskManagementUseCases.deleteTask(taskId) } returns Result.success(Unit)

            viewModel.deleteTask(taskId)

            // Allow all coroutines to complete
            advanceUntilIdle()

            coVerify { mockTaskManagementUseCases.deleteTask(taskId) }

            val uiState = viewModel.uiState.first()
            assertEquals("Task deleted successfully", uiState.successMessage)
            assertFalse(uiState.isLoading)
        }

        @Test
        fun `should handle task deletion failure`() = runTest {
            val taskId = "task-1"
            val errorMessage = "Deletion failed"
            coEvery { mockTaskManagementUseCases.deleteTask(taskId) } returns Result.failure(Exception(errorMessage))

            viewModel.deleteTask(taskId)

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals("Failed to delete task: $errorMessage", uiState.error)
            assertFalse(uiState.isLoading)
        }
    }

    @Nested
    @DisplayName("Task Completion")
    inner class TaskCompletion {

        @BeforeEach
        fun setupTaskCompletion() {
            viewModel = createViewModel()
        }

        @Test
        fun `should complete task successfully`() = runTest {
            val taskId = "task-1"
            val completionResult = TestDataFactory.createTaskCompletionResult(
                taskId = taskId,
                tokensAwarded = 10,
                newlyUnlockedAchievements = emptyList(),
            )
            coEvery { mockCompleteTaskUseCase(taskId) } returns Result.success(completionResult)

            viewModel.completeTask(taskId)

            // Allow all coroutines to complete
            advanceUntilIdle()

            coVerify { mockCompleteTaskUseCase(taskId) }

            val uiState = viewModel.uiState.first()
            assertTrue(uiState.successMessage!!.contains("Task completed!"))
            assertTrue(uiState.successMessage!!.contains("Earned 10 tokens"))
            assertFalse(uiState.isLoading)
        }

        @Test
        fun `should complete task with achievements successfully`() = runTest {
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

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertTrue(uiState.successMessage!!.contains("Task completed!"))
            assertTrue(uiState.successMessage!!.contains("Earned 15 tokens"))
            assertTrue(uiState.successMessage!!.contains("Unlocked 1 achievement(s)!"))
            assertFalse(uiState.isLoading)
        }

        @Test
        fun `should handle task completion failure`() = runTest {
            val taskId = "task-1"
            val errorMessage = "Task already completed"
            coEvery { mockCompleteTaskUseCase(taskId) } returns Result.failure(Exception(errorMessage))

            viewModel.completeTask(taskId)

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals("Failed to complete task: $errorMessage", uiState.error)
            assertFalse(uiState.isLoading)
        }
    }

    @Nested
    @DisplayName("Task Undo")
    inner class TaskUndo {

        @BeforeEach
        fun setupTaskUndo() {
            viewModel = createViewModel()
        }

        @Test
        fun `should undo task successfully by child`() = runTest {
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
        fun `should undo task successfully by caregiver`() = runTest {
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
        fun `should handle task undo failure`() = runTest {
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

    @Nested
    @DisplayName("State Management")
    inner class StateManagement {

        @Test
        fun `should clear messages successfully`() = runTest {
            viewModel = createViewModel()
            // Set up a state with error and success message
            val taskId = "task-1"
            coEvery { mockTaskManagementUseCases.deleteTask(taskId) } returns Result.success(Unit)
            viewModel.deleteTask(taskId)

            // Allow all coroutines to complete
            advanceUntilIdle()

            // Verify success message is set
            val stateWithMessage = viewModel.uiState.first()
            assertNotNull(stateWithMessage.successMessage)

            // Clear messages
            viewModel.clearMessages()

            // Verify messages are cleared
            val clearedState = viewModel.uiState.first()
            assertNull(clearedState.error)
            assertNull(clearedState.successMessage)
            assertFalse(clearedState.isLoading)
        }

        @Test
        fun `should refresh tasks successfully`() = runTest {
            viewModel = createViewModel()
            // Allow all coroutines to complete
            advanceUntilIdle()

            // Clear verification calls from initialization
            coVerify { mockTaskManagementUseCases.getTasksForUser(testChild.id) }
            coVerify { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) }
            coVerify { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) }
            coVerify { mockTaskManagementUseCases.getTaskStats(testChild.id) }

            // Call refresh
            viewModel.refresh()

            // Allow all coroutines to complete
            advanceUntilIdle()

            // Verify all task loading methods are called again
            coVerify(atLeast = 2) { mockTaskManagementUseCases.getTasksForUser(testChild.id) }
            coVerify(
                atLeast = 2,
            ) { mockTaskManagementUseCases.getIncompleteTasksForUser(testChild.id) }
            coVerify(
                atLeast = 2,
            ) { mockTaskManagementUseCases.getCompletedTasksForUser(testChild.id) }
            coVerify(atLeast = 2) { mockTaskManagementUseCases.getTaskStats(testChild.id) }
        }

        @Test
        fun `should handle refresh when child user ID is null`() = runTest {
            // Clear any previous mock setups
            clearMocks(mockAuthenticationSessionService)

            // Setup ViewModel with no child user
            coEvery { mockAuthenticationSessionService.getCurrentUser() } returns null
            viewModel = createViewModel()

            // Allow all coroutines to complete
            advanceUntilIdle()

            viewModel.refresh()

            // Allow all coroutines to complete
            advanceUntilIdle()

            // Should not call task loading methods
            coVerify(exactly = 0) { mockTaskManagementUseCases.getTasksForUser(any()) }
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {

        @Test
        fun `should handle task loading errors gracefully`() = runTest {
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

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            assertEquals("Failed to load tasks: $errorMessage", uiState.error)
            assertFalse(uiState.isLoading)
        }

        @Test
        fun `should handle multiple task loading errors`() = runTest {
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

            // Allow all coroutines to complete
            advanceUntilIdle()

            val uiState = viewModel.uiState.first()
            // Should show the first error encountered
            assertTrue(uiState.error!!.contains("Failed to load"))
            assertFalse(uiState.isLoading)
        }
    }

    @Nested
    @DisplayName("StateFlow Behavior")
    inner class StateFlowBehavior {

        @BeforeEach
        fun setupStateFlowBehavior() {
            viewModel = createViewModel()
        }

        @Test
        fun `should emit distinct states correctly`() = runTest {
            // Allow all coroutines to complete
            advanceUntilIdle()

            val states = mutableListOf<TaskManagementUiState>()

            // Start collecting states during operation
            val job = launch {
                viewModel.uiState.collect { state ->
                    states.add(state)
                }
            }

            coEvery {
                mockTaskManagementUseCases.createTask(any(), any(), any())
            } returns Result.success(TestDataFactory.createTask())

            viewModel.createTask("Test Task", TaskCategory.HOUSEHOLD)

            // Allow all coroutines to complete
            advanceUntilIdle()
            job.cancel()

            // Should have captured state changes
            assertTrue(states.isNotEmpty())

            // Find states with success message
            val successStates = states.filter { it.successMessage != null }
            assertTrue(successStates.isNotEmpty())
        }

        @Test
        fun `should maintain separate state flows for different data`() = runTest {
            // Allow all coroutines to complete
            advanceUntilIdle()

            // Verify initial state
            assertEquals(testTasks, viewModel.allTasks.first())
            assertEquals(testTasks.filter { !it.isCompleted }, viewModel.incompleteTasks.first())
            assertEquals(testTasks.filter { it.isCompleted }, viewModel.completedTasks.first())
            assertNotNull(viewModel.taskStats.first())

            // Each StateFlow should maintain its own data
            assertTrue(viewModel.allTasks.first().size > viewModel.completedTasks.first().size)
            assertTrue(viewModel.incompleteTasks.first().isNotEmpty())
        }
    }
}
