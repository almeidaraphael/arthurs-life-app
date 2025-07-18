package com.lemonqwest.app.ui.theme

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.theme.model.AppTheme
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import com.lemonqwest.app.presentation.theme.components.ThemeSelector
import com.lemonqwest.app.presentation.theme.mario.MarioClassicTheme
import com.lemonqwest.app.presentation.theme.materialdark.MaterialDarkTheme
import com.lemonqwest.app.presentation.theme.materiallight.MaterialLightTheme
import com.lemonqwest.app.testutils.AndroidTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.inject.Inject

class ThemePersistenceUiIntegrationTest : AndroidTestBase() {
    @get:Rule
    val composeTestRule: ComposeContentTestRule = createComposeRule()

    @Inject
    lateinit var themePreferencesDataStore: com.lemonqwest.app.data.theme.ThemePreferencesDataStore

    private var selectedTheme: com.lemonqwest.app.presentation.theme.BaseAppTheme? = null
    private var selectionCount = 0

    @BeforeEach
    override fun setUpAndroidTest() {
        super.setUpAndroidTest()
        selectedTheme = null
        selectionCount = 0
    }

    @Test
    fun themePersistence_uiIntegration_persistsThemeSelection() = runTest {
        val userRole = UserRole.CHILD
        val availableThemes = listOf(
            MaterialLightTheme,
            MaterialDarkTheme,
            MarioClassicTheme,
        )
        var currentTheme = MaterialLightTheme
        composeTestRule.setContent {
            LemonQwestTheme {
                ThemeSelector(
                    currentTheme = currentTheme,
                    availableThemes = availableThemes,
                    onThemeSelected = { theme ->
                        selectedTheme = theme
                        selectionCount++
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
        composeTestRule.onNodeWithText("Material Dark").performClick()
        val retrievedTheme = themePreferencesDataStore.getTheme(userRole).first()
        assert(retrievedTheme == AppTheme.MATERIAL_DARK) {
            "Selected theme should be persisted via DataStore"
        }
        assert(selectionCount == 1) { "Selection callback should be triggered" }
        assert(selectedTheme == MaterialDarkTheme) { "Selected theme should be correct" }
    }
}
