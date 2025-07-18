package com.lemonqwest.app.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
class ProfileScreenChildProfileTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()
    private val testChildUser = User(
        id = "child1",
        name = "Lemmy",
        role = UserRole.CHILD,
        tokenBalance = TokenBalance.create(50),
        displayName = "Little Arthur",
        favoriteColor = "Blue",
    )

    @BeforeEach
    fun setup() { hiltRule.inject() }

    @Test
    fun profileScreen_displaysChildProfile_whenChildUserSelected() {
        composeTestRule.setContent {
            BaseAppTheme { profileScreen(selectedUser = testChildUser, onRefresh = {}) }
        }
        composeTestRule.onNodeWithText("Little Arthur").assertIsDisplayed()
        composeTestRule.onNodeWithText("Child").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Stats").assertIsDisplayed()
        composeTestRule.onNodeWithText("50 tokens").assertIsDisplayed()
    }
}
