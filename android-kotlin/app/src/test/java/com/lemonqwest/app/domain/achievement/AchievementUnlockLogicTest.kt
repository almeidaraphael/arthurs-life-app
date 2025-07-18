package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlockable
import com.lemonqwest.app.domain.shouldBeUnlocked
import com.lemonqwest.app.domain.shouldNotBeUnlockable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Focused test suite for achievement unlock logic.
 *
 * Tests cover:
 * - Achievement unlock when progress reaches target
 * - Unlock prevention when progress below target
 * - Already unlocked achievement handling
 * - Progress overflow during unlock
 * - Unlock timestamp generation
 */
@DisplayName("Achievement Unlock Logic Tests")
class AchievementUnlockLogicTest {

    @Test
    @DisplayName("Should unlock achievement when progress reaches target")
    fun shouldUnlockAchievementWhenProgressReachesTarget() {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            progress = 1,
        )

        achievement.shouldBeUnlockable()

        val unlockedAchievement = achievement.unlock()

        unlockedAchievement.shouldBeUnlocked()
        assertNotNull(
            unlockedAchievement.unlockedAt,
            "Unlocked achievement should have timestamp",
        )
    }

    @Test
    @DisplayName("Should not unlock achievement when progress is below target")
    fun shouldNotUnlockAchievementWhenProgressIsBelowTarget() {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.CENTURY_CLUB,
            progress = 5,
        )

        achievement.shouldNotBeUnlockable()
    }

    @Test
    @DisplayName("Should not unlock already unlocked achievement")
    fun shouldNotUnlockAlreadyUnlockedAchievement() {
        val achievement = TestDataFactory.createUnlockedAchievement()

        achievement.shouldBeUnlocked()
        achievement.shouldNotBeUnlockable() // Already unlocked
    }

    @Test
    @DisplayName("Should handle unlock with progress exceeding target")
    fun shouldHandleUnlockWithProgressExceedingTarget() {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            progress = 5, // Exceeds target of 1
        )

        achievement.shouldBeUnlockable()

        val unlockedAchievement = achievement.unlock()

        unlockedAchievement.shouldBeUnlocked()
        assertEquals(
            1,
            unlockedAchievement.progress,
            "Progress should be set to target on unlock",
        )
    }

    @Test
    @DisplayName("Should generate unlock timestamp")
    fun shouldGenerateUnlockTimestamp() {
        val beforeUnlock = System.currentTimeMillis()
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.FIRST_STEPS,
            progress = 1,
        )

        val unlockedAchievement = achievement.unlock()
        val afterUnlock = System.currentTimeMillis()

        assertNotNull(
            unlockedAchievement.unlockedAt,
            "Unlocked achievement should have timestamp",
        )
        assertTrue(
            unlockedAchievement.unlockedAt!! >= beforeUnlock,
            "Unlock timestamp should be after test start",
        )
        assertTrue(
            unlockedAchievement.unlockedAt!! <= afterUnlock,
            "Unlock timestamp should be before test end",
        )
    }
}
