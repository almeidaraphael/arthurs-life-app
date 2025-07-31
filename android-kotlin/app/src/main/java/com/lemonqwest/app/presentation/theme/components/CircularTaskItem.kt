package com.lemonqwest.app.presentation.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.lemonqwest.app.domain.task.Task

private const val CIRCLE_SIZE = 120
private const val STROKE_WIDTH = 8
private const val ICON_SIZE = 32
private const val MAX_TITLE_LENGTH = 15
private const val ANIMATION_DAMPING_RATIO = 0.8f
private const val CIRCLE_SIZE_OFFSET = 20
private const val COMPLETED_TASK_ALPHA = 0.1f

/**
 * Data class for task visual properties.
 */
private data class TaskVisuals(
    val taskColor: Color,
    val progress: Float,
    val strokeWidthPx: Float,
)

/**
 * Calculates visual properties for the task item.
 */
// ...existing code...

/**
 * Circular task item component inspired by Avocation's design.
 *
 * Displays a task in a circular format with:
 * - Animated progress ring indicating completion
 * - Custom or category-default icon
 * - Custom or category-default color
 * - Task title with overflow handling
 * - Theme-aware styling
 *
 * @param task Task domain object to display
 * @param onClick Callback when the task item is clicked
 * @param modifier Modifier for styling
 */
@Composable
@Suppress("UnusedParameter")
fun CircularTaskItem(
    task: Task,
    onClick: () -> Unit, // For future click handling
    modifier: Modifier = Modifier, // For future styling
) {
    val taskVisuals = calculateTaskVisuals(task)

    Box(
        modifier = Modifier
            .size((CIRCLE_SIZE - CIRCLE_SIZE_OFFSET).dp)
            .clip(CircleShape)
            .background(
                if (task.isCompleted) {
                    taskVisuals.taskColor.copy(alpha = COMPLETED_TASK_ALPHA)
                } else {
                    MaterialTheme.colorScheme.surface
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        TaskProgressCanvas(taskVisuals)
        taskIcon(task, taskVisuals.taskColor)
    }
    TaskCircleWithProgress(task, taskVisuals)
    taskTitleText(task, taskVisuals.taskColor)
    // onClick and modifier are currently unused, but kept for future extensibility
}

/**
 * Calculates visual properties for the task item.
 */
@Composable
private fun calculateTaskVisuals(task: Task): TaskVisuals {
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { STROKE_WIDTH.dp.toPx() }

    // Parse custom color or use category default
    val taskColor = try {
        Color(task.getDisplayColor().toColorInt())
    } catch (e: IllegalArgumentException) {
        // Log the exception and use fallback color
        println("Invalid task color: ${task.getDisplayColor()}, error: ${e.message}")
        MaterialTheme.colorScheme.primary
    }

    // Animate completion progress
    val progress by animateFloatAsState(
        targetValue = if (task.isCompleted) 1f else 0f,
        animationSpec = spring(dampingRatio = ANIMATION_DAMPING_RATIO),
        label = "completion_progress",
    )

    return TaskVisuals(taskColor, progress, strokeWidthPx)
}

/**
 * Displays the circular progress ring and task icon.
 */
@Composable
private fun TaskCircleWithProgress(task: Task, visuals: TaskVisuals) {
    Box(
        modifier = Modifier
            .size((CIRCLE_SIZE - CIRCLE_SIZE_OFFSET).dp)
            .clip(CircleShape)
            .background(
                if (task.isCompleted) {
                    visuals.taskColor.copy(alpha = COMPLETED_TASK_ALPHA)
                } else {
                    MaterialTheme.colorScheme.surface
                },
// Magic number constants for UI
            ),
        contentAlignment = Alignment.Center,
    ) {
        TaskProgressCanvas(visuals)
        taskIcon(task, visuals.taskColor)
    }
}

/**
 * Draws the progress ring using Canvas.
 */
@Composable
private fun TaskProgressCanvas(visuals: TaskVisuals) {
    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val radius = (size.minDimension - visuals.strokeWidthPx) / 2
        val center = androidx.compose.ui.geometry.Offset(
            size.width / 2,
            size.height / 2,
        )

        // Background circle
        drawCircle(
            color = visuals.taskColor.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(width = visuals.strokeWidthPx),
        )

        // Progress arc
        if (visuals.progress > 0f) {
            drawArc(
                color = visuals.taskColor,
                startAngle = -90f,
                sweepAngle = 360f * visuals.progress,
                useCenter = false,
                style = Stroke(width = visuals.strokeWidthPx),
                topLeft = androidx.compose.ui.geometry.Offset(
                    center.x - radius,
                    center.y - radius,
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            )
        }
    }
}

/**
 * Displays the task icon.
 */
@Composable
private fun taskIcon(task: Task, taskColor: Color) {
    val iconResource = getIconResource(task.getDisplayIcon())
    Icon(
        painter = painterResource(id = iconResource),
        contentDescription = task.title,
        modifier = Modifier.size(ICON_SIZE.dp),
        tint = if (task.isCompleted) taskColor else MaterialTheme.colorScheme.onSurface,
    )
}

/**
 * Displays the task title with proper formatting.
 */
@Composable
private fun taskTitleText(task: Task, taskColor: Color) {
    Text(
        text = if (task.title.length > MAX_TITLE_LENGTH) {
            "${task.title.take(MAX_TITLE_LENGTH)}..."
        } else {
            task.title
        },
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (task.isCompleted) FontWeight.Bold else FontWeight.Normal,
            fontSize = 11.sp,
        ),
        color = if (task.isCompleted) taskColor else MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 4.dp),
    )
}

/**
 * Maps icon names to drawable resources.
 * Fallback to a default icon if the specific icon is not found.
 */
private fun getIconResource(iconName: String): Int {
    return when (iconName) {
        "person" -> android.R.drawable.ic_menu_my_calendar // Placeholder - replace with actual icons
        "home" -> android.R.drawable.ic_menu_mylocation
        "school" -> android.R.drawable.ic_menu_agenda
        else -> android.R.drawable.ic_menu_help
    }
}
