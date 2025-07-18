package com.lemonqwest.app.ui.theme

import androidx.test.core.app.ApplicationProvider
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
class ThemePersistenceDataStoreIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun themePersistence_dataStoreIntegration_handlesAppRestartScenario() = runTest {
        val userRole = UserRole.CAREGIVER
        val selectedTheme = AppTheme.MATERIAL_DARK
        themePreferencesDataStore.saveTheme(userRole, selectedTheme)
        val restartedDataStore = ThemePreferencesDataStore(
            ApplicationProvider.getApplicationContext(),
        )
        val retrievedTheme = restartedDataStore.getTheme(userRole).first()
        assert(retrievedTheme == selectedTheme) {
            "Theme should persist across app restart with new DataStore instance"
        }
    }
}
