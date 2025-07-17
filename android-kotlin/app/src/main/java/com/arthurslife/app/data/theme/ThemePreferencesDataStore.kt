package com.arthurslife.app.data.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.user.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences",
)

class ThemePreferencesDataStore(
    private val context: Context,
) {
    private companion object {
        val CHILD_THEME_KEY = stringPreferencesKey("child_theme")
        val CAREGIVER_THEME_KEY = stringPreferencesKey("caregiver_theme")
    }

    private fun getThemeKey(userRole: UserRole): Preferences.Key<String> {
        return when (userRole) {
            UserRole.CHILD -> CHILD_THEME_KEY
            UserRole.CAREGIVER -> CAREGIVER_THEME_KEY
        }
    }

    fun getTheme(userRole: UserRole): Flow<AppTheme> {
        return context.themeDataStore.data.map { preferences ->
            val defaultTheme = when (userRole) {
                UserRole.CHILD -> AppTheme.MARIO_CLASSIC
                UserRole.CAREGIVER -> AppTheme.MATERIAL_LIGHT
            }
            val themeKey = preferences[getThemeKey(userRole)] ?: defaultTheme.key
            AppTheme.values().firstOrNull { it.key == themeKey } ?: defaultTheme
        }
    }

    suspend fun saveTheme(userRole: UserRole, theme: AppTheme) {
        context.themeDataStore.edit { preferences ->
            preferences[getThemeKey(userRole)] = theme.key
        }
    }

    suspend fun clearTheme(userRole: UserRole) {
        context.themeDataStore.edit { preferences ->
            preferences.remove(getThemeKey(userRole))
        }
    }

    suspend fun clearAllThemes() {
        context.themeDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
