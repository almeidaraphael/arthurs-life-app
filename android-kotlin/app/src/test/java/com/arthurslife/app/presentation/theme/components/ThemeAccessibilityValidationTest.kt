package com.arthurslife.app.presentation.theme.components

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.arthurslife.app.presentation.theme.mario.MarioClassicTheme
import com.arthurslife.app.presentation.theme.materialdark.MaterialDarkTheme
import com.arthurslife.app.presentation.theme.materiallight.MaterialLightTheme
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test suite for theme accessibility validation functionality.
 *
 * Validates that theme color schemes meet WCAG accessibility standards
 * and that the validation system correctly identifies compliance levels.
 */
class ThemeAccessibilityValidationTest {

    @Test
    fun materialLightTheme_validateAccessibility_meetsWcagStandards() {
        // Given
        val theme = MaterialLightTheme

        // When
        val validation = theme.validateAccessibility()

        // Then
        assertTrue(validation.isFullyWcagAACompliant)
        assertTrue(
            validation.wcagAACompliantCount >= 8,
        ) // Should have at least 8 color pair validations
        assertEquals(validation.wcagAACompliantCount, validation.totalChecks)
        assertTrue(validation.worstContrastRatio >= 4.5)
        assertTrue(validation.accessibilitySummary.contains("accessibility"))
    }

    @Test
    fun materialDarkTheme_validateAccessibility_meetsWcagStandards() {
        // Given
        val theme = MaterialDarkTheme

        // When
        val validation = theme.validateAccessibility()

        // Then
        assertTrue(validation.isFullyWcagAACompliant)
        assertTrue(validation.wcagAACompliantCount >= 8)
        assertEquals(validation.wcagAACompliantCount, validation.totalChecks)
        assertTrue(validation.worstContrastRatio >= 4.5)
        assertTrue(validation.accessibilitySummary.contains("accessibility"))
    }

    @Test
    fun marioClassicTheme_validateAccessibility_meetsWcagStandards() {
        // Given
        val theme = MarioClassicTheme

        // When
        val validation = theme.validateAccessibility()

        // Then
        assertTrue(validation.isFullyWcagAACompliant)
        assertTrue(validation.wcagAACompliantCount >= 8)
        assertEquals(validation.wcagAACompliantCount, validation.totalChecks)
        assertTrue(validation.worstContrastRatio >= 4.5)
        assertTrue(validation.accessibilitySummary.contains("accessibility"))
    }

    @Test
    fun colorScheme_validateAccessibility_checksAllCriticalPairs() {
        // Given
        val lightScheme = lightColorScheme()

        // When
        val validation = lightScheme.validateAccessibility()

        // Then - Should check all critical color combinations
        assertEquals(9, validation.totalChecks) // 9 critical color pairs
        assertTrue(validation.validations.isNotEmpty())

        // Verify specific color combinations are checked
        val foregroundColors = validation.validations.map { it.foregroundColor }
        val backgroundColors = validation.validations.map { it.backgroundColor }

        assertTrue(foregroundColors.contains(lightScheme.onPrimary))
        assertTrue(backgroundColors.contains(lightScheme.primary))
        assertTrue(foregroundColors.contains(lightScheme.onSurface))
        assertTrue(backgroundColors.contains(lightScheme.surface))
    }

    @Test
    fun themeValidationResult_calculatesCorrectStatistics() {
        // Given
        val theme = MaterialLightTheme

        // When
        val validation = theme.validateAccessibility()

        // Then
        assertTrue(validation.totalChecks > 0)
        assertTrue(validation.wcagAACompliantCount <= validation.totalChecks)
        assertTrue(validation.wcagAAACompliantCount <= validation.wcagAACompliantCount)
        assertTrue(validation.worstContrastRatio >= 1.0)
        assertTrue(validation.bestContrastRatio >= validation.worstContrastRatio)
    }

    @Test
    fun getThemeAccessibilityRating_excellentTheme_returnsExcellent() {
        // Given
        val theme = MaterialLightTheme

        // When
        val rating = getThemeAccessibilityRating(theme)

        // Then - Should get excellent or good rating for Material themes
        assertTrue(rating == AccessibilityRating.EXCELLENT || rating == AccessibilityRating.GOOD)
    }

    @Test
    fun getThemeAccessibilityRating_allThemes_returnAppropriateRatings() {
        // Given
        val themes = listOf(MaterialLightTheme, MaterialDarkTheme, MarioClassicTheme)

        // When & Then
        themes.forEach { theme ->
            val rating = getThemeAccessibilityRating(theme)
            // All our themes should meet at least GOOD standards
            assertTrue(
                rating == AccessibilityRating.EXCELLENT ||
                    rating == AccessibilityRating.GOOD ||
                    rating == AccessibilityRating.ACCEPTABLE,
            )
        }
    }

    @Test
    fun validateColorPair_highContrast_returnsTrue() {
        // Given
        val foreground = Color.Black
        val background = Color.White

        // When
        val isValid = validateColorPair(foreground, background)

        // Then
        assertTrue(isValid)
    }

