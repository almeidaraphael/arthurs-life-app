package com.lemonqwest.app.ui.theme

import com.lemonqwest.app.data.theme.ThemePreferencesDataStore\nimport com.lemonqwest.app.testutils.AndroidTestBase
import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.user.UserRole
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

class ThemePersistenceSessionManagementTest : AndroidTestBase() {

    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
    }

    @Test
    fun themePersistence_multipleSessions_maintainsState() = runTest {
        val userRole = UserRole.CAREGIVER
        val initialTheme = AppTheme.MATERIAL_LIGHT
        val changedTheme = AppTheme.MATERIAL_DARK
        themePreferencesDataStore.saveTheme(userRole, initialTheme)
        val firstSessionTheme = themePreferencesDataStore.getTheme(userRole).first()
        themePreferencesDataStore.saveTheme(userRole, changedTheme)
        val secondSessionTheme = themePreferencesDataStore.getTheme(userRole).first()
        val thirdSessionTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(firstSessionTheme == initialTheme) { "First session theme should be correct" }
        assert(secondSessionTheme == changedTheme) { "Second session theme should be correct" }
        assert(thirdSessionTheme == changedTheme) { "Third session theme should persist" }
    }

    @Test
    fun themePersistence_longRunningSession_maintainsConsistency() = runTest {
        val userRole = UserRole.CHILD
        val themes = listOf(
            AppTheme.MARIO_CLASSIC,
            AppTheme.MATERIAL_LIGHT,
            AppTheme.MATERIAL_DARK,
            AppTheme.MARIO_CLASSIC,
            AppTheme.MATERIAL_LIGHT,
        )
        themes.forEach { theme ->
            themePreferencesDataStore.saveTheme(userRole, theme)
            val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
            assert(retrievedTheme == theme) {
                "Theme $theme should be immediately persisted and retrievable"
            }
        }
        val finalTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(finalTheme == themes.last()) {
            "Final theme should persist after long session"
        }
    }

    @Test
    fun themePersistence_concurrentUserSessions_maintainSeparateState() = runTest {
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childThemes = listOf(AppTheme.MARIO_CLASSIC, AppTheme.MATERIAL_DARK)
        val caregiverThemes = listOf(AppTheme.MATERIAL_LIGHT, AppTheme.MATERIAL_DARK)
        childThemes.forEach { theme ->
            themePreferencesDataStore.saveTheme(childRole, theme)
        }
        caregiverThemes.forEach { theme ->
            themePreferencesDataStore.saveTheme(caregiverRole, theme)
        }
        val finalChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val finalCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()
        assert(finalChildTheme == childThemes.last()) {
            "Child theme should be isolated from caregiver changes"
        }
        assert(finalCaregiverTheme == caregiverThemes.last()) {
            "Caregiver theme should be isolated from child changes"
        }
    }
}
