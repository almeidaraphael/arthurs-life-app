package com.arthurslife.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthurslife.app.domain.common.DomainException
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.usecase.CompleteTaskUseCase
import com.arthurslife.app.domain.task.usecase.TaskManagementUseCases
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.domain.task.usecase.UndoTaskUseCase
import com.arthurslife.app.domain.user.UserRepository
import com.arthurslife.app.domain.user.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for task management operations in Arthur's Life MVP.
 *
 * This ViewModel handles the state and business logic for caregiver task management,
 * including creating, editing, deleting tasks, and viewing task statistics.
 * It also handles task completion for child users.
 */
@HiltViewModel
class TaskManagementViewModel
@Inject
constructor(
    private val taskManagementUseCases: TaskManagementUseCases,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val undoTaskUseCase: UndoTaskUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(TaskManagementUiState())
    val uiState: StateFlow<TaskManagementUiState> = _uiState.asStateFlow()

    // Task Lists
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks.asStateFlow()

    private val _incompleteTasks = MutableStateFlow<List<Task>>(emptyList())
    val incompleteTasks: StateFlow<List<Task>> = _incompleteTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<Task>>(emptyList())
    val completedTasks: StateFlow<List<Task>> = _completedTasks.asStateFlow()

    // Task Statistics
    private val _taskStats = MutableStateFlow<TaskStats?>(null)
    val taskStats: StateFlow<TaskStats?> = _taskStats.asStateFlow()

    // Child user ID (for MVP single-child family)
    private var childUserId: String? = null

    init {
        loadChildUser()
    }

    /**
     * Loads the child user from the repository.
     */
    private fun loadChildUser() {
        viewModelScope.launch {
            try {
                val child = userRepository.findByRole(UserRole.CHILD)
                childUserId = child?.id
                child?.let { loadTasksForUser(it.id) }
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Loads all tasks for the specified user.
     */
    private fun loadTasksForUser(userId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Load all tasks
                taskManagementUseCases.getTasksForUser(userId).fold(
                    onSuccess = { tasks ->
                        _allTasks.value = tasks
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(error = "Failed to load tasks: ${error.message}")
                    },
                )

                // Load incomplete tasks
                taskManagementUseCases.getIncompleteTasksForUser(userId).fold(
                    onSuccess = { tasks ->
                        _incompleteTasks.value = tasks
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to load incomplete tasks: ${error.message}",
                        )
                    },
                )

                // Load completed tasks
                taskManagementUseCases.getCompletedTasksForUser(userId).fold(
                    onSuccess = { tasks ->
                        _completedTasks.value = tasks
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(error = "Failed to load completed tasks: ${error.message}")
                    },
                )

                // Load task statistics
                taskManagementUseCases.getTaskStats(userId).fold(
                    onSuccess = { stats ->
                        _taskStats.value = stats
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(error = "Failed to load task stats: ${error.message}")
                    },
                )

                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Creates a new task.
     */
    fun createTask(title: String, category: TaskCategory) {
        val userId = childUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                taskManagementUseCases.createTask(title, category, userId).fold(
                    onSuccess = {
                        loadTasksForUser(userId) // Refresh the task list
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Task created successfully",
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to create task: ${error.message}",
                        )
                    },
                )
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Updates an existing task.
     */
    fun updateTask(taskId: String, title: String, category: TaskCategory) {
        val userId = childUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                taskManagementUseCases.updateTask(taskId, title, category).fold(
                    onSuccess = {
                        loadTasksForUser(userId) // Refresh the task list
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Task updated successfully",
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to update task: ${error.message}",
                        )
                    },
                )
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Deletes a task.
     */
    fun deleteTask(taskId: String) {
        val userId = childUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                taskManagementUseCases.deleteTask(taskId).fold(
                    onSuccess = {
                        loadTasksForUser(userId) // Refresh the task list
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = "Task deleted successfully",
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to delete task: ${error.message}",
                        )
                    },
                )
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Completes a task (for child users).
     */
    fun completeTask(taskId: String) {
        val userId = childUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                completeTaskUseCase(taskId).fold(
                    onSuccess = { result ->
                        loadTasksForUser(userId) // Refresh the task list
                        val message = buildString {
                            append("Task completed! ")
                            append("Earned ${result.tokensAwarded} tokens. ")
                            if (result.newlyUnlockedAchievements.isNotEmpty()) {
                                append(
                                    "Unlocked ${result.newlyUnlockedAchievements.size} achievement(s)!",
                                )
                            }
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = message,
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to complete task: ${error.message}",
                        )
                    },
                )
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Undoes a task completion (supports both child and caregiver users).
     * This allows children to undo their own tasks or caregivers to undo any task.
     */
    fun undoTask(taskId: String) {
        val userId = childUserId ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                undoTaskUseCase(taskId).fold(
                    onSuccess = { result ->
                        loadTasksForUser(userId) // Refresh the task list
                        val message = buildString {
                            append("Task undone! ")
                            append("${result.tokensDeducted} tokens were deducted. ")
                            append("New balance: ${result.newTokenBalance} tokens.")

                            // Add role-specific context
                            when (result.undoneByRole) {
                                com.arthurslife.app.domain.user.UserRole.CAREGIVER -> {
                                    append(" (Administrative override)")
                                }
                                com.arthurslife.app.domain.user.UserRole.CHILD -> {
                                    // No additional context for child users
                                }
                            }
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            successMessage = message,
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to undo task: ${error.message}",
                        )
                    },
                )
            } catch (e: DomainException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message,
                )
            }
        }
    }

    /**
     * Clears any error or success messages.
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }

    /**
     * Refreshes all task data.
     */
    fun refresh() {
        childUserId?.let { loadTasksForUser(it) }
    }
}

/**
 * UI state for task management screens.
 */
data class TaskManagementUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)
