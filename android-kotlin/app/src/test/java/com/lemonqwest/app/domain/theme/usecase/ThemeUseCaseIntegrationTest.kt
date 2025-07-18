package com.lemonqwest.app.domain.theme.usecase

import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.theme.repository.ThemeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for theme use case integration scenarios.
 *
 * Tests cover:
 * - Realistic theme switching scenarios
 * - Different users independently
 * - Consistent repository interactions
 * - Concurrent use case execution
 */
@DisplayName("Theme Use Case Integration Tests")
class ThemeUseCaseIntegrationTest {

    private lateinit var themeRepository: ThemeRepository
    private lateinit var getThemeUseCase: GetThemeUseCase
    private lateinit var saveThemeUseCase: SaveThemeUseCase
    private lateinit var getAvailableThemesUseCase: GetAvailableThemesUseCase

    @BeforeEach
    fun setUp() {
        themeRepository = mockk()
        getThemeUseCase = GetThemeUseCase(themeRepository)
        saveThemeUseCase = SaveThemeUseCase(themeRepository)
        getAvailableThemesUseCase = GetAvailableThemesUseCase(themeRepository)

        // Set up default mock behavior
        every { themeRepository.getTheme(any()) } returns flowOf(AppTheme.MATERIAL_LIGHT)
        every {
            themeRepository.getAvailableThemes()
        } returns listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK, AppTheme.MARIO_CLASSIC)
        coEvery { themeRepository.saveTheme(any(), any()) } returns Unit
    }

    @Test
    @DisplayName("Should work together in realistic theme switching scenario")
    fun shouldWorkTogetherInRealisticThemeSwitchingScenario() = runTest {
        // Given
        val userId = "child-user-123"
        val currentTheme = AppTheme.MARIO_CLASSIC
        val newTheme = AppTheme.MATERIAL_LIGHT
        val availableThemes = listOf(
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
            AppTheme.MARIO_CLASSIC,
        )

        every { themeRepository.getTheme(userId) } returns flowOf(currentTheme, newTheme)
        every { themeRepository.getAvailableThemes() } returns availableThemes
        coEvery { themeRepository.saveTheme(userId, newTheme) } returns Unit

        // When
        val initialTheme = getThemeUseCase(userId).first()
        val availableOptions = getAvailableThemesUseCase()
        saveThemeUseCase(userId, newTheme)

        // Then
        assertEquals(currentTheme, initialTheme, "Should get initial theme")
        assertEquals(availableThemes, availableOptions, "Should get available themes")

        verify(exactly = 1) { themeRepository.getTheme(userId) }
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, newTheme) }
    }

    @Test
    @DisplayName("Should handle different users independently")
    fun shouldHandleDifferentUsersIndependently() = runTest {
        // Given
        val childUserId = "child-user-123"
        val caregiverUserId = "caregiver-user-456"
        val childTheme = AppTheme.MARIO_CLASSIC
        val caregiverTheme = AppTheme.MATERIAL_DARK

        every { themeRepository.getTheme(childUserId) } returns flowOf(childTheme)
        every { themeRepository.getTheme(caregiverUserId) } returns flowOf(caregiverTheme)
        coEvery { themeRepository.saveTheme(childUserId, childTheme) } returns Unit
        coEvery { themeRepository.saveTheme(caregiverUserId, caregiverTheme) } returns Unit

        // When
        val childResult = getThemeUseCase(childUserId).first()
        val caregiverResult = getThemeUseCase(caregiverUserId).first()
        saveThemeUseCase(childUserId, childTheme)
        saveThemeUseCase(caregiverUserId, caregiverTheme)

        // Then
        assertEquals(childTheme, childResult, "Should handle child theme independently")
        assertEquals(
            caregiverTheme,
            caregiverResult,
            "Should handle caregiver theme independently",
        )

        verify(exactly = 1) { themeRepository.getTheme(childUserId) }
        verify(exactly = 1) { themeRepository.getTheme(caregiverUserId) }
        coVerify(exactly = 1) { themeRepository.saveTheme(childUserId, childTheme) }
        coVerify(exactly = 1) { themeRepository.saveTheme(caregiverUserId, caregiverTheme) }
    }

    @Test
    @DisplayName("Should maintain consistent repository interactions")
    fun shouldMaintainConsistentRepositoryInteractions() = runTest {
        // Given
        val userId = "caregiver-user-456"
        val theme = AppTheme.MATERIAL_LIGHT
        val availableThemes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK)

        every { themeRepository.getTheme(userId) } returns flowOf(theme)
        every { themeRepository.getAvailableThemes() } returns availableThemes
        coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

        // When
        getThemeUseCase(userId).first()
        getAvailableThemesUseCase()
        saveThemeUseCase(userId, theme)

        // Then
        verify(exactly = 1) { themeRepository.getTheme(userId) }
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
    }

    @Test
    @DisplayName("Should support concurrent use case execution")
    fun shouldSupportConcurrentUseCaseExecution() = runTest {
        // Given
        val userId = "child-user-123"
        val theme = AppTheme.MARIO_CLASSIC
        val availableThemes = listOf(AppTheme.MARIO_CLASSIC, AppTheme.MATERIAL_LIGHT)

        every { themeRepository.getTheme(userId) } returns flowOf(theme)
        every { themeRepository.getAvailableThemes() } returns availableThemes
        coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

        // When
        val getThemeResult = getThemeUseCase(userId).first()
        val availableThemesResult = getAvailableThemesUseCase()
        saveThemeUseCase(userId, theme)

        // Then
        assertEquals(theme, getThemeResult, "Should handle concurrent get theme")
        assertEquals(
            availableThemes,
            availableThemesResult,
            "Should handle concurrent get available themes",
        )

        verify(exactly = 1) { themeRepository.getTheme(userId) }
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
        coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
    }
}
