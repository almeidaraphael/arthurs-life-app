package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlockable
import com.lemonqwest.app.domain.shouldBeUnlocked
import com.lemonqwest.app.domain.shouldHaveProgress
import com.lemonqwest.app.domain.shouldNotBeUnlockable
import com.lemonqwest.app.domain.shouldNotBeUnlocked
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Comprehensive test suite for achievement progress tracking with complete test isolation.
 *
 * Tests cover:
 * - Progress tracking and updates with test isolation
 * - Progress percentage calculations with parallel safety
 * - Progress overflow handling with modern patterns
 * - Negative progress handling with thread safety
 * - Progress preservation during unlock with complete isolation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Progress Tracking Tests")
class AchievementProgressTrackingTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should track progress correctly")
    fun shouldTrackProgressCorrectly() = runTest {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.CENTURY_CLUB,
            progress = 3,
        )

        achievement.shouldHaveProgress(3, 30) // 3/10 = 30%
        achievement.shouldNotBeUnlocked()
        achievement.shouldNotBeUnlockable()
    }

    @Test
    @DisplayName("Should update progress")
    fun shouldUpdateProgress() = runTest {
        val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

        val updatedAchievement = achievement.updateProgress(5)

        updatedAchievement.shouldHaveProgress(5, 50)
        updatedAchievement.shouldNotBeUnlocked()
        updatedAchievement.shouldNotBeUnlockable()

        // Original should remain unchanged
        achievement.shouldHaveProgress(0, 0)
    }

    @Test
    @DisplayName("Should calculate progress percentage correctly")
    fun shouldCalculateProgressPercentageCorrectly() = runTest {
        val testCases = listOf(
            Triple(AchievementType.FIRST_STEPS, 0, 0), // 0/1 = 0%
            Triple(AchievementType.FIRST_STEPS, 1, 100), // 1/1 = 100%
            Triple(AchievementType.CENTURY_CLUB, 0, 0), // 0/10 = 0%
            Triple(AchievementType.CENTURY_CLUB, 3, 30), // 3/10 = 30%
            Triple(AchievementType.CENTURY_CLUB, 5, 50), // 5/10 = 50%
            Triple(AchievementType.CENTURY_CLUB, 10, 100), // 10/10 = 100%
            Triple(AchievementType.TOKEN_COLLECTOR, 25, 50), // 25/50 = 50%
            Triple(AchievementType.TOKEN_COLLECTOR, 50, 100), // 50/50 = 100%
        )

        testCases.forEach { (type, progress, expectedPercentage) ->
            val achievement = TestDataFactory.createAchievement(
                type = type,
                progress = progress,
            )

            assertEquals(
                expectedPercentage,
                achievement.progressPercentage,
                "Progress percentage should be $expectedPercentage for $progress/${type.target}",
            )
        }
    }

    @Test
    @DisplayName("Should handle progress overflow gracefully")
    fun shouldHandleProgressOverflowGracefully() = runTest {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            progress = 5, // More than target of 1
        )

        assertEquals(5, achievement.progress, "Progress should be preserved even if > target")
        assertEquals(
            100,
            achievement.progressPercentage,
            "Progress percentage should be capped at 100%",
        )
    }

    @Test
    @DisplayName("Should handle negative progress")
    fun shouldHandleNegativeProgress() = runTest {
        val achievement = TestDataFactory.createAchievement(type = AchievementType.CENTURY_CLUB)

        val updatedAchievement = achievement.updateProgress(-5)

        updatedAchievement.shouldHaveProgress(0, 0) // Should be coerced to 0
    }

    @Test
    @DisplayName("Should preserve progress during unlock")
    fun shouldPreserveProgressDuringUnlock() = runTest {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.CENTURY_CLUB,
            progress = 10,
        )

        achievement.shouldBeUnlockable()

        val unlockedAchievement = achievement.unlock()

        unlockedAchievement.shouldBeUnlocked()
        unlockedAchievement.shouldHaveProgress(10, 100) // Progress set to target
    }
}
