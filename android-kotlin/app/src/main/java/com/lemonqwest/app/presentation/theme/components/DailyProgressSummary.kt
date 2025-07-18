package com.lemonqwest.app.presentation.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private const val BOTTLE_WIDTH = 60
private const val BOTTLE_HEIGHT = 120
private const val BOTTLE_NECK_HEIGHT = 20
private const val CARD_ELEVATION = 4
private const val CONTAINER_PADDING = 16
private const val CONTENT_SPACING = 8
private const val DAMPENING_RATIO = 0.8f
private const val BOTTLE_STROKE_WIDTH = 3f

private const val PROGRESS_THRESHOLD_HIGH = 0.75f
private const val PROGRESS_THRESHOLD_MEDIUM = 0.5f
private const val ALPHA_HIGH = 0.8f
private const val ALPHA_MEDIUM = 0.6f
private const val ALPHA_LOW = 0.4f
private const val ALPHA_WATER_HIGH = 0.8f
private const val ALPHA_WATER_MEDIUM = 0.9f
private const val ALPHA_WATER_LOW = 0.7f
private const val ALPHA_CAP = 0.6f
private const val TOKEN_ICON_SPACING = 4
private const val TOKEN_ICON_SIZE = 16
private const val BOTTLE_BOTTOM_CURVE = 10
private const val BUBBLE_RADIUS = 2
private const val BUBBLE_OFFSET_1_X = -8
private const val BUBBLE_OFFSET_1_Y = 15
private const val BUBBLE_OFFSET_2_X = 6
private const val BUBBLE_OFFSET_2_Y = 25
private const val BUBBLE_OFFSET_3_X = -2
private const val BUBBLE_OFFSET_3_Y = 35
private const val CAP_STROKE_WIDTH = 6
private const val CAP_SIDE_EXTENSION = 2
private const val WATER_BOTTOM_OFFSET_DP = 10f
private const val WATER_SIDE_OFFSET_DP = 3f
private const val COLOR_GOLD = 0xFFFFD700
private const val COLOR_BOTTLE_GRAY = 0xFF90A4AE
private const val COLOR_WATER_GREEN = 0xFF4CAF50
private const val COLOR_WATER_BLUE = 0xFF2196F3
private const val COLOR_WATER_LIGHT_BLUE = 0xFF03A9F4
private const val COLOR_CAP_GRAY = 0xFF757575
private const val WATER_WAVE_AMPLITUDE_DP = 1f
private const val WAVE_QUARTER_DIVISOR = 4

/**
 * Daily progress summary component using water bottle metaphor.
 *
 * Displays daily task completion progress as water filling a bottle:
 * - Empty bottle: No tasks completed
 * - Partial fill: Some tasks completed
 * - Full bottle: All tasks completed
 * - Overflow sparkles: Bonus achievements
 *
 * @param completedTasks Number of completed tasks
 * @param totalTasks Total number of tasks for the day
 * @param tokensEarned Total tokens earned today
 * @param modifier Modifier for styling
 */
@Composable
fun DailyProgressSummary(
    completedTasks: Int,
    totalTasks: Int,
    tokensEarned: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = DAMPENING_RATIO),
        label = "water_fill",
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = CARD_ELEVATION.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        ProgressSummaryContent(
            animatedProgress = animatedProgress,
            progress = progress,
            completedTasks = completedTasks,
            totalTasks = totalTasks,
            tokensEarned = tokensEarned,
        )
    }
}

@Composable
private fun ProgressSummaryContent(
    animatedProgress: Float,
    progress: Float,
    completedTasks: Int,
    totalTasks: Int,
    tokensEarned: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(CONTAINER_PADDING.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WaterBottleVisualization(animatedProgress, progress)
        ProgressDetailsColumn(progress, completedTasks, totalTasks, tokensEarned)
    }
}

@Composable
private fun WaterBottleVisualization(animatedProgress: Float, progress: Float) {
    Box(
        modifier = Modifier.size(BOTTLE_WIDTH.dp, BOTTLE_HEIGHT.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier.size(BOTTLE_WIDTH.dp, BOTTLE_HEIGHT.dp),
        ) {
            drawWaterBottle(animatedProgress, progress >= 1f)
        }
    }
}

@Composable
private fun RowScope.ProgressDetailsColumn(
    progress: Float,
    completedTasks: Int,
    totalTasks: Int,
    tokensEarned: Int,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(start = CONTAINER_PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(CONTENT_SPACING.dp),
    ) {
        ProgressTitleText(progress)
        ProgressTasksText(completedTasks, totalTasks)
        ProgressTokensRow(tokensEarned)
        ProgressPercentageText(progress)
    }
}

