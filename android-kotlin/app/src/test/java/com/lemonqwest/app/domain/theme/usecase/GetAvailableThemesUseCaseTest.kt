package com.lemonqwest.app.domain.theme.usecase

import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.theme.repository.ThemeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for GetAvailableThemesUseCase functionality.
 *
 * Tests cover:
 * - Getting all available themes
 * - Handling empty theme list
 * - Handling single theme
 * - Maintaining theme order from repository
 * - Repository delegation
 * - Operator invoke syntax support
 * - Handling all possible theme combinations
 */
@DisplayName("GetAvailableThemesUseCase Tests")
class GetAvailableThemesUseCaseTest {

    private lateinit var themeRepository: ThemeRepository
    private lateinit var getAvailableThemesUseCase: GetAvailableThemesUseCase

    @BeforeEach
    fun setUp() {
        themeRepository = mockk()
        getAvailableThemesUseCase = GetAvailableThemesUseCase(themeRepository)

        // Set up default mock behavior
        every {
            themeRepository.getAvailableThemes()
        } returns listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK, AppTheme.MARIO_CLASSIC)
    }

    @Test
    @DisplayName("Should get all available themes")
    fun shouldGetAllAvailableThemes() {
        // Given
        val expectedThemes = listOf(
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
            AppTheme.MARIO_CLASSIC,
        )
        every { themeRepository.getAvailableThemes() } returns expectedThemes

        // When
        val result = getAvailableThemesUseCase()

        // Then
        assertEquals(expectedThemes, result, "Should return all available themes")
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
    }

    @Test
    @DisplayName("Should handle empty theme list")
    fun shouldHandleEmptyThemeList() {
        // Given
        val emptyThemes = emptyList<AppTheme>()
        every { themeRepository.getAvailableThemes() } returns emptyThemes

        // When
        val result = getAvailableThemesUseCase()

        // Then
        assertTrue(result.isEmpty(), "Should return empty list when no themes available")
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
    }

    @Test
    @DisplayName("Should handle single theme")
    fun shouldHandleSingleTheme() {
        // Given
        val singleTheme = listOf(AppTheme.MARIO_CLASSIC)
        every { themeRepository.getAvailableThemes() } returns singleTheme

        // When
        val result = getAvailableThemesUseCase()

        // Then
        assertEquals(1, result.size, "Should return single theme")
        assertEquals(AppTheme.MARIO_CLASSIC, result[0], "Should return correct theme")
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
    }

    @Test
    @DisplayName("Should maintain theme order from repository")
    fun shouldMaintainThemeOrderFromRepository() {
        // Given
        val orderedThemes = listOf(
            AppTheme.MARIO_CLASSIC,
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
        )
        every { themeRepository.getAvailableThemes() } returns orderedThemes

        // When
        val result = getAvailableThemesUseCase()

        // Then
        assertEquals(orderedThemes, result, "Should maintain theme order from repository")
        assertEquals(AppTheme.MARIO_CLASSIC, result[0], "First theme should match")
        assertEquals(AppTheme.MATERIAL_LIGHT, result[1], "Second theme should match")
        assertEquals(AppTheme.MATERIAL_DARK, result[2], "Third theme should match")
    }

    @Test
    @DisplayName("Should delegate to repository correctly")
    fun shouldDelegateToRepositoryCorrectly() {
        // Given
        val themes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK)
        every { themeRepository.getAvailableThemes() } returns themes

        // When
        getAvailableThemesUseCase()

        // Then
        verify(exactly = 1) { themeRepository.getAvailableThemes() }
    }

    @Test
    @DisplayName("Should support operator invoke syntax")
    fun shouldSupportOperatorInvokeSyntax() {
        // Given
        val themes = listOf(AppTheme.MARIO_CLASSIC)
        every { themeRepository.getAvailableThemes() } returns themes

        // When
        val result = getAvailableThemesUseCase.invoke()

        // Then
        assertEquals(themes, result, "Should support operator invoke syntax")
    }

    @Test
    @DisplayName("Should handle all possible theme combinations")
    fun shouldHandleAllPossibleThemeCombinations() {
        // Given
        val allThemes = AppTheme.values().toList()
        val combinations = listOf(
            listOf(AppTheme.MATERIAL_LIGHT),
            listOf(AppTheme.MATERIAL_DARK),
            listOf(AppTheme.MARIO_CLASSIC),
            listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK),
            listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MARIO_CLASSIC),
            listOf(AppTheme.MATERIAL_DARK, AppTheme.MARIO_CLASSIC),
            allThemes,
        )

        // When/Then
        combinations.forEach { themeList ->
            every { themeRepository.getAvailableThemes() } returns themeList

            val result = getAvailableThemesUseCase()
            assertEquals(themeList, result, "Should handle theme combination: $themeList")
        }
    }
}
