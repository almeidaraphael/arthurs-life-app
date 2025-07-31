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

class ThemePersistenceMultiUserTest : AndroidTestBase() {
    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
    }

    @Test
    fun themePersistence_multipleUserRoles_persistsIndependently() = runTest {
        val childRole = UserRole.CHILD
        val caregiverRole = UserRole.CAREGIVER
        val childTheme = AppTheme.MARIO_CLASSIC
        val caregiverTheme = AppTheme.MATERIAL_LIGHT
        themePreferencesDataStore.saveTheme(childRole, childTheme)
        themePreferencesDataStore.saveTheme(caregiverRole, caregiverTheme)
        val retrievedChildTheme = themePreferencesDataStore.getTheme(childRole).first()
        val retrievedCaregiverTheme = themePreferencesDataStore.getTheme(caregiverRole).first()
        assert(retrievedChildTheme == childTheme) {
            "Child theme should persist independently"
        }
        assert(retrievedCaregiverTheme == caregiverTheme) {
            "Caregiver theme should persist independently"
        }
    }
}
