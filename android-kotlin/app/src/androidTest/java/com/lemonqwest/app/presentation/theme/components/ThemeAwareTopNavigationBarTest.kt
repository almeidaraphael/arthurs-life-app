package com.lemonqwest.app.presentation.theme.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lemonqwest.app.domain.user.TokenBalance
import com.lemonqwest.app.domain.user.UserRole
import com.lemonqwest.app.presentation.theme.LemonQwestTheme
import com.lemonqwest.app.presentation.theme.mario.MarioClassicTheme
import com.lemonqwest.app.presentation.viewmodels.AchievementsProgress
import com.lemonqwest.app.presentation.viewmodels.SelectedChild
import com.lemonqwest.app.presentation.viewmodels.TasksProgress
import com.lemonqwest.app.presentation.viewmodels.TopBarScreen
import com.lemonqwest.app.presentation.viewmodels.TopBarState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@HiltAndroidTest
class ThemeAwareTopNavigationBarTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private var avatarClickCount = 0
    private var settingsClickCount = 0
    private var childSelectedCount = 0
    private var selectedChildId = ""

    @BeforeEach
    fun setup() {
        hiltRule.inject()
        avatarClickCount = 0
        settingsClickCount = 0
        childSelectedCount = 0
        selectedChildId = ""
    }

    @Test
    fun themeAwareTopNavigationBar_withChildHomeState_displaysCorrectElements() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.HOME,
            tasksProgress = TasksProgress(completedTasks = 3, totalTasks = 5),
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Open profile for Test Child")
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithContentDescription("Open settings")
            .assertIsDisplayed()
            .assertHasClickAction()

        // Token balance should be displayed
        composeTestRule
            .onNodeWithText("150")
            .assertIsDisplayed()

        // Tasks progress should be displayed
        composeTestRule
            .onNodeWithText("3/5")
            .assertIsDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_withChildTasksState_displaysTaskSpecificElements() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.TASKS,
            tasksProgress = TasksProgress(completedTasks = 2, totalTasks = 4),
            totalTokensEarned = TokenBalance.fromInt(300),
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // Current token balance
        composeTestRule
            .onNodeWithText("150")
            .assertIsDisplayed()

        // Tasks progress
        composeTestRule
            .onNodeWithText("2/4")
            .assertIsDisplayed()

        // Total tokens earned (tasks screen only)
        composeTestRule
            .onNodeWithText("300")
            .assertIsDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_withChildRewardsState_displaysRewardsElements() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.REWARDS,
            rewardsAvailable = 5,
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // Available rewards count
        composeTestRule
            .onNodeWithText("5")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Available")
            .assertIsDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_withChildAchievementsState_displaysAchievementsElements() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.ACHIEVEMENTS,
            achievementsProgress = AchievementsProgress(
                unlockedAchievements = 7,
                totalAchievements = 10,
            ),
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // Achievements progress
        composeTestRule
            .onNodeWithText("7/10")
            .assertIsDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_withCaregiverHomeState_displaysSelectedChild() {
        // Given
        val caregiverState = TopBarState.forCaregiver(
            user = createTestCaregiverUser(),
            screen = TopBarScreen.HOME,
            selectedChild = SelectedChild(
                childId = "child-123",
                childName = "Emma",
                childAvatar = "emma_avatar",
            ),
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = caregiverState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = {
                        childSelectedCount++
                        selectedChildId = it
                    },
                )
            }
        }

        // Then
        // Selected child name should be displayed
        composeTestRule
            .onNodeWithText("Emma")
            .assertIsDisplayed()
            .assertHasClickAction()

        // No token balance for caregivers
        composeTestRule
            .onNodeWithText("150")
            .assertIsNotDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_withCaregiverNonHomeState_doesNotDisplaySelectedChild() {
        // Given
        val caregiverState = TopBarState.forCaregiver(
            user = createTestCaregiverUser(),
            screen = TopBarScreen.CHILDREN_MANAGEMENT,
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = caregiverState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // No selected child on non-home screens
        composeTestRule
            .onNode(hasContentDescription("Selected child"))
            .assertIsNotDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_avatarClick_triggersCallback() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.HOME,
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithContentDescription("Open profile for Test Child")
            .performClick()

        // Then
        assert(avatarClickCount == 1)
    }

    @Test
    fun themeAwareTopNavigationBar_settingsClick_triggersCallback() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.HOME,
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithContentDescription("Open settings")
            .performClick()

        // Then
        assert(settingsClickCount == 1)
    }

    @Test
    fun themeAwareTopNavigationBar_childSelected_triggersCallback() {
        // Given
        val caregiverState = TopBarState.forCaregiver(
            user = createTestCaregiverUser(),
            screen = TopBarScreen.HOME,
            selectedChild = SelectedChild(
                childId = "child-456",
                childName = "Alex",
                childAvatar = "alex_avatar",
            ),
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = caregiverState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = {
                        childSelectedCount++
                        selectedChildId = it
                    },
                )
            }
        }

        // Perform click
        composeTestRule
            .onNodeWithContentDescription("Selected child: Alex, tap to change")
            .performClick()

        // Then
        assert(childSelectedCount == 1)
        assert(selectedChildId == "child-456")
    }

    @Test
    fun themeAwareTopNavigationBar_withMarioTheme_displaysCorrectStyling() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.HOME,
        )

        // When
        composeTestRule.setContent {
            MarioClassicTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = MarioClassicTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // Verify basic elements are still displayed with Mario theme
        composeTestRule
            .onNodeWithContentDescription("Open profile for Test Child")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Open settings")
            .assertIsDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_accessibilityContentDescription_isCorrect() {
        // Given
        val childState = TopBarState.forChild(
            user = createTestUser(),
            screen = TopBarScreen.TASKS,
            tasksProgress = TasksProgress(completedTasks = 2, totalTasks = 5),
        )

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = childState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // Check that the top bar has proper accessibility description
        composeTestRule
            .onNode(
                hasContentDescription(
                    "Top navigation bar for child on tasks screen, 150 tokens, tasks progress 2/5",
                ),
            )
            .assertIsDisplayed()
    }

    @Test
    fun themeAwareTopNavigationBar_emptyState_displaysMinimalElements() {
        // Given
        val emptyState = TopBarState.empty()

        // When
        composeTestRule.setContent {
            LemonQwestTheme {
                themeAwareTopNavigationBar(
                    state = emptyState,
                    theme = LemonQwestTheme,
                    onAvatarClick = { avatarClickCount++ },
                    onSettingsClick = { settingsClickCount++ },
                    onChildSelected = { selectedChildId = it },
                )
            }
        }

        // Then
        // Avatar should not be clickable in empty state
        composeTestRule
            .onNodeWithContentDescription("User avatar")
            .assertIsDisplayed()

        // Settings should not be visible in empty state
        composeTestRule
            .onNodeWithContentDescription("Open settings")
            .assertIsNotDisplayed()
    }

    private fun createTestUser() = com.lemonqwest.app.domain.user.User(
        id = "test-child-123",
        name = "Test Child",
        displayName = "Test Child",
        role = UserRole.CHILD,
        avatarData = "test_avatar",
        tokenBalance = TokenBalance.fromInt(150),
        pin = com.lemonqwest.app.domain.auth.PIN.create("1234").getOrThrow(),
        isAdmin = false,
    )

    private fun createTestCaregiverUser() = com.lemonqwest.app.domain.user.User(
        id = "test-caregiver-123",
        name = "Test Caregiver",
        displayName = "Test Caregiver",
        role = UserRole.CAREGIVER,
        avatarData = "caregiver_avatar",
        tokenBalance = TokenBalance.fromInt(0),
        pin = com.lemonqwest.app.domain.auth.PIN.create("5678").getOrThrow(),
        isAdmin = true,
    )
}
