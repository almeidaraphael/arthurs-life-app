package com.lemonqwest.app.ui.theme

import com.lemonqwest.app.data.theme.ThemePreferencesDataStore
import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.user.UserRole
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

@HiltAndroidTest
class ThemePersistenceChildRoleTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun themePersistence_childUserRole_persistsAcrossAppRestart() = runTest {
        val userRole = UserRole.CHILD
        val selectedTheme = AppTheme.MARIO_CLASSIC
        themePreferencesDataStore.saveTheme(userRole, selectedTheme)
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme == selectedTheme) {
            "Theme should persist across app restart for child user role"
        }
    }

    @Test
    fun themePersistence_defaultTheme_childUserRole() = runTest {
        val userRole = UserRole.CHILD
        val expectedDefaultTheme = AppTheme.MARIO_CLASSIC
        themePreferencesDataStore.clearTheme(userRole)
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme == expectedDefaultTheme) {
            "Should return default Mario theme for child user role"
        }
    }
}
