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

class ThemePersistenceEdgeCasesTest : AndroidTestBase() {
    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
    }

    @Test
    fun themePersistence_clearAllThemes_resetsToDefaults() = runTest {
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childTheme = AppTheme.MATERIAL_DARK
        val caregiverTheme = AppTheme.MARIO_CLASSIC
        themePreferencesDataStore.saveTheme(childRole, childTheme)
        themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme)
        themePreferencesDataStore.clearAllThemes()
        val retrievedChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val retrievedCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()
        assert(retrievedChildTheme == AppTheme.MARIO_CLASSIC) {
            "Child theme should reset to default Mario Classic"
        }
        assert(retrievedCaregiverTheme == AppTheme.MATERIAL_LIGHT) {
            "Caregiver theme should reset to default Material Light"
        }
    }

    @Test
    fun themePersistence_partialClear_maintainsOtherUserThemes() = runTest {
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childTheme = AppTheme.MARIO_CLASSIC
        val caregiverTheme = AppTheme.MATERIAL_DARK
        themePreferencesDataStore.saveTheme(childRole, childTheme)
        themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme)
        themePreferencesDataStore.clearTheme(childRole)
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
    fun themePersistence_errorRecovery_maintainsConsistentState() = runTest {
        val userRole = UserRole.CHILD
        val validTheme = AppTheme.MARIO_CLASSIC
        themePreferencesDataStore.saveTheme(userRole, validTheme)
        themePreferencesDataStore.clearTheme(userRole)
        val recoveredTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(recoveredTheme == AppTheme.MARIO_CLASSIC) {
            "Should recover to default theme after error/clear"
        }
    }
}
