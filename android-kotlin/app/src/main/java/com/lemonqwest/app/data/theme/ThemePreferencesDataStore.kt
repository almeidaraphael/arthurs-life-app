package com.lemonqwest.app.data.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lemonqwest.app.domain.theme.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences",
)

class ThemePreferencesDataStore(
    private val context: Context,
) {
    private companion object {
        private const val THEME_KEY_PREFIX = "user_theme_"
    }

    private fun getThemeKey(userId: String): Preferences.Key<String> {
        return stringPreferencesKey("${THEME_KEY_PREFIX}$userId")
    }

    fun getTheme(userId: String): Flow<AppTheme> {
        return context.themeDataStore.data.map { preferences ->
            val defaultTheme = AppTheme.MATERIAL_LIGHT
            val themeKey = preferences[getThemeKey(userId)] ?: defaultTheme.key
            AppTheme.values().firstOrNull { it.key == themeKey } ?: defaultTheme
        }
    }

    suspend fun saveTheme(userId: String, theme: AppTheme) {
        context.themeDataStore.edit { preferences ->
            preferences[getThemeKey(userId)] = theme.key
        }
    }

    suspend fun clearTheme(userId: String) {
        context.themeDataStore.edit { preferences ->
            preferences.remove(getThemeKey(userId))
        }
    }

    suspend fun clearAllThemes() {
        context.themeDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
