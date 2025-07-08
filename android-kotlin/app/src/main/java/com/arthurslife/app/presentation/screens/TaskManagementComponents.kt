package com.arthurslife.app.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arthurslife.app.domain.task.Task
import com.arthurslife.app.domain.task.TaskCategory
import com.arthurslife.app.domain.task.usecase.TaskStats

/**
 * Card component displaying task completion statistics.
 */
@Composable
fun TaskStatsCard(
    stats: TaskStats,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Task Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            taskStatsRow(stats)
            taskCompletionRate(stats)
        }
    }
}

@Composable
private fun taskStatsRow(stats: TaskStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatItem(
            label = "Completed",
            value = stats.totalCompletedTasks.toString(),
        )
        StatItem(
            label = "Tokens Earned",
            value = stats.totalTokensEarned.toString(),
        )
        StatItem(
            label = "Incomplete",
            value = stats.incompleteTasks.toString(),
        )
    }
}

@Composable
private fun taskCompletionRate(stats: TaskStats) {
    // Completion Rate Progress Bar
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Completion Rate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "${stats.completionRate}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        LinearProgressIndicator(
            progress = { stats.completionRate / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * Individual statistic item component.
 */
@Composable
private fun StatItem(
    label: String,
    value: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
        )
    }
}

/**
 * Card component for displaying individual tasks.
 */
@Composable
fun TaskCard(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            taskCardContent(
                task = task,
                modifier = Modifier.weight(1f),
            )
            taskCardActions(onEdit, onDelete)
        }
    }
}

@Composable
private fun taskCardContent(task: Task, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = task.category.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        taskCardMetrics(task)
    }
}

@Composable
private fun taskCardMetrics(task: Task) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${task.tokenReward} tokens",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (task.isCompleted) {
            Text(
                text = "✓ Completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun taskCardActions(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row {
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Task",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

/**
 * Dialog for creating or editing tasks.
 */
@Composable
fun TaskCreateEditDialog(
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
    val dialogTitle = if (isEditing) "Edit Task" else "Create New Task"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            taskDialogContent(
                title = title,
                onTitleChange = { title = it },
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
            )
        },
        confirmButton = {
            taskDialogConfirmButton(
                title = title,
                isEditing = isEditing,
                selectedCategory = selectedCategory,
                onSave = onSave,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun taskDialogContent(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedCategory: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        taskCategoryDropdown(
            selectedCategory = selectedCategory,
            onCategoryChange = onCategoryChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun taskCategoryDropdown(
    selectedCategory: TaskCategory,
    onCategoryChange: (TaskCategory) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedCategory.displayName,
            onValueChange = { },
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            TaskCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(category.displayName)
                            Text(
                                text = "${category.defaultTokenReward} tokens - ${category.description}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun taskDialogConfirmButton(
    title: String,
    isEditing: Boolean,
    selectedCategory: TaskCategory,
    onSave: (String, TaskCategory) -> Unit,
) {
    Button(
        onClick = {
            if (title.isNotBlank()) {
                onSave(title.trim(), selectedCategory)
            }
        },
        enabled = title.isNotBlank(),
    ) {
        Text(if (isEditing) "Update" else "Create")
    }
}
