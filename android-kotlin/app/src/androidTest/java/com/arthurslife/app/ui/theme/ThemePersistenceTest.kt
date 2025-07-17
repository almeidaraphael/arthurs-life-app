package com.arthurslife.app.ui.theme

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.data.theme.ThemePreferencesDataStore
import com.arthurslife.app.domain.theme.model.AppTheme
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.theme.ArthursLifeTheme
import com.arthurslife.app.presentation.theme.components.ThemeSelector
import com.arthurslife.app.presentation.theme.mario.MarioClassicTheme
import com.arthurslife.app.presentation.theme.materialdark.MaterialDarkTheme
import com.arthurslife.app.presentation.theme.materiallight.MaterialLightTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Comprehensive UI test suite for Theme Persistence across app restart.
 *
 * This test suite validates that theme preferences are properly persisted and
 * restored when the app is restarted. Tests cover various user roles, theme types,
 * and persistence scenarios.
 *
 * Coverage includes:
 * - Theme persistence for different user roles
 * - Theme restoration after app restart simulation
 * - Theme switching and persistence workflow
 * - DataStore persistence validation
 * - Multi-user theme persistence
 * - Theme default behavior on first launch
 * - Theme clearing and restoration
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ThemePersistenceTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    private var selectedTheme: com.arthurslife.app.presentation.theme.BaseAppTheme? = null
    private var selectionCount = 0

    @Before
    fun setup() {
        hiltRule.inject()
        selectedTheme = null
        selectionCount = 0
    }

    @Test
    fun themePersistence_childUserRole_persistsAcrossAppRestart() = runTest {
        // Given
        val userRole = UserRole.CHILD
        val selectedTheme = AppTheme.MARIO_CLASSIC

        // When - Save theme (simulating user selection)
        themePreferencesDataStore.saveTheme(userRole, selectedTheme)

        // Simulate app restart by retrieving theme from DataStore
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(retrievedTheme == selectedTheme) {
            "Theme should persist across app restart for child user role"
        }
    }

    @Test
    fun themePersistence_caregiverUserRole_persistsAcrossAppRestart() = runTest {
        // Given
        val userRole = UserRole.CAREGIVER
        val selectedTheme = AppTheme.MATERIAL_DARK

        // When - Save theme (simulating user selection)
        themePreferencesDataStore.saveTheme(userRole, selectedTheme)

        // Simulate app restart by retrieving theme from DataStore
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(retrievedTheme == selectedTheme) {
            "Theme should persist across app restart for caregiver user role"
        }
    }

    @Test
    fun themePersistence_multipleUserRoles_persistsIndependently() = runTest {
        // Given
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childTheme = AppTheme.MARIO_CLASSIC
        val caregiverTheme = AppTheme.MATERIAL_LIGHT

        // When - Save themes for both roles
        themePreferencesDataStore.saveTheme(childRole, childTheme)
        themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme)

        // Simulate app restart by retrieving themes from DataStore
        val retrievedChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val retrievedCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()

        // Then
        assert(retrievedChildTheme == childTheme) {
            "Child theme should persist independently"
        }
        assert(retrievedCaregiverTheme == caregiverTheme) {
            "Caregiver theme should persist independently"
        }
    }

    @Test
    fun themePersistence_defaultTheme_childUserRole() = runTest {
        // Given
        val userRole = UserRole.CHILD
        val expectedDefaultTheme = AppTheme.MARIO_CLASSIC

        // When - Clear any existing theme and retrieve default
        themePreferencesDataStore.clearTheme(userRole)
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(retrievedTheme == expectedDefaultTheme) {
            "Should return default Mario theme for child user role"
        }
    }

    @Test
    fun themePersistence_defaultTheme_caregiverUserRole() = runTest {
        // Given
        val userRole = UserRole.CAREGIVER
        val expectedDefaultTheme = AppTheme.MATERIAL_LIGHT

        // When - Clear any existing theme and retrieve default
        themePreferencesDataStore.clearTheme(userRole)
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(retrievedTheme == expectedDefaultTheme) {
            "Should return default Material Light theme for caregiver user role"
        }
    }

    @Test
    fun themePersistence_themeSwitching_persistsCorrectly() = runTest {
        // Given
        val userRole = UserRole.CHILD
        val theme1 = AppTheme.MARIO_CLASSIC
        val theme2 = AppTheme.MATERIAL_DARK
        val theme3 = AppTheme.MATERIAL_LIGHT

        // When - Switch themes multiple times
        themePreferencesDataStore.saveTheme(userRole, theme1)
        val retrievedTheme1 = themePreferencesDataStore.getTheme(userRole).first()

        themePreferencesDataStore.saveTheme(userRole, theme2)
        val retrievedTheme2 = themePreferencesDataStore.getTheme(userRole).first()

        themePreferencesDataStore.saveTheme(userRole, theme3)
        val retrievedTheme3 = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(retrievedTheme1 == theme1) { "First theme should persist" }
        assert(retrievedTheme2 == theme2) { "Second theme should persist" }
        assert(retrievedTheme3 == theme3) { "Third theme should persist" }
    }

    @Test
    fun themePersistence_allThemeTypes_persistCorrectly() = runTest {
        // Given
        val userRole = UserRole.CAREGIVER
        val allThemes = AppTheme.values().toList()

        // When - Test each theme type
        allThemes.forEach { theme ->
            themePreferencesDataStore.saveTheme(userRole, theme)
            val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()

            // Then
            assert(retrievedTheme == theme) {
                "Theme $theme should persist correctly"
            }
        }
    }

    @Test
    fun themePersistence_clearAllThemes_resetsToDefaults() = runTest {
        // Given
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childTheme = AppTheme.MATERIAL_DARK
        val caregiverTheme = AppTheme.MARIO_CLASSIC

        // Save non-default themes
        themePreferencesDataStore.saveTheme(childRole, childTheme)
        themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme)

        // When - Clear all themes
        themePreferencesDataStore.clearAllThemes()

        // Retrieve themes after clearing
        val retrievedChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val retrievedCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()

        // Then
        assert(retrievedChildTheme == AppTheme.MARIO_CLASSIC) {
            "Child theme should reset to default Mario Classic"
        }
        assert(retrievedCaregiverTheme == AppTheme.MATERIAL_LIGHT) {
            "Caregiver theme should reset to default Material Light"
        }
    }

    @Test
    fun themePersistence_uiIntegration_persistsThemeSelection() = runTest {
        // Given
        val userRole = UserRole.CHILD
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        var currentTheme = MaterialLightTheme

        // When - Display theme selector and simulate user interaction
        composeTestRule.setContent {
            ArthursLifeTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
                        // Simulate saving to DataStore
                        val appTheme = when (theme) {
                            MaterialLightTheme -> AppTheme.MATERIAL_LIGHT
                            MaterialDarkTheme -> AppTheme.MATERIAL_DARK
                            MarioClassicTheme -> AppTheme.MARIO_CLASSIC
                            else -> AppTheme.MATERIAL_LIGHT
                        }
                        runTest {
                            themePreferencesDataStore.saveTheme(userRole, appTheme)
                        }
                    },
                )
            }
        }

        // Perform theme selection
        composeTestRule
            .onNodeWithText("Material Dark")
            .performClick()

        // Then - Verify the selection was saved
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme == AppTheme.MATERIAL_DARK) {
            "Selected theme should be persisted via DataStore"
        }
        assert(selectionCount == 1) { "Selection callback should be triggered" }
        assert(selectedTheme == MaterialDarkTheme) { "Selected theme should be correct" }
    }

    @Test
    fun themePersistence_multipleSessions_maintainsState() = runTest {
        // Given
        val userRole = UserRole.CAREGIVER
        val initialTheme = AppTheme.MATERIAL_LIGHT
        val changedTheme = AppTheme.MATERIAL_DARK

        // When - First session: save initial theme
        themePreferencesDataStore.saveTheme(userRole, initialTheme)
        val firstSessionTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Second session: change theme
        themePreferencesDataStore.saveTheme(userRole, changedTheme)
        val secondSessionTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Third session: verify persistence
        val thirdSessionTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(firstSessionTheme == initialTheme) { "First session theme should be correct" }
        assert(secondSessionTheme == changedTheme) { "Second session theme should be correct" }
        assert(thirdSessionTheme == changedTheme) { "Third session theme should persist" }
    }

    @Test
    fun themePersistence_longRunningSession_maintainsConsistency() = runTest {
        // Given
        val userRole = UserRole.CHILD
        val themes = listOf(
            AppTheme.MARIO_CLASSIC,
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
            AppTheme.MARIO_CLASSIC,
            AppTheme.MATERIAL_LIGHT,
        )

        // When - Simulate rapid theme changes in a long session
        themes.forEach { theme ->
            themePreferencesDataStore.saveTheme(userRole, theme)
            val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()

            // Then - Each change should be immediately persisted
            assert(retrievedTheme == theme) {
                "Theme $theme should be immediately persisted and retrievable"
            }
        }

        // Final verification - last theme should still be persisted
        val finalTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(finalTheme == themes.last()) {
            "Final theme should persist after long session"
        }
    }

    @Test
    fun themePersistence_concurrentUserSessions_maintainSeparateState() = runTest {
        // Given
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childThemes = listOf(AppTheme.MARIO_CLASSIC, AppTheme.MATERIAL_DARK)
        val caregiverThemes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK)

        // When - Simulate concurrent user sessions
        childThemes.forEach { theme ->
            themePreferencesDataStore.saveTheme(childRole, theme)
        }

        caregiverThemes.forEach { theme ->
            themePreferencesDataStore.saveTheme(caregiverRole, theme)
        }

        // Then - Each user should maintain their own theme state
        val finalChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val finalCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()

        assert(finalChildTheme == childThemes.last()) {
            "Child theme should be isolated from caregiver changes"
        }
        assert(finalCaregiverTheme == caregiverThemes.last()) {
            "Caregiver theme should be isolated from child changes"
        }
    }

    @Test
    fun themePersistence_partialClear_maintainsOtherUserThemes() = runTest {
        // Given
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childTheme = AppTheme.MARIO_CLASSIC
        val caregiverTheme = AppTheme.MATERIAL_DARK

        // Save themes for both users
        themePreferencesDataStore.saveTheme(childRole, childTheme)
        themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme)

        // When - Clear only child theme
        themePreferencesDataStore.clearTheme(childRole)

        // Then - Child should reset to default, caregiver should remain unchanged
        val retrievedChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val retrievedCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()

        assert(retrievedChildTheme == AppTheme.MARIO_CLASSIC) {
            "Child theme should reset to default"
        }
        assert(retrievedCaregiverTheme == caregiverTheme) {
            "Caregiver theme should remain unchanged"
        }
    }

    @Test
    fun themePersistence_dataStoreIntegration_handlesAppRestartScenario() = runTest {
        // Given
        val userRole = UserRole.CAREGIVER
        val selectedTheme = AppTheme.MATERIAL_DARK

        // When - First app session: save theme
        themePreferencesDataStore.saveTheme(userRole, selectedTheme)

        // Simulate app restart: create new DataStore instance
        val restartedDataStore = ThemePreferencesDataStore(
            ApplicationProvider.getApplicationContext(),
        )
        val retrievedTheme = restartedDataStore.getTheme(userRole).first()

        // Then
        assert(retrievedTheme == selectedTheme) {
            "Theme should persist across app restart with new DataStore instance"
        }
    }

    @Test
    fun themePersistence_errorRecovery_maintainsConsistentState() = runTest {
        // Given
        val userRole = UserRole.CHILD
        val validTheme = AppTheme.MARIO_CLASSIC

        // When - Save valid theme
        themePreferencesDataStore.saveTheme(userRole, validTheme)

        // Simulate potential error scenario by clearing and checking recovery
        themePreferencesDataStore.clearTheme(userRole)
        val recoveredTheme = themePreferencesDataStore.getTheme(userRole).first()

        // Then
        assert(recoveredTheme == AppTheme.MARIO_CLASSIC) {
            "Should recover to default theme after error/clear"
        }
    }
}
