package com.arthurslife.app.domain.theme.usecase

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import com.arthurslife.app.domain.user.UserRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    operator fun invoke(userRole: UserRole): Flow<AppTheme> {
        return themeRepository.getTheme(userRole)
    }
}

class SaveThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    suspend operator fun invoke(userRole: UserRole, theme: AppTheme) {
        themeRepository.saveTheme(userRole, theme)
    }
}

class GetAvailableThemesUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
) {
    operator fun invoke(): List<AppTheme> {
        return themeRepository.getAvailableThemes()
    }
}
