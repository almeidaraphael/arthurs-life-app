package com.arthurslife.app.domain.theme.repository

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.user.UserRole
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getTheme(userRole: UserRole): Flow<AppTheme>
    suspend fun saveTheme(userRole: UserRole, theme: AppTheme)
    fun getAvailableThemes(): List<AppTheme>
}
