package com.arthurslife.app.data.theme

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import com.arthurslife.app.domain.user.UserRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeRepositoryImpl @Inject constructor(
    private val themePreferencesDataStore: ThemePreferencesDataStore,
) : ThemeRepository {

    override fun getTheme(userRole: UserRole): Flow<AppTheme> {
        return themePreferencesDataStore.getTheme(userRole)
    }

    override suspend fun saveTheme(userRole: UserRole, theme: AppTheme) {
        themePreferencesDataStore.saveTheme(userRole, theme)
    }

    override fun getAvailableThemes(): List<AppTheme> {
        return AppTheme.values().toList()
    }
}
