package com.lemonqwest.app.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ProfileScreenEmptyStateTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @BeforeEach
    fun setup() { hiltRule.inject() }

    @Test
    fun profileScreen_displaysNoUserMessage_whenNoUserSelected() {
        composeTestRule.setContent {
            BaseAppTheme { profileScreen(selectedUser = null, onRefresh = {}) }
        }
        composeTestRule.onNodeWithText(
            "Select a family member to view their profile",
        ).assertIsDisplayed()
    }
}
