package com.arthurslife.app.presentation.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.CARD_ELEVATION
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.CARD_PADDING
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.CONTENT_PADDING
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.DISABLED_CONTAINER_ALPHA
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.ELEVATED_CARD_ELEVATION
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.MEDIUM_SPACING
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.SECONDARY_TEXT_ALPHA
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.SMALL_ICON_SIZE
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.SMALL_SPACING
import com.arthurslife.app.presentation.theme.components.TaskComponentConstants.STATS_PADDING

/**
 * Theme-aware card component displaying task completion statistics.
 */
@Composable
fun ThemeAwareTaskStatsCard(
    currentTheme: BaseAppTheme,
    stats: TaskStats,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.primaryContainer,
        ),
        shape = currentTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(CONTENT_PADDING),
        ) {
            Text(
                text = "Task Statistics",
                style = currentTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onPrimaryContainer,
            )

            themeAwareStatsRow(currentTheme, stats)
            themeAwareCompletionProgress(currentTheme, stats)
        }
    }
}

/**
 * Theme-aware row displaying task statistics.
 */
@Composable
private fun themeAwareStatsRow(
    currentTheme: BaseAppTheme,
    stats: TaskStats,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ThemeAwareStatItem(
            currentTheme = currentTheme,
            label = "Completed",
            value = stats.totalCompletedTasks.toString(),
        )
        ThemeAwareStatItem(
            currentTheme = currentTheme,
            label = "Tokens Earned",
            value = stats.totalTokensEarned.toString(),
        )
        ThemeAwareStatItem(
            currentTheme = currentTheme,
            label = "Incomplete",
            value = stats.incompleteTasks.toString(),
        )
    }
}

/**
 * Theme-aware completion progress section.
 */
@Composable
private fun themeAwareCompletionProgress(
    currentTheme: BaseAppTheme,
    stats: TaskStats,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Completion Rate",
                style = currentTheme.typography.bodyMedium,
                color = currentTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "${stats.completionRate}%",
                style = currentTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = currentTheme.colorScheme.onPrimaryContainer,
            )
        }
        LinearProgressIndicator(
            progress = { stats.completionRate / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = currentTheme.colorScheme.primary,
            trackColor = currentTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * Theme-aware individual statistic item component.
 */
@Composable
private fun ThemeAwareStatItem(
    currentTheme: BaseAppTheme,
    label: String,
    value: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = currentTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = currentTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = currentTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onPrimaryContainer.copy(alpha = SECONDARY_TEXT_ALPHA),
        )
    }
}

/**
 * Theme-aware card component for displaying individual tasks.
 */
@Composable
fun ThemeAwareTaskCard(
    currentTheme: BaseAppTheme,
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUndo: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                currentTheme.colorScheme.secondaryContainer
            } else {
                currentTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION),
        shape = currentTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaskCardContent(
                currentTheme = currentTheme,
                task = task,
                modifier = Modifier.weight(1f),
            )

            TaskCardActions(
                currentTheme = currentTheme,
                task = task,
                onEdit = onEdit,
                onDelete = onDelete,
                onUndo = onUndo,
            )
        }
    }
}

/**
 * Content section of the task card showing task details.
 */
@Composable
private fun TaskCardContent(
    currentTheme: BaseAppTheme,
    task: Task,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        Text(
            text = task.title,
            style = currentTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = task.category.displayName,
            style = currentTheme.typography.bodySmall,
            color = currentTheme.colorScheme.primary,
        )
        TaskCardMetadata(
            currentTheme = currentTheme,
            task = task,
        )
    }
}

/**
 * Metadata section showing token reward and completion status.
 */
@Composable
private fun TaskCardMetadata(
    currentTheme: BaseAppTheme,
    task: Task,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MEDIUM_SPACING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TokenRewardDisplay(
            currentTheme = currentTheme,
            tokenReward = task.tokenReward,
        )
        if (task.isCompleted) {
            CompletionBadge(currentTheme = currentTheme)
        }
    }
}

/**
 * Display component for token reward.
 */
