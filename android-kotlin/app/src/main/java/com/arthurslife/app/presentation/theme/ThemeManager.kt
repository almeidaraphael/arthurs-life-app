package com.arthurslife.app.presentation.theme

import androidx.compose.ui.graphics.Color
import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.presentation.theme.mario.MarioTheme
import com.arthurslife.app.presentation.theme.materialdark.MaterialDarkTheme
import com.arthurslife.app.presentation.theme.materiallight.MaterialLightTheme

/**
 * Central orchestrator for all available app themes.
 * Provides registration, lookup, and fallback logic.
 */
object ThemeManager {
    private val themes: Map<AppTheme, BaseAppTheme> = mapOf(
        AppTheme.MATERIAL_LIGHT to MaterialLightTheme,
        AppTheme.MATERIAL_DARK to MaterialDarkTheme,
        AppTheme.MARIO_CLASSIC to MarioTheme,
    )

    /**
     * Returns the theme for the given key, or [MaterialLightTheme] as fallback.
     */
    fun getTheme(appTheme: AppTheme?): BaseAppTheme {
        return themes[appTheme] ?: MaterialLightTheme
    }

    /**
     * Returns the AppTheme key for a given BaseAppTheme instance, or null if not found.
     * Uses the unique key property for robust mapping.
     */
    fun getAppThemeKey(theme: BaseAppTheme): AppTheme? {
        return themes.entries.firstOrNull { it.value.displayName == theme.displayName }?.key
    }

    /**
     * Returns the AppTheme for a given key string, or null if not found.
     */
    fun getAppThemeByKey(key: String): AppTheme? {
        return themes.keys.firstOrNull { it.key == key }
    }

    /**
     * Returns all registered themes.
     */
    fun allThemes(): Collection<BaseAppTheme> = themes.values

    /**
     * Returns theme metadata for UI display purposes.
     */
    fun getThemeMetadata(): List<ThemeMetadata> = themes.map { (key, theme) ->
        ThemeMetadata(
            key = key,
            displayName = theme.displayName,
            description = theme.description,
            previewColors = listOf(
                theme.colorScheme.primary,
                theme.colorScheme.secondary,
                theme.colorScheme.tertiary,
            ),
            avatarContent = theme.avatarContent,
        )
    }
}

/**
 * Metadata for theme selection UI.
 */
data class ThemeMetadata(
    val key: AppTheme,
    val displayName: String,
    val description: String,
    val previewColors: List<Color>,
    val avatarContent: String,
)
