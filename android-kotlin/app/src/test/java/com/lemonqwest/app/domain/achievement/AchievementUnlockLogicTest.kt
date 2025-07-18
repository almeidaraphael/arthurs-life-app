package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlockable
import com.lemonqwest.app.domain.shouldBeUnlocked
import com.lemonqwest.app.domain.shouldNotBeUnlockable
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Comprehensive test suite for achievement unlock logic with complete test isolation.
 *
 * Tests cover:
 * - Achievement unlock when progress reaches target with test isolation
 * - Unlock prevention when progress below target with parallel safety
 * - Already unlocked achievement handling with modern patterns
 * - Progress overflow during unlock with thread safety
 * - Unlock timestamp generation with complete isolation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Unlock Logic Tests")
class AchievementUnlockLogicTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should unlock achievement when progress reaches target")
    fun shouldUnlockAchievementWhenProgressReachesTarget() = runTest {
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
    fun shouldNotUnlockAchievementWhenProgressIsBelowTarget() = runTest {
        val achievement = TestDataFactory.createAchievement(
            type = AchievementType.CENTURY_CLUB,
            progress = 5,
        )

        achievement.shouldNotBeUnlockable()
    }

    @Test
    @DisplayName("Should not unlock already unlocked achievement")
    fun shouldNotUnlockAlreadyUnlockedAchievement() = runTest {
        val achievement = TestDataFactory.createUnlockedAchievement()

        achievement.shouldBeUnlocked()
        achievement.shouldNotBeUnlockable() // Already unlocked
    }

    @Test
    @DisplayName("Should handle unlock with progress exceeding target")
    fun shouldHandleUnlockWithProgressExceedingTarget() = runTest {
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
    fun shouldGenerateUnlockTimestamp() = runTest {
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
