package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.usecase.TaskStats
import com.arthurslife.app.presentation.theme.BaseAppTheme
import com.arthurslife.app.presentation.theme.components.ThemeAwareProgressIndicator
import com.arthurslife.app.presentation.theme.components.themeAwareAlertDialog
import com.arthurslife.app.presentation.theme.components.themeAwareButton
import com.arthurslife.app.presentation.theme.components.themeAwareDialogTextButton
import com.arthurslife.app.presentation.theme.components.themeAwareIconButton
import com.arthurslife.app.presentation.theme.components.themeAwareOutlinedTextField

/**
 * Card component displaying task completion statistics.
 */
@Composable
fun themeAwareTaskStatsCard(
    stats: TaskStats,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    com.arthurslife.app.presentation.theme.components.ThemeAwareCard(
        theme = theme,
        modifier = modifier.fillMaxWidth(),
        containerColor = theme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "${theme.taskLabel} Statistics",
                style = theme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onPrimaryContainer,
            )

            themeAwareTaskStatsRow(stats, theme)
            themeAwareTaskCompletionRate(stats, theme)
        }
    }
}

@Composable
private fun themeAwareTaskStatsRow(stats: TaskStats, theme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        themeAwareStatItem(
            label = "Completed",
            value = stats.totalCompletedTasks.toString(),
            theme = theme,
        )
        themeAwareStatItem(
            label = "${theme.tokenLabel} Earned",
            value = stats.totalTokensEarned.toString(),
            theme = theme,
        )
        themeAwareStatItem(
            label = "Incomplete",
            value = stats.incompleteTasks.toString(),
            theme = theme,
        )
    }
}

@Composable
private fun themeAwareTaskCompletionRate(stats: TaskStats, theme: BaseAppTheme) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Completion Rate",
                style = theme.typography.bodyMedium,
                color = theme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "${stats.completionRate}%",
                style = theme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = theme.colorScheme.onPrimaryContainer,
            )
        }
        ThemeAwareProgressIndicator(
            progress = stats.completionRate / 100f,
            theme = theme,
            modifier = Modifier.fillMaxWidth(),
            color = theme.colorScheme.primary,
        )
    }
}

/**
 * Individual statistic item component.
 */
@Composable
private fun themeAwareStatItem(
    label: String,
    value: String,
    theme: BaseAppTheme,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = theme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = theme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = theme.typography.bodySmall,
            color = theme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
        )
    }
}

/**
 * Card component for displaying individual tasks.
 */
@Composable
fun themeAwareTaskCard(
    task: Task,
    theme: BaseAppTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    com.arthurslife.app.presentation.theme.components.ThemeAwareCard(
        theme = theme,
        modifier = modifier.fillMaxWidth(),
        containerColor = if (task.isCompleted) {
            theme.colorScheme.secondaryContainer
        } else {
            theme.colorScheme.surface
        },
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            themeAwareTaskCardContent(
                task = task,
                theme = theme,
                modifier = Modifier.weight(1f),
            )
            themeAwareTaskCardActions(
                theme = theme,
                onEdit = onEdit,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun themeAwareTaskCardContent(
    task: Task,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = task.title,
            style = theme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = theme.colorScheme.onSurface,
        )
        Text(
            text = task.category.displayName,
            style = theme.typography.bodySmall,
            color = theme.colorScheme.primary,
        )
        themeAwareTaskCardMetrics(task, theme)
    }
}

@Composable
private fun themeAwareTaskCardMetrics(task: Task, theme: BaseAppTheme) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${task.tokenReward} ${theme.tokenLabel.lowercase()}",
            style = theme.typography.bodySmall,
            color = theme.colorScheme.onSurfaceVariant,
        )
        if (task.isCompleted) {
            Text(
                text = "✓ Completed",
                style = theme.typography.bodySmall,
                color = theme.colorScheme.tertiary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun themeAwareTaskCardActions(
    theme: BaseAppTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row {
        themeAwareIconButton(
            onClick = onEdit,
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${theme.taskLabel.dropLast(1)}",
                    tint = theme.colorScheme.primary,
                )
            },
        )
        themeAwareIconButton(
            onClick = onDelete,
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete ${theme.taskLabel.dropLast(1)}",
                    tint = theme.colorScheme.error,
                )
            },
        )
    }
}

/**
 * Dialog for creating or editing tasks.
 */
@Composable
fun themeAwareTaskCreateEditDialog(
    theme: BaseAppTheme,
    task: Task? = null,
    onDismiss: () -> Unit,
    onSave: (String, TaskCategory) -> Unit,
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var selectedCategory by remember {
        mutableStateOf(
            task?.category ?: TaskCategory.PERSONAL_CARE,
        )
    }

    val isEditing = task != null
    val dialogTitle = if (isEditing) {
        "Edit ${theme.taskLabel.dropLast(
            1,
        )}"
    } else {
        "Create New ${theme.taskLabel.dropLast(1)}"
    }

    themeAwareAlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            themeAwareTaskDialogConfirmButton(
                title = title,
                isEditing = isEditing,
                selectedCategory = selectedCategory,
                theme = theme,
                onSave = onSave,
            )
        },
        theme = theme,
        dismissButton = {
            themeAwareDialogTextButton(
                text = "Cancel",
                onClick = onDismiss,
                theme = theme,
            )
        },
        title = {
            Text(
                text = dialogTitle,
                style = theme.typography.titleLarge,
                color = theme.colorScheme.onSurface,
            )
        },
        text = {
            themeAwareTaskDialogContent(
                title = title,
                onTitleChange = { title = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                theme = theme,
            )
        },
    )
}

@Composable
private fun themeAwareTaskDialogContent(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedCategory: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
    theme: BaseAppTheme,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        themeAwareOutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            theme = theme,
            label = "${theme.taskLabel.dropLast(1)} Title",
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        themeAwareTaskCategoryDropdown(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
            theme = theme,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun themeAwareTaskCategoryDropdown(
    selectedCategory: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
    theme: BaseAppTheme,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        themeAwareOutlinedTextField(
            value = selectedCategory.displayName,
            onValueChange = { },
            theme = theme,
            label = "Category",
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            TaskCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = category.displayName,
                                color = theme.colorScheme.onSurface,
                            )
                            Text(
                                text = "${category.defaultTokenReward} ${theme.tokenLabel.lowercase()} - ${category.description}",
                                style = theme.typography.bodySmall,
                                color = theme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    onClick = {
                        onCategoryChange(category)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun themeAwareTaskDialogConfirmButton(
    title: String,
    isEditing: Boolean,
    selectedCategory: TaskCategory,
    theme: BaseAppTheme,
    onSave: (String, TaskCategory) -> Unit,
) {
    themeAwareButton(
        text = if (isEditing) "Update" else "Create",
        onClick = {
            if (title.isNotBlank()) {
                onSave(title.trim(), selectedCategory)
            }
        },
        theme = theme,
        enabled = title.isNotBlank(),
    )
}
