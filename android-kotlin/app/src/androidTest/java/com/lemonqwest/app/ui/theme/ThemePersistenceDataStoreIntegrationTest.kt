package com.lemonqwest.app.ui.theme

import androidx.test.core.app.ApplicationProvider
import com.lemonqwest.app.data.theme.ThemePreferencesDataStore
import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.testutils.AndroidTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

class ThemePersistenceDataStoreIntegrationTest : AndroidTestBase() {
    @Inject
    lateinit var themePreferencesDataStore: ThemePreferencesDataStore

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
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
