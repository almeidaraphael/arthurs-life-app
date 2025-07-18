package com.lemonqwest.app.ui.theme

import com.lemonqwest.app.data.theme.ThemePreferencesDataStore
import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.AndroidTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

class ThemePersistenceSwitchingTest : AndroidTestBase() {
    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
    }

    @Test
    fun themePersistence_themeSwitching_persistsCorrectly() = runTest {
        val userRole = UserRole.CHILD
        val theme1 = AppTheme.MARIO_CLASSIC
        val theme2 = AppTheme.MATERIAL_DARK
        val theme3 = AppTheme.MATERIAL_LIGHT
        themePreferencesDataStore.saveTheme(userRole, theme1)
        val retrievedTheme1 = themePreferencesDataStore.getTheme(userRole).first()
        themePreferencesDataStore.saveTheme(userRole, theme2)
        val retrievedTheme2 = themePreferencesDataStore.getTheme(userRole).first()
        themePreferencesDataStore.saveTheme(userRole, theme3)
        val retrievedTheme3 = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme1 == theme1) { "First theme should persist" }
        assert(retrievedTheme2 == theme2) { "Second theme should persist" }
        assert(retrievedTheme3 == theme3) { "Third theme should persist" }
    }

    @Test
    fun themePersistence_allThemeTypes_persistCorrectly() = runTest {
        val userRole = UserRole.CAREGIVER
        val allThemes = AppTheme.values().toList()
        allThemes.forEach { theme ->
            themePreferencesDataStore.saveTheme(userRole, theme)
            val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
            assert(retrievedTheme == theme) {
                "Theme $theme should persist correctly"
            }
        }
    }
}
