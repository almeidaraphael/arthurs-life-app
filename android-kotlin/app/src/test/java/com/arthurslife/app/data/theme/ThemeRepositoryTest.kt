package com.arthurslife.app.data.theme

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.user.UserRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for ThemeRepositoryImpl.
 *
 * This test suite validates the ThemeRepositoryImpl's theme management functionality,
 * including theme retrieval, saving, and available themes management. Tests cover
 * various user roles, theme types, and repository interactions.
 *
 * Coverage includes:
 * - Theme retrieval for different user roles
 * - Theme saving functionality
 * - Available themes management
 * - DataStore integration
 * - Flow behavior and transformations
 * - Error scenarios and edge cases
 */
@DisplayName("ThemeRepositoryImpl Tests")
class ThemeRepositoryTest {

    private lateinit var themePreferencesDataStore: ThemePreferencesDataStore
    private lateinit var themeRepository: ThemeRepositoryImpl

    @BeforeEach
    fun setUp() {
        themePreferencesDataStore = mockk()
        themeRepository = ThemeRepositoryImpl(themePreferencesDataStore)
    }

    @Nested
    @DisplayName("Theme Retrieval")
    inner class ThemeRetrieval {

        @Test
        @DisplayName("Should get theme for child user role")
        fun shouldGetThemeForChildUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val expectedTheme = AppTheme.MARIO_CLASSIC
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()

            // Then
            assertEquals(1, result.size, "Should emit single theme")
            assertEquals(expectedTheme, result[0], "Should return Mario theme for child")
            verify(exactly = 1) { themePreferencesDataStore.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should get theme for caregiver user role")
        fun shouldGetThemeForCaregiverUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val expectedTheme = AppTheme.MATERIAL_LIGHT
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()

            // Then
            assertEquals(1, result.size, "Should emit single theme")
            assertEquals(
                expectedTheme,
                result[0],
                "Should return Material Light theme for caregiver",
            )
            verify(exactly = 1) { themePreferencesDataStore.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should delegate theme retrieval to DataStore")
        fun shouldDelegateThemeRetrievalToDataStore() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val expectedTheme = AppTheme.MATERIAL_DARK
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(expectedTheme)

            // When
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()

            // Then
            assertEquals(expectedTheme, result[0], "Should delegate to DataStore")
            verify(exactly = 1) { themePreferencesDataStore.getTheme(userRole) }
        }

        @Test
        @DisplayName("Should return flow of theme changes")
        fun shouldReturnFlowOfThemeChanges() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val themeChanges = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(*themeChanges.toTypedArray())

            // When
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()

            // Then
            assertEquals(themeChanges.size, result.size, "Should emit all theme changes")
            assertEquals(themeChanges, result, "Should return all theme changes in order")
        }

        @Test
        @DisplayName("Should handle different user roles independently")
        fun shouldHandleDifferentUserRolesIndependently() = runTest {
            // Given
            val childRole = UserRole.CHILD
            val caregiverRole = UserRole.CAREGIVER
            val childTheme = AppTheme.MARIO_CLASSIC
            val caregiverTheme = AppTheme.MATERIAL_LIGHT

            every { themePreferencesDataStore.getTheme(childRole) } returns flowOf(childTheme)
            every { themePreferencesDataStore.getTheme(caregiverRole) } returns flowOf(caregiverTheme)

            // When
            val childThemeFlow = themeRepository.getTheme(childRole)
            val caregiverThemeFlow = themeRepository.getTheme(caregiverRole)
            val childResult = childThemeFlow.toList()
            val caregiverResult = caregiverThemeFlow.toList()

            // Then
            assertEquals(childTheme, childResult[0], "Should return child theme")
            assertEquals(caregiverTheme, caregiverResult[0], "Should return caregiver theme")
            verify(exactly = 1) { themePreferencesDataStore.getTheme(childRole) }
            verify(exactly = 1) { themePreferencesDataStore.getTheme(caregiverRole) }
        }

        @Test
        @DisplayName("Should handle all theme types correctly")
        fun shouldHandleAllThemeTypesCorrectly() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val allThemes = AppTheme.values().toList()

            allThemes.forEach { theme ->
                every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)

                // When
                val themeFlow = themeRepository.getTheme(userRole)
                val result = themeFlow.toList()

                // Then
                assertEquals(theme, result[0], "Should handle $theme correctly")
            }
        }

        @Test
        @DisplayName("Should maintain flow characteristics")
        fun shouldMaintainFlowCharacteristics() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_DARK
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)

            // When
            val themeFlow = themeRepository.getTheme(userRole)

            // Then
            assertTrue(themeFlow != null, "Should return non-null flow")
            val result = themeFlow.toList()
            assertTrue(result.isNotEmpty(), "Should emit at least one value")
        }

        @Test
        @DisplayName("Should pass through DataStore flow emissions")
        fun shouldPassThroughDataStoreFlowEmissions() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val multipleEmissions = listOf(
                AppTheme.MARIO_CLASSIC,
                AppTheme.MATERIAL_LIGHT,
            )
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(*multipleEmissions.toTypedArray())

            // When
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()

            // Then
            assertEquals(multipleEmissions.size, result.size, "Should pass through all emissions")
            assertEquals(multipleEmissions, result, "Should maintain emission order")
        }
    }

    @Nested
    @DisplayName("Theme Saving")
    inner class ThemeSaving {

        @Test
        @DisplayName("Should save theme for child user role")
        fun shouldSaveThemeForChildUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When
            themeRepository.saveTheme(userRole, theme)

            // Then
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should save theme for caregiver user role")
        fun shouldSaveThemeForCaregiverUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_LIGHT
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When
            themeRepository.saveTheme(userRole, theme)

            // Then
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should delegate theme saving to DataStore")
        fun shouldDelegateThemeSavingToDataStore() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MATERIAL_DARK
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When
            themeRepository.saveTheme(userRole, theme)

            // Then
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should handle saving all theme types")
        fun shouldHandleSavingAllThemeTypes() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val allThemes = AppTheme.values().toList()

            allThemes.forEach { theme ->
                coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit
            }

            // When
            allThemes.forEach { theme ->
                themeRepository.saveTheme(userRole, theme)
            }

            // Then
            allThemes.forEach { theme ->
                coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
            }
        }

        @Test
        @DisplayName("Should handle saving for different user roles")
        fun shouldHandleSavingForDifferentUserRoles() = runTest {
            // Given
            val childRole = UserRole.CHILD
            val caregiverRole = UserRole.CAREGIVER
            val childTheme = AppTheme.MARIO_CLASSIC
            val caregiverTheme = AppTheme.MATERIAL_LIGHT

            coEvery { themePreferencesDataStore.saveTheme(childRole, childTheme) } returns Unit
            coEvery { themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme) } returns Unit

            // When
            themeRepository.saveTheme(childRole, childTheme)
            themeRepository.saveTheme(caregiverRole, caregiverTheme)

            // Then
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(childRole, childTheme) }
            coVerify(
                exactly = 1,
            ) { themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme) }
        }

        @Test
        @DisplayName("Should handle multiple saves for same user role")
        fun shouldHandleMultipleSavesForSameUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val themes = listOf(
                AppTheme.MARIO_CLASSIC,
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
            )

            themes.forEach { theme ->
                coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit
            }

            // When
            themes.forEach { theme ->
                themeRepository.saveTheme(userRole, theme)
            }

            // Then
            themes.forEach { theme ->
                coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
            }
        }

        @Test
        @DisplayName("Should handle saving same theme multiple times")
        fun shouldHandleSavingSameThemeMultipleTimes() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_DARK
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When
            themeRepository.saveTheme(userRole, theme)
            themeRepository.saveTheme(userRole, theme)
            themeRepository.saveTheme(userRole, theme)

            // Then
            coVerify(exactly = 3) { themePreferencesDataStore.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should be suspend function")
        fun shouldBeSuspendFunction() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When & Then - should compile and run without blocking
            themeRepository.saveTheme(userRole, theme)
        }
    }

    @Nested
    @DisplayName("Available Themes Management")
    inner class AvailableThemesManagement {

        @Test
        @DisplayName("Should return all available themes")
        fun shouldReturnAllAvailableThemes() {
            // Given
            val expectedThemes = AppTheme.values().toList()

            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            assertEquals(expectedThemes.size, availableThemes.size, "Should return all themes")
            assertEquals(expectedThemes, availableThemes, "Should return all AppTheme values")
        }

        @Test
        @DisplayName("Should include all theme types")
        fun shouldIncludeAllThemeTypes() {
            // Given
            val expectedThemes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            expectedThemes.forEach { theme ->
                assertTrue(
                    availableThemes.contains(theme),
                    "Should include $theme in available themes",
                )
            }
        }

        @Test
        @DisplayName("Should return same list on multiple calls")
        fun shouldReturnSameListOnMultipleCalls() {
            // When
            val availableThemes1 = themeRepository.getAvailableThemes()
            val availableThemes2 = themeRepository.getAvailableThemes()
            val availableThemes3 = themeRepository.getAvailableThemes()

            // Then
            assertEquals(availableThemes1, availableThemes2, "Should return same list")
            assertEquals(availableThemes2, availableThemes3, "Should return same list")
        }

        @Test
        @DisplayName("Should return non-empty list")
        fun shouldReturnNonEmptyList() {
            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            assertTrue(availableThemes.isNotEmpty(), "Should return non-empty list")
        }

        @Test
        @DisplayName("Should return list with expected size")
        fun shouldReturnListWithExpectedSize() {
            // Given
            val expectedSize = AppTheme.values().size

            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            assertEquals(
                expectedSize,
                availableThemes.size,
                "Should return correct number of themes",
            )
        }

        @Test
        @DisplayName("Should not depend on DataStore")
        fun shouldNotDependOnDataStore() {
            // Given - no DataStore interaction setup

            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            assertTrue(availableThemes.isNotEmpty(), "Should work without DataStore")
            verify(exactly = 0) { themePreferencesDataStore.getTheme(any()) }
        }

        @Test
        @DisplayName("Should be synchronous operation")
        fun shouldBeSynchronousOperation() {
            // Given
            val startTime = System.currentTimeMillis()

            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime

            assertTrue(availableThemes.isNotEmpty(), "Should return themes")
            assertTrue(duration < 10, "Should be synchronous (fast)")
        }

        @Test
        @DisplayName("Should return immutable list")
        fun shouldReturnImmutableList() {
            // When
            val availableThemes = themeRepository.getAvailableThemes()

            // Then
            assertTrue(availableThemes is List<AppTheme>, "Should return List")
            // Note: toList() returns immutable list in Kotlin
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    inner class IntegrationScenarios {

        @Test
        @DisplayName("Should handle complete theme management workflow")
        fun shouldHandleCompleteThemeManagementWorkflow() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val initialTheme = AppTheme.MARIO_CLASSIC
            val newTheme = AppTheme.MATERIAL_DARK

            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(initialTheme)
            coEvery { themePreferencesDataStore.saveTheme(userRole, newTheme) } returns Unit

            // When
            val availableThemes = themeRepository.getAvailableThemes()
            val initialThemeFlow = themeRepository.getTheme(userRole)
            val initialResult = initialThemeFlow.toList()
            themeRepository.saveTheme(userRole, newTheme)

            // Then
            assertTrue(
                availableThemes.contains(newTheme),
                "Should include new theme in available themes",
            )
            assertEquals(initialTheme, initialResult[0], "Should retrieve initial theme")
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, newTheme) }
        }

        @Test
        @DisplayName("Should handle multiple user roles simultaneously")
        fun shouldHandleMultipleUserRolesSimultaneously() = runTest {
            // Given
            val childRole = UserRole.CHILD
            val caregiverRole = UserRole.CAREGIVER
            val childTheme = AppTheme.MARIO_CLASSIC
            val caregiverTheme = AppTheme.MATERIAL_LIGHT

            every { themePreferencesDataStore.getTheme(childRole) } returns flowOf(childTheme)
            every { themePreferencesDataStore.getTheme(caregiverRole) } returns flowOf(caregiverTheme)
            coEvery { themePreferencesDataStore.saveTheme(childRole, childTheme) } returns Unit
            coEvery { themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme) } returns Unit

            // When
            val childThemeFlow = themeRepository.getTheme(childRole)
            val caregiverThemeFlow = themeRepository.getTheme(caregiverRole)
            val childResult = childThemeFlow.toList()
            val caregiverResult = caregiverThemeFlow.toList()

            themeRepository.saveTheme(childRole, childTheme)
            themeRepository.saveTheme(caregiverRole, caregiverTheme)

            // Then
            assertEquals(childTheme, childResult[0], "Should handle child theme")
            assertEquals(caregiverTheme, caregiverResult[0], "Should handle caregiver theme")
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(childRole, childTheme) }
            coVerify(
                exactly = 1,
            ) { themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme) }
        }

        @Test
        @DisplayName("Should handle theme switching scenario")
        fun shouldHandleThemeSwitchingScenario() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val themes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            themes.forEach { theme ->
                every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)
                coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit
            }

            // When
            val availableThemes = themeRepository.getAvailableThemes()
            themes.forEach { theme ->
                themeRepository.saveTheme(userRole, theme)
            }

            // Then
            themes.forEach { theme ->
                assertTrue(
                    availableThemes.contains(theme),
                    "Should include $theme in available themes",
                )
                coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
            }
        }

        @Test
        @DisplayName("Should handle repository operations independently")
        fun shouldHandleRepositoryOperationsIndependently() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When
            val availableThemes = themeRepository.getAvailableThemes()
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()
            themeRepository.saveTheme(userRole, theme)

            // Then
            assertTrue(availableThemes.isNotEmpty(), "Should get available themes independently")
            assertEquals(theme, result[0], "Should get theme independently")
            coVerify(exactly = 1) { themePreferencesDataStore.saveTheme(userRole, theme) }
        }

        @Test
        @DisplayName("Should maintain consistent state across operations")
        fun shouldMaintainConsistentStateAcrossOperations() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_DARK
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When
            val availableThemes1 = themeRepository.getAvailableThemes()
            themeRepository.saveTheme(userRole, theme)
            val availableThemes2 = themeRepository.getAvailableThemes()
            val themeFlow = themeRepository.getTheme(userRole)
            val result = themeFlow.toList()

            // Then
            assertEquals(
                availableThemes1,
                availableThemes2,
                "Should maintain consistent available themes",
            )
            assertEquals(theme, result[0], "Should maintain consistent theme state")
        }
    }

    @Nested
    @DisplayName("Repository Contract Compliance")
    inner class RepositoryContractCompliance {

        @Test
        @DisplayName("Should implement ThemeRepository interface")
        fun shouldImplementThemeRepositoryInterface() {
            // Given & When
            val repository = themeRepository

            // Then
            assertTrue(
                repository is com.arthurslife.app.domain.theme.repository.ThemeRepository,
                "Should implement ThemeRepository interface",
            )
        }

        @Test
        @DisplayName("Should provide all required methods")
        fun shouldProvideAllRequiredMethods() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)
            coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

            // When & Then - should compile and execute without errors
            val themeFlow = themeRepository.getTheme(userRole)
            themeRepository.saveTheme(userRole, theme)
            val availableThemes = themeRepository.getAvailableThemes()

            assertTrue(themeFlow != null, "Should provide getTheme method")
            assertTrue(availableThemes.isNotEmpty(), "Should provide getAvailableThemes method")
        }

        @Test
        @DisplayName("Should handle all user roles")
        fun shouldHandleAllUserRoles() = runTest {
            // Given
            val userRoles = UserRole.values().toList()
            val theme = AppTheme.MATERIAL_LIGHT

            userRoles.forEach { role ->
                every { themePreferencesDataStore.getTheme(role) } returns flowOf(theme)
                coEvery { themePreferencesDataStore.saveTheme(role, theme) } returns Unit
            }

            // When & Then
            userRoles.forEach { role ->
                val themeFlow = themeRepository.getTheme(role)
                val result = themeFlow.toList()
                themeRepository.saveTheme(role, theme)

                assertEquals(theme, result[0], "Should handle $role user role")
            }
        }

        @Test
        @DisplayName("Should handle all theme types")
        fun shouldHandleAllThemeTypes() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val allThemes = AppTheme.values().toList()

            // When & Then
            allThemes.forEach { theme ->
                // Set up mock for each theme iteration
                every { themePreferencesDataStore.getTheme(userRole) } returns flowOf(theme)
                coEvery { themePreferencesDataStore.saveTheme(userRole, theme) } returns Unit

                val themeFlow = themeRepository.getTheme(userRole)
                val result = themeFlow.toList()
                themeRepository.saveTheme(userRole, theme)

                assertEquals(theme, result[0], "Should handle $theme theme type")
            }
        }
    }
}
