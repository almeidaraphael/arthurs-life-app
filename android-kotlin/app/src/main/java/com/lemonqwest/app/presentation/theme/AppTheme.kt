package com.lemonqwest.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import com.lemonqwest.app.domain.theme.model.AppTheme

@Composable
fun AppTheme(
    appTheme: AppTheme = defaultAppTheme(),
    content: @Composable () -> Unit,
) {
    val theme = ThemeManager.getTheme(appTheme)

    MaterialTheme(
        colorScheme = theme.colorScheme,
        shapes = theme.shapes,
        typography = theme.typography,
    ) {
        CompositionLocalProvider(
            LocalAppTheme provides appTheme,
            LocalBaseTheme provides theme,
        ) {
            com.lemonqwest.app.presentation.theme.components.ThemeAwareBackground(
                theme = theme,
            ) {
                content()
            }
        }
    }
}

@Composable
fun defaultAppTheme(): AppTheme {
    return if (isSystemInDarkTheme()) {
        AppTheme.MATERIAL_DARK
    } else {
        AppTheme.MATERIAL_LIGHT
    }
}

/**
 * Composition local for accessing the current AppTheme throughout the app.
 */
val LocalAppTheme = compositionLocalOf { AppTheme.MATERIAL_LIGHT }

/**
 * Composition local for accessing the current BaseAppTheme implementation throughout the app.
 */
val LocalBaseTheme = compositionLocalOf<BaseAppTheme> {
    ThemeManager.getTheme(
        AppTheme.MATERIAL_LIGHT,
    )
}
