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

class ThemePersistenceChildRoleTest : AndroidTestBase() {
    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
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
