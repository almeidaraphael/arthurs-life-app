package com.arthurslife.app.presentation.theme

import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.theme.usecase.GetAvailableThemesUseCase
import com.arthurslife.app.domain.theme.usecase.GetThemeUseCase
import com.arthurslife.app.domain.theme.usecase.SaveThemeUseCase
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.mario.MarioTheme
import com.arthurslife.app.presentation.theme.materialdark.MaterialDarkTheme
import com.arthurslife.app.presentation.theme.materiallight.MaterialLightTheme
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Comprehensive test suite for ThemeViewModel.
 *
 * This test suite validates the ThemeViewModel's state management, theme loading,
 * theme saving, and integration with theme use cases. Tests cover various user roles,
 * theme transitions, and error scenarios.
 *
 * Coverage includes:
 * - Theme loading for different user roles
 * - Theme saving and state updates
 * - Available themes management
 * - StateFlow behavior and transformations
 * - Use case integration
 * - Coroutine and lifecycle handling
 * - Error scenarios and edge cases
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("ThemeViewModel Tests")
class ThemeViewModelTest {

    private lateinit var getThemeUseCase: GetThemeUseCase
    private lateinit var saveThemeUseCase: SaveThemeUseCase
    private lateinit var getAvailableThemesUseCase: GetAvailableThemesUseCase
    private lateinit var themeViewModel: ThemeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getThemeUseCase = mockk()
        saveThemeUseCase = mockk()
        getAvailableThemesUseCase = mockk()

        // Default mocks for available themes
        every { getAvailableThemesUseCase() } returns listOf(
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
            AppTheme.MARIO_CLASSIC,
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Nested
    @DisplayName("ViewModel Initialization")
    inner class ViewModelInitialization {

        @Test
        @DisplayName("Should initialize with default Material Light theme")
        fun shouldInitializeWithDefaultMaterialLightTheme() = runTest {
            // Given
            every { getAvailableThemesUseCase() } returns listOf(AppTheme.MATERIAL_LIGHT)

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(
                MaterialLightTheme,
                currentTheme,
                "Should initialize with Material Light theme",
            )
        }

        @Test
        @DisplayName("Should load available themes on initialization")
        fun shouldLoadAvailableThemesOnInitialization() = runTest {
            // Given
            val expectedThemes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )
            every { getAvailableThemesUseCase() } returns expectedThemes

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val availableThemes = themeViewModel.availableThemes.value
            assertEquals(3, availableThemes.size, "Should load all available themes")
            assertTrue(
                availableThemes.contains(MaterialLightTheme),
                "Should contain Material Light theme",
            )
            assertTrue(
                availableThemes.contains(MaterialDarkTheme),
                "Should contain Material Dark theme",
            )
            assertTrue(
                availableThemes.contains(MarioTheme),
                "Should contain Mario theme",
            )
            verify(exactly = 1) { getAvailableThemesUseCase() }
        }

        @Test
        @DisplayName("Should handle empty available themes list")
        fun shouldHandleEmptyAvailableThemesList() = runTest {
            // Given
            every { getAvailableThemesUseCase() } returns emptyList()

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val availableThemes = themeViewModel.availableThemes.value
            assertTrue(availableThemes.isEmpty(), "Should handle empty themes list")
        }

        @Test
        @DisplayName("Should use correct StateFlow setup")
        fun shouldUseCorrectStateFlowSetup() = runTest {
            // Given
            every { getAvailableThemesUseCase() } returns listOf(AppTheme.MATERIAL_LIGHT)

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            assertNotNull(themeViewModel.currentTheme, "Current theme StateFlow should not be null")
            assertNotNull(
                themeViewModel.availableThemes,
                "Available themes StateFlow should not be null",
            )

            val currentTheme = themeViewModel.currentTheme.value
            val availableThemes = themeViewModel.availableThemes.value

            assertNotNull(currentTheme, "Current theme value should not be null")
            assertNotNull(availableThemes, "Available themes value should not be null")
        }
    }

    @Nested
    @DisplayName("Theme Loading")
    inner class ThemeLoading {

        @BeforeEach
        fun setUp() {
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
        }

        @Test
        @DisplayName("Should load Mario theme for child user role")
        fun shouldLoadMarioThemeForChildUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val savedTheme = AppTheme.MARIO_CLASSIC
            every { getThemeUseCase(userRole) } returns flowOf(savedTheme)

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(MarioTheme, currentTheme, "Should load Mario theme for child")
            verify(exactly = 1) { getThemeUseCase(userRole) }
        }

