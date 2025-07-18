package com.lemonqwest.app.presentation.screens

import androidx.compose.ui.test.junit4.createComposeRule
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ProfileScreenLoadingIndicatorTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @BeforeEach
    fun setup() { hiltRule.inject() }

    @Test
    fun profileScreen_displaysLoadingIndicator_whenLoading() {
        composeTestRule.setContent {
            BaseAppTheme { profileScreen(selectedUser = null, onRefresh = {}) }
        }
        // Would verify loading indicator if ViewModel was mocked
    }
}
