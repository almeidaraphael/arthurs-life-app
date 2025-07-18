package com.lemonqwest.app.domain.theme.usecase

import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.theme.repository.ThemeRepository
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
 * Focused test suite for GetThemeUseCase functionality.
 *
 * Tests cover:
 * - Getting theme for user by ID
 * - Getting theme for different users
 * - Handling multiple theme changes
 * - Flow behavior from repository
 * - All available themes handling
 * - Repository delegation
 * - Operator invoke syntax support
 */
@DisplayName("GetThemeUseCase Tests")
class GetThemeUseCaseTest {

    private lateinit var themeRepository: ThemeRepository
    private lateinit var getThemeUseCase: GetThemeUseCase

    @BeforeEach
    fun setUp() {
        themeRepository = mockk()
        getThemeUseCase = GetThemeUseCase(themeRepository)

        // Set up default mock behavior
        every { themeRepository.getTheme(any()) } returns flowOf(AppTheme.MATERIAL_LIGHT)
        // Default stub for any un-stubbed calls
        io.mockk.every { themeRepository.getTheme(any()) } returns flowOf(AppTheme.MATERIAL_LIGHT)
    }

    @Test
    @DisplayName("Should get theme for user by ID")
    fun shouldGetThemeForUserById() = runTest {
        // Given
        val userId = "child-user-123"
        val expectedTheme = AppTheme.MARIO_CLASSIC
        every { themeRepository.getTheme(userId) } returns flowOf(expectedTheme)

        // When
        val result = getThemeUseCase(userId).first()

        // Then
        assertEquals(expectedTheme, result, "Should return Mario Classic theme for user")
        verify(exactly = 1) { themeRepository.getTheme(userId) }
    }

    @Test
    @DisplayName("Should get theme for different user ID")
    fun shouldGetThemeForDifferentUserId() = runTest {
        // Given
        val userId = "caregiver-user-456"
        val expectedTheme = AppTheme.MATERIAL_LIGHT
        every { themeRepository.getTheme(userId) } returns flowOf(expectedTheme)

        // When
        val result = getThemeUseCase(userId).first()

        // Then
        assertEquals(
            expectedTheme,
            result,
            "Should return Material Light theme for different user",
        )
        verify(exactly = 1) { themeRepository.getTheme(userId) }
    }

    @Test
    @DisplayName("Should handle multiple theme changes for same user")
    fun shouldHandleMultipleThemeChangesForSameUser() = runTest {
        // Given
        val userId = "user-123"
        val themes = listOf(
            AppTheme.MARIO_CLASSIC,
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
        )
        every { themeRepository.getTheme(userId) } returns flowOf(
            themes[0], themes[1], themes[2],
        )

        // When
        val results = mutableListOf<AppTheme>()
        getThemeUseCase(userId).collect { theme ->
            results.add(theme)
        }

        // Then
        assertEquals(3, results.size, "Should collect all theme changes")
        assertEquals(themes[0], results[0], "First theme should match")
        assertEquals(themes[1], results[1], "Second theme should match")
        assertEquals(themes[2], results[2], "Third theme should match")
        verify(exactly = 1) { themeRepository.getTheme(userId) }
    }

    @Test
    @DisplayName("Should return Flow from repository")
    fun shouldReturnFlowFromRepository() = runTest {
        // Given
        val userId = "user-456"
        val expectedTheme = AppTheme.MATERIAL_DARK
        val themeFlow = flowOf(expectedTheme)
        every { themeRepository.getTheme(userId) } returns themeFlow

        // When
        val resultFlow = getThemeUseCase(userId)
        val result = resultFlow.first()

        // Then
        assertEquals(expectedTheme, result, "Should return theme from repository Flow")
        verify(exactly = 1) { themeRepository.getTheme(userId) }
    }

    @Test
    @DisplayName("Should handle all available themes")
    fun shouldHandleAllAvailableThemes() = runTest {
        // Given
        val userId = "child-user-123"
        val allThemes = AppTheme.values().toList()

        // When/Then
        allThemes.forEach { theme ->
            every { themeRepository.getTheme(userId) } returns flowOf(theme)

            val result = getThemeUseCase(userId).first()
            assertEquals(theme, result, "Should handle $theme correctly")
        }

        verify(exactly = allThemes.size) { themeRepository.getTheme(userId) }
    }

    @Test
    @DisplayName("Should delegate to repository correctly")
    fun shouldDelegateToRepositoryCorrectly() = runTest {
        // Given
        val userId = "caregiver-user-456"
        val expectedTheme = AppTheme.MATERIAL_LIGHT
        every { themeRepository.getTheme(userId) } returns flowOf(expectedTheme)

        // When
        getThemeUseCase(userId).first()

        // Then
        verify(exactly = 1) { themeRepository.getTheme(userId) }
    }

    @Test
    @DisplayName("Should support operator invoke syntax")
    fun shouldSupportOperatorInvokeSyntax() = runTest {
        // Given
        val userId = "child-user-123"
        val expectedTheme = AppTheme.MARIO_CLASSIC
        every { themeRepository.getTheme(userId) } returns flowOf(expectedTheme)

        // When
        val result = getThemeUseCase.invoke(userId).first()

        // Then
        assertEquals(expectedTheme, result, "Should support operator invoke syntax")
    }
}
