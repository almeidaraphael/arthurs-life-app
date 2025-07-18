package com.lemonqwest.app.domain.user

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldAfford
import com.lemonqwest.app.domain.shouldBeCaregiver
import com.lemonqwest.app.domain.shouldBeChild
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for User integration scenarios.
 *
 * Tests cover:
 * - Task completion integration
 * - Reward redemption scenarios
 * - Family scenario testing
 */
@DisplayName("User Integration Tests")
class UserIntegrationTest {

    @Test
    @DisplayName("Should integrate with task completion scenario")
    fun shouldIntegrateWithTaskCompletion() {
        val (task, user, achievement) = TestDataFactory.createTaskCompletionScenario()

        // Verify user is properly set up for task completion
        assertEquals(user.id, task.assignedToUserId, "Task should be assigned to user")
        assertEquals(user.id, achievement.userId, "Achievement should belong to user")

        user.shouldBeChild()
        user.shouldAfford(10) // Should afford some rewards
    }

    @Test
    @DisplayName("Should integrate with reward redemption scenario")
    fun shouldIntegrateWithRewardRedemption() {
        val user = TestDataFactory.createChildUser(
            tokenBalance = TokenBalance.create(50),
        )
        val canAfford = 50 >= 30

        assertEquals(
            50,
            user.tokenBalance.getValue(),
            "User should have expected token balance",
        )
        assertTrue(canAfford, "User should be able to afford the reward")
        user.shouldAfford(30)
    }

    @Test
    @DisplayName("Should integrate with family scenario")
    fun shouldIntegrateWithFamilyScenario() {
        val child = TestDataFactory.createChildUser(
            name = "Emma",
            tokenBalance = TokenBalance.create(75),
        )
        val caregiver = TestDataFactory.createCaregiverUser(name = "Mom")

        child.shouldBeChild(expectedName = "Emma", expectedTokens = 75)
        caregiver.shouldBeCaregiver(expectedName = "Mom")

        assertNotEquals(child.id, caregiver.id, "Child and caregiver should have different IDs")
    }
}
