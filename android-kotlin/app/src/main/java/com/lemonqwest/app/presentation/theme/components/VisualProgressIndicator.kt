package com.lemonqwest.app.presentation.theme.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val PROGRESS_HEIGHT = 160
private const val MUSHROOM_SIZE = 40
private const val STEM_WIDTH = 12
private const val DAMPENING_RATIO = 0.8f
private const val CORNER_RADIUS = 16
private const val ALPHA_LIGHT = 0.3f
private const val ALPHA_SUBTITLE = 0.7f
private const val STAGE_1_THRESHOLD = 0.25f
private const val STAGE_2_THRESHOLD = 0.5f
private const val STAGE_3_THRESHOLD = 0.75f
private const val FONT_SIZE_LARGE = 18

private const val SOIL_WIDTH_DP = 30
private const val SOIL_HEIGHT_DP = 10
private const val SOIL_BOTTOM_OFFSET_DP = 20
const val GROUND_LINE_STROKE_WIDTH = 4
const val SPOT_SIZE = 3
const val SPOT_SIZE_RATIO = 0.7f

private const val TITLE_TOP_PADDING = 12
private const val SUBTITLE_TOP_PADDING = 4
private const val PERCENTAGE_TOP_PADDING = 8
private const val SHADOW_OFFSET = 2
private const val CAP_RADIUS_DIVISOR = 2
private const val CAP_SPOTS_THRESHOLD = 0.5f
private const val CENTER_WIDTH_DIVISOR = 2
private const val SPOT_OFFSET_QUARTER = 4
private const val SPOT_OFFSET_SIXTH = 6
private const val SPOT_OFFSET_THIRD = 3
private const val COLOR_LIGHT_SKY_BLUE = 0xFFE3F2FD
private const val COLOR_LIGHT_GREEN = 0xFF8BC34A
private const val COLOR_SOIL_BROWN = 0xFF6D4C41
private const val COLOR_STEM_GREEN = 0xFF4CAF50
private const val COLOR_MUSHROOM_SHADOW = 0xFFD32F2F
private const val COLOR_MUSHROOM_CAP = 0xFFE53935
private const val SHADOW_ALPHA = 0.3f
private const val MAX_STEM_HEIGHT_RATIO = 0.6f
private const val SMALL_STEM_HEIGHT_RATIO = 0.3f
private const val STAGE_PROGRESS_RANGE = 0.25f
private const val GROWING_STEM_RANGE = 0.5f

/**
 * Visual progress indicator inspired by Avocation's plant growth metaphor.
 *
 * Shows progress through animated mushroom growth:
 * - 0-25%: Soil only
 * - 25-50%: Small stem appears
 * - 50-75%: Stem grows taller
 * - 75-100%: Mushroom cap appears and grows
 *
 * @param progress Current progress value (0.0 to 1.0)
 * @param title Progress title/description
 * @param subtitle Optional subtitle text
 * @param modifier Modifier for styling
 */
@Composable
fun VisualProgressIndicator(
    progress: Float,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = DAMPENING_RATIO),
        label = "mushroom_growth",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProgressVisualizationBox(animatedProgress)
        ProgressTextSection(title, subtitle, animatedProgress)
    }
}

@Composable
private fun ProgressVisualizationBox(animatedProgress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PROGRESS_HEIGHT.dp)
            .clip(RoundedCornerShape(CORNER_RADIUS.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(COLOR_LIGHT_SKY_BLUE), // Light sky blue
                        Color(COLOR_LIGHT_GREEN).copy(alpha = ALPHA_LIGHT), // Light green ground
                    ),
                ),
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(PROGRESS_HEIGHT.dp),
        ) {
            drawMushroomProgress(animatedProgress)
        }
    }
}

@Composable
private fun ProgressTextSection(title: String, subtitle: String?, animatedProgress: Float) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = FONT_SIZE_LARGE.sp,
        ),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = TITLE_TOP_PADDING.dp),
    )

    subtitle?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_SUBTITLE),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = SUBTITLE_TOP_PADDING.dp),
        )
    }

    Text(
        text = "${(animatedProgress * 100).toInt()}%",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = PERCENTAGE_TOP_PADDING.dp),
    )
}

/**
 * Draws animated mushroom growth based on progress value.
 */
private fun DrawScope.drawMushroomProgress(progress: Float) {
    val metrics = calculateMushroomMetrics()

    drawGroundLine(metrics)

    when {
        progress < STAGE_1_THRESHOLD -> drawSoilStage(metrics)
        progress < STAGE_2_THRESHOLD -> drawSmallStemStage(progress, metrics)
        progress < STAGE_3_THRESHOLD -> drawGrowingStemStage(progress, metrics)
        else -> drawMushroomCapStage(progress, metrics)
    }
}

/**
 * Mushroom drawing metrics and positioning.
 */
private data class MushroomMetrics(
    val centerX: Float,
    val groundY: Float,
    val maxStemHeight: Float,
)

private fun DrawScope.calculateMushroomMetrics(): MushroomMetrics {
    val centerX = size.width / CENTER_WIDTH_DIVISOR
    val groundY = size.height - SOIL_BOTTOM_OFFSET_DP.dp.toPx()
    val maxStemHeight = size.height * MAX_STEM_HEIGHT_RATIO

    return MushroomMetrics(centerX, groundY, maxStemHeight)
}

/**
 * Draws the ground line.
 */
private fun DrawScope.drawGroundLine(metrics: MushroomMetrics) {
    drawLine(
        color = Color(COLOR_LIGHT_GREEN),
        start = androidx.compose.ui.geometry.Offset(0f, metrics.groundY),
        end = androidx.compose.ui.geometry.Offset(size.width, metrics.groundY),
        strokeWidth = GROUND_LINE_STROKE_WIDTH.dp.toPx(),
    )
}