@Composable
private fun TokenRewardDisplay(
    currentTheme: BaseAppTheme,
    tokenReward: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        ThemeAwareIcon(
            semanticType = SemanticIconType.TOKEN,
            theme = currentTheme,
            modifier = Modifier.size(SMALL_ICON_SIZE),
            contentDescription = null,
        )
        Text(
            text = tokenReward.toString(),
            style = currentTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Completion status badge.
 */
@Composable
private fun CompletionBadge(
    currentTheme: BaseAppTheme,
) {
    Text(
        text = "✓ Completed",
        style = currentTheme.typography.bodySmall,
        color = currentTheme.colorScheme.tertiary,
        fontWeight = FontWeight.Medium,
    )
}

/**
 * Action buttons for edit, delete, and undo.
 */
@Composable
private fun TaskCardActions(
    currentTheme: BaseAppTheme,
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUndo: (() -> Unit)? = null,
) {
    Row {
        // Show undo button for completed tasks if callback is provided
        if (task.isCompleted && onUndo != null) {
            IconButton(onClick = onUndo) {
                ThemeAwareIcon(
                    semanticType = SemanticIconType.UNDO,
                    theme = currentTheme,
                    contentDescription = "Undo Task Completion",
                    tint = currentTheme.colorScheme.primary,
                )
            }
        }

        IconButton(onClick = onEdit) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.EDIT,
                theme = currentTheme,
                contentDescription = "Edit Task",
            )
        }
        IconButton(onClick = onDelete) {
            ThemeAwareIcon(
                semanticType = SemanticIconType.DELETE,
                theme = currentTheme,
                contentDescription = "Delete Task",
                tint = currentTheme.colorScheme.error,
            )
        }
    }
}

/**
 * Theme-aware card for tasks that can be completed by children.
 */
@Composable
fun ThemeAwareChildTaskCard(
    currentTheme: BaseAppTheme,
    task: Task,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = ELEVATED_CARD_ELEVATION),
        shape = currentTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(CARD_PADDING),
            verticalArrangement = Arrangement.spacedBy(CONTENT_PADDING),
        ) {
            ChildTaskTitle(
                currentTheme = currentTheme,
                title = task.title,
            )

            ChildTaskDetails(
                currentTheme = currentTheme,
                task = task,
                onComplete = onComplete,
            )
        }
    }
}

/**
 * Title section for child task card.
 */
@Composable
private fun ChildTaskTitle(
    currentTheme: BaseAppTheme,
    title: String,
) {
    Text(
        text = title,
        style = currentTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

/**
 * Details and action section for child task card.
 */
@Composable
private fun ChildTaskDetails(
    currentTheme: BaseAppTheme,
    task: Task,
    onComplete: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChildTaskInfo(
            currentTheme = currentTheme,
            task = task,
        )

        ThemeAwareActionButtons(
            currentTheme = currentTheme,
            primaryText = "Complete Task",
            onPrimaryClick = onComplete,
        )
    }
}

/**
 * Information section showing category and reward.
 */
@Composable
private fun ChildTaskInfo(
    currentTheme: BaseAppTheme,
    task: Task,
) {
    Column {
        Text(
            text = task.category.displayName,
            style = currentTheme.typography.bodyMedium,
            color = currentTheme.colorScheme.primary,
        )
        ChildTaskReward(
            currentTheme = currentTheme,
            tokenReward = task.tokenReward,
        )
    }
}

/**
 * Reward display for child task.
 */
@Composable
private fun ChildTaskReward(
    currentTheme: BaseAppTheme,
    tokenReward: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        Text(
            text = "Reward:",
            style = currentTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onSurfaceVariant,
        )
        ThemeAwareIcon(
            semanticType = SemanticIconType.TOKEN,
            theme = currentTheme,
            modifier = Modifier.size(SMALL_ICON_SIZE),
            contentDescription = null,
        )
        Text(
            text = tokenReward.toString(),
            style = currentTheme.typography.bodySmall,
            color = currentTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Theme-aware card for completed tasks with undo functionality.
 */
@Composable
fun ThemeAwareCompletedTaskCard(
    currentTheme: BaseAppTheme,
    task: Task,
    onUndo: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.secondaryContainer.copy(
                alpha = DISABLED_CONTAINER_ALPHA,
            ),
        ),
        shape = currentTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CARD_PADDING),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            completedTaskContent(
                task = task,
                currentTheme = currentTheme,
                modifier = Modifier.weight(1f),
            )

            completedTaskActions(
                onUndo = onUndo,
                currentTheme = currentTheme,
            )
        }
    }
}

