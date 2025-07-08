package com.arthurslife.app.domain.theme.usecase

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.repository.ThemeRepository
import com.arthurslife.app.domain.user.UserRole
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
        @DisplayName("Should get theme for child user role")
        fun shouldGetThemeForChildUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val expectedTheme = AppTheme.MARIO_CLASSIC
            every { themeRepository.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            val result = getThemeUseCase(userRole).first()

            // Then
            assertEquals(expectedTheme, result, "Should return Mario Classic theme for child")
            verify(exactly = 1) { themeRepository.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should get theme for caregiver user role")
        fun shouldGetThemeForCaregiverUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val expectedTheme = AppTheme.MATERIAL_LIGHT
            every { themeRepository.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            val result = getThemeUseCase(userRole).first()

            // Then
            assertEquals(expectedTheme, result, "Should return Material Light theme for caregiver")
            verify(exactly = 1) { themeRepository.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should handle multiple theme changes for same user role")
        fun shouldHandleMultipleThemeChangesForSameUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val themes = listOf(
                AppTheme.MARIO_CLASSIC,
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
            )
            every { themeRepository.getTheme(userRole) } returns flowOf(
                themes[0], themes[1], themes[2],
            )

            // When
            val results = mutableListOf<AppTheme>()
            getThemeUseCase(userRole).collect { theme ->
                results.add(theme)
            }

            // Then
            assertEquals(3, results.size, "Should collect all theme changes")
            assertEquals(themes[0], results[0], "First theme should match")
            assertEquals(themes[1], results[1], "Second theme should match")
            assertEquals(themes[2], results[2], "Third theme should match")
            verify(exactly = 1) { themeRepository.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should return Flow from repository")
        fun shouldReturnFlowFromRepository() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val expectedTheme = AppTheme.MATERIAL_DARK
            val themeFlow = flowOf(expectedTheme)
            every { themeRepository.getTheme(userRole) } returns themeFlow

            // When
            val resultFlow = getThemeUseCase(userRole)
            val result = resultFlow.first()

            // Then
            assertEquals(expectedTheme, result, "Should return theme from repository Flow")
            verify(exactly = 1) { themeRepository.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should handle all available themes")
        fun shouldHandleAllAvailableThemes() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val allThemes = AppTheme.values().toList()

            // When/Then
            allThemes.forEach { theme ->
                every { themeRepository.getTheme(userRole) } returns flowOf(theme)

                val result = getThemeUseCase(userRole).first()
                assertEquals(theme, result, "Should handle $theme correctly")
            }

            verify(exactly = allThemes.size) { themeRepository.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should delegate to repository correctly")
        fun shouldDelegateToRepositoryCorrectly() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val expectedTheme = AppTheme.MATERIAL_LIGHT
            every { themeRepository.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            getThemeUseCase(userRole).first()

            // Then
            verify(exactly = 1) { themeRepository.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should support operator invoke syntax")
        fun shouldSupportOperatorInvokeSyntax() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val expectedTheme = AppTheme.MARIO_CLASSIC
            every { themeRepository.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            val result = getThemeUseCase.invoke(userRole).first()

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
        @DisplayName("Should save theme for child user role")
        fun shouldSaveThemeForChildUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit

            // When
            saveThemeUseCase(userRole, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should save theme for caregiver user role")
        fun shouldSaveThemeForCaregiverUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_DARK
            coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit

            // When
            saveThemeUseCase(userRole, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should handle saving all available themes")
        fun shouldHandleSavingAllAvailableThemes() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val allThemes = AppTheme.values().toList()

            allThemes.forEach { theme ->
                coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit
            }

            // When
            allThemes.forEach { theme ->
                saveThemeUseCase(userRole, theme)
            }

            // Then
            allThemes.forEach { theme ->
                coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
            }
        }

        @Test
        @DisplayName("Should handle multiple saves for same user role")
        fun shouldHandleMultipleSavesForSameUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val themes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            themes.forEach { theme ->
                coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit
            }

            // When
            themes.forEach { theme ->
                saveThemeUseCase(userRole, theme)
            }

            // Then
            themes.forEach { theme ->
                coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
            }
        }

        @Test
        @DisplayName("Should delegate to repository correctly")
        fun shouldDelegateToRepositoryCorrectly() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit

            // When
            saveThemeUseCase(userRole, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should support operator invoke syntax")
        fun shouldSupportOperatorInvokeSyntax() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_LIGHT
            coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit

            // When
            saveThemeUseCase.invoke(userRole, theme)

            // Then
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should handle theme changes for both user roles")
        fun shouldHandleThemeChangesForBothUserRoles() = runTest {
            // Given
            val scenarios = listOf(
                UserRole.CHILD to AppTheme.MARIO_CLASSIC,
                UserRole.CHILD to AppTheme.MATERIAL_LIGHT,
                UserRole.CAREGIVER to AppTheme.MATERIAL_DARK,
                UserRole.CAREGIVER to AppTheme.MARIO_CLASSIC,
            )

            scenarios.forEach { (role, theme) ->
                coEvery { themeRepository.saveTheme(role, theme) } returns Unit
            }

            // When
            scenarios.forEach { (role, theme) ->
                saveThemeUseCase(role, theme)
            }

            // Then
            scenarios.forEach { (role, theme) ->
                coVerify(exactly = 1) { themeRepository.saveTheme(role, theme) }
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
            val userRole = UserRole.CHILD
            val currentTheme = AppTheme.MARIO_CLASSIC
            val newTheme = AppTheme.MATERIAL_LIGHT
            val availableThemes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            every { themeRepository.getTheme(userRole) } returns flowOf(currentTheme, newTheme)
            every { themeRepository.getAvailableThemes() } returns availableThemes
            coEvery { themeRepository.saveTheme(userRole, newTheme) } returns Unit

            // When
            val initialTheme = getThemeUseCase(userRole).first()
            val availableOptions = getAvailableThemesUseCase()
            saveThemeUseCase(userRole, newTheme)

            // Then
            assertEquals(currentTheme, initialTheme, "Should get initial theme")
            assertEquals(availableThemes, availableOptions, "Should get available themes")

            verify(exactly = 1) { themeRepository.getTheme(userRole) }
            verify(exactly = 1) { themeRepository.getAvailableThemes() }
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, newTheme) }
        }

        @Test
        @DisplayName("Should handle different user roles independently")
        fun shouldHandleDifferentUserRolesIndependently() = runTest {
            // Given
            val childRole = UserRole.CHILD
            val caregiverRole = UserRole.CAREGIVER
            val childTheme = AppTheme.MARIO_CLASSIC
            val caregiverTheme = AppTheme.MATERIAL_DARK

            every { themeRepository.getTheme(childRole) } returns flowOf(childTheme)
            every { themeRepository.getTheme(caregiverRole) } returns flowOf(caregiverTheme)
            coEvery { themeRepository.saveTheme(childRole, childTheme) } returns Unit
            coEvery { themeRepository.saveTheme(caregiverRole, caregiverTheme) } returns Unit

            // When
            val childResult = getThemeUseCase(childRole).first()
            val caregiverResult = getThemeUseCase(caregiverRole).first()
            saveThemeUseCase(childRole, childTheme)
            saveThemeUseCase(caregiverRole, caregiverTheme)

            // Then
            assertEquals(childTheme, childResult, "Should handle child theme independently")
            assertEquals(
                caregiverTheme,
                caregiverResult,
                "Should handle caregiver theme independently",
            )

            verify(exactly = 1) { themeRepository.getTheme(childRole) }
            verify(exactly = 1) { themeRepository.getTheme(caregiverRole) }
            coVerify(exactly = 1) { themeRepository.saveTheme(childRole, childTheme) }
            coVerify(exactly = 1) { themeRepository.saveTheme(caregiverRole, caregiverTheme) }
        }

        @Test
        @DisplayName("Should maintain consistent repository interactions")
        fun shouldMaintainConsistentRepositoryInteractions() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_LIGHT
            val availableThemes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK)

            every { themeRepository.getTheme(userRole) } returns flowOf(theme)
            every { themeRepository.getAvailableThemes() } returns availableThemes
            coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit

            // When
            getThemeUseCase(userRole).first()
            getAvailableThemesUseCase()
            saveThemeUseCase(userRole, theme)

            // Then
            verify(exactly = 1) { themeRepository.getTheme(userRole) }
            verify(exactly = 1) { themeRepository.getAvailableThemes() }
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should support concurrent use case execution")
        fun shouldSupportConcurrentUseCaseExecution() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            val availableThemes = listOf(AppTheme.MARIO_CLASSIC, AppTheme.MATERIAL_LIGHT)

            every { themeRepository.getTheme(userRole) } returns flowOf(theme)
            every { themeRepository.getAvailableThemes() } returns availableThemes
            coEvery { themeRepository.saveTheme(userRole, theme) } returns Unit

            // When
            val getThemeResult = getThemeUseCase(userRole).first()
            val availableThemesResult = getAvailableThemesUseCase()
            saveThemeUseCase(userRole, theme)

            // Then
            assertEquals(theme, getThemeResult, "Should handle concurrent get theme")
            assertEquals(
                availableThemes,
                availableThemesResult,
                "Should handle concurrent get available themes",
            )

            verify(exactly = 1) { themeRepository.getTheme(userRole) }
            verify(exactly = 1) { themeRepository.getAvailableThemes() }
            coVerify(exactly = 1) { themeRepository.saveTheme(userRole, theme) }
        }
    }
}
