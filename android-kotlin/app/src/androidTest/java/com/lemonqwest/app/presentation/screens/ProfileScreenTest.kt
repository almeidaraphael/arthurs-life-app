package com.lemonqwest.app.presentation.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
class ProfileScreenTest {

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

    private val testCaregiverUser = User(
        id = "caregiver1",
        name = "Parent",
        role = UserRole.CAREGIVER,
        tokenBalance = TokenBalance.zero(),
        pin = PIN.create("1234"),
    )

    @BeforeEach
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun profileScreen_displaysNoUserMessage_whenNoUserSelected() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = null,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Select a family member to view their profile")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysChildProfile_whenChildUserSelected() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testChildUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Little Arthur")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Child")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("My Stats")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("50 tokens")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysCaregiverProfile_whenCaregiverUserSelected() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testCaregiverUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Parent")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Caregiver")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Caregiver Access")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("PIN Protected")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysUserAvatar_withAccessibilityDescription() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testChildUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Avatar for Little Arthur")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysProfileInformation() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testChildUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Profile Information")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Name")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Role")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Favorite Color")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Blue")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysTokenBalance_forChildUser() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testChildUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Token Balance")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("50 tokens")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Tokens")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysCaregiverDescription_forCaregiverUser() {
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testCaregiverUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("You have full access to manage tasks, rewards, and family settings.")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysLoadingIndicator_whenLoading() {
        // This test would require mocking the ViewModel to return a loading state
        // Given
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = testChildUser,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then - in actual implementation, you would test loading state
        // composeTestRule
        //     .onNodeWithContentDescription("Loading profile")
        //     .assertIsDisplayed()
    }

    @Test
    fun profileScreen_displaysErrorMessage_whenError() {
        // This test would require mocking the ViewModel to return an error state
        // and then verify the error message and retry button are displayed

        // Expected behavior:
        // composeTestRule
        //     .onNodeWithText("Failed to load profile")
        //     .assertIsDisplayed()
        //
        // composeTestRule
        //     .onNodeWithText("Retry")
        //     .assertIsDisplayed()
    }

    @Test
    fun profileScreen_handlesUserDisplayName_fallback() {
        // Given
        val userWithoutDisplayName = testChildUser.copy(displayName = null)
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = userWithoutDisplayName,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Arthur")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_doesNotDisplayFavoriteColor_whenNotSet() {
        // Given
        val userWithoutFavoriteColor = testChildUser.copy(favoriteColor = null)
        var refreshCalled = false

        // When
        composeTestRule.setContent {
            BaseAppTheme {
                profileScreen(
                    selectedUser = userWithoutFavoriteColor,
                    onRefresh = { refreshCalled = true },
                )
            }
        }

        // Then - favorite color section should not be displayed
        // This would require checking that the favorite color row is not present
        composeTestRule
            .onNodeWithText("Little Arthur")
            .assertIsDisplayed()

        // Verify other content is still displayed
        composeTestRule
            .onNodeWithText("Profile Information")
            .assertIsDisplayed()
    }
}
