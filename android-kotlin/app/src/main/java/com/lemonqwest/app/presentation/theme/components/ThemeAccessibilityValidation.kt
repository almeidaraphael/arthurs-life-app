package com.lemonqwest.app.presentation.theme.components

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.lemonqwest.app.presentation.theme.BaseAppTheme

/**
 * Theme accessibility validation utilities for ensuring WCAG compliance.
 *
 * This file provides extension functions and utilities to validate theme color combinations
 * for accessibility compliance, specifically focusing on contrast ratios that meet
 * WCAG 2.1 guidelines.
 */

/**
 * Validate all critical color combinations in a theme's ColorScheme.
 *
 * @return ThemeValidationResult containing validation details for all color pairs
 */
fun ColorScheme.validateAccessibility(): ThemeValidationResult {
    val validations = mutableListOf<ContrastValidationResult>()

    validations.addAll(validatePrimaryColors())
    validations.addAll(validateSecondaryColors())
    validations.addAll(validateSurfaceColors())
    validations.addAll(validateBackgroundColors())
    validations.addAll(validateErrorColors())

    return ThemeValidationResult(
        validations = validations,
        colorScheme = this,
    )
}

/**
 * Validate primary color combinations.
 */
private fun ColorScheme.validatePrimaryColors(): List<ContrastValidationResult> {
    return listOf(
        ColorContrastValidator.validateColorCombination(
            foreground = onPrimary,
            background = primary,
        ),
        ColorContrastValidator.validateColorCombination(
            foreground = onPrimaryContainer,
            background = primaryContainer,
        ),
    )
}

/**
 * Validate secondary color combinations.
 */
private fun ColorScheme.validateSecondaryColors(): List<ContrastValidationResult> {
    return listOf(
        ColorContrastValidator.validateColorCombination(
            foreground = onSecondary,
            background = secondary,
        ),
        ColorContrastValidator.validateColorCombination(
            foreground = onSecondaryContainer,
            background = secondaryContainer,
        ),
    )
}

/**
 * Validate surface color combinations.
 */
private fun ColorScheme.validateSurfaceColors(): List<ContrastValidationResult> {
    return listOf(
        ColorContrastValidator.validateColorCombination(
            foreground = onSurface,
            background = surface,
        ),
        ColorContrastValidator.validateColorCombination(
            foreground = onSurfaceVariant,
            background = surface,
        ),
    )
}

/**
 * Validate background color combinations.
 */
private fun ColorScheme.validateBackgroundColors(): List<ContrastValidationResult> {
    return listOf(
        ColorContrastValidator.validateColorCombination(
            foreground = onBackground,
            background = background,
        ),
    )
}

/**
 * Validate error color combinations.
 */
private fun ColorScheme.validateErrorColors(): List<ContrastValidationResult> {
    return listOf(
        ColorContrastValidator.validateColorCombination(
            foreground = onError,
            background = error,
        ),
        ColorContrastValidator.validateColorCombination(
            foreground = onErrorContainer,
            background = errorContainer,
        ),
    )
}

/**
 * Extension function to validate a BaseAppTheme for accessibility compliance.
 *
 * @return ThemeValidationResult for the theme's color scheme
 */
fun BaseAppTheme.validateAccessibility(): ThemeValidationResult {
    return this.colorScheme.validateAccessibility()
}

/**
 * Composable function to remember theme validation results.
 * This prevents recalculation on every recomposition.
 *
 * @param theme The theme to validate
 * @return Remembered ThemeValidationResult
 */
@Composable
fun rememberThemeValidation(theme: BaseAppTheme): ThemeValidationResult {
    return remember(theme) {
        theme.validateAccessibility()
    }
}

/**
 * Check if two specific colors meet WCAG AA standards (4.5:1).
 *
 * @param foreground Foreground color (typically text)
 * @param background Background color
 * @return True if contrast ratio meets WCAG AA standards
 */
fun validateColorPair(foreground: Color, background: Color): Boolean {
    return ColorContrastValidator.meetsWcagAA(foreground, background)
}