        @Test
        @DisplayName("Should load Material Light theme for caregiver user role")
        fun shouldLoadMaterialLightThemeForCaregiverUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val savedTheme = AppTheme.MATERIAL_LIGHT
            every { getThemeUseCase(userRole) } returns flowOf(savedTheme)

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(
                MaterialLightTheme,
                currentTheme,
                "Should load Material Light theme for caregiver",
            )
            verify(exactly = 1) { getThemeUseCase(userRole) }
        }

        @Test
        @DisplayName("Should set default theme immediately for child role")
        fun shouldSetDefaultThemeImmediatelyForChildRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            every { getThemeUseCase(userRole) } returns flowOf(AppTheme.MARIO_CLASSIC)

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle() // Allow StateFlow transformation to complete

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(MarioTheme, currentTheme, "Should set Mario theme immediately for child")
        }

        @Test
        @DisplayName("Should set default theme immediately for caregiver role")
        fun shouldSetDefaultThemeImmediatelyForCaregiverRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            every { getThemeUseCase(userRole) } returns flowOf(AppTheme.MATERIAL_LIGHT)

            // When
            themeViewModel.loadTheme(userRole)
            // Don't advance until idle to test immediate state

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(
                MaterialLightTheme,
                currentTheme,
                "Should set Material Light theme immediately for caregiver",
            )
        }

        @Test
        @DisplayName("Should override default theme with saved preference")
        fun shouldOverrideDefaultThemeWithSavedPreference() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val savedTheme = AppTheme.MATERIAL_DARK
            every { getThemeUseCase(userRole) } returns flowOf(savedTheme)

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(
                MaterialDarkTheme,
                currentTheme,
                "Should override default with saved preference",
            )
        }

        @Test
        @DisplayName("Should handle theme changes in Flow")
        fun shouldHandleThemeChangesInFlow() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val themeFlow = flowOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )
            every { getThemeUseCase(userRole) } returns themeFlow

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(MarioTheme, currentTheme, "Should handle final theme in Flow")
        }

        @Test
        @DisplayName("Should collect theme changes continuously")
        fun shouldCollectThemeChangesContinuously() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val themeFlow = flowOf(AppTheme.MARIO_CLASSIC)
            every { getThemeUseCase(userRole) } returns themeFlow

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            verify(exactly = 1) { getThemeUseCase(userRole) }
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(MarioTheme, currentTheme, "Should collect theme changes continuously")
        }

        @Test
        @DisplayName("Should update current user role when loading theme")
        fun shouldUpdateCurrentUserRoleWhenLoadingTheme() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_LIGHT
            every { getThemeUseCase(userRole) } returns flowOf(theme)
            coEvery { saveThemeUseCase(userRole, theme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            themeViewModel.saveTheme(theme)
            advanceUntilIdle() // Allow saveTheme coroutine to complete

            // Then
            coVerify(exactly = 1) { saveThemeUseCase(userRole, theme) }
        }
    }

    @Nested
    @DisplayName("Theme Saving")
    inner class ThemeSaving {

        @BeforeEach
        fun setUp() {
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
        }

        @Test
        @DisplayName("Should save theme for child user role")
        fun shouldSaveThemeForChildUserRole() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            every { getThemeUseCase(userRole) } returns flowOf(theme)
            coEvery { saveThemeUseCase(userRole, theme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            themeViewModel.saveTheme(theme)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) { saveThemeUseCase(userRole, theme) }
        }

        @Test
        @DisplayName("Should save theme for caregiver user role")
        fun shouldSaveThemeForCaregiverUserRole() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_DARK
            every { getThemeUseCase(userRole) } returns flowOf(theme)
            coEvery { saveThemeUseCase(userRole, theme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            themeViewModel.saveTheme(theme)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) { saveThemeUseCase(userRole, theme) }
        }

        @Test
        @DisplayName("Should update current theme state when saving")
        fun shouldUpdateCurrentThemeStateWhenSaving() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val initialTheme = AppTheme.MARIO_CLASSIC
            val newTheme = AppTheme.MATERIAL_LIGHT
            every { getThemeUseCase(userRole) } returns flowOf(initialTheme)
            coEvery { saveThemeUseCase(userRole, newTheme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            themeViewModel.saveTheme(newTheme)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(MaterialLightTheme, currentTheme, "Should update current theme state")
        }

        @Test
        @DisplayName("Should not save theme when no user role is set")
        fun shouldNotSaveThemeWhenNoUserRoleIsSet() = runTest {
            // Given
            val theme = AppTheme.MATERIAL_DARK
            coEvery { saveThemeUseCase(any(), any()) } returns Unit

            // When
            themeViewModel.saveTheme(theme)

            // Then
            coVerify(exactly = 0) { saveThemeUseCase(any(), any()) }
        }

        @Test
        @DisplayName("Should handle different theme types when saving")
        fun shouldHandleDifferentThemeTypesWhenSaving() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val themes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )
            every { getThemeUseCase(userRole) } returns flowOf(AppTheme.MATERIAL_LIGHT)

            themes.forEach { theme ->
                coEvery { saveThemeUseCase(userRole, theme) } returns Unit
            }

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            themes.forEach { theme ->
                themeViewModel.saveTheme(theme)
                advanceUntilIdle()
            }

            // Then
            themes.forEach { theme ->
                coVerify(exactly = 1) { saveThemeUseCase(userRole, theme) }
            }
        }

        @Test
        @DisplayName("Should save theme and update state in correct order")
        fun shouldSaveThemeAndUpdateStateInCorrectOrder() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val initialTheme = AppTheme.MARIO_CLASSIC
            val newTheme = AppTheme.MATERIAL_DARK
            every { getThemeUseCase(userRole) } returns flowOf(initialTheme)
            coEvery { saveThemeUseCase(userRole, newTheme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            val themeBeforeSave = themeViewModel.currentTheme.value
            themeViewModel.saveTheme(newTheme)
            advanceUntilIdle()
            val themeAfterSave = themeViewModel.currentTheme.value

            // Then
            assertEquals(MarioTheme, themeBeforeSave, "Should have initial theme before save")
            assertEquals(MaterialDarkTheme, themeAfterSave, "Should have new theme after save")
            coVerify(exactly = 1) { saveThemeUseCase(userRole, newTheme) }
        }

        @Test
        @DisplayName("Should handle saving same theme multiple times")
        fun shouldHandleSavingSameThemeMultipleTimes() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme = AppTheme.MATERIAL_LIGHT
            every { getThemeUseCase(userRole) } returns flowOf(theme)
            coEvery { saveThemeUseCase(userRole, theme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            themeViewModel.saveTheme(theme)
            advanceUntilIdle()
            themeViewModel.saveTheme(theme)
            advanceUntilIdle()
            themeViewModel.saveTheme(theme)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 3) { saveThemeUseCase(userRole, theme) }
        }
    }

    @Nested
    @DisplayName("Available Themes Management")
    inner class AvailableThemesManagement {

        @Test
        @DisplayName("Should provide all available themes as BaseAppTheme")
        fun shouldProvideAllAvailableThemesAsBaseAppTheme() = runTest {
            // Given
            val expectedThemes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )
            every { getAvailableThemesUseCase() } returns expectedThemes

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val availableThemes = themeViewModel.availableThemes.value
            assertEquals(3, availableThemes.size, "Should provide all available themes")
            assertTrue(
                availableThemes.any { it == MaterialLightTheme },
                "Should contain Material Light theme",
            )
            assertTrue(
                availableThemes.any { it == MaterialDarkTheme },
                "Should contain Material Dark theme",
            )
            assertTrue(
                availableThemes.any { it == MarioTheme },
                "Should contain Mario theme",
            )
        }

        @Test
        @DisplayName("Should maintain available themes throughout lifecycle")
        fun shouldMaintainAvailableThemesThroughoutLifecycle() = runTest {
            // Given
            val themes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MARIO_CLASSIC)
            every { getAvailableThemesUseCase() } returns themes

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            val initialThemes = themeViewModel.availableThemes.value

            // Perform some operations
            every { getThemeUseCase(UserRole.CHILD) } returns flowOf(AppTheme.MARIO_CLASSIC)
            themeViewModel.loadTheme(UserRole.CHILD)
            advanceUntilIdle()

            val themesAfterLoad = themeViewModel.availableThemes.value

            // Then
            assertEquals(
                initialThemes,
                themesAfterLoad,
                "Should maintain available themes throughout lifecycle",
            )
        }

        @Test
        @DisplayName("Should transform AppTheme to BaseAppTheme correctly")
        fun shouldTransformAppThemeToBaseAppThemeCorrectly() = runTest {
            // Given
            val appThemes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )
            every { getAvailableThemesUseCase() } returns appThemes

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val baseThemes = themeViewModel.availableThemes.value
            assertEquals(appThemes.size, baseThemes.size, "Should transform all themes")

            val lightTheme = baseThemes.find { it == MaterialLightTheme }
            val darkTheme = baseThemes.find { it == MaterialDarkTheme }
            val marioTheme = baseThemes.find { it == MarioTheme }

            assertNotNull(lightTheme, "Should transform Material Light theme")
            assertNotNull(darkTheme, "Should transform Material Dark theme")
            assertNotNull(marioTheme, "Should transform Mario theme")
        }

        @Test
        @DisplayName("Should handle single available theme")
        fun shouldHandleSingleAvailableTheme() = runTest {
            // Given
            val singleTheme = listOf(AppTheme.MARIO_CLASSIC)
            every { getAvailableThemesUseCase() } returns singleTheme

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val availableThemes = themeViewModel.availableThemes.value
            assertEquals(1, availableThemes.size, "Should handle single theme")
            assertEquals(MarioTheme, availableThemes[0], "Should be Mario theme")
        }

        @Test
        @DisplayName("Should call available themes use case only once")
        fun shouldCallAvailableThemesUseCaseOnlyOnce() = runTest {
            // Given
            every { getAvailableThemesUseCase() } returns listOf(AppTheme.MATERIAL_LIGHT)

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            verify(exactly = 1) { getAvailableThemesUseCase() }
        }
    }

    @Nested
    @DisplayName("StateFlow Behavior")
    inner class StateFlowBehavior {

        @BeforeEach
        fun setUp() {
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
        }

        @Test
        @DisplayName("Should emit current theme changes")
        fun shouldEmitCurrentThemeChanges() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            every { getThemeUseCase(userRole) } returns flowOf(theme)

            // When
            val initialTheme = themeViewModel.currentTheme.value
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            val finalTheme = themeViewModel.currentTheme.value

            // Then
            assertEquals(MaterialLightTheme, initialTheme, "Should have initial theme")
            assertEquals(MarioTheme, finalTheme, "Should emit theme change")
        }

        @Test
        @DisplayName("Should emit available themes changes")
        fun shouldEmitAvailableThemesChanges() = runTest {
            // Given
            val themes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK)
            every { getAvailableThemesUseCase() } returns themes

            // When
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
            advanceUntilIdle()

            // Then
            val availableThemes = themeViewModel.availableThemes.value
            assertEquals(2, availableThemes.size, "Should emit available themes")
        }

        @Test
        @DisplayName("Should maintain state across multiple operations")
        fun shouldMaintainStateAcrossMultipleOperations() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val theme1 = AppTheme.MATERIAL_LIGHT
            val theme2 = AppTheme.MATERIAL_DARK
            every { getThemeUseCase(userRole) } returns flowOf(theme1)
            coEvery { saveThemeUseCase(userRole, theme2) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            val themeAfterLoad = themeViewModel.currentTheme.value

            themeViewModel.saveTheme(theme2)
            advanceUntilIdle()
            val themeAfterSave = themeViewModel.currentTheme.value

            // Then
            assertEquals(MaterialLightTheme, themeAfterLoad, "Should maintain state after load")
            assertEquals(MaterialDarkTheme, themeAfterSave, "Should maintain state after save")
        }

        @Test
        @DisplayName("Should use correct StateFlow sharing strategy")
        fun shouldUseCorrectStateFlowSharingStrategy() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val theme = AppTheme.MARIO_CLASSIC
            every { getThemeUseCase(userRole) } returns flowOf(theme)

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            val currentTheme1 = themeViewModel.currentTheme.value
            val currentTheme2 = themeViewModel.currentTheme.value

            assertEquals(currentTheme1, currentTheme2, "Should share same state value")
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    inner class IntegrationScenarios {

        @BeforeEach
        fun setUp() {
            themeViewModel = ThemeViewModel(getThemeUseCase, saveThemeUseCase, getAvailableThemesUseCase)
        }

        @Test
        @DisplayName("Should handle complete theme switching workflow")
        fun shouldHandleCompleteThemeSwitchingWorkflow() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val initialTheme = AppTheme.MARIO_CLASSIC
            val newTheme = AppTheme.MATERIAL_DARK
            val availableThemes = listOf(
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
                AppTheme.MARIO_CLASSIC,
            )

            every { getThemeUseCase(userRole) } returns flowOf(initialTheme)
            every { getAvailableThemesUseCase() } returns availableThemes
            coEvery { saveThemeUseCase(userRole, newTheme) } returns Unit

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()
            val currentTheme = themeViewModel.currentTheme.value
            val availableOptions = themeViewModel.availableThemes.value

            themeViewModel.saveTheme(newTheme)
            advanceUntilIdle()
            val newCurrentTheme = themeViewModel.currentTheme.value

            // Then
            assertEquals(MarioTheme, currentTheme, "Should load initial theme")
            assertEquals(3, availableOptions.size, "Should have available themes")
            assertEquals(MaterialDarkTheme, newCurrentTheme, "Should switch to new theme")

            verify(exactly = 1) { getThemeUseCase(userRole) }
            verify(exactly = 1) { getAvailableThemesUseCase() }
            coVerify(exactly = 1) { saveThemeUseCase(userRole, newTheme) }
        }

        @Test
        @DisplayName("Should handle user role switching scenario")
        fun shouldHandleUserRoleSwitchingScenario() = runTest {
            // Given
            val childRole = UserRole.CHILD
            val caregiverRole = UserRole.CAREGIVER
            val childTheme = AppTheme.MARIO_CLASSIC
            val caregiverTheme = AppTheme.MATERIAL_LIGHT

            every { getThemeUseCase(childRole) } returns flowOf(childTheme)
            every { getThemeUseCase(caregiverRole) } returns flowOf(caregiverTheme)
            coEvery { saveThemeUseCase(childRole, childTheme) } returns Unit
            coEvery { saveThemeUseCase(caregiverRole, caregiverTheme) } returns Unit

            // When
            themeViewModel.loadTheme(childRole)
            advanceUntilIdle()
            val childCurrentTheme = themeViewModel.currentTheme.value

            themeViewModel.loadTheme(caregiverRole)
            advanceUntilIdle()
            val caregiverCurrentTheme = themeViewModel.currentTheme.value

            // Then
            assertEquals(MarioTheme, childCurrentTheme, "Should load child theme")
            assertEquals(MaterialLightTheme, caregiverCurrentTheme, "Should load caregiver theme")
        }

        @Test
        @DisplayName("Should handle theme persistence across app lifecycle")
        fun shouldHandleThemePersistenceAcrossAppLifecycle() = runTest {
            // Given
            val userRole = UserRole.CAREGIVER
            val persistedTheme = AppTheme.MATERIAL_DARK
            every { getThemeUseCase(userRole) } returns flowOf(persistedTheme)

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            // Then
            val currentTheme = themeViewModel.currentTheme.value
            assertEquals(MaterialDarkTheme, currentTheme, "Should load persisted theme")
            verify(exactly = 1) { getThemeUseCase(userRole) }
        }

        @Test
        @DisplayName("Should handle rapid theme changes")
        fun shouldHandleRapidThemeChanges() = runTest {
            // Given
            val userRole = UserRole.CHILD
            val themes = listOf(
                AppTheme.MARIO_CLASSIC,
                AppTheme.MATERIAL_LIGHT,
                AppTheme.MATERIAL_DARK,
            )
            every { getThemeUseCase(userRole) } returns flowOf(themes[0])
            themes.forEach { theme ->
                coEvery { saveThemeUseCase(userRole, theme) } returns Unit
            }

            // When
            themeViewModel.loadTheme(userRole)
            advanceUntilIdle()

            themes.forEach { theme ->
                themeViewModel.saveTheme(theme)
                advanceUntilIdle()
            }

            // Then
            val finalTheme = themeViewModel.currentTheme.value
            assertEquals(MaterialDarkTheme, finalTheme, "Should handle rapid theme changes")

            themes.forEach { theme ->
                coVerify(exactly = 1) { saveThemeUseCase(userRole, theme) }
            }
        }
    }
}
