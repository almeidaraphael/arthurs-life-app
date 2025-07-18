package com.lemonqwest.app.domain.achievement

import com.lemonqwest.app.domain.TestDataFactory
import com.lemonqwest.app.domain.shouldBeUnlocked
import com.lemonqwest.app.domain.shouldHaveProgress
import com.lemonqwest.app.domain.shouldHaveProperties
import com.lemonqwest.app.domain.shouldNotBeUnlockable
import com.lemonqwest.app.domain.shouldNotBeUnlocked
import com.lemonqwest.app.testutils.LemonQwestTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.UUID

/**
 * Focused test suite for achievement creation.
 *
 * Tests cover:
 * - Achievement creation with default values
 * - Achievement creation with custom parameters
 * - Factory method usage
 * - Unlocked achievement creation
 * - Partial achievement creation
 */
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Achievement Creation Tests")
class AchievementCreationTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    @Test
    @DisplayName("Should create achievement with default values")
    fun shouldCreateAchievementWithDefaults() {
        val achievement = TestDataFactory.createAchievement()

        assertNotNull(achievement.id, "Achievement should have an ID")
        assertTrue(achievement.id.isNotBlank(), "Achievement ID should not be blank")
        assertEquals(
            AchievementType.FIRST_STEPS,
            achievement.type,
            "Default type should be FIRST_STEPS",
        )
        achievement.shouldNotBeUnlocked()
        achievement.shouldHaveProgress(0, 0)
        assertNull(achievement.unlockedAt, "New achievement should not have unlock timestamp")
    }

    @Test
    @DisplayName("Should create achievement with custom parameters")
    fun shouldCreateAchievementWithCustomParameters() {
        val customType = AchievementType.CENTURY_CLUB
        val customUserId = UUID.randomUUID().toString()
        val customProgress = 5

        val achievement = TestDataFactory.createAchievement(
            type = customType,
            userId = customUserId,
            progress = customProgress,
            isUnlocked = false,
        )

        achievement.shouldHaveProperties(customType, customUserId)
        achievement.shouldHaveProgress(customProgress, 50) // 5/10 = 50%
        achievement.shouldNotBeUnlocked()
    }

    @Test
    @DisplayName("Should create achievement using factory method")
    fun shouldCreateAchievementUsingFactory() {
        val userId = UUID.randomUUID().toString()
        val achievement = Achievement.create(AchievementType.TASK_MASTER, userId)

        achievement.shouldHaveProperties(AchievementType.TASK_MASTER, userId)
        assertEquals("task_master-$userId", achievement.id, "Should generate correct ID format")
        achievement.shouldNotBeUnlocked()
        achievement.shouldHaveProgress(0, 0)
    }

    @Test
    @DisplayName("Should create unlocked achievement")
    fun shouldCreateUnlockedAchievement() {
        val achievement = TestDataFactory.createUnlockedAchievement()

        achievement.shouldBeUnlocked()
        assertNotNull(achievement.unlockedAt, "Unlocked achievement should have timestamp")
        assertTrue(achievement.unlockedAt!! > 0, "Unlock timestamp should be positive")
    }

    @Test
    @DisplayName("Should create partial achievement")
    fun shouldCreatePartialAchievement() {
        val achievement = TestDataFactory.createPartialAchievement(
            type = AchievementType.CENTURY_CLUB,
            progressPercentage = 70,
        )

        achievement.shouldHaveProgress(7, 70) // 70% of 10 = 7
        achievement.shouldNotBeUnlocked()
        achievement.shouldNotBeUnlockable() // 7 < 10
    }
}
