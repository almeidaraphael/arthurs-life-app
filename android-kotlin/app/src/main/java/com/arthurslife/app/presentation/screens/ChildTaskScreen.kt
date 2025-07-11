package com.arthurslife.app.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.components.ThemeAwareChildTaskCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareCompletedTaskCard
import com.arthurslife.app.presentation.theme.components.ThemeAwareTokenBalanceCard
import com.arthurslife.app.presentation.viewmodels.TaskManagementUiState
import com.arthurslife.app.presentation.viewmodels.TaskManagementViewModel

private const val DEFAULT_PADDING = 16

data class ChildTaskUiState(
    val currentTheme: BaseAppTheme,
    val incompleteTasks: List<Task>,
    val completedTasks: List<Task>,
    val taskStats: TaskStats?,
    val currentTokenBalance: TokenBalance?,
    val isLoading: Boolean,
)

data class ChildTaskActions(
    val onCompleteTask: (String) -> Unit,
    val onUndoTask: (String) -> Unit,
)

@Composable
fun ChildTaskScreen(
    currentTheme: BaseAppTheme,
    onNavigateBack: () -> Unit,
    viewModel: TaskManagementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val incompleteTasks by viewModel.incompleteTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val taskStats by viewModel.taskStats.collectAsState()
    val currentTokenBalance by viewModel.currentTokenBalance.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val childTaskUiState = ChildTaskUiState(
        currentTheme = currentTheme,
        incompleteTasks = incompleteTasks,
        completedTasks = completedTasks,
        taskStats = taskStats,
        currentTokenBalance = currentTokenBalance,
        isLoading = uiState.isLoading,
    )

    val actions = ChildTaskActions(
        onCompleteTask = { taskId -> viewModel.completeTask(taskId) },
        onUndoTask = { taskId -> viewModel.undoTask(taskId) },
    )

    childTaskScreenEffects(uiState, snackbarHostState, viewModel)

    // Handle back navigation
    BackHandler {
        onNavigateBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        childTaskScreenContent(
            uiState = childTaskUiState,
            actions = actions,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun childTaskScreenEffects(
    uiState: TaskManagementUiState,
    snackbarHostState: SnackbarHostState,
    viewModel: TaskManagementViewModel,
) {
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
}

@Composable
private fun childTaskScreenContent(
    uiState: ChildTaskUiState,
    actions: ChildTaskActions,
    paddingValues: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            childTaskList(
                uiState = uiState,
                actions = actions,
            )
        }
    }
}

@Composable
private fun childTaskList(
    uiState: ChildTaskUiState,
    actions: ChildTaskActions,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(DEFAULT_PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(DEFAULT_PADDING.dp),
    ) {
        taskScreenHeader(uiState.currentTheme)
        tokenBalanceSection(uiState.currentTheme, uiState.taskStats, uiState.currentTokenBalance)
        incompleteTasksSection(uiState, actions)
        completedTasksSection(uiState, actions)
        emptyStateSection(uiState)
    }
}

private fun LazyListScope.taskScreenHeader(currentTheme: BaseAppTheme) {
    item {
        Column {
            Text(
                text = if (currentTheme.taskLabel == "Quests") "Quests" else "Tasks",
                style = currentTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = currentTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (currentTheme.taskLabel == "Quests") {
                    "Complete your quests to earn coins and level up!"
                } else {
                    "Complete your tasks to earn tokens and unlock rewards!"
                },
                style = currentTheme.typography.bodyLarge,
                color = currentTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            )
        }
    }
}

private fun LazyListScope.tokenBalanceSection(
    currentTheme: BaseAppTheme,
    taskStats: TaskStats?,
    currentTokenBalance: TokenBalance?,
) {
    item {
        taskStats?.let { stats ->
            ThemeAwareTokenBalanceCard(
                currentTheme = currentTheme,
                tokensEarned = stats.totalTokensEarned,
                completedTasks = stats.totalCompletedTasks,
                currentTokenBalance = currentTokenBalance?.getValue() ?: 0,
            )
        }
    }
}

private fun LazyListScope.incompleteTasksSection(
    uiState: ChildTaskUiState,
    actions: ChildTaskActions,
) {
    if (uiState.incompleteTasks.isNotEmpty()) {
        item {
            Text(
                text = "Active ${uiState.currentTheme.taskLabel} (${uiState.incompleteTasks.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        items(items = uiState.incompleteTasks) { task ->
            ThemeAwareChildTaskCard(
                currentTheme = uiState.currentTheme,
                task = task,
                onComplete = { actions.onCompleteTask(task.id) },
            )
        }
    }
}

private fun LazyListScope.completedTasksSection(
    uiState: ChildTaskUiState,
    actions: ChildTaskActions,
) {
    if (uiState.completedTasks.isNotEmpty()) {
        item {
            Text(
                text = "Completed ${uiState.currentTheme.taskLabel} (${uiState.completedTasks.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        items(items = uiState.completedTasks) { task ->
            ThemeAwareCompletedTaskCard(
                currentTheme = uiState.currentTheme,
                task = task,
                onUndo = { actions.onUndoTask(task.id) },
            )
        }
    }
}

private fun LazyListScope.emptyStateSection(uiState: ChildTaskUiState) {
    if (uiState.incompleteTasks.isEmpty() && uiState.completedTasks.isEmpty()) {
        item {
            ThemeAwareEmptyTaskState(uiState.currentTheme)
        }
    }
}

@Composable
private fun ThemeAwareEmptyTaskState(currentTheme: BaseAppTheme) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = "No ${currentTheme.taskLabel.lowercase()} today!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Ask your caregiver to add some ${currentTheme.taskLabel.lowercase()} for you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
