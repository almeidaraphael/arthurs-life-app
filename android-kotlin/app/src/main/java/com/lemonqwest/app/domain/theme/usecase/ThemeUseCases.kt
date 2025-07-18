package com.lemonqwest.app.domain.theme.usecase

import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    operator fun invoke(userId: String): Flow<AppTheme> {
        return themeRepository.getTheme(userId)
    }
}

class SaveThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    suspend operator fun invoke(userId: String, theme: AppTheme) {
        themeRepository.saveTheme(userId, theme)
    }
}

class GetAvailableThemesUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    operator fun invoke(): List<AppTheme> {
        return themeRepository.getAvailableThemes()
    }
}
