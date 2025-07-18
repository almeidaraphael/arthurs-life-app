package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlockable
import com.lemonqwest.app.domain.shouldBeUnlocked
import com.lemonqwest.app.domain.shouldHaveProgress
import com.lemonqwest.app.domain.shouldNotBeUnlocked
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.UUID

/**
 * Focused test suite for achievement integration scenarios.
 *
 * Tests cover:
 * - Task completion integration scenarios
 * - Multiple achievements for same user
 * - Progress tracking across achievement types
 * - Cross-domain integration scenarios
 */
@DisplayName("Achievement Integration Tests")
class AchievementIntegrationTest {

    @Test
    @DisplayName("Should integrate with task completion scenario")
    fun shouldIntegrateWithTaskCompletionScenario() {
        val (user, task, achievement) = TestDataFactory.createTaskCompletionScenario()

        // Verify achievement is properly set up for task completion
        assertEquals(user.id, achievement.userId, "Achievement should belong to user")
        assertEquals(
            AchievementType.FIRST_STEPS,
            achievement.type,
            "Should use First Steps achievement",
        )
        achievement.shouldNotBeUnlocked()
        achievement.shouldHaveProgress(0, 0)

        // Simulate task completion and achievement unlock
        val progressedAchievement = achievement.updateProgress(1)
        progressedAchievement.shouldBeUnlockable()

        val unlockedAchievement = progressedAchievement.unlock()
        unlockedAchievement.shouldBeUnlocked()
    }

    @Test
    @DisplayName("Should handle multiple achievements for same user")
    fun shouldHandleMultipleAchievementsForSameUser() {
        val userId = UUID.randomUUID().toString()
        val achievements = TestDataFactory.createAchievementSet(userId = userId)

        // Verify all achievements belong to the same user
        achievements.forEach { achievement ->
            assertEquals(
                userId,
                achievement.userId,
                "All achievements should belong to same user",
            )
        }

        // Verify unique achievement IDs
        val achievementIds = achievements.map { it.id }
        assertEquals(
            achievements.size,
            achievementIds.toSet().size,
            "All achievement IDs should be unique",
        )
    }

    @Test
    @DisplayName("Should handle achievement progress tracking across types")
    fun shouldHandleAchievementProgressTrackingAcrossTypes() {
        val userId = UUID.randomUUID().toString()
        val achievements = TestDataFactory.createAchievementSet(
            userId = userId,
            includeProgress = true,
        )

        // Verify different achievement types have appropriate progress
        achievements.forEach { achievement ->
            assertTrue(
                achievement.progress >= 0,
                "Progress should be non-negative for ${achievement.type}",
            )
            assertTrue(
                achievement.progress <= achievement.target || achievement.isUnlocked,
                "Progress should not exceed target unless unlocked for ${achievement.type}",
            )
        }
    }
}