private const val EXCELLENT_THRESHOLD = 0.9 // 90% AAA compliance
private const val GOOD_THRESHOLD = 0.8 // 80% AA compliance
private const val ACCEPTABLE_THRESHOLD = 0.7 // 70% AA compliance
private const val NEEDS_IMPROVEMENT_THRESHOLD = 0.6

/**
 * Get a user-friendly accessibility rating for a theme.
 *
 * @param theme The theme to rate
 * @return AccessibilityRating enum value
 */
fun getThemeAccessibilityRating(theme: BaseAppTheme): AccessibilityRating {
    val validation = theme.validateAccessibility()
    val totalChecks = validation.validations.size
    val aaCompliantChecks = validation.validations.count { it.meetsWcagAA }
    val aaaCompliantChecks = validation.validations.count { it.meetsWcagAAA }

    val aaaRatio = aaaCompliantChecks.toDouble() / totalChecks
    val aaRatio = aaCompliantChecks.toDouble() / totalChecks

    return when {
        aaaRatio >= EXCELLENT_THRESHOLD -> AccessibilityRating.EXCELLENT
        aaRatio >= GOOD_THRESHOLD -> AccessibilityRating.GOOD
        aaRatio >= ACCEPTABLE_THRESHOLD -> AccessibilityRating.ACCEPTABLE
        aaRatio >= NEEDS_IMPROVEMENT_THRESHOLD -> AccessibilityRating.NEEDS_IMPROVEMENT
        else -> AccessibilityRating.POOR
    }
}

/**
 * Data class containing validation results for an entire theme.
 */
data class ThemeValidationResult(
    val validations: List<ContrastValidationResult>,
    val colorScheme: ColorScheme,
) {
    /**
     * Get the number of color combinations that meet WCAG AA standards.
     */
    val wcagAACompliantCount: Int
        get() = validations.count { it.meetsWcagAA }

    /**
     * Get the number of color combinations that meet WCAG AAA standards.
     */
    val wcagAAACompliantCount: Int
        get() = validations.count { it.meetsWcagAAA }

    /**
     * Get the total number of color combinations checked.
     */
    val totalChecks: Int
        get() = validations.size

    /**
     * Check if all color combinations meet WCAG AA standards.
     */
    val isFullyWcagAACompliant: Boolean
        get() = wcagAACompliantCount == totalChecks

    /**
     * Check if all color combinations meet WCAG AAA standards.
     */
    val isFullyWcagAAACompliant: Boolean
        get() = wcagAAACompliantCount == totalChecks

    /**
     * Get the worst (lowest) contrast ratio in the theme.
     */
    val worstContrastRatio: Double
        get() = validations.minOfOrNull { it.contrastRatio } ?: 0.0

    /**
     * Get the best (highest) contrast ratio in the theme.
     */
    val bestContrastRatio: Double
        get() = validations.maxOfOrNull { it.contrastRatio } ?: 0.0

    /**
     * Get a summary of the theme's accessibility compliance.
     */
    val accessibilitySummary: String
        get() = when {
            isFullyWcagAAACompliant -> "Excellent accessibility (WCAG AAA)"
            isFullyWcagAACompliant -> "Good accessibility (WCAG AA)"
            wcagAACompliantCount >= (totalChecks * ACCESSIBLE_THRESHOLD).toInt() -> "Mostly accessible"
            else -> "Accessibility concerns detected"
        }

    /**
     * Get validation results that failed WCAG AA standards.
     */
    val failedValidations: List<ContrastValidationResult>
        get() = validations.filter { !it.meetsWcagAA }
}

/**
 * Enum representing accessibility rating levels.
 */
private const val ACCESSIBLE_THRESHOLD = 0.8

enum class AccessibilityRating(val displayName: String, val description: String) {
    EXCELLENT("Excellent", "Exceeds WCAG AAA standards"),
    GOOD("Good", "Meets WCAG AA standards"),
    ACCEPTABLE("Acceptable", "Meets most WCAG standards"),
    NEEDS_IMPROVEMENT("Needs Improvement", "Some accessibility concerns"),
    POOR("Poor", "Significant accessibility issues"),
}
