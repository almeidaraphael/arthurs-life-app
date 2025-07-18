package com.lemonqwest.app.presentation.screens

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
import com.lemonqwest.app.domain.task.Task
import com.lemonqwest.app.domain.task.TaskCategory
import com.lemonqwest.app.domain.task.usecase.TaskStats
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import com.lemonqwest.app.presentation.theme.components.ThemeAwareAlertDialog
import com.lemonqwest.app.presentation.theme.components.ThemeAwareButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareDialogTextButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareIconButton
import com.lemonqwest.app.presentation.theme.components.ThemeAwareOutlinedTextField
import com.lemonqwest.app.presentation.theme.components.ThemeAwareProgressIndicator

/**
 * Card component displaying task completion statistics.
 */
@Composable
fun ThemeAwareTaskStatsCard(
    stats: TaskStats,
    theme: BaseAppTheme,
    modifier: Modifier = Modifier,
) {
    com.lemonqwest.app.presentation.theme.components.ThemeAwareCard(
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

            ThemeAwareTaskStatsRow(stats, theme)
            ThemeAwareTaskCompletionRate(stats, theme)
        }
    }
}

@Composable
private fun ThemeAwareTaskStatsRow(stats: TaskStats, theme: BaseAppTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ThemeAwareStatItem(
            label = "Completed",
            value = stats.totalCompletedTasks.toString(),
            theme = theme,
        )
        ThemeAwareStatItem(
            label = "${theme.tokenLabel} Earned",
            value = stats.totalTokensEarned.toString(),
            theme = theme,
        )
        ThemeAwareStatItem(
            label = "Incomplete",
            value = stats.incompleteTasks.toString(),
            theme = theme,
        )
    }
}

@Composable
private fun ThemeAwareTaskCompletionRate(stats: TaskStats, theme: BaseAppTheme) {
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
private fun ThemeAwareStatItem(
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
fun ThemeAwareTaskCard(
    task: Task,
    theme: BaseAppTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    com.lemonqwest.app.presentation.theme.components.ThemeAwareCard(
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
            ThemeAwareTaskCardContent(
                task = task,
                theme = theme,
                modifier = Modifier.weight(1f),
            )
            ThemeAwareTaskCardActions(
                theme = theme,
                onEdit = onEdit,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun ThemeAwareTaskCardContent(
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
        ThemeAwareTaskCardMetrics(task, theme)
    }
}

@Composable
private fun ThemeAwareTaskCardMetrics(task: Task, theme: BaseAppTheme) {
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
private fun ThemeAwareTaskCardActions(
    theme: BaseAppTheme,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row {
        ThemeAwareIconButton(
            onClick = onEdit,
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit ${theme.taskLabel.dropLast(1)}",
                    tint = theme.colorScheme.primary,
                )
            },
        )
        ThemeAwareIconButton(
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
fun ThemeAwareTaskCreateEditDialog(
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

    ThemeAwareAlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            ThemeAwareTaskDialogConfirmButton(
                title = title,
                isEditing = isEditing,
                selectedCategory = selectedCategory,
                theme = theme,
                onSave = onSave,
            )
        },
        theme = theme,
        dismissButton = {
            ThemeAwareDialogTextButton(
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
            ThemeAwareTaskDialogContent(
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
private fun ThemeAwareTaskDialogContent(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedCategory: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
    theme: BaseAppTheme,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ThemeAwareOutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            theme = theme,
            label = "${theme.taskLabel.dropLast(1)} Title",
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        ThemeAwareTaskCategoryDropdown(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
            theme = theme,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeAwareTaskCategoryDropdown(
    selectedCategory: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
    theme: BaseAppTheme,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ThemeAwareOutlinedTextField(
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
private fun ThemeAwareTaskDialogConfirmButton(
    title: String,
    isEditing: Boolean,
    selectedCategory: TaskCategory,
    theme: BaseAppTheme,
    onSave: (String, TaskCategory) -> Unit,
) {
    ThemeAwareButton(
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