/**
 * Content section for completed task card showing title and earned tokens.
 */
@Composable
private fun completedTaskContent(
    task: Task,
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = task.title,
            style = currentTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
        ) {
            Text(
                text = "Earned",
                style = currentTheme.typography.bodySmall,
                color = currentTheme.colorScheme.tertiary,
            )
            ThemeAwareIcon(
                semanticType = SemanticIconType.TOKEN,
                theme = currentTheme,
                modifier = Modifier.size(SMALL_ICON_SIZE),
                contentDescription = null,
            )
            Text(
                text = task.tokenReward.toString(),
                style = currentTheme.typography.bodySmall,
                color = currentTheme.colorScheme.tertiary,
            )
        }
    }
}

/**
 * Action buttons section for completed task card.
 */
@Composable
private fun completedTaskActions(
    onUndo: (() -> Unit)?,
    currentTheme: BaseAppTheme,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SMALL_SPACING),
    ) {
        onUndo?.let { undoCallback ->
            IconButton(
                onClick = undoCallback,
                modifier = Modifier.size(SMALL_ICON_SIZE + 8.dp),
            ) {
                ThemeAwareIcon(
                    semanticType = SemanticIconType.UNDO,
                    theme = currentTheme,
                    contentDescription = "Undo task completion",
                    tint = currentTheme.colorScheme.primary,
                    modifier = Modifier.size(SMALL_ICON_SIZE),
                )
            }
        }

        ThemeAwareIcon(
            semanticType = SemanticIconType.SUCCESS,
            theme = currentTheme,
            contentDescription = "Completed",
            tint = currentTheme.colorScheme.tertiary,
        )
    }
}

/**
 * Theme-aware card showing the child's token balance and progress.
 */
@Composable
fun ThemeAwareTokenBalanceCard(
    currentTheme: BaseAppTheme,
    tokensEarned: Int,
    completedTasks: Int,
    currentTokenBalance: Int = 0,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.secondaryContainer,
        ),
        shape = currentTheme.shapes.medium,
    ) {
        tokenBalanceCardContent(
            currentTheme = currentTheme,
            tokensEarned = tokensEarned,
            completedTasks = completedTasks,
            currentTokenBalance = currentTokenBalance,
        )
    }
}

/**
 * Content section for the token balance card with three statistics.
 */
@Composable
private fun tokenBalanceCardContent(
    currentTheme: BaseAppTheme,
    tokensEarned: Int,
    completedTasks: Int,
    currentTokenBalance: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(STATS_PADDING),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tokenBalanceStatColumn(
            value = currentTokenBalance,
            label = if (currentTheme.taskLabel == "Quests") "Coins" else "Token Balance",
            valueColor = currentTheme.colorScheme.secondary,
            labelColor = currentTheme.colorScheme.onSecondaryContainer,
            currentTheme = currentTheme,
        )

        tokenBalanceStatColumn(
            value = tokensEarned,
            label = if (currentTheme.taskLabel == "Quests") "Coins Earned" else "Tokens Earned",
            valueColor = currentTheme.colorScheme.primary,
            labelColor = currentTheme.colorScheme.onSecondaryContainer,
            currentTheme = currentTheme,
        )

        tokenBalanceStatColumn(
            value = completedTasks,
            label = if (currentTheme.taskLabel == "Quests") "Quests Done" else "Tasks Done",
            valueColor = currentTheme.colorScheme.tertiary,
            labelColor = currentTheme.colorScheme.onSecondaryContainer,
            currentTheme = currentTheme,
        )
    }
}

/**
 * Individual statistic column for the token balance card.
 */
@Composable
private fun tokenBalanceStatColumn(
    value: Int,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color,
    labelColor: androidx.compose.ui.graphics.Color,
    currentTheme: BaseAppTheme,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value.toString(),
            style = currentTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
        Text(
            text = label,
            style = currentTheme.typography.bodyMedium,
            color = labelColor,
        )
    }
}
