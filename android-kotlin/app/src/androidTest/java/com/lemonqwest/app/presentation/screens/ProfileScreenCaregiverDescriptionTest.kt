package com.lemonqwest.app.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.lemonqwest.app.domain.auth.PIN
import com.lemonqwest.app.domain.user.TokenBalance
import com.lemonqwest.app.domain.user.User
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.BaseAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ProfileScreenCaregiverDescriptionTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()
    private val testCaregiverUser = User(
        id = "caregiver1",
        name = "Parent",
        role = UserRole.CAREGIVER,
        tokenBalance = TokenBalance.zero(),
        pin = PIN.create("1234"),
    )

    @BeforeEach
    fun setup() { hiltRule.inject() }

    @Test
    fun profileScreen_displaysCaregiverDescription_forCaregiverUser() {
        composeTestRule.setContent {
            BaseAppTheme { profileScreen(selectedUser = testCaregiverUser, onRefresh = {}) }
        }
        composeTestRule.onNodeWithText(
            "You have full access to manage tasks, rewards, and family settings.",
        ).assertIsDisplayed()
    }
}
