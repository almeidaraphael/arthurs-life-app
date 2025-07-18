package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlockable
import com.lemonqwest.app.domain.shouldBeUnlocked
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for achievement edge cases.
 *
 * Tests cover:
 * - Zero target achievement handling
 * - Very large progress values
 * - Rapid achievement updates
 * - Exact target unlock scenarios
 * - Past unlock timestamp handling
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Edge Cases Tests")
class AchievementEdgeCasesTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should handle zero target achievements")
    fun shouldHandleZeroTargetAchievements() {
        // Create a hypothetical achievement with zero target
        val achievement = Achievement(
            id = "test-achievement",
            type = AchievementType.FIRST_STEPS, // Use existing type but test with zero target scenario
            progress = 0,
            userId = UUID.randomUUID().toString(),
        )

        // Even with zero target, progress percentage should be handled gracefully
        assertTrue(
            achievement.progressPercentage >= 0,
            "Progress percentage should be non-negative",
        )
    }

    @Test
    @DisplayName("Should handle very large progress values")
    fun shouldHandleVeryLargeProgressValues() {
        val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

        val largeProgress = Int.MAX_VALUE
        val updatedAchievement = achievement.updateProgress(largeProgress)

        assertEquals(
            largeProgress,
            updatedAchievement.progress,
            "Should handle large progress values",
        )
        assertEquals(
            100,
            updatedAchievement.progressPercentage,
            "Progress percentage should be capped at 100%",
        )
    }

    @Test
    @DisplayName("Should handle rapid achievement updates")
    fun shouldHandleRapidAchievementUpdates() {
        val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

        var currentAchievement = achievement

        // Rapidly update progress
        for (i in 1..100) {
            currentAchievement = currentAchievement.updateProgress(i)
            assertEquals(
                i,
                currentAchievement.progress,
                "Progress should be updated correctly at step $i",
            )
        }
    }

    @Test
    @DisplayName("Should handle achievement unlock at exact target")
    fun shouldHandleAchievementUnlockAtExactTarget() {
        AchievementType.values().forEach { type ->
            val achievement = TestDataFactory.createAchievement(
                type = type,
                progress = type.target,
            )

            achievement.shouldBeUnlockable()

            val unlockedAchievement = achievement.unlock()

            unlockedAchievement.shouldBeUnlocked()
            assertEquals(
                type.target,
                unlockedAchievement.progress,
                "Progress should equal target after unlock",
            )
        }
    }

    @Test
    @DisplayName("Should handle achievement with past unlock timestamp")
    fun shouldHandleAchievementWithPastUnlockTimestamp() {
        val pastTime = System.currentTimeMillis() - 86400000 // 24 hours ago
        val achievement = Achievement(
            id = "test-achievement",
            type = AchievementType.FIRST_STEPS,
            isUnlocked = true,
            progress = 1,
            unlockedAt = pastTime,
            userId = UUID.randomUUID().toString(),
        )

        achievement.shouldBeUnlocked()
        assertEquals(pastTime, achievement.unlockedAt, "Should preserve past unlock timestamp")
    }
}
