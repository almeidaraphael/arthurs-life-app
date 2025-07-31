package com.lemonqwest.app.presentation.theme.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val COLOR_RED_HEX = 0xFFE53935
private const val COLOR_BLUE_HEX = 0xFF1E88E5
private const val COLOR_GREEN_HEX = 0xFF43A047
private const val COLOR_AMBER_HEX = 0xFFFFB300
private const val COLOR_PURPLE_HEX = 0xFF8E24AA
private const val COLOR_PINK_HEX = 0xFFD81B60
private const val COLOR_GOLD_HEX = 0xFFFFD700
private const val COLOR_WHITE_HEX = 0xFFFFFFFF
private const val COLOR_LIGHT_YELLOW_HEX = 0xFFFFE082
private const val COLOR_YELLOW_HEX = 0xFFFFF176
private const val CONFETTI_ROTATION_MAX = 180f
private const val SPARKLE_SIZE_MAX = 12f
private const val SPARKLE_ALPHA_MULTIPLIER = 2f
private const val SPARKLE_PROGRESS_THRESHOLD = 0.5f
private val COLOR_RED = Color(COLOR_RED_HEX)
private val COLOR_BLUE = Color(COLOR_BLUE_HEX)
private val COLOR_GREEN = Color(COLOR_GREEN_HEX)
private val COLOR_AMBER = Color(COLOR_AMBER_HEX)
private val COLOR_PURPLE = Color(COLOR_PURPLE_HEX)
private val COLOR_PINK = Color(COLOR_PINK_HEX)
private val COLOR_GOLD = Color(COLOR_GOLD_HEX)
private val COLOR_WHITE = Color(COLOR_WHITE_HEX)
private val COLOR_LIGHT_YELLOW = Color(COLOR_LIGHT_YELLOW_HEX)
private val COLOR_YELLOW = Color(COLOR_YELLOW_HEX)
private const val ANIMATION_DURATION = 2000
private const val CONFETTI_COUNT = 20
private const val SPARKLE_COUNT = 8
private const val CONFETTI_SIZE_MIN = 4f
private const val CONFETTI_SIZE_MAX = 8f
private const val STAR_PATH_RATIO = 0.3f
private const val CONFETTI_ROTATION_PROGRESS_MULTIPLIER = 2f
private const val CONFETTI_SIZE_DIVISOR = 2f
private const val SPARKLE_X_OFFSET = 200f
private const val SPARKLE_X_OFFSET_HALF = 100f
private const val SPARKLE_Y_OFFSET = 200f
private const val SPARKLE_Y_OFFSET_HALF = 100f
private const val CONFETTI_ROTATION_SPEED_DIVISOR = 2f
private const val CONFETTI_ROTATION_SPEED_DIVISOR_HALF = 4f
private const val SPARKLE_SIZE_OFFSET = 3f
private const val SPARKLE_DELAY_MAX = 0.5f
private const val ANIMATION_START = 0f
private const val ANIMATION_END = 1f
private const val ROTATION_END = 360f
private const val SCREEN_WIDTH_APPROX = 800f
private const val START_Y_OFFSET = -50f
private const val FALL_RANGE = 400f
private const val FALL_OFFSET = 600f

/**
 * Positive feedback animation component for task completion celebrations.
 *
 * Shows animated confetti and sparkles with customizable colors and duration.
 * Perfect for celebrating task completions and achievements.
 *
 * @param isVisible Whether the animation should be visible
 * @param onAnimationEnd Callback when animation completes
 * @param modifier Modifier for styling
 */
@Composable
fun PositiveFeedbackAnimation(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {},
) {
    if (!isVisible) return

    var confetti by remember { mutableStateOf(generateConfetti()) }
    var sparkles by remember { mutableStateOf(generateSparkles()) }

    val infiniteTransition = rememberInfiniteTransition(label = "celebration")

    val animationProgress by infiniteTransition.animateFloat(
        initialValue = ANIMATION_START,
        targetValue = ANIMATION_END,
        animationSpec = infiniteRepeatable(
            animation = tween(ANIMATION_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "animation_progress",
    )

    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = ANIMATION_START,
        targetValue = ROTATION_END,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sparkle_rotation",
    )

    LaunchedEffect(isVisible) {
        if (isVisible) {
            confetti = generateConfetti()
            sparkles = generateSparkles()
            delay(ANIMATION_DURATION.toLong())
            onAnimationEnd()
        }
    }

    Canvas(
        modifier = modifier.fillMaxSize(),
    ) {
        // Draw confetti
        confetti.forEach { piece ->
            drawConfettiPiece(piece, animationProgress)
        }

        // Draw sparkles
        sparkles.forEach { sparkle ->
            drawSparkle(sparkle, animationProgress, sparkleRotation)
        }
    }
}

/**
 * Data class representing a confetti piece.
 */
private data class ConfettiPiece(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
)

/**
 * Data class representing a sparkle.
 */
private data class Sparkle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val delay: Float,
)

/**
 * Generates random confetti pieces.
 */