/**
 * Stage 1: Just soil (0-25%)
 */
private fun DrawScope.drawSoilStage(metrics: MushroomMetrics) {
    val soilPath = Path().apply {
        moveTo(metrics.centerX - SOIL_WIDTH_DP.dp.toPx(), metrics.groundY)
        quadraticTo(
            metrics.centerX,
            metrics.groundY - SOIL_HEIGHT_DP.dp.toPx(),
            metrics.centerX + SOIL_WIDTH_DP.dp.toPx(),
            metrics.groundY,
        )
        lineTo(
            metrics.centerX + SOIL_WIDTH_DP.dp.toPx(),
            metrics.groundY + SOIL_HEIGHT_DP.dp.toPx(),
        )
        lineTo(
            metrics.centerX - SOIL_WIDTH_DP.dp.toPx(),
            metrics.groundY + SOIL_HEIGHT_DP.dp.toPx(),
        )
        close()
    }

    drawPath(
        path = soilPath,
        color = Color(COLOR_SOIL_BROWN),
    )
}

/**
 * Stage 2: Small stem appears (25-50%)
 */
private fun DrawScope.drawSmallStemStage(progress: Float, metrics: MushroomMetrics) {
    val stemProgress = (progress - STAGE_1_THRESHOLD) / STAGE_PROGRESS_RANGE
    val stemHeight = metrics.maxStemHeight * SMALL_STEM_HEIGHT_RATIO * stemProgress

    drawLine(
        color = Color(COLOR_STEM_GREEN),
        start = androidx.compose.ui.geometry.Offset(metrics.centerX, metrics.groundY),
        end = androidx.compose.ui.geometry.Offset(metrics.centerX, metrics.groundY - stemHeight),
        strokeWidth = STEM_WIDTH.dp.toPx(),
    )
}

/**
 * Stage 3: Stem grows (50-75%)
 */
private fun DrawScope.drawGrowingStemStage(progress: Float, metrics: MushroomMetrics) {
    val stemProgress = (progress - STAGE_1_THRESHOLD) / GROWING_STEM_RANGE
    val stemHeight = metrics.maxStemHeight * stemProgress

    drawLine(
        color = Color(COLOR_STEM_GREEN),
        start = androidx.compose.ui.geometry.Offset(metrics.centerX, metrics.groundY),
        end = androidx.compose.ui.geometry.Offset(metrics.centerX, metrics.groundY - stemHeight),
        strokeWidth = STEM_WIDTH.dp.toPx(),
    )
}

/**
 * Stage 4: Mushroom cap appears (75-100%)
 */
private fun DrawScope.drawMushroomCapStage(progress: Float, metrics: MushroomMetrics) {
    val stemHeight = metrics.maxStemHeight
    val capProgress = (progress - STAGE_3_THRESHOLD) / STAGE_PROGRESS_RANGE
    val capSize = MUSHROOM_SIZE.dp.toPx() * capProgress

    // Draw full stem
    drawLine(
        color = Color(COLOR_STEM_GREEN),
        start = androidx.compose.ui.geometry.Offset(metrics.centerX, metrics.groundY),
        end = androidx.compose.ui.geometry.Offset(metrics.centerX, metrics.groundY - stemHeight),
        strokeWidth = STEM_WIDTH.dp.toPx(),
    )

    // Draw mushroom cap
    if (capSize > 0) {
        drawMushroomCap(capSize, capProgress, metrics)
    }
}

/**
 * Draws the mushroom cap with shadow and spots.
 */
private fun DrawScope.drawMushroomCap(
    capSize: Float,
    capProgress: Float,
    metrics: MushroomMetrics,
) {
    val capY = metrics.groundY - metrics.maxStemHeight - capSize / CAP_RADIUS_DIVISOR

    // Cap shadow/base
    drawCircle(
        color = Color(COLOR_MUSHROOM_SHADOW).copy(alpha = SHADOW_ALPHA),
        radius = capSize / CAP_RADIUS_DIVISOR + SHADOW_OFFSET.dp.toPx(),
        center = androidx.compose.ui.geometry.Offset(
            metrics.centerX,
            capY + SHADOW_OFFSET.dp.toPx(),
        ),
    )

    // Main cap
    drawCircle(
        color = Color(COLOR_MUSHROOM_CAP),
        radius = capSize / CAP_RADIUS_DIVISOR,
        center = androidx.compose.ui.geometry.Offset(metrics.centerX, capY),
    )

    // White spots on cap
    if (capProgress > CAP_SPOTS_THRESHOLD) {
        drawCapSpots(capSize, capY, metrics.centerX)
    }
}

/**
 * Draws white spots on the mushroom cap.
 */
private fun DrawScope.drawCapSpots(capSize: Float, capY: Float, centerX: Float) {
    val spotSize = SPOT_SIZE.dp.toPx()

    drawCircle(
        color = Color.White,
        radius = spotSize,
        center = androidx.compose.ui.geometry.Offset(
            centerX - capSize / SPOT_OFFSET_QUARTER,
            capY - capSize / SPOT_OFFSET_SIXTH,
        ),
    )

    drawCircle(
        color = Color.White,
        radius = spotSize * SPOT_SIZE_RATIO,
        center = androidx.compose.ui.geometry.Offset(
            centerX + capSize / SPOT_OFFSET_THIRD,
            capY - capSize / SPOT_OFFSET_QUARTER,
        ),
    )
}