@Composable
private fun ProgressTitleText(progress: Float) {
    Text(
        text = when {
            progress >= 1f -> "Perfect Day! 🎉"
            progress >= PROGRESS_THRESHOLD_HIGH -> "Almost There!"
            progress >= PROGRESS_THRESHOLD_MEDIUM -> "Good Progress!"
            progress > 0f -> "Getting Started"
            else -> "Ready to Begin"
        },
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun ProgressTasksText(completedTasks: Int, totalTasks: Int) {
    Text(
        text = "$completedTasks of $totalTasks tasks completed",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_HIGH),
    )
}

@Composable
private fun ProgressTokensRow(tokensEarned: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TOKEN_ICON_SPACING.dp),
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.btn_star_big_on),
            contentDescription = "Tokens",
            modifier = Modifier.size(TOKEN_ICON_SIZE.dp),
            tint = Color(COLOR_GOLD), // Gold color
        )
        Text(
            text = "$tokensEarned tokens earned",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun ProgressPercentageText(progress: Float) {
    Text(
        text = "${(progress * 100).toInt()}% complete",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_MEDIUM),
    )
}

/**
 * Draws an animated water bottle with filling water based on progress.
 */
private fun DrawScope.drawWaterBottle(progress: Float, isComplete: Boolean) {
    val bottleMetrics = calculateBottleMetrics()

    drawBottleOutline(bottleMetrics)

    if (progress > 0f) {
        drawWaterFill(progress, isComplete, bottleMetrics)
    }

    drawBottleCap(bottleMetrics)
}

/**
 * Calculates bottle dimensions and positioning.
 */
private data class BottleMetrics(
    val bottleWidth: Float,
    val bottleHeight: Float,
    val neckHeight: Float,
    val neckWidth: Float,
    val centerX: Float,
    val bottleTop: Float,
    val bottleBottom: Float,
    val bodyTop: Float,
)

private fun DrawScope.calculateBottleMetrics(): BottleMetrics {
    val bottleWidth = BOTTLE_WIDTH.dp.toPx()
    val bottleHeight = BOTTLE_HEIGHT.dp.toPx()
    val neckHeight = BOTTLE_NECK_HEIGHT.dp.toPx()
    val neckWidth = bottleWidth * ALPHA_LOW

    val centerX = size.width / 2
    val bottleTop = (size.height - bottleHeight) / 2
    val bottleBottom = bottleTop + bottleHeight
    val bodyTop = bottleTop + neckHeight

    return BottleMetrics(
        bottleWidth,
        bottleHeight,
        neckHeight,
        neckWidth,
        centerX,
        bottleTop,
        bottleBottom,
        bodyTop,
    )
}

/**
 * Draws the bottle outline shape.
 */
private fun DrawScope.drawBottleOutline(metrics: BottleMetrics) {
    val bottlePath = Path().apply {
        // Neck
        moveTo(metrics.centerX - metrics.neckWidth / 2, metrics.bottleTop)
        lineTo(metrics.centerX + metrics.neckWidth / 2, metrics.bottleTop)
        lineTo(metrics.centerX + metrics.neckWidth / 2, metrics.bodyTop)

        // Right side of body
        lineTo(metrics.centerX + metrics.bottleWidth / 2, metrics.bodyTop)
        lineTo(
            metrics.centerX + metrics.bottleWidth / 2,
            metrics.bottleBottom - BOTTLE_BOTTOM_CURVE.dp.toPx(),
        )

        // Rounded bottom
        quadraticTo(
            metrics.centerX + metrics.bottleWidth / 2,
            metrics.bottleBottom,
            metrics.centerX,
            metrics.bottleBottom,
        )
        quadraticTo(
            metrics.centerX - metrics.bottleWidth / 2,
            metrics.bottleBottom,
            metrics.centerX - metrics.bottleWidth / 2,
            metrics.bottleBottom - BOTTLE_BOTTOM_CURVE.dp.toPx(),
        )

        // Left side of body
        lineTo(metrics.centerX - metrics.bottleWidth / 2, metrics.bodyTop)
        lineTo(metrics.centerX - metrics.neckWidth / 2, metrics.bodyTop)
        close()
    }

    drawPath(
        path = bottlePath,
        color = Color(COLOR_BOTTLE_GRAY),
        style = Stroke(width = BOTTLE_STROKE_WIDTH.dp.toPx(), cap = StrokeCap.Round),
    )
}

/**
 * Draws the water fill with gradient and bubbles.
 */
private fun DrawScope.drawWaterFill(progress: Float, isComplete: Boolean, metrics: BottleMetrics) {
    val waterHeight = (metrics.bottleHeight - metrics.neckHeight - WATER_BOTTOM_OFFSET_DP.dp.toPx()) * progress
    val waterTop = metrics.bottleBottom - waterHeight

    val waterPath = createWaterPath(waterTop, metrics)
    val waterBrush = createWaterGradient(isComplete)

    drawPath(path = waterPath, brush = waterBrush)

    if (isComplete) {
        drawCelebrationBubbles(waterTop, metrics.centerX)
    }
}

