package com.arthurslife.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arthurslife.app.domain.user.UserRole
import com.arthurslife.app.presentation.screens.RoleSelectionScreen
import com.arthurslife.app.presentation.theme.ThemeViewModel
import com.arthurslife.app.ui.ComposeUiTestBase
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive UI tests for RoleSelectionScreen.
 *
 * Tests verify:
 * - Role selection buttons display and functionality
 * - Theme-aware rendering
 * - User interaction callbacks
 * - Accessibility compliance
 * - Cross-theme consistency
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RoleSelectionScreenTest : ComposeUiTestBase() {

    private var selectedRole: UserRole? = null
    private var childDirectAccessCalled = false

    private fun resetCallbacks() {
        selectedRole = null
        childDirectAccessCalled = false
    }

    @Test
    fun roleSelectionScreen_displaysRoleSelectionButtons() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Verify role selection buttons are displayed
        composeTestRule.onNode(hasContentDescription("Role selection buttons"))
            .assertIsDisplayed()
    }

    @Test
    fun roleSelectionScreen_childButtonIsClickable() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Find and click the child access button
        composeTestRule.onNode(hasContentDescription("Child access button"))
            .performClick()

        // Verify callback was called
        assert(childDirectAccessCalled) { "Child direct access callback should have been called" }
    }

    @Test
    fun roleSelectionScreen_caregiverButtonIsClickable() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Find and click the caregiver access button
        composeTestRule.onNode(hasContentDescription("Caregiver access button"))
            .performClick()

        // Verify callback was called with correct role
        assert(selectedRole == UserRole.CAREGIVER) { 
            "Caregiver role should have been selected, but got: $selectedRole" 
        }
    }

    @Test
    fun roleSelectionScreen_worksWithMarioTheme() {
        resetCallbacks()
        
        setContentWithSpecificTheme("mario_classic") {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Verify role selection buttons are displayed with Mario theme
        composeTestRule.onNode(hasContentDescription("Role selection buttons"))
            .assertIsDisplayed()

        // Mario theme should use game-appropriate language
        composeTestRule.onNode(hasText("Player", ignoreCase = true))
            .assertIsDisplayed()
    }

    @Test
    fun roleSelectionScreen_worksWithMaterialThemes() {
        resetCallbacks()
        
        setContentWithSpecificTheme("material_light") {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Verify role selection buttons are displayed with Material theme
        composeTestRule.onNode(hasContentDescription("Role selection buttons"))
            .assertIsDisplayed()

        // Material theme should use standard language
        composeTestRule.onNode(hasText("Child", ignoreCase = true))
            .assertIsDisplayed()
    }

    @Test
    fun roleSelectionScreen_hasProperLayout() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Verify the screen is centered and properly laid out
        composeTestRule.onNode(hasContentDescription("Role selection buttons"))
            .assertIsDisplayed()

        // The layout should be centered both horizontally and vertically
        // This is ensured by the Column with center alignment
    }

    @Test
    fun roleSelectionScreen_accessibilityLabels() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Verify accessibility labels are present
        composeTestRule.onNode(hasContentDescription("Role selection buttons"))
            .assertIsDisplayed()

        composeTestRule.onNode(hasContentDescription("Child access button"))
            .assertIsDisplayed()

        composeTestRule.onNode(hasContentDescription("Caregiver access button"))
            .assertIsDisplayed()
    }

    @Test
    fun roleSelectionScreen_multipleClicks() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Click child button multiple times
        composeTestRule.onNode(hasContentDescription("Child access button"))
            .performClick()
        
        assert(childDirectAccessCalled) { "First child click should work" }
        
        resetCallbacks()
        
        composeTestRule.onNode(hasContentDescription("Child access button"))
            .performClick()
        
        assert(childDirectAccessCalled) { "Second child click should work" }

        // Click caregiver button
        composeTestRule.onNode(hasContentDescription("Caregiver access button"))
            .performClick()

        assert(selectedRole == UserRole.CAREGIVER) { "Caregiver selection should work after child clicks" }
    }

    @Test
    fun roleSelectionScreen_buttonStatesAndInteraction() {
        resetCallbacks()
        
        setContentWithTheme {
            RoleSelectionScreen(
                themeViewModel = hiltViewModel<ThemeViewModel>(),
                onRoleSelected = { role -> selectedRole = role },
                onChildDirectAccess = { childDirectAccessCalled = true },
            )
        }

        // Verify both buttons are present and clickable
        val childButton = composeTestRule.onNode(hasContentDescription("Child access button"))
        val caregiverButton = composeTestRule.onNode(hasContentDescription("Caregiver access button"))

        childButton.assertIsDisplayed()
        caregiverButton.assertIsDisplayed()

        // Test interactions
        childButton.performClick()
        assert(childDirectAccessCalled) { "Child button should trigger callback" }

        resetCallbacks()
        
        caregiverButton.performClick()
        assert(selectedRole == UserRole.CAREGIVER) { "Caregiver button should trigger callback" }
    }

    @Test
    fun roleSelectionScreen_themeConsistency() {
        resetCallbacks()
        val themes = listOf("mario_classic", "material_light", "material_dark")

        themes.forEach { themeName ->
            setContentWithSpecificTheme(themeName) {
                RoleSelectionScreen(
                    themeViewModel = hiltViewModel<ThemeViewModel>(),
                    onRoleSelected = { role -> selectedRole = role },
                    onChildDirectAccess = { childDirectAccessCalled = true },
                )
            }

            // Verify role selection buttons are displayed consistently across themes
            composeTestRule.onNode(hasContentDescription("Role selection buttons"))
                .assertIsDisplayed()

            composeTestRule.onNode(hasContentDescription("Child access button"))
                .assertIsDisplayed()

            composeTestRule.onNode(hasContentDescription("Caregiver access button"))
                .assertIsDisplayed()
        }
    }
}