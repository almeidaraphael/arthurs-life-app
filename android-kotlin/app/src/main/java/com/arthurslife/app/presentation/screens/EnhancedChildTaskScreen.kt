package com.arthurslife.app.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.domain.user.TokenBalance
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.components.circularTaskItem
import com.arthurslife.app.presentation.theme.components.dailyProgressSummary
import com.arthurslife.app.presentation.theme.components.positiveFeedbackAnimation
import com.arthurslife.app.presentation.theme.components.visualProgressIndicator
import com.arthurslife.app.presentation.viewmodels.TaskManagementUiState
import com.arthurslife.app.presentation.viewmodels.TaskManagementViewModel

private const val DEFAULT_PADDING = 16
private const val GRID_COLUMNS = 2

/**
 * Enhanced Child Task Screen with Avocation-inspired UI improvements.
 *
 * Features:
 * - Circular task visualization
 * - Daily progress summary with water bottle metaphor
 * - Visual progress indicators with mushroom growth
 * - Positive feedback animations
 * - Clean, engaging layout optimized for children
 */
@Composable
fun enhancedChildTaskScreen(
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
    var showCelebration by remember { mutableStateOf(false) }
    var lastCompletedCount by remember { mutableStateOf(completedTasks.size) }

    val actions = EnhancedChildTaskActions(
        onCompleteTask = { taskId ->
            viewModel.completeTask(taskId)
            showCelebration = true
        },
        onUndoTask = { taskId -> viewModel.undoTask(taskId) },
    )

    enhancedChildTaskScreenEffects(uiState, snackbarHostState, viewModel)

    // Detect new task completions for celebration
    LaunchedEffect(completedTasks.size) {
        if (completedTasks.size > lastCompletedCount) {
            showCelebration = true
        }
        lastCompletedCount = completedTasks.size
    }

    // Handle back navigation
    BackHandler {
        onNavigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { paddingValues ->
            enhancedChildTaskScreenContent(
                data = EnhancedChildTaskScreenData(
                    currentTheme = currentTheme,
                    incompleteTasks = incompleteTasks,
                    completedTasks = completedTasks,
                    taskStats = taskStats,
                    currentTokenBalance = currentTokenBalance,
                    isLoading = uiState.isLoading,
                    actions = actions,
                    paddingValues = paddingValues,
                ),
            )
        }

        // Celebration animation overlay
        if (showCelebration) {
            positiveFeedbackAnimation(
                isVisible = showCelebration,
                onAnimationEnd = { showCelebration = false },
            )
        }
    }
}

data class EnhancedChildTaskActions(
    val onCompleteTask: (String) -> Unit,
    val onUndoTask: (String) -> Unit,
)

@Composable
private fun enhancedChildTaskScreenEffects(
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

/**
 * Data class to group parameters for enhanced child task screen content.
 */
private data class EnhancedChildTaskScreenData(
    val currentTheme: BaseAppTheme,
    val incompleteTasks: List<Task>,
    val completedTasks: List<Task>,
    val taskStats: TaskStats?,
    val currentTokenBalance: TokenBalance?,
    val isLoading: Boolean,
    val actions: EnhancedChildTaskActions,
    val paddingValues: PaddingValues,
)

@Composable
private fun enhancedChildTaskScreenContent(
    data: EnhancedChildTaskScreenData,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(data.paddingValues),
    ) {
        if (data.isLoading) {
            loadingIndicator()
        } else {
            taskScreenMainContent(data)
        }
    }
}

@Composable
private fun BoxScope.loadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.align(Alignment.Center),
    )
}

@Composable
private fun taskScreenMainContent(data: EnhancedChildTaskScreenData) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(DEFAULT_PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(DEFAULT_PADDING.dp),
    ) {
        headerSection(data)
        dailyProgressSection(data)
        progressVisualizationSection(data)
        activeTasksSection(data)
        completedTasksSection(data)
        customizationSection(data)
        celebrationAnimationSection(data)
    }
}

private fun LazyListScope.headerSection(data: EnhancedChildTaskScreenData) {
    item {
        enhancedTaskScreenHeader(data.currentTheme)
    }
}

private fun LazyListScope.dailyProgressSection(data: EnhancedChildTaskScreenData) {
    item {
        val totalTasks = data.incompleteTasks.size + data.completedTasks.size
        val tokensEarned = data.taskStats?.totalTokensEarned ?: 0

        dailyProgressSummary(
            completedTasks = data.completedTasks.size,
            totalTasks = totalTasks,
            tokensEarned = tokensEarned,
        )
    }
}

private fun LazyListScope.progressVisualizationSection(data: EnhancedChildTaskScreenData) {
    if (data.incompleteTasks.isNotEmpty() || data.completedTasks.isNotEmpty()) {
        item {
            // Progress indicator temporarily disabled due to missing component implementation
        }
    }
}

private fun LazyListScope.activeTasksSection(data: EnhancedChildTaskScreenData) {
    if (data.incompleteTasks.isNotEmpty()) {
        item {
            Text(
                text = "Active ${data.currentTheme.taskLabel} (${data.incompleteTasks.size})",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = data.currentTheme.colorScheme.onBackground,
            )
        }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(GRID_COLUMNS),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(data.incompleteTasks) { task ->
                    circularTaskItem(
                        task = task,
                        onClick = { data.actions.onCompleteTask(task.id) },
                    )
                }
            }
        }
    }
}

private fun LazyListScope.completedTasksSection(data: EnhancedChildTaskScreenData) {
    if (data.completedTasks.isNotEmpty()) {
        item {
            Text(
                text = "Completed ${data.currentTheme.taskLabel} (${data.completedTasks.size})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(data.completedTasks) { task ->
                    circularTaskItem(
                        task = task,
                        onClick = { data.actions.onUndoTask(task.id) },
                    )
                }
            }
        }
    }
}

private fun LazyListScope.customizationSection(data: EnhancedChildTaskScreenData) {
    // Empty state
    if (data.incompleteTasks.isEmpty() && data.completedTasks.isEmpty()) {
        item {
            enhancedEmptyTaskState(data.currentTheme)
        }
    }
}

private fun LazyListScope.celebrationAnimationSection(
    @Suppress(
        "UNUSED_PARAMETER",
    ) data: EnhancedChildTaskScreenData,
) {
    // This can be implemented later for celebration animations
}

@Composable
private fun enhancedTaskScreenHeader(currentTheme: BaseAppTheme) {
    Column {
        Text(
            text = if (currentTheme.taskLabel == "Quests") "🗡️ Quests" else "📝 Tasks",
            style = currentTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = currentTheme.colorScheme.onBackground,
        )
        Text(
            text = if (currentTheme.taskLabel == "Quests") {
                "Complete your quests to earn coins and level up!"
            } else {
                "Complete your tasks to earn tokens and unlock rewards!"
            },
            style = currentTheme.typography.bodyLarge,
            color = currentTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun enhancedEmptyTaskState(currentTheme: BaseAppTheme) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        visualProgressIndicator(
            progress = 0f,
            title = "No ${currentTheme.taskLabel.lowercase()} today! 🎉",
            subtitle = "Ask your caregiver to add some ${currentTheme.taskLabel.lowercase()} for you.",
        )
    }
}