/**
 * Creates the water path shape.
 */
private fun DrawScope.createWaterPath(waterTop: Float, metrics: BottleMetrics): Path {
    return Path().apply {
        // Start from bottom left
        moveTo(
            metrics.centerX - metrics.bottleWidth / 2 + WATER_SIDE_OFFSET_DP.dp.toPx(),
            metrics.bottleBottom - WATER_BOTTOM_OFFSET_DP.dp.toPx(),
        )
        lineTo(
            metrics.centerX + metrics.bottleWidth / 2 - WATER_SIDE_OFFSET_DP.dp.toPx(),
            metrics.bottleBottom - WATER_BOTTOM_OFFSET_DP.dp.toPx(),
        )

        // Right side up to water level
        lineTo(metrics.centerX + metrics.bottleWidth / 2 - WATER_SIDE_OFFSET_DP.dp.toPx(), waterTop)

        // Water surface with slight wave
        val waveAmplitude = WATER_WAVE_AMPLITUDE_DP.dp.toPx()
        quadraticTo(
            metrics.centerX + metrics.bottleWidth / WAVE_QUARTER_DIVISOR,
            waterTop - waveAmplitude,
            metrics.centerX,
            waterTop,
        )
        quadraticTo(
            metrics.centerX - metrics.bottleWidth / WAVE_QUARTER_DIVISOR,
            waterTop + waveAmplitude,
            metrics.centerX - metrics.bottleWidth / 2 + WATER_SIDE_OFFSET_DP.dp.toPx(),
            waterTop,
        )

        // Left side back to bottom
        lineTo(
            metrics.centerX - metrics.bottleWidth / 2 + WATER_SIDE_OFFSET_DP.dp.toPx(),
            metrics.bottleBottom - WATER_BOTTOM_OFFSET_DP.dp.toPx(),
        )
        close()
    }
}

/**
 * Creates water gradient based on completion state.
 */
private fun createWaterGradient(isComplete: Boolean): Brush {
    return if (isComplete) {
        Brush.verticalGradient(
            colors = listOf(
                Color(COLOR_WATER_GREEN).copy(alpha = ALPHA_WATER_HIGH),
                Color(COLOR_WATER_BLUE).copy(alpha = ALPHA_WATER_MEDIUM),
            ),
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(COLOR_WATER_BLUE).copy(alpha = ALPHA_WATER_LOW),
                Color(COLOR_WATER_LIGHT_BLUE).copy(alpha = ALPHA_WATER_MEDIUM),
            ),
        )
    }
}

/**
 * Draws celebration bubbles for completed state.
 */
private fun DrawScope.drawCelebrationBubbles(waterTop: Float, centerX: Float) {
    val bubbleOffset1X = BUBBLE_OFFSET_1_X
    val bubbleOffset1Y = BUBBLE_OFFSET_1_Y
    val bubbleOffset2X = BUBBLE_OFFSET_2_X
    val bubbleOffset2Y = BUBBLE_OFFSET_2_Y
    val bubbleOffset3X = BUBBLE_OFFSET_3_X
    val bubbleOffset3Y = BUBBLE_OFFSET_3_Y
    val bubblePositions = listOf(
        androidx.compose.ui.geometry.Offset(
            centerX + bubbleOffset1X.dp.toPx(),
            waterTop + bubbleOffset1Y.dp.toPx(),
        ),
        androidx.compose.ui.geometry.Offset(
            centerX + bubbleOffset2X.dp.toPx(),
            waterTop + bubbleOffset2Y.dp.toPx(),
        ),
        androidx.compose.ui.geometry.Offset(
            centerX + bubbleOffset3X.dp.toPx(),
            waterTop + bubbleOffset3Y.dp.toPx(),
        ),
    )

    bubblePositions.forEach { position ->
        drawCircle(
            color = Color.White.copy(alpha = ALPHA_CAP),
            radius = BUBBLE_RADIUS.dp.toPx(),
            center = position,
        )
    }
}

/**
 * Draws the bottle cap.
 */
private fun DrawScope.drawBottleCap(metrics: BottleMetrics) {
    drawLine(
        color = Color(COLOR_CAP_GRAY),
        start = androidx.compose.ui.geometry.Offset(
            metrics.centerX - metrics.neckWidth / 2 - CAP_SIDE_EXTENSION.dp.toPx(),
            metrics.bottleTop,
        ),
        end = androidx.compose.ui.geometry.Offset(
            metrics.centerX + metrics.neckWidth / 2 + CAP_SIDE_EXTENSION.dp.toPx(),
            metrics.bottleTop,
        ),
        strokeWidth = CAP_STROKE_WIDTH.dp.toPx(),
        cap = StrokeCap.Round,
    )
}
