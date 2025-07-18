package com.lemonqwest.app.domain.theme.repository

import com.lemonqwest.app.domain.theme.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getTheme(userId: String): Flow<AppTheme>
    suspend fun saveTheme(userId: String, theme: AppTheme)
    fun getAvailableThemes(): List<AppTheme>
}
