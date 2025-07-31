package com.lemonqwest.app.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.lemonqwest.app.di.IoDispatcher
import com.lemonqwest.app.domain.achievement.Achievement
import com.lemonqwest.app.domain.achievement.usecase.AchievementTrackingUseCase
import com.lemonqwest.app.domain.auth.AuthenticationSessionService
import com.lemonqwest.app.domain.common.AchievementEventManager
import com.lemonqwest.app.domain.common.DomainException
import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.usecase.TaskStats
import com.lemonqwest.app.domain.user.TokenBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Dependencies container for TaskManagementViewModel to reduce parameter count.
 */
data class TaskManagementDependencies(
    val taskUseCases: com.lemonqwest.app.di.TaskUseCases,
    val achievementTrackingUseCase: AchievementTrackingUseCase,
    val authenticationSessionService: AuthenticationSessionService,
    val achievementEventManager: AchievementEventManager,
)

/**
 * ViewModel for task management operations in LemonQwest MVP.
 *
 * This ViewModel handles the state and business logic for caregiver task management,
 * including creating, editing, deleting tasks, and viewing task statistics.
 * It also handles task completion for child users.
 */
@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val dependencies: TaskManagementDependencies,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
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

    // Current user token balance
    private val _currentTokenBalance = MutableStateFlow<TokenBalance?>(null)
    val currentTokenBalance: StateFlow<TokenBalance?> = _currentTokenBalance.asStateFlow()

    // Daily progress (0.0 to 1.0)
    private val _dailyProgress = MutableStateFlow(0.0f)
    val dailyProgress: StateFlow<Float> = _dailyProgress.asStateFlow()

    // Recent achievements (for new badges display)
    private val _recentAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val recentAchievements: StateFlow<List<Achievement>> = _recentAchievements.asStateFlow()

    // Current authenticated user ID
    private var currentUserId: String? = null

    private var isInitialized = false

    /**
     * Initialize the task management state. This should be called explicitly in tests,
     * but will be called automatically in production when needed.
     */
    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        // Use IO dispatcher for initialization to avoid Main dispatcher issues in tests
        CoroutineScope(ioDispatcher).launch {
            loadCurrentUser()
        }
    }

    /**
     * Refreshes the current user data and tasks.
     */
    fun refreshCurrentUser() {
        initialize() // Ensure initialization happens in production
        scope.launch {
            loadCurrentUser()
        }
    }

    /**
     * Loads the current authenticated user and their tasks.
     */
    private suspend fun loadCurrentUser() {
        try {
            val currentUser = dependencies.authenticationSessionService.getCurrentUser()
            currentUserId = currentUser?.id
            _currentTokenBalance.value = currentUser?.tokenBalance
            currentUser?.let { loadTasksForUser(it.id) }
        } catch (e: DomainException) {
            _uiState.value = _uiState.value.copy(error = e.message)
        }
    }

    /**
     * Refreshes the current user's token balance from the authentication service.
     * @return true if refresh was successful, false otherwise
     */
    private suspend fun refreshCurrentUserTokenBalance(): Boolean {
        return try {
            val currentUser = dependencies.authenticationSessionService.getCurrentUser()
            _currentTokenBalance.value = currentUser?.tokenBalance
            true
        } catch (e: DomainException) {
            println("TaskManagementVM: Failed to refresh token balance - ${e.message}")
            // Show a warning message to the user
            _uiState.value = _uiState.value.copy(
                error = "Token balance may not be up to date. Please refresh the app if needed.",
            )
            false
        }
    }

    /**
     * Loads all tasks for the specified user.
     */
    private suspend fun loadTasksForUser(userId: String) {
        try {
            _uiState.value = _uiState.value.copy(isLoading = true)

            loadUserTasks(userId)
            loadTaskStatistics(userId)
            loadDailyProgress(userId)
            loadRecentAchievements(userId)

            _uiState.value = _uiState.value.copy(isLoading = false)
        } catch (e: DomainException) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message,
            )
        }
    }

    private suspend fun loadUserTasks(userId: String) {
        // Load all tasks
        dependencies.taskUseCases.taskManagementUseCases.getTasksForUser(userId).fold(
            onSuccess = { tasks ->
                _allTasks.value = tasks
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(error = "Failed to load tasks: ${error.message}")
            },
        )

        // Load incomplete tasks
        dependencies.taskUseCases.taskManagementUseCases.getIncompleteTasksForUser(userId).fold(
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
        dependencies.taskUseCases.taskManagementUseCases.getCompletedTasksForUser(userId).fold(
            onSuccess = { tasks ->
                _completedTasks.value = tasks
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(error = "Failed to load completed tasks: ${error.message}")
            },
        )
    }

    private suspend fun loadTaskStatistics(userId: String) {
        dependencies.taskUseCases.taskManagementUseCases.getTaskStats(userId).fold(
            onSuccess = { stats ->
                _taskStats.value = stats
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(error = "Failed to load task stats: ${error.message}")
            },
        )
    }

    private suspend fun loadDailyProgress(userId: String) {
        try {
            val progress = dependencies.taskUseCases.calculateDailyProgressUseCase(userId)
            _dailyProgress.value = progress
        } catch (e: IllegalArgumentException) {
            // Daily progress calculation failed, keep previous value or default to 0
            println("TaskManagementViewModel: Daily progress calculation failed - ${e.message}")
            _dailyProgress.value = 0.0f
        } catch (e: IllegalStateException) {
            // Daily progress calculation failed, keep previous value or default to 0
            println("TaskManagementViewModel: Daily progress calculation failed - ${e.message}")
            _dailyProgress.value = 0.0f
        }
    }

    private suspend fun loadRecentAchievements(userId: String) {
        try {
            val recentAchievements = dependencies.achievementTrackingUseCase.getUnlockedAchievements(
                userId,
            )
                .filter { it.unlockedAt != null && it.unlockedAt > (System.currentTimeMillis() - RECENT_ACHIEVEMENT_WINDOW) }
            _recentAchievements.value = recentAchievements
        } catch (e: IllegalArgumentException) {
            // Recent achievements loading failed, set to empty
            println("TaskManagementViewModel: Recent achievements loading failed - ${e.message}")
            _recentAchievements.value = emptyList()
        } catch (e: IllegalStateException) {
            // Recent achievements loading failed, set to empty
            println("TaskManagementViewModel: Recent achievements loading failed - ${e.message}")
            _recentAchievements.value = emptyList()
        }
    }

    /**
     * Creates a new task.
     */
    fun createTask(title: String, category: TaskCategory) {
        initialize() // Ensure initialization happens in production
        val userId = currentUserId ?: return

        scope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                dependencies.taskUseCases.taskManagementUseCases.createTask(
                    title,
                    category,
                    userId,
                ).fold(
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
        initialize() // Ensure initialization happens in production
        val userId = currentUserId ?: return

        scope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                dependencies.taskUseCases.taskManagementUseCases.updateTask(
                    taskId,
                    title,
                    category,
                ).fold(
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
        initialize() // Ensure initialization happens in production
        val userId = currentUserId ?: return

        scope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                dependencies.taskUseCases.taskManagementUseCases.deleteTask(taskId).fold(
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
        initialize() // Ensure initialization happens in production
        val userId = currentUserId ?: return

        scope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                dependencies.taskUseCases.completeTaskUseCase(taskId).fold(
                    onSuccess = { result ->
                        // Update recent achievements if any new ones were unlocked
                        if (result.newlyUnlockedAchievements.isNotEmpty()) {
                            val currentRecent = _recentAchievements.value.toMutableList()
                            currentRecent.addAll(result.newlyUnlockedAchievements)
                            _recentAchievements.value = currentRecent

                            // Emit achievement update event for real-time updates
                            dependencies.achievementEventManager.emitAchievementUpdate(
                                userId = userId,
                                newlyUnlockedAchievements = result.newlyUnlockedAchievements,
                            )
                        } else {
                            // Even if no new achievements were unlocked, emit an update
                            // to refresh progress for existing achievements
                            dependencies.achievementEventManager.emitAchievementUpdate(userId)
                        }

                        loadTasksForUser(userId) // Refresh the task list
                        val tokenRefreshSuccess = refreshCurrentUserTokenBalance() // Refresh the token balance
                        val message = buildString {
                            append("Task completed! ")
                            append("Earned ${result.tokensAwarded} tokens. ")
                            if (!tokenRefreshSuccess) {
                                append("(Token balance may need manual refresh) ")
                            }
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
        initialize() // Ensure initialization happens in production
        val userId = currentUserId ?: return

        scope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                dependencies.taskUseCases.undoTaskUseCase(taskId).fold(
                    onSuccess = { result ->
                        loadTasksForUser(userId) // Refresh the task list
                        val tokenRefreshSuccess = refreshCurrentUserTokenBalance() // Refresh the token balance
                        val message = buildString {
                            append("Task undone! ")
                            append("${result.tokensDeducted} tokens were deducted. ")
                            if (!tokenRefreshSuccess) {
                                append("(Token balance may need manual refresh) ")
                            }
                            append("New balance: ${result.newTokenBalance} tokens.")

                            // Add role-specific context
                            when (result.undoneByRole) {
                                com.lemonqwest.app.domain.user.UserRole.CAREGIVER -> {
                                    append(" (Administrative override)")
                                }
                                com.lemonqwest.app.domain.user.UserRole.CHILD -> {
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
        initialize() // Ensure initialization happens in production
        scope.launch {
            currentUserId?.let { loadTasksForUser(it) }
        }
    }

    companion object {
        private const val RECENT_ACHIEVEMENT_WINDOW = 24 * 60 * 60 * 1000L // 24 hours in milliseconds
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
