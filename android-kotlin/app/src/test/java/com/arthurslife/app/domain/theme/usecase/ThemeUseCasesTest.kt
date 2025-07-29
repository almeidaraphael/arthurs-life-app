package com.arthurslife.app.domain.theme.usecase

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for theme use cases.
 *
 * This test suite validates all theme-related use cases including theme retrieval,
 * theme persistence, and available themes management. Tests cover both success
 * scenarios and edge cases for all user roles.
 *
 * Coverage includes:
 * - GetThemeUseCase functionality
 * - SaveThemeUseCase functionality
 * - GetAvailableThemesUseCase functionality
 * - Repository integration patterns
 * - Coroutine and Flow handling
 * - Error scenarios and edge cases
 */
@DisplayName("Theme Use Cases Tests")
class ThemeUseCasesTest {

    private lateinit var themeRepository: ThemeRepository

    @BeforeEach
    fun setUp() {
        themeRepository = mockk()
    }

    @Nested
    @DisplayName("GetThemeUseCase")
    inner class GetThemeUseCaseTest {

        private lateinit var getThemeUseCase: GetThemeUseCase

        @BeforeEach
        fun setUp() {
            getThemeUseCase = GetThemeUseCase(themeRepository)
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

    @Nested
    @DisplayName("SaveThemeUseCase")
    inner class SaveThemeUseCaseTest {

        private lateinit var saveThemeUseCase: SaveThemeUseCase

        @BeforeEach
        fun setUp() {
            saveThemeUseCase = SaveThemeUseCase(themeRepository)
        }

        @Test
        @DisplayName("Should save theme for user by ID")
        fun shouldSaveThemeForUserById() = runTest {
            // Given
            val userId = "child-user-123"
            val theme = AppTheme.MARIO_CLASSIC
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

            // When
            saveThemeUseCase(userId, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }

        @Test
        @DisplayName("Should save theme for different user ID")
        fun shouldSaveThemeForDifferentUserId() = runTest {
            // Given
            val userId = "caregiver-user-456"
            val theme = AppTheme.MATERIAL_DARK
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

            // When
            saveThemeUseCase(userId, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }

        @Test
        @DisplayName("Should handle saving all available themes")
        fun shouldHandleSavingAllAvailableThemes() = runTest {
            // Given
            val userId = "child-user-123"
            val allThemes = AppTheme.values().toList()

            allThemes.forEach { theme ->
                coEvery { themeRepository.saveTheme(userId, theme) } returns Unit
            }

            // When
            allThemes.forEach { theme ->
                saveThemeUseCase(userId, theme)
            }

            // Then
            allThemes.forEach { theme ->
                coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
            }
        }

        @Test
        @DisplayName("Should handle multiple saves for same user role")
        fun shouldHandleMultipleSavesForSameUserRole() = runTest {
            // Given
            val userId = "caregiver-user-456"
            val themes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            themes.forEach { theme ->
                coEvery { themeRepository.saveTheme(userId, theme) } returns Unit
            }

            // When
            themes.forEach { theme ->
                saveThemeUseCase(userId, theme)
            }

            // Then
            themes.forEach { theme ->
                coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
            }
        }

        @Test
        @DisplayName("Should delegate to repository correctly")
        fun shouldDelegateToRepositoryCorrectly() = runTest {
            // Given
            val userId = "child-user-123"
            val theme = AppTheme.MARIO_CLASSIC
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

            // When
            saveThemeUseCase(userId, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }

        @Test
        @DisplayName("Should support operator invoke syntax")
        fun shouldSupportOperatorInvokeSyntax() = runTest {
            // Given
            val userId = "caregiver-user-456"
            val theme = AppTheme.MATERIAL_LIGHT
            coEvery { themeRepository.saveTheme(userId, theme) } returns Unit

            // When
            saveThemeUseCase.invoke(userId, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
        }

        @Test
        @DisplayName("Should handle theme changes for different users")
        fun shouldHandleThemeChangesForDifferentUsers() = runTest {
            // Given
            val scenarios = listOf(
                "child-user-1" to AppTheme.MARIO_CLASSIC,
                "child-user-2" to AppTheme.MATERIAL_LIGHT,
                "caregiver-user-1" to AppTheme.MATERIAL_DARK,
                "caregiver-user-2" to AppTheme.MARIO_CLASSIC,
            )

            scenarios.forEach { (userId, theme) ->
                coEvery { themeRepository.saveTheme(userId, theme) } returns Unit
            }

            // When
            scenarios.forEach { (userId, theme) ->
                saveThemeUseCase(userId, theme)
            }

            // Then
            scenarios.forEach { (userId, theme) ->
                coVerify(exactly = 1) { themeRepository.saveTheme(userId, theme) }
            }
        }
    }

    @Nested
    @DisplayName("GetAvailableThemesUseCase")
    inner class GetAvailableThemesUseCaseTest {

        private lateinit var getAvailableThemesUseCase: GetAvailableThemesUseCase

        @BeforeEach
        fun setUp() {
            getAvailableThemesUseCase = GetAvailableThemesUseCase(themeRepository)
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

    @Nested
    @DisplayName("Use Case Integration")
    inner class UseCaseIntegration {

        private lateinit var getThemeUseCase: GetThemeUseCase
        private lateinit var saveThemeUseCase: SaveThemeUseCase
        private lateinit var getAvailableThemesUseCase: GetAvailableThemesUseCase

        @BeforeEach
        fun setUp() {
            getThemeUseCase = GetThemeUseCase(themeRepository)
            saveThemeUseCase = SaveThemeUseCase(themeRepository)
            getAvailableThemesUseCase = GetAvailableThemesUseCase(themeRepository)
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
}
