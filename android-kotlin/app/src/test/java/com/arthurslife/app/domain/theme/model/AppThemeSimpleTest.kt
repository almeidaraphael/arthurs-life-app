package com.arthurslife.app.domain.theme.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Comprehensive test suite for AppTheme enum and theme capabilities.
 *
 * This test suite validates the theme system's core domain model, ensuring proper
 * enum values, theme capabilities work correctly across all supported themes.
 *
 * Coverage includes:
 * - AppTheme enum structure and values
 * - Theme capabilities and feature flags
 * - Extension functions and utility methods
 */
@DisplayName("AppTheme Domain Model Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppThemeSimpleTest {

    @Nested
    @DisplayName("AppTheme Enum")
    inner class AppThemeEnum {

        @Test
        @DisplayName("Should have correct enum values")
        fun shouldHaveCorrectEnumValues() {
            // Given - AppTheme enum
            val expectedThemes = setOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            // When
            val actualThemes = AppTheme.values().toSet()

            // Then
            assertEquals(expectedThemes, actualThemes, "AppTheme should have expected values")
            assertEquals(3, AppTheme.values().size, "AppTheme should have exactly 3 values")
        }

        @Test
        @DisplayName("Should have correct Material Light theme properties")
        fun shouldHaveCorrectMaterialLightProperties() {
            // Given
            val theme = AppTheme.MATERIAL_LIGHT

            // When/Then
            assertEquals("material_light", theme.key, "Material Light should have correct key")
            assertEquals(
                "Material Light",
                theme.displayName,
                "Material Light should have correct display name",
            )
        }

        @Test
        @DisplayName("Should have correct Material Dark theme properties")
        fun shouldHaveCorrectMaterialDarkProperties() {
            // Given
            val theme = AppTheme.MATERIAL_DARK

            // When/Then
            assertEquals("material_dark", theme.key, "Material Dark should have correct key")
            assertEquals(
                "Material Dark",
                theme.displayName,
                "Material Dark should have correct display name",
            )
        }

        @Test
        @DisplayName("Should have correct Mario Classic theme properties")
        fun shouldHaveCorrectMarioClassicProperties() {
            // Given
            val theme = AppTheme.MARIO_CLASSIC

            // When/Then
            assertEquals("mario_classic", theme.key, "Mario Classic should have correct key")
            assertEquals(
                "Mario Classic",
                theme.displayName,
                "Mario Classic should have correct display name",
            )
        }

        @Test
        @DisplayName("Should have unique keys for all themes")
        fun shouldHaveUniqueKeysForAllThemes() {
            // Given
            val themes = AppTheme.values()

            // When
            val keys = themes.map { it.key }
            val uniqueKeys = keys.toSet()

            // Then
            assertEquals(keys.size, uniqueKeys.size, "All theme keys should be unique")
        }

        @Test
        @DisplayName("Should have unique display names for all themes")
        fun shouldHaveUniqueDisplayNamesForAllThemes() {
            // Given
            val themes = AppTheme.values()

            // When
            val displayNames = themes.map { it.displayName }
            val uniqueDisplayNames = displayNames.toSet()

            // Then
            assertEquals(
                displayNames.size,
                uniqueDisplayNames.size,
                "All theme display names should be unique",
            )
        }

        @Test
        @DisplayName("Should support enum valueOf operations")
        fun shouldSupportEnumValueOfOperations() {
            // Given
            val themeNames = listOf("MATERIAL_LIGHT", "MATERIAL_DARK", "MARIO_CLASSIC")

            // When/Then
            themeNames.forEach { name ->
                val theme = AppTheme.valueOf(name)
                assertNotNull(theme, "Theme $name should be retrievable via valueOf")
                assertEquals(name, theme.name, "Theme name should match")
            }
        }

        @Test
        @DisplayName("Should have consistent key format")
        fun shouldHaveConsistentKeyFormat() {
            // Given
            val themes = AppTheme.values()

            // When/Then
            themes.forEach { theme ->
                assertTrue(
                    theme.key.matches(Regex("^[a-z_]+$")),
                    "Theme key '${theme.key}' should be lowercase with underscores",
                )
                assertFalse(
                    theme.key.startsWith("_") || theme.key.endsWith("_"),
                    "Theme key '${theme.key}' should not start or end with underscore",
                )
            }
        }
    }

    @Nested
    @DisplayName("Theme Capabilities")
    inner class ThemeCapabilities {

        @Test
        @DisplayName("Should have Material theme capabilities")
        fun shouldHaveMaterialThemeCapabilities() {
            // Given
            val capabilities = MaterialThemeCapabilities

            // When/Then
            assertFalse(capabilities.hasCustomIcons, "Material theme should not have custom icons")
            assertFalse(
                capabilities.hasCustomShapes,
                "Material theme should not have custom shapes",
            )
            assertFalse(
                capabilities.hasCustomAvatars,
                "Material theme should not have custom avatars",
            )
            assertFalse(
                capabilities.hasCustomProgressIndicators,
                "Material theme should not have custom progress indicators",
            )
            assertFalse(
                capabilities.hasCustomMotivationalContent,
                "Material theme should not have custom motivational content",
            )
        }

        @Test
        @DisplayName("Should have Mario theme capabilities")
        fun shouldHaveMarioThemeCapabilities() {
            // Given
            val capabilities = MarioThemeCapabilities

            // When/Then
            assertTrue(capabilities.hasCustomIcons, "Mario theme should have custom icons")
            assertTrue(capabilities.hasCustomShapes, "Mario theme should have custom shapes")
            assertTrue(capabilities.hasCustomAvatars, "Mario theme should have custom avatars")
            assertTrue(
                capabilities.hasCustomProgressIndicators,
                "Mario theme should have custom progress indicators",
            )
            assertTrue(
                capabilities.hasCustomMotivationalContent,
                "Mario theme should have custom motivational content",
            )
        }

        @Test
        @DisplayName("Should provide correct capabilities for each theme")
        fun shouldProvideCorrectCapabilitiesForEachTheme() {
            // Given
            val materialLightCapabilities = AppTheme.MATERIAL_LIGHT.getCapabilities()
            val materialDarkCapabilities = AppTheme.MATERIAL_DARK.getCapabilities()
            val marioCapabilities = AppTheme.MARIO_CLASSIC.getCapabilities()

            // When/Then
            // Material themes should have same capabilities
            assertEquals(
                MaterialThemeCapabilities,
                materialLightCapabilities,
                "Material Light should have Material capabilities",
            )
            assertEquals(
                MaterialThemeCapabilities,
                materialDarkCapabilities,
                "Material Dark should have Material capabilities",
            )

            // Mario theme should have different capabilities
            assertEquals(
                MarioThemeCapabilities,
                marioCapabilities,
                "Mario Classic should have Mario capabilities",
            )
        }

        @Test
        @DisplayName("Should implement ThemeCapabilities interface correctly")
        fun shouldImplementThemeCapabilitiesInterfaceCorrectly() {
            // Given
            val capabilities = listOf(MaterialThemeCapabilities, MarioThemeCapabilities)

            // When/Then
            capabilities.forEach { capability ->
                assertTrue(
                    capability is com.arthurslife.app.domain.theme.model.ThemeCapabilities,
                    "All capability objects should implement ThemeCapabilities interface",
                )
            }
        }

        @Test
        @DisplayName("Should have consistent capability behavior")
        fun shouldHaveConsistentCapabilityBehavior() {
            // Given
            val allThemes = AppTheme.values()

            // When/Then
            allThemes.forEach { theme ->
                val capabilities = theme.getCapabilities()
                assertNotNull(capabilities, "Theme $theme should have capabilities")

                // Verify all boolean properties are accessible
                val hasCustomIcons = capabilities.hasCustomIcons
                val hasCustomShapes = capabilities.hasCustomShapes
                val hasCustomAvatars = capabilities.hasCustomAvatars
                val hasCustomProgressIndicators = capabilities.hasCustomProgressIndicators
                val hasCustomMotivationalContent = capabilities.hasCustomMotivationalContent

                // Properties should be deterministic
                assertTrue(
                    hasCustomIcons == capabilities.hasCustomIcons,
                    "hasCustomIcons should be consistent",
                )
                assertTrue(
                    hasCustomShapes == capabilities.hasCustomShapes,
                    "hasCustomShapes should be consistent",
                )
                assertTrue(
                    hasCustomAvatars == capabilities.hasCustomAvatars,
                    "hasCustomAvatars should be consistent",
                )
                assertTrue(
                    hasCustomProgressIndicators == capabilities.hasCustomProgressIndicators,
                    "hasCustomProgressIndicators should be consistent",
                )
                assertTrue(
                    hasCustomMotivationalContent == capabilities.hasCustomMotivationalContent,
                    "hasCustomMotivationalContent should be consistent",
                )
            }
        }
    }

    @Nested
    @DisplayName("Theme System Integration")
    inner class ThemeSystemIntegration {

        @Test
        @DisplayName("Should maintain consistency between theme types and capabilities")
        fun shouldMaintainConsistencyBetweenThemeTypesAndCapabilities() {
            // Given
            val allThemes = AppTheme.values()

            // When/Then
            allThemes.forEach { theme ->
                val capabilities = theme.getCapabilities()

                when (theme) {
                    AppTheme.MARIO_CLASSIC -> {
                        assertTrue(
                            capabilities.hasCustomIcons,
                            "Mario theme should have custom capabilities",
                        )
                        assertTrue(
                            capabilities.hasCustomShapes,
                            "Mario theme should have custom capabilities",
                        )
                        assertTrue(
                            capabilities.hasCustomAvatars,
                            "Mario theme should have custom capabilities",
                        )
                        assertTrue(
                            capabilities.hasCustomProgressIndicators,
                            "Mario theme should have custom capabilities",
                        )
                        assertTrue(
                            capabilities.hasCustomMotivationalContent,
                            "Mario theme should have custom capabilities",
                        )
                    }
                    AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK -> {
                        assertFalse(
                            capabilities.hasCustomIcons,
                            "Material themes should not have custom capabilities",
                        )
                        assertFalse(
                            capabilities.hasCustomShapes,
                            "Material themes should not have custom capabilities",
                        )
                        assertFalse(
                            capabilities.hasCustomAvatars,
                            "Material themes should not have custom capabilities",
                        )
                        assertFalse(
                            capabilities.hasCustomProgressIndicators,
                            "Material themes should not have custom capabilities",
                        )
                        assertFalse(
                            capabilities.hasCustomMotivationalContent,
                            "Material themes should not have custom capabilities",
                        )
                    }
                }
            }
        }

        @Test
        @DisplayName("Should handle all theme enum values in capabilities")
        fun shouldHandleAllThemeEnumValuesInCapabilities() {
            // Given
            val allThemes = AppTheme.values()

            // When/Then
            allThemes.forEach { theme ->
                // All capability functions should handle all enum values without throwing
                assertNotNull(
                    theme.getCapabilities(),
                    "Should handle capabilities for $theme",
                )
            }
        }
    }
}
