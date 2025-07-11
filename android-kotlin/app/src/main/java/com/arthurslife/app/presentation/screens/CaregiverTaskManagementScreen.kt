package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.components.ThemeAwareCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareTaskCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareTaskStatsCard
import com.arthurslife.app.presentation.theme.components.themeAwareFloatingActionButton
import com.arthurslife.app.presentation.viewmodels.TaskManagementUiState
import com.arthurslife.app.presentation.viewmodels.TaskManagementViewModel

/**
 * Caregiver task management screen for Arthur's Life MVP.
 *
 * This screen allows caregivers to:
 * - View all tasks assigned to the child
 * - Create new tasks
 * - Edit existing tasks
 * - Delete tasks
 * - View task completion statistics
 *
 * The screen follows Material Design 3 principles and integrates with
 * the existing theme system.
 */
@Composable
fun CaregiverTaskManagementScreen(
    currentTheme: BaseAppTheme,
    viewModel: TaskManagementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val allTasks by viewModel.allTasks.collectAsState()
    val taskStats by viewModel.taskStats.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    HandleSnackbarMessages(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onClearMessages = viewModel::clearMessages,
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            TaskManagementFab(
                theme = currentTheme,
                onCreateTask = { showCreateDialog = true },
            )
        },
    ) { paddingValues ->
        TaskManagementContent(
            params = TaskManagementContentParams(
                currentTheme = currentTheme,
                uiState = uiState,
                allTasks = allTasks,
                taskStats = taskStats,
                paddingValues = paddingValues,
                onEditTask = { editingTask = it },
                onDeleteTask = viewModel::deleteTask,
                onUndoTask = viewModel::undoTask,
            ),
        )
    }

    TaskDialogs(
        params = TaskDialogsParams(
            showCreateDialog = showCreateDialog,
            editingTask = editingTask,
            onDismissCreateDialog = { showCreateDialog = false },
            onDismissEditDialog = { editingTask = null },
            onCreateTask = { title, category ->
                viewModel.createTask(title, category)
                showCreateDialog = false
            },
            onUpdateTask = { taskId, title, category ->
                viewModel.updateTask(taskId, title, category)
                editingTask = null
            },
        ),
        currentTheme = currentTheme,
    )
}

/**
 * Handles snackbar messages for the caregiver task management screen.
 */
@Composable
private fun HandleSnackbarMessages(
    uiState: TaskManagementUiState,
    snackbarHostState: SnackbarHostState,
    onClearMessages: () -> Unit,
) {
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }
}

/**
 * Floating action button for creating new tasks.
 */
@Composable
private fun TaskManagementFab(
    theme: BaseAppTheme,
    onCreateTask: () -> Unit,
) {
    themeAwareFloatingActionButton(
        onClick = onCreateTask,
        theme = theme,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add ${theme.taskLabel.dropLast(1)}",
            tint = theme.colorScheme.onPrimary,
        )
    }
}

/**
 * Parameters for TaskManagementContent composable.
 */
private data class TaskManagementContentParams(
    val currentTheme: BaseAppTheme,
    val uiState: TaskManagementUiState,
    val allTasks: List<Task>,
    val taskStats: TaskStats?,
    val paddingValues: PaddingValues,
    val onEditTask: (Task) -> Unit,
    val onDeleteTask: (String) -> Unit,
    val onUndoTask: (String) -> Unit,
)

/**
 * Main content area for the task management screen.
 */
@Composable
private fun TaskManagementContent(
    params: TaskManagementContentParams,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(params.paddingValues),
    ) {
        if (params.uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            TaskList(
                params = TaskListParams(
                    currentTheme = params.currentTheme,
                    allTasks = params.allTasks,
                    taskStats = params.taskStats,
                    onEditTask = params.onEditTask,
                    onDeleteTask = params.onDeleteTask,
                    onUndoTask = params.onUndoTask,
                ),
            )
        }
    }
}

/**
 * Parameters for TaskList composable.
 */
private data class TaskListParams(
    val currentTheme: BaseAppTheme,
    val allTasks: List<Task>,
    val taskStats: TaskStats?,
    val onEditTask: (Task) -> Unit,
    val onDeleteTask: (String) -> Unit,
    val onUndoTask: (String) -> Unit,
)

/**
 * Scrollable list of tasks with statistics.
 */
@Composable
private fun TaskList(
    params: TaskListParams,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Task Statistics Card
        item {
            params.taskStats?.let { stats ->
                ThemeAwareTaskStatsCard(
                    currentTheme = params.currentTheme,
                    stats = stats,
                )
            }
        }

        // Task List Header
        item {
            Text(
                text = "All ${params.currentTheme.taskLabel} (${params.allTasks.size})",
                style = params.currentTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = params.currentTheme.colorScheme.onBackground,
            )
        }

        // Task List
        if (params.allTasks.isEmpty()) {
            item {
                EmptyTasksCard(params.currentTheme)
            }
        } else {
            items(params.allTasks) { task ->
                ThemeAwareTaskCard(
                    currentTheme = params.currentTheme,
                    task = task,
                    onEdit = { params.onEditTask(task) },
                    onDelete = { params.onDeleteTask(task.id) },
                    onUndo = if (task.isCompleted) {
                        { params.onUndoTask(task.id) }
                    } else {
                        null
                    },
                )
            }
        }
    }
}

/**
 * Empty state card when no tasks exist.
 */
@Composable
private fun EmptyTasksCard(theme: BaseAppTheme) {
    ThemeAwareCard(
        theme = theme,
        modifier = Modifier.fillMaxWidth(),
        containerColor = theme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = "No ${theme.taskLabel.lowercase()} yet. Create your first ${theme.taskLabel.dropLast(
                1,
            ).lowercase()}!",
            modifier = Modifier.padding(24.dp),
            style = theme.typography.bodyLarge,
            color = theme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Parameters for TaskDialogs composable.
 */
private data class TaskDialogsParams(
    val showCreateDialog: Boolean,
    val editingTask: Task?,
    val onDismissCreateDialog: () -> Unit,
    val onDismissEditDialog: () -> Unit,
    val onCreateTask: (String, TaskCategory) -> Unit,
    val onUpdateTask: (String, String, TaskCategory) -> Unit,
)

/**
 * Task creation and editing dialogs.
 */
@Composable
private fun TaskDialogs(
    params: TaskDialogsParams,
    currentTheme: BaseAppTheme,
) {
    if (params.showCreateDialog) {
        themeAwareTaskCreateEditDialog(
            theme = currentTheme,
            task = null,
            onDismiss = params.onDismissCreateDialog,
            onSave = params.onCreateTask,
        )
    }

    params.editingTask?.let { task ->
        themeAwareTaskCreateEditDialog(
            theme = currentTheme,
            task = task,
            onDismiss = params.onDismissEditDialog,
            onSave = { title, category ->
                params.onUpdateTask(task.id, title, category)
            },
        )
    }
}