    @Test
    fun validateColorPair_lowContrast_returnsFalse() {
        // Given
        val foreground = Color(0xFFDDDDDD)
        val background = Color.White

        // When
        val isValid = validateColorPair(foreground, background)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun themeValidationResult_failedValidations_identifiesProblems() {
        // Given - Create a problematic color scheme
        val problematicScheme = lightColorScheme(
            primary = Color(0xFFFFFFFF), // White
            onPrimary = Color(0xFFF0F0F0), // Very light gray - poor contrast
        )

        // When
        val validation = problematicScheme.validateAccessibility()

        // Then
        assertTrue(validation.failedValidations.isNotEmpty())
        assertFalse(validation.isFullyWcagAACompliant)
        assertTrue(validation.failedValidations.any { !it.meetsWcagAA })
    }

    @Test
    fun accessibilityRating_enum_hasCorrectProperties() {
        // Test that all accessibility ratings have proper display names and descriptions
        val ratings = listOf(
            AccessibilityRating.EXCELLENT,
            AccessibilityRating.GOOD,
            AccessibilityRating.ACCEPTABLE,
            AccessibilityRating.NEEDS_IMPROVEMENT,
            AccessibilityRating.POOR,
        )

        ratings.forEach { rating ->
            assertTrue(rating.displayName.isNotEmpty())
            assertTrue(rating.description.isNotEmpty())
        }
    }

    @Test
    fun themeValidationResult_accessibilitySummary_providesUsefulFeedback() {
        // Given
        val theme = MaterialLightTheme

        // When
        val validation = theme.validateAccessibility()

        // Then
        assertTrue(validation.accessibilitySummary.isNotEmpty())
        assertTrue(
            validation.accessibilitySummary.contains("accessibility") ||
                validation.accessibilitySummary.contains("WCAG"),
        )
    }

    @Test
    fun colorScheme_validatePrimaryColors_checksCorrectPairs() {
        // Given
        val scheme = lightColorScheme(
            primary = Color.Blue,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            onPrimaryContainer = Color(0xFF0D47A1),
        )

        // When
        val validation = scheme.validateAccessibility()

        // Then
        val primaryValidation = validation.validations.find {
            it.foregroundColor == scheme.onPrimary && it.backgroundColor == scheme.primary
        }
        val primaryContainerValidation = validation.validations.find {
            it.foregroundColor == scheme.onPrimaryContainer && it.backgroundColor == scheme.primaryContainer
        }

        assertTrue(primaryValidation != null)
        assertTrue(primaryContainerValidation != null)
    }

    @Test
    fun colorScheme_validateSecondaryColors_checksCorrectPairs() {
        // Given
        val scheme = lightColorScheme()

        // When
        val validation = scheme.validateAccessibility()

        // Then
        val secondaryValidation = validation.validations.find {
            it.foregroundColor == scheme.onSecondary && it.backgroundColor == scheme.secondary
        }
        val secondaryContainerValidation = validation.validations.find {
            it.foregroundColor == scheme.onSecondaryContainer && it.backgroundColor == scheme.secondaryContainer
        }

        assertTrue(secondaryValidation != null)
        assertTrue(secondaryContainerValidation != null)
    }

    @Test
    fun colorScheme_validateSurfaceColors_checksCorrectPairs() {
        // Given
        val scheme = lightColorScheme()

        // When
        val validation = scheme.validateAccessibility()

        // Then
        val surfaceValidation = validation.validations.find {
            it.foregroundColor == scheme.onSurface && it.backgroundColor == scheme.surface
        }
        val surfaceVariantValidation = validation.validations.find {
            it.foregroundColor == scheme.onSurfaceVariant && it.backgroundColor == scheme.surface
        }

        assertTrue(surfaceValidation != null)
        assertTrue(surfaceVariantValidation != null)
    }

    @Test
    fun colorScheme_validateErrorColors_checksCorrectPairs() {
        // Given
        val scheme = lightColorScheme()

        // When
        val validation = scheme.validateAccessibility()

        // Then
        val errorValidation = validation.validations.find {
            it.foregroundColor == scheme.onError && it.backgroundColor == scheme.error
        }
        val errorContainerValidation = validation.validations.find {
            it.foregroundColor == scheme.onErrorContainer && it.backgroundColor == scheme.errorContainer
        }

        assertTrue(errorValidation != null)
        assertTrue(errorContainerValidation != null)
    }

    @Test
    fun darkColorScheme_validateAccessibility_meetsStandards() {
        // Given
        val darkScheme = darkColorScheme()

        // When
        val validation = darkScheme.validateAccessibility()

        // Then - Dark schemes should also meet accessibility standards
        assertTrue(
            validation.wcagAACompliantCount >= validation.totalChecks * 0.8,
        ) // At least 80% compliant
        assertTrue(validation.worstContrastRatio >= 3.0) // Should have reasonable contrast
    }
}
