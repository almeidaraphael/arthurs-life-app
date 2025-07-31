package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import kotlin.math.max
import kotlin.math.min

/**
 * Utility for validating color contrast ratios according to WCAG accessibility guidelines.
 *
 * This utility provides methods to calculate contrast ratios between colors and validate
 * them against WCAG standards (AA level requires 4.5:1 for normal text, AAA requires 7:1).
 *
 * Used by the theme system to ensure all color combinations meet accessibility standards
 * for users with visual impairments.
 */
object ColorContrastValidator {

    /**
     * WCAG 2.1 contrast ratio thresholds
     */
    private const val WCAG_AA_NORMAL_TEXT_RATIO = 4.5
    private const val WCAG_AA_LARGE_TEXT_RATIO = 3.0
    private const val WCAG_AAA_NORMAL_TEXT_RATIO = 7.0
    private const val WCAG_AAA_LARGE_TEXT_RATIO = 4.5
    private const val LUMINANCE_OFFSET = 0.05

    /**
     * Calculate the contrast ratio between two colors.
     *
     * The contrast ratio is calculated using the WCAG 2.1 formula:
     * (L1 + 0.05) / (L2 + 0.05)
     * where L1 is the relative luminance of the lighter color and L2 is the relative
     * luminance of the darker color.
     *
     * @param foreground The foreground color (typically text)
     * @param background The background color
     * @return The contrast ratio as a Double (1.0 to 21.0)
     */
    fun calculateContrastRatio(foreground: Color, background: Color): Double {
        val foregroundLuminance = foreground.luminance().toDouble()
        val backgroundLuminance = background.luminance().toDouble()

        val lighter = max(foregroundLuminance, backgroundLuminance)
        val darker = min(foregroundLuminance, backgroundLuminance)

        return (lighter + LUMINANCE_OFFSET) / (darker + LUMINANCE_OFFSET)
    }

    /**
     * Check if a color combination meets WCAG AA standards for normal text (4.5:1).
     *
     * @param foreground The foreground color (typically text)
     * @param background The background color
     * @return True if the contrast ratio meets or exceeds 4.5:1
     */
    fun meetsWcagAA(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= WCAG_AA_NORMAL_TEXT_RATIO
    }

    /**
     * Check if a color combination meets WCAG AA standards for large text (3.0:1).
     *
     * @param foreground The foreground color (typically text)
     * @param background The background color
     * @return True if the contrast ratio meets or exceeds 3.0:1
     */
    fun meetsWcagAALargeText(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= WCAG_AA_LARGE_TEXT_RATIO
    }

    /**
     * Check if a color combination meets WCAG AAA standards for normal text (7.0:1).
     *
     * @param foreground The foreground color (typically text)
     * @param background The background color
     * @return True if the contrast ratio meets or exceeds 7.0:1
     */
    fun meetsWcagAAA(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= WCAG_AAA_NORMAL_TEXT_RATIO
    }

    /**
     * Check if a color combination meets WCAG AAA standards for large text (4.5:1).
     *
     * @param foreground The foreground color (typically text)
     * @param background The background color
     * @return True if the contrast ratio meets or exceeds 4.5:1
     */
    fun meetsWcagAAALargeText(foreground: Color, background: Color): Boolean {
        return calculateContrastRatio(foreground, background) >= WCAG_AAA_LARGE_TEXT_RATIO
    }

    /**
     * Validate a color combination and return a detailed result.
     *
     * @param foreground The foreground color (typically text)
     * @param background The background color
     * @return ContrastValidationResult with detailed information
     */
    fun validateColorCombination(
        foreground: Color,
        background: Color,
    ): ContrastValidationResult {
        val ratio = calculateContrastRatio(foreground, background)
        return ContrastValidationResult(
            contrastRatio = ratio,
            meetsWcagAA = ratio >= WCAG_AA_NORMAL_TEXT_RATIO,
            meetsWcagAALargeText = ratio >= WCAG_AA_LARGE_TEXT_RATIO,
            meetsWcagAAA = ratio >= WCAG_AAA_NORMAL_TEXT_RATIO,
            meetsWcagAAALargeText = ratio >= WCAG_AAA_LARGE_TEXT_RATIO,
            foregroundColor = foreground,
            backgroundColor = background,
        )
    }

    /**
     * Get a descriptive message about the contrast level.
     *
     * @param foreground The foreground color
     * @param background The background color
     * @return A human-readable description of the contrast level
     */
    fun getContrastDescription(foreground: Color, background: Color): String {
        val ratio = calculateContrastRatio(foreground, background)
        return when {
            ratio >= WCAG_AAA_NORMAL_TEXT_RATIO -> "Excellent contrast (WCAG AAA)"
            ratio >= WCAG_AA_NORMAL_TEXT_RATIO -> "Good contrast (WCAG AA)"
            ratio >= WCAG_AA_LARGE_TEXT_RATIO -> "Acceptable for large text only"
            else -> "Poor contrast - accessibility concerns"
        }
    }

    /**
     * Convert a Color to a hex string for debugging.
     *
     * @param color The color to convert
     * @return Hex string representation (e.g., "#FF0000")
     */
    fun colorToHex(color: Color): String {
        val argb = color.toArgb()
        return "#%08X".format(argb)
    }
}

/**
 * Data class containing the results of contrast validation.
 */
data class ContrastValidationResult(
    val contrastRatio: Double,
    val meetsWcagAA: Boolean,
    val meetsWcagAALargeText: Boolean,
    val meetsWcagAAA: Boolean,
    val meetsWcagAAALargeText: Boolean,
    val foregroundColor: Color,
    val backgroundColor: Color,
) {
    /**
     * Get a summary of which standards this color combination meets.
     */
    val complianceSummary: String
        get() = buildString {
            when {
                meetsWcagAAA -> append("WCAG AAA compliant")
                meetsWcagAA -> append("WCAG AA compliant")
                meetsWcagAALargeText -> append("WCAG AA large text only")
                else -> append("Does not meet WCAG standards")
            }
            append(" (${"%.2f".format(java.util.Locale.US, contrastRatio)}:1)")
        }

    /**
     * Check if this combination is suitable for normal text.
     */
    val isSuitableForNormalText: Boolean
        get() = meetsWcagAA

    /**
     * Check if this combination is suitable for large text.
     */
    val isSuitableForLargeText: Boolean
        get() = meetsWcagAALargeText
}
