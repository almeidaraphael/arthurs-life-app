package com.arthurslife.app.data.theme

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val themePreferencesDataStore: ThemePreferencesDataStore,
) : ThemeRepository {

    override fun getTheme(userId: String): Flow<AppTheme> {
        return themePreferencesDataStore.getTheme(userId)
    }

    override suspend fun saveTheme(userId: String, theme: AppTheme) {
        themePreferencesDataStore.saveTheme(userId, theme)
    }

    override fun getAvailableThemes(): List<AppTheme> {
        return AppTheme.values().toList()
    }
}