private fun generateConfetti(): List<ConfettiPiece> {
    val colors = listOf(
        COLOR_RED,
        COLOR_BLUE,
        COLOR_GREEN,
        COLOR_AMBER,
        COLOR_PURPLE,
        COLOR_PINK,
    )

    return (0 until CONFETTI_COUNT).map {
        ConfettiPiece(
            startX = Random.nextFloat() * SCREEN_WIDTH_APPROX,
            startY = START_Y_OFFSET,
            endX = Random.nextFloat() * SCREEN_WIDTH_APPROX,
            endY = Random.nextFloat() * FALL_RANGE + FALL_OFFSET,
            color = colors.random(),
            size = Random.nextFloat() * CONFETTI_SIZE_MAX + CONFETTI_SIZE_MIN,
            rotation = Random.nextFloat() * CONFETTI_ROTATION_MAX,
            rotationSpeed = Random.nextFloat() * CONFETTI_ROTATION_MAX / CONFETTI_ROTATION_SPEED_DIVISOR - CONFETTI_ROTATION_MAX / CONFETTI_ROTATION_SPEED_DIVISOR_HALF, // -90 to 90 degrees per second
        )
    }
}

/**
 * Generates random sparkles.
 */
private fun generateSparkles(): List<Sparkle> {
    val colors = listOf(
        COLOR_GOLD,
        COLOR_WHITE,
        COLOR_LIGHT_YELLOW,
        COLOR_YELLOW,
    )

    return (0 until SPARKLE_COUNT).map {
        Sparkle(
            x = Random.nextFloat() * (SCREEN_WIDTH_APPROX - SPARKLE_X_OFFSET) + SPARKLE_X_OFFSET_HALF,
            y = Random.nextFloat() * (FALL_RANGE - SPARKLE_Y_OFFSET) + SPARKLE_Y_OFFSET_HALF,
            color = colors.random(),
            size = Random.nextFloat() * SPARKLE_SIZE_MAX + SPARKLE_SIZE_OFFSET,
            delay = Random.nextFloat() * SPARKLE_DELAY_MAX,
        )
    }
}

/**
 * Draws a single confetti piece with animation.
 */
private fun DrawScope.drawConfettiPiece(piece: ConfettiPiece, progress: Float) {
    val currentX = piece.startX + (piece.endX - piece.startX) * progress
    val currentY = piece.startY + (piece.endY - piece.startY) * progress * progress // Gravity effect
    val currentRotation = piece.rotation + piece.rotationSpeed * progress * CONFETTI_ROTATION_PROGRESS_MULTIPLIER

    rotate(currentRotation, pivot = androidx.compose.ui.geometry.Offset(currentX, currentY)) {
        drawRect(
            color = piece.color,
            topLeft = androidx.compose.ui.geometry.Offset(
                currentX - piece.size / CONFETTI_SIZE_DIVISOR,
                currentY - piece.size / CONFETTI_SIZE_DIVISOR,
            ),
            size = androidx.compose.ui.geometry.Size(piece.size, piece.size),
        )
    }
}

/**
 * Draws a sparkle with animation.
 */
private fun DrawScope.drawSparkle(sparkle: Sparkle, progress: Float, rotation: Float) {
    val adjustedProgress = (progress - sparkle.delay).coerceIn(0f, 1f)
    if (adjustedProgress <= 0f) return

    val alpha = if (adjustedProgress < SPARKLE_PROGRESS_THRESHOLD) {
        adjustedProgress * SPARKLE_ALPHA_MULTIPLIER
    } else {
        SPARKLE_ALPHA_MULTIPLIER - adjustedProgress * SPARKLE_ALPHA_MULTIPLIER
    }

    val sparkleColor = sparkle.color.copy(alpha = alpha)
    val center = androidx.compose.ui.geometry.Offset(sparkle.x, sparkle.y)

    rotate(rotation, pivot = center) {
        // Draw 4-pointed star
        val starPath = Path().apply {
            // Top point
            moveTo(sparkle.x, sparkle.y - sparkle.size)
            lineTo(
                sparkle.x - sparkle.size * STAR_PATH_RATIO,
                sparkle.y - sparkle.size * STAR_PATH_RATIO,
            )

            // Left point
            lineTo(sparkle.x - sparkle.size, sparkle.y)
            lineTo(
                sparkle.x - sparkle.size * STAR_PATH_RATIO,
                sparkle.y + sparkle.size * STAR_PATH_RATIO,
            )

            // Bottom point
            lineTo(sparkle.x, sparkle.y + sparkle.size)
            lineTo(
                sparkle.x + sparkle.size * STAR_PATH_RATIO,
                sparkle.y + sparkle.size * STAR_PATH_RATIO,
            )

            // Right point
            lineTo(sparkle.x + sparkle.size, sparkle.y)
            lineTo(
                sparkle.x + sparkle.size * STAR_PATH_RATIO,
                sparkle.y - sparkle.size * STAR_PATH_RATIO,
            )

            close()
        }

        drawPath(
            path = starPath,
            color = sparkleColor,
        )
    }
}
