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

class ThemePersistenceCaregiverRoleTest : AndroidTestBase() {
    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
    }

    @Test
    fun themePersistence_caregiverUserRole_persistsAcrossAppRestart() = runTest {
        val userRole = UserRole.CAREGIVER
        val selectedTheme = AppTheme.MATERIAL_DARK
        themePreferencesDataStore.saveTheme(userRole, selectedTheme)
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme == selectedTheme) {
            "Theme should persist across app restart for caregiver user role"
        }
    }

    @Test
    fun themePersistence_defaultTheme_caregiverUserRole() = runTest {
        val userRole = UserRole.CAREGIVER
        val expectedDefaultTheme = AppTheme.MATERIAL_LIGHT
        themePreferencesDataStore.clearTheme(userRole)
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme == expectedDefaultTheme) {
            "Should return default Material Light theme for caregiver user role"
        }
    }
}
